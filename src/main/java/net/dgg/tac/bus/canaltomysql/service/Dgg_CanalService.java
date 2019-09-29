package net.dgg.tac.bus.canaltomysql.service;

import ch.qos.logback.core.db.dialect.DBUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.*;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import net.dgg.tac.bus.canaltomysql.utils.DggDBCPUtil;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;


/**
 * @ClassName Dgg_MapUtil
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/9
 * @Version 1.0
 **/

public class Dgg_CanalService {

    public static void dgg_canalService(CanalConnector connector, String path,Map<String,List<String>> listMap,KafkaProducer kafkaProducer,String topic){

        while (true){
            Connection conn = DggDBCPUtil.getConnection();
            Message message = connector.getWithoutAck(1000);
            List<CanalEntry.Entry> entries = message.getEntries();
            columnsOperation(entries,path,conn,listMap,kafkaProducer,topic);
            long id = message.getId();
            connector.ack(id);

            DggDBCPUtil.close(conn,null,null);
        }
    }

    /**
     * 列的操作
     */
    private static void columnsOperation(List<CanalEntry.Entry> entries, String path, Connection conn, Map<String,List<String>> listMap, KafkaProducer kafkaProducer, String topic){
        for (Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange = null;
            try {
                rowChange= CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            EventType eventType = rowChange.getEventType();
            if(eventType== CanalEntry.EventType.DELETE || eventType== CanalEntry.EventType.UPDATE || eventType== CanalEntry.EventType.INSERT){
                String tableName = entry.getHeader().getTableName();
                String dbName = entry.getHeader().getSchemaName();
                long offset = entry.getHeader().getLogfileOffset();
                List<String> columnList = null;
                switch (tableName){
                    case "bus_business_emp":
                        columnList = listMap.get("busColumnList");
                        break;
                    case "bus_business_mng":
                        columnList = listMap.get("busColumnList");
                        break;
                    case "bus_business_day_drop":
                        columnList = listMap.get("busColumnList");
                        break;
                    case "vis_customer_record" :
                        columnList = listMap.get("recordColumnList");
                        break;
                    case "orf_order":
                        columnList = listMap.get("orderColumnList");
                        break;
                    case "orf_performance_profit":
                        columnList = listMap.get("profitColumnList");
                        break;
                    case "cms_rule_config":
                        columnList = listMap.get("ruleColumnList");
                        break;
                    case "vis_out_meet_customer":
                        columnList = listMap.get("outColumnList");
                        break;
                    case "sys_org_closure":
                        columnList = listMap.get("closureColumnList");
                        break;
                    case "sys_org_main":
                        columnList = listMap.get("mainColumnList");
                        break;
                    case "sys_user_info":
                        columnList = listMap.get("userInfoColumnList");
                        break;
                    default:
                        System.out.println("+++++++++监控表错误+++++++++"+tableName);
                        return;
                }

                jsonOperation(rowChange,path,conn,kafkaProducer,columnList,topic,tableName,dbName,eventType.toString(),offset);
            }
        }
    }

    /**
     * 将列的值写入到json，并操作json
     * @param rowChange
     * @param path
     * @param conn
     * @param kafkaProducer
     * @param columnList
     * @param topic
     * @param tableName
     * @param dbName
     * @param eventType
     * @param offset
     */
    private static void jsonOperation(RowChange rowChange,String path, Connection conn,KafkaProducer kafkaProducer, List<String> columnList, String topic,String tableName,String dbName,String eventType,Long offset){

        for (RowData rowData : rowChange.getRowDatasList()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("eventType", eventType);
            jsonObject.put("table_name", tableName);
            jsonObject.put("dbName", dbName);
            jsonObject.put("offset", offset);
            if (eventType.equals("DELETE")) {
                //写入删除前的数据到json
                for (Column column : rowData.getBeforeColumnsList()) {
                    String key = column.getName();
                    if (columnList.contains(key)) {
                        String value = column.getValue();
                        jsonObject.put(key, value);
                    }
                }
            } else {

                if(eventType.equals("UPDATE")){
                    //emp表json添加之前的follower_id
                    if(tableName.equals("bus_business_emp")){
                        for (Column column : rowData.getBeforeColumnsList()) {
                            if(column.getName().equals("follower_id")){
                                jsonObject.put("pre_follower",column.getValue());
                            }

                        }
                    }
                    //sys_user_info表json添加之前的org_id
                    if(tableName.equals("sys_user_info")){
                        for (Column column : rowData.getBeforeColumnsList()) {
                            if(column.getName().equals("org_id")){
                                jsonObject.put("pre_org_id",column.getValue());
                            }

                        }
                    }

                }
                for (Column column : rowData.getAfterColumnsList()) {
                    String key = column.getName();
                    //只添加需要的列
                    if (columnList.contains(key)) {
                        jsonObject.put(column.getName(), column.getValue());
                    }
                }
            }
            //写入mysql
            jsonToMysql(conn,jsonObject,eventType,tableName,columnList);
            //写入kafka
            jsonToKafka(kafkaProducer,topic,jsonObject,dbName);
            //写入本地文件
            jsonToLocal(path,jsonObject,tableName);
        }
    }

    /**
     * 将json写入kafka
     * @param kafkaProducer
     * @param topics
     * @param jsonObject
     */
    private static void jsonToKafka(KafkaProducer kafkaProducer,String topics,JSONObject jsonObject,String dbName){
        //bus_business_day_drop表的数据不会写到kafka
        String tbName = jsonObject.get("table_name").toString();
        if(tbName.equals("bus_business_day_drop") || tbName.equals("sys_org_main") || tbName.equals("sys_org_closure")){
            return;
        }
        String[] split = topics.split(",");
        for (String topic : split) {
            try {
                if(topic.contains(dbName)){
                    kafkaProducer.send(new ProducerRecord(topic,jsonObject.toJSONString())).get();
                    System.out.println("="+topic+"=");
                }
            } catch (InterruptedException e) {
                System.out.println("+++++++++写入kafka失败++++++++");
                e.printStackTrace();
            } catch (ExecutionException e) {
                System.out.println("+++++++++写入kafka失败++++++++");
                e.printStackTrace();
            }
        }

    }

    /**
     * 将json写入本地文件
     * @param paths
     * @param jsonObject
     *
     */
    private static void jsonToLocal(String paths,JSONObject jsonObject,String tableName) {
        //将json数据写入本地文件
        String[] splitPath = paths.split(",");
        String fPath="/opt/service/ibossworkbench/canalproject/db_iboss/txts/";
        //String fPath="D://";
        for (String path : splitPath) {
            if (Files.notExists(Paths.get(fPath+path))) {
                try {
                    Files.createFile(Paths.get(fPath+path));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (path.contains(tableName)) {
                try (
                        BufferedWriter bfw = Files.newBufferedWriter(Paths.get(fPath+path), StandardOpenOption.APPEND)) {
                    bfw.write(jsonObject.toJSONString());
                    bfw.newLine();
                    bfw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将json写入mysql
     * @param conn
     * @param jsonObject
     */
    private static void jsonToMysql(Connection conn,JSONObject jsonObject,String eventType,String tableName,List<String> culomnList){
        String targetTable = null;
        switch (tableName){
            case "bus_business_emp":
                targetTable="dgg_business_emp_mid";
                break;
            case "bus_business_mng":
                targetTable="dgg_business_mng_mid";
                break;
            case "bus_business_day_drop":
                targetTable="dgg_drop_bus_info";
                break;
            case "orf_order":
                targetTable="dgg_orf_order_mid";
                break;
            case "orf_performance_profit":
                targetTable="dgg_orf_performance_profit_mid";
                break;
            case "vis_customer_record":
                targetTable="dgg_customer_record_mid";
                break;
            case "cms_rule_config":
                targetTable="dgg_rule_config";
                break;
            case "vis_out_meet_customer":
                targetTable="dgg_out_meet_mid";
                break;
            case "sys_org_closure":
                targetTable="sys_org_closure_mid";
                break;
            case "sys_org_main":
                targetTable="sys_org_main_mid";
                break;
            case "sys_user_info":
                targetTable="sys_user_info_mid";
                break;
            default:
                System.out.println("+++++++++++++++表错误+++++++++++：" + tableName);
        }

        if(eventType.equals("DELETE")){
            String del_sql="delete from "+targetTable+" where id = ?;";
            String tmpKey="id";
            String tmpKey2="ancestor_id";
            Long ancestorId=0L;
            //sys_org_closure表的id为空，需换一个列作为判断条件
            if(tableName.equals("sys_org_closure")){
                ancestorId = Long.parseLong(jsonObject.get(tmpKey2).toString());
                del_sql="delete from "+targetTable+" where organization_id = ? and ancestor_id=?;";
                tmpKey="organization_id";

            }
            Long id = Long.parseLong(jsonObject.get(tmpKey).toString());

            PreparedStatement ps = null;
            try {

                ps = conn.prepareStatement(del_sql);
                ps.setLong(1,id);
                if(tableName.equals("sys_org_closure")){
                    ps.setLong(2,ancestorId);
                }
                int i = ps.executeUpdate();
                System.out.println(i+"=deleted=: "+ id);
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }else {
            try {
                int i = excuteReplace(jsonObject, targetTable,conn,culomnList);
                //System.out.println(i);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拼接执行的sql并执行
     * @param jsonObject
     * @param tableName
     * @param conn
     * @return
     * @throws SQLException
     */
    private static int excuteReplace(JSONObject jsonObject,String tableName,Connection conn,List<String> culomnList) throws SQLException {

        /**要插入的字段sql，其实就是用key拼起来的**/
        StringBuilder columnSql = new StringBuilder();
        /**要插入的字段值，其实就是？**/
        StringBuilder unknownMarkSql = new StringBuilder();
        Object[] bindArgs = new Object[culomnList.size()];
        int i = 0;
        for (String key:culomnList) {
            columnSql.append(i == 0 ? "" : ",");
            columnSql.append(key);
            unknownMarkSql.append(i == 0 ? "" : ",");
            unknownMarkSql.append("?");
            bindArgs[i] = jsonObject.get(key);
            /*if(jsonObject.get(key).toString().equals("bus_business_emp")){
                bindArgs[i] = "emp";
            }
            if(jsonObject.get(key).toString().equals("bus_business_mng")){
                bindArgs[i] = "mng";
            }*/
            i++;
        }
        /**开始拼插入的sql语句**/
        StringBuilder sql = new StringBuilder();
        sql.append("replace into ");
        sql.append(tableName);
        sql.append(" (");
        sql.append(columnSql);
        sql.append(" )  VALUES (");
        sql.append(unknownMarkSql);
        sql.append(" )");
        return executeUpdate(sql.toString(), bindArgs,conn);
    }

    /**
     * 执行sql语句：replace
     * @param sql
     * @param bindArgs
     * @param conn
     * @return
     * @throws SQLException
     */
    public static int executeUpdate(String sql, Object[] bindArgs,Connection conn)  {
        /**影响的行数**/
        int affectRowCount = -1;

        PreparedStatement ps = null;
        try {
            /**执行SQL预编译**/
            ps = conn.prepareStatement(sql);

            if (bindArgs != null) {
                /**绑定参数设置sql占位符中的值**/
                for (int i = 0; i < bindArgs.length; i++) {
                    ps.setObject(i + 1, bindArgs[i]=="" ? null:bindArgs[i]);
                }
            }
            /**执行sql**/
            affectRowCount = ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return affectRowCount;
        }
    }



}
