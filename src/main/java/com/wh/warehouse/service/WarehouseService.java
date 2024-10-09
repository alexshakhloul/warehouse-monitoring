package com.wh.warehouse.service;

import com.wh.model.SensorData;
import com.wh.model.SensorType;
import com.wh.warehouse.config.UDPConfiguration;
import com.wh.warehouse.service.kafka.KafkaPublisher;
import com.wh.warehouse.service.listener.udp.UDPListener;
import com.wh.warehouse.service.listener.udp.UDPListenerFactory;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

@Service
@Slf4j
public class WarehouseService {

    private final UDPConfiguration udpConfiguration;
    private final KafkaPublisher kafkaPublisher;
    private final UDPListenerFactory udpListenerFactory;

    public WarehouseService(UDPConfiguration udpConfiguration, KafkaPublisher kafkaPublisher, UDPListenerFactory udpListenerFactory) {
        this.udpConfiguration = udpConfiguration;
        this.kafkaPublisher = kafkaPublisher;
        this.udpListenerFactory = udpListenerFactory;
    }

    @PostConstruct
    public void startConsumingSensorData() {
        log.info("Starting UDP channels");
        Map<SensorType, Integer> udpPortMap = udpConfiguration.getUdpPortMap();
        UDPListener temperatureListener = udpListenerFactory.create(udpPortMap.get(SensorType.TEMPERATURE), SensorType.TEMPERATURE);
        Flux<SensorData> temperatureFlux = temperatureListener.listen();
        temperatureFlux.subscribe(kafkaPublisher::publish);
        UDPListener humidityListener = udpListenerFactory.create(udpPortMap.get(SensorType.HUMIDITY), SensorType.HUMIDITY);
        Flux<SensorData> humidityFlux = humidityListener.listen();
        humidityFlux.subscribe(kafkaPublisher::publish);
    }
}
