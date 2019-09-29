package net.dgg.tac.bus.canaltomysql.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import redis.clients.jedis.JedisCluster;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * @ClassName Dgg_MapUtil
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/9
 * @Version 1.0
 **/

public class Dgg_CanalToMysql {

    public static void dgg_canalToMysql(CanalConnector connector, String path, Connection conn,List<String> column_list,KafkaProducer kafkaProducer,String topic){

        while (true){
            Message message = connector.getWithoutAck(1000);
            List<CanalEntry.Entry> entries = message.getEntries();
            columnsToMysql(entries,path,conn,column_list,kafkaProducer,topic);
            long id = message.getId();
            connector.ack(id);

        }
    }

    /**
     * 将指定列写入mysql
     */
    private static void columnsToMysql(List<CanalEntry.Entry> entries, String path, Connection conn, List<String> column_list, KafkaProducer kafkaProducer, String topic){
        Path textPath = Paths.get(path);

        for (CanalEntry.Entry entry : entries) {

            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange = null;
            try {
                rowChange= CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
            CanalEntry.EventType eventType = rowChange.getEventType();
            if(eventType== CanalEntry.EventType.DELETE || eventType== CanalEntry.EventType.UPDATE || eventType== CanalEntry.EventType.INSERT){
                for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                    JSONObject jsonObject=new JSONObject();
                    jsonObject.put("eventType",rowChange.getEventType());
                    jsonObject.put("tableName",entry.getHeader().getTableName());
                    jsonObject.put("dbName",entry.getHeader().getSchemaName());
                    jsonObject.put("offset",entry.getHeader().getLogfileOffset());
                    String table_name = String .valueOf(jsonObject.get("tableName"));
                    if(eventType.equals(CanalEntry.EventType.DELETE)){
                        //写入删除前的数据到json
                        for (CanalEntry.Column column : rowData.getBeforeColumnsList()) {
                            String key=column.getName();
                            if(key.equals("id") || key.equals("follower_id") || key.equals("follower_organization_id")){
                                String value=column.getValue();
                                jsonObject.put(key,value);
                            }
                        }
                        //取出json对应列的值，拼接后删除redis中指定数据

                        Long id = Long.parseLong(jsonObject.get("id").toString());

                        Long follower_id = 0L;

                        PreparedStatement ps = null;
                        try {
                            String del_sql="delete from dgg_business_mid where id = ?;";
                            ps = conn.prepareStatement(del_sql);
                            ps.setLong(1,id);

                            int i = ps.executeUpdate();
                            System.out.println("======delete done======: "+ i);
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
                        for (CanalEntry.Column column : rowData.getAfterColumnsList()) {

                            String key=column.getName();
                            //只添加需要的列
                            if(column_list.contains(key)){
                                jsonObject.put(column.getName(),column.getValue());
                            }
                        }


                        String sql = "replace into dgg_business_mid values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        PreparedStatement ps =null;
                        try {
                            ps = conn.prepareStatement(sql);

                            ps.setObject(1,jsonObject.get("id"));
                            ps.setObject(2,jsonObject.get("create_time")=="" ? null:jsonObject.get("create_time"));
                            ps.setObject(3,jsonObject.get("update_time")=="" ? null:jsonObject.get("update_time"));
                            ps.setObject(4,jsonObject.get("no"));
                            ps.setObject(5,jsonObject.get("follower_id")=="" ? -100:jsonObject.get("follower_id"));
                            ps.setObject(6,jsonObject.get("customer_name"));
                            ps.setObject(7,jsonObject.get("customer_no"));
                            ps.setObject(8,jsonObject.get("customer_phone"));
                            ps.setObject(9,jsonObject.get("next_follow_time")=="" ? null:jsonObject.get("next_follow_time"));
                            ps.setObject(10,jsonObject.get("business_status"));
                            ps.setObject(11,jsonObject.get("will_drop_time")=="" ? null:jsonObject.get("will_drop_time"));
                            ps.setObject(12,jsonObject.get("follower_organization_id")=="" ? -100:jsonObject.get("follower_organization_id"));
                            ps.setObject(13,jsonObject.get("way_code"));
                            ps.setObject(14,jsonObject.get("distribution_time")=="" ? null:jsonObject.get("distribution_time"));
                            ps.setObject(15,table_name.equals("bus_business_emp") ? "emp":"mng");



                            int i = ps.executeUpdate();
                            System.out.println("======update done======: "+i);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }finally {
                            try {
                                ps.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //写入kafka主题
                    try {
                        kafkaProducer.send(new ProducerRecord(topic,jsonObject.toJSONString())).get();
                        System.out.println("=0=");
                    } catch (InterruptedException e) {
                        System.out.println("+++++++++写入kafka失败++++++++");
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        System.out.println("+++++++++写入kafka失败++++++++");
                        e.printStackTrace();
                    }
                    //将json数据写入本地文件
                    try (
                            BufferedWriter bfw =Files.newBufferedWriter(textPath, StandardOpenOption.APPEND)){
                        bfw.write(jsonObject.toJSONString());
                        bfw.newLine();
                        bfw.flush();
                        System.out.println(1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
