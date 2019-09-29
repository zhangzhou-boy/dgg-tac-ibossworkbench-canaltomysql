package net.dgg.tac.bus.canaltomysql.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


/**
 * @ClassName Dgg_MapUtil
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/9
 * @Version 1.0
 **/

public class Dgg_CanalToMysqlService {

    public static void dgg_canalService(CanalConnector connector, String path,Map<String,List<String>> listMap){

        while (true){
            Connection conn = DggDBCPUtil.getConnection();
            Message message = connector.getWithoutAck(1000);
            List<Entry> entries = message.getEntries();
            columnsOperation(entries,path,conn,listMap);
            long id = message.getId();
            connector.ack(id);

            DggDBCPUtil.close(conn,null,null);
        }
    }

    /**
     * 列的操作
     */
    private static void columnsOperation(List<Entry> entries, String path, Connection conn, Map<String,List<String>> listMap){
        for (Entry entry : entries) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange = null;
            try {
                rowChange= RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            EventType eventType = rowChange.getEventType();
            if(eventType== EventType.DELETE || eventType== EventType.UPDATE || eventType== EventType.INSERT){
                String tableName = entry.getHeader().getTableName();
                String dbName = entry.getHeader().getSchemaName();
                long offset = entry.getHeader().getLogfileOffset();
                List<String> columnList = null;
                switch (tableName){
                    case "sys_user_info":
                        columnList = listMap.get("userColumnList");
                        break;
                    case "sys_org_main":
                        columnList = listMap.get("mainColumnList");
                        break;
                    case "sys_org_closure":
                        columnList = listMap.get("closureColumnList");
                        break;
                    case "gssc_product_order" :
                        columnList = listMap.get("proOrderColumnList");
                        break;
                    case "gssc_product_time_status":
                        columnList = listMap.get("proTimeColumnList");
                        break;
                    case "gssc_product_node_operating":
                        columnList = listMap.get("proNodeColumnList");
                        break;
                    case "gssc_product_busbottomsheet":
                        columnList = listMap.get("proBusColumnList");
                        break;
                    case "sc_cms_node_timeout_punish":
                        columnList = listMap.get("scCmsColumnList");
                        break;
                    default:
                        System.out.println("+++++++++监控表错误+++++++++"+tableName);
                        return;
                }

                jsonOperation(rowChange,path,conn,columnList,tableName,dbName,eventType.toString(),offset);
            }
        }
    }

    /**
     * 将列的值写入到json，并操作json
     * @param rowChange
     * @param path
     * @param conn
     * @param columnList
     * @param tableName
     * @param dbName
     * @param eventType
     * @param offset
     */
    private static void jsonOperation(RowChange rowChange,String path, Connection conn, List<String> columnList, String tableName,String dbName,String eventType,Long offset){

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

            //写入本地文件
            jsonToLocal(path,jsonObject,tableName);
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
        String fPath="/opt/service/ibossworkbench/canalproject/bi_gs/txts/";
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
            case "sys_user_info":
                targetTable="gs_sys_user_info";
                break;
            case "sys_org_main":
                targetTable="gs_sys_org_main";
                break;
            case "sys_org_closure":
                targetTable="gs_sys_org_closure";
                break;
            case "gssc_product_order":
                targetTable="gs_gssc_product_order";
                break;
            case "gssc_product_time_status":
                targetTable="gs_gssc_product_time_status";
                break;
            case "gssc_product_node_operating":
                targetTable="gs_gssc_product_node_operating";
                break;
            case "gssc_product_busbottomsheet":
                targetTable="gs_gssc_product_busbottomsheet";
                break;
            case "sc_cms_node_timeout_punish":
                targetTable="gs_sc_cms_node_timeout_punish";
                break;
            default:
                System.out.println("+++++++++++++++表错误+++++++++++：" + tableName);
        }

        if(eventType.equals("DELETE")){
            String del_sql="delete from "+targetTable+" where id = ?;";
            String tmpKey="id";
            String tmpKey2="ancestor_id";
            Long ancestorId = 0L;
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
