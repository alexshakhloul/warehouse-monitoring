package com.wh.central.service.kafka;

import com.wh.central.service.CentralMonitoringService;
import com.wh.model.SensorData;
import com.wh.util.ObjectMapperHelper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private CentralMonitoringService centralMonitoringService;

    public KafkaConsumer(CentralMonitoringService centralMonitoringService) {
        this.centralMonitoringService = centralMonitoringService;
    }

    @KafkaListener(topics = {"${service.consuming-topics.sensors-data}"}, groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> record) {
        SensorData sensorData = ObjectMapperHelper.convertFromJson(record.value(), SensorData.class);
        centralMonitoringService.handleSensorData(sensorData);
    }
}
