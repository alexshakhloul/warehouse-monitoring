package com.wh.warehouse.service.kafka;


import com.wh.model.SensorData;
import com.wh.util.ObjectMapperHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPublisher implements Publisher<SensorData> {
    @Value(value = "${service.producer-topics.sensors-data}")
    private String producerTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(SensorData data) {
        kafkaTemplate.send(producerTopic, data.getSensorId(), ObjectMapperHelper.convertToJson(data));
    }
}
