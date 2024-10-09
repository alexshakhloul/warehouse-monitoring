package com.wh.warehouse.service;

import com.wh.model.SensorData;
import com.wh.model.SensorType;
import com.wh.warehouse.config.UDPConfiguration;
import com.wh.warehouse.service.WarehouseService;
import com.wh.warehouse.service.kafka.KafkaPublisher;
import com.wh.warehouse.service.listener.udp.UDPListener;
import com.wh.warehouse.service.listener.udp.UDPListenerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class WarehouseServiceTest {
    @Mock
    private UDPConfiguration udpConfiguration;

    @Mock
    private KafkaPublisher kafkaPublisher;

    @Mock
    private UDPListenerFactory udpListenerFactory;

    @Mock
    private UDPListener temperatureListener;

    @Mock
    private UDPListener humidityListener;

    @InjectMocks
    private WarehouseService warehouseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testStartConsumingSensorData() {
        Map<SensorType, Integer> udpPortMap = new HashMap<>();
        udpPortMap.put(SensorType.TEMPERATURE, 3344);
        udpPortMap.put(SensorType.HUMIDITY, 3355);
        when(udpConfiguration.getUdpPortMap()).thenReturn(udpPortMap);

        Flux<SensorData> mockTemperatureFlux = Flux.just(
                SensorData.builder().setSensorId("temp1").setValue(23.5).setType(SensorType.TEMPERATURE).build()
        );
        Flux<SensorData> mockHumidityFlux = Flux.just(
                SensorData.builder().setSensorId("hum1").setValue(60.0).setType(SensorType.HUMIDITY).build()
        );

        when(temperatureListener.listen()).thenReturn(mockTemperatureFlux);
        when(humidityListener.listen()).thenReturn(mockHumidityFlux);

        when(udpListenerFactory.create(3344, SensorType.TEMPERATURE)).thenReturn(temperatureListener);
        when(udpListenerFactory.create(3355, SensorType.HUMIDITY)).thenReturn(humidityListener);

        warehouseService.startConsumingSensorData();

        verify(kafkaPublisher, times(2)).publish(any(SensorData.class));
    }
}