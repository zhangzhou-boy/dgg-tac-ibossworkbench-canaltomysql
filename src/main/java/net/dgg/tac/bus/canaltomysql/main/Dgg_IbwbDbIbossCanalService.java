package net.dgg.tac.bus.canaltomysql.main;

import com.alibaba.otter.canal.client.impl.ClusterCanalConnector;
import com.alibaba.otter.canal.client.impl.ClusterNodeAccessStrategy;
import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import net.dgg.tac.bus.canaltomysql.service.Dgg_CanalService;
import net.dgg.tac.bus.canaltomysql.utils.DggDBCPUtil;
import net.dgg.tac.bus.canaltomysql.utils.DggKafkaUtil;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * @ClassName Dgg_T_Business_ToMysql
 * @Description TODO
 * @Auther Administrator
 * @Date 2019/5/24 0024  15:13
 * @Version 1.0
 **/

public class Dgg_IbwbDbIbossCanalService {


    public static void main(String[] args) {
        Properties prop = new Properties();
        try {
            prop.load(Dgg_IbwbDbIbossCanalService.class.getResourceAsStream("/conf.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String destination = prop.getProperty("DESTINATION");
        String user = prop.getProperty("USER");
        String password = prop.getProperty("PASSWORD");
        String zkClients = prop.getProperty("ZKCLIENTS");
        String paths = prop.getProperty("PATHS");
        String filter = prop.getProperty("FILTER");
        String topic = prop.getProperty("TOPIC");

        //获得kafka producer
        KafkaProducer producer = DggKafkaUtil.getKafkaProducer();

        //5152集群链接
        ClusterCanalConnector connector = new ClusterCanalConnector(
                user,
                password,
                destination,
                new ClusterNodeAccessStrategy(destination, ZkClientx.getZkClient(zkClients))
        );
        String busColumns = prop.getProperty("dgg_business_mid_columns");
        String orderColumns = prop.getProperty("dgg_orf_order_mid_columns");
        String profitColumns = prop.getProperty("dgg_orf_performance_profit_mid_columns");
        String recordColumns = prop.getProperty("dgg_customer_record_mid_columns");
        String ruleColumns = prop.getProperty("dgg_rule_config_columns");
        String outColumns = prop.getProperty("dgg_out_meet_mid_columns");
        String closureColumns = prop.getProperty("sys_org_closure_mid_columns");
        String mainColumns = prop.getProperty("sys_org_main_mid_columns");
        String userInfoColumns = prop.getProperty("sys_user_info_mid_columns");

        List<String> busColumnList = getColumList(busColumns);
        List<String> orderColumnList = getColumList(orderColumns);
        List<String> profitColumnList = getColumList(profitColumns);
        List<String> recordColumnList = getColumList(recordColumns);
        List<String> ruleColumnList = getColumList(ruleColumns);
        List<String> outColumnList = getColumList(outColumns);
        List<String> closureColumnList = getColumList(closureColumns);
        List<String> mainColumnList = getColumList(mainColumns);
        List<String> userInfoColumnList = getColumList(userInfoColumns);

        Map<String,List<String>> listMap = new HashMap<>(9);
        listMap.put("busColumnList",busColumnList);
        listMap.put("orderColumnList",orderColumnList);
        listMap.put("profitColumnList",profitColumnList);
        listMap.put("recordColumnList",recordColumnList);
        listMap.put("ruleColumnList",ruleColumnList);
        listMap.put("outColumnList",outColumnList);
        listMap.put("closureColumnList",closureColumnList);
        listMap.put("mainColumnList",mainColumnList);
        listMap.put("userInfoColumnList",userInfoColumnList);

        try {
            connector.connect();
            connector.subscribe(filter);
            connector.rollback();
//            Dgg_CanalToMysql.dgg_canalToMysql(connector,PATHS,conn,columnList,producer,TOPIC);
            Dgg_CanalService.dgg_canalService(connector,paths,listMap,producer,topic);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            producer.close();
            connector.stopRunning();
        }

    }
    private static List<String> getColumList(String columns){
        List<String> columnList = new ArrayList<>();
        String[] split = columns.split(",");
        for (String column : split) {
            columnList.add(column);
        }
        return columnList;
    }
}
