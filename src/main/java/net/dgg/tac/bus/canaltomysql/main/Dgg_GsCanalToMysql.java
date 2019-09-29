package net.dgg.tac.bus.canaltomysql.main;

import com.alibaba.otter.canal.client.impl.ClusterCanalConnector;
import com.alibaba.otter.canal.client.impl.ClusterNodeAccessStrategy;
import com.alibaba.otter.canal.common.zookeeper.ZkClientx;
import net.dgg.tac.bus.canaltomysql.service.Dgg_CanalService;
import net.dgg.tac.bus.canaltomysql.service.Dgg_CanalToMysqlService;
import net.dgg.tac.bus.canaltomysql.utils.DggKafkaUtil;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName Dgg_GsCanalToMysql
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/7/15
 * @Version 1.0
 **/

public class Dgg_GsCanalToMysql {


    public static void main(String[] args) {
        Properties prop = new Properties();
        try {
            prop.load(Dgg_GsCanalToMysql.class.getResourceAsStream("/gs_conf.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String destination = prop.getProperty("DESTINATION");
        String user = prop.getProperty("USER");
        String password = prop.getProperty("PASSWORD");
        String zkClients = prop.getProperty("ZKCLIENTS");
        String paths = prop.getProperty("PATHS");
        String filter = prop.getProperty("FILTER");


        //5152集群链接
        ClusterCanalConnector connector = new ClusterCanalConnector(
                user,
                password,
                destination,
                new ClusterNodeAccessStrategy(destination, ZkClientx.getZkClient(zkClients))
        );
        String userInfoColumns = prop.getProperty("gs_sys_user_info_columns");
        String orgMainColumns = prop.getProperty("gs_sys_org_main_columns");
        String orgClosureColumns = prop.getProperty("gs_sys_org_closure_columns");
        String proOrderColumns = prop.getProperty("gs_gssc_product_order_columns");
        String proTimeStatusColumns = prop.getProperty("gs_gssc_product_time_status_columns");
        String proNodeOperColumns = prop.getProperty("gs_gssc_product_node_operating_columns");
        String proBusColumns = prop.getProperty("gs_gssc_product_busbottomsheet_columns");
        String scCmsColumns = prop.getProperty("gs_sc_cms_node_timeout_punish_columns");

        List<String> userColumnList = getColumList(userInfoColumns);
        List<String> mainColumnList = getColumList(orgMainColumns);
        List<String> closureColumnList = getColumList(orgClosureColumns);
        List<String> proOrderColumnList = getColumList(proOrderColumns);
        List<String> proTimeColumnList = getColumList(proTimeStatusColumns);
        List<String> proNodeColumnList = getColumList(proNodeOperColumns);
        List<String> proBusColumnList = getColumList(proBusColumns);
        List<String> scCmsColumnList = getColumList(scCmsColumns);


        Map<String,List<String>> listMap = new HashMap<>(8);
        listMap.put("userColumnList",userColumnList);
        listMap.put("mainColumnList",mainColumnList);
        listMap.put("closureColumnList",closureColumnList);
        listMap.put("proOrderColumnList",proOrderColumnList);
        listMap.put("proTimeColumnList",proTimeColumnList);
        listMap.put("proNodeColumnList",proNodeColumnList);
        listMap.put("proBusColumnList",proBusColumnList);
        listMap.put("scCmsColumnList",scCmsColumnList);


        try {
            connector.connect();
            connector.subscribe(filter);
            connector.rollback();
            Dgg_CanalToMysqlService.dgg_canalService(connector,paths,listMap);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
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
