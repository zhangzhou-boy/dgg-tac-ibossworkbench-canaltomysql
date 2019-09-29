package net.dgg.tac.bus.canaltomysql.utils;


import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName DggKafkaUtil
 * @Description TODO
 * @Auther zhangzhou
 * @Date 2019/5/13
 * @Version 1.0
 **/

public class DggKafkaUtil {

    public static KafkaProducer getKafkaProducer(){
        Properties props = new Properties();
        try {
            props.load(KafkaProducer.class.getResourceAsStream("/kafka.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        KafkaProducer kafkaProducer = new KafkaProducer(props);
        return kafkaProducer;
    }
}
