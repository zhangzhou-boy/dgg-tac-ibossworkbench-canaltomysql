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
/*        props.put("bootstrap.servers", "172.16.0.217:9092,172.16.0.218:9092,172.16.0.219:9092");
        props.put("request.required.acks",1);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");*/

        try {
            props.load(KafkaProducer.class.getResourceAsStream("/kafka.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        KafkaProducer kafkaProducer = new KafkaProducer(props);
        return kafkaProducer;
    }
}
