package com.wh.central.service;

import com.wh.model.SensorData;
import com.wh.model.SensorType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class CentralMonitoringServiceTest {
    private CentralMonitoringService centralMonitoringService;

    @BeforeEach
    public void setUp() {
        centralMonitoringService = Mockito.spy(new CentralMonitoringService());
    }

    @Test
    public void testHandleSensorData_TemperatureAboveThreshold() {
        SensorData sensorData = SensorData.builder()
                .setSensorId("temp1")
                .setValue(36.0)
                .setType(SensorType.TEMPERATURE)
                .build();

        centralMonitoringService.handleSensorData(sensorData);
        verify(centralMonitoringService, times(1)).raiseAlarm(sensorData);
    }

    @Test
    public void testHandleSensorData_TemperatureBelowThreshold() {
        SensorData sensorData = SensorData.builder()
                .setSensorId("temp2")
                .setValue(30.0)
                .setType(SensorType.TEMPERATURE)
                .build();

        centralMonitoringService.handleSensorData(sensorData);
        verify(centralMonitoringService, never()).raiseAlarm(sensorData);
    }

    @Test
    public void testHandleSensorData_HumidityAboveThreshold() {
        SensorData sensorData = SensorData.builder()
                .setSensorId("hum1")
                .setValue(55.0)
                .setType(SensorType.HUMIDITY)
                .build();

        centralMonitoringService.handleSensorData(sensorData);
        verify(centralMonitoringService, times(1)).raiseAlarm(sensorData);
    }

    @Test
    public void testHandleSensorData_HumidityBelowThreshold() {
        SensorData sensorData = SensorData.builder()
                .setSensorId("hum2")
                .setValue(45.0)
                .setType(SensorType.HUMIDITY)
                .build();

        centralMonitoringService.handleSensorData(sensorData);
        verify(centralMonitoringService, never()).raiseAlarm(sensorData);
    }
}
