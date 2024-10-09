package com.wh.central.service;

import com.wh.model.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CentralMonitoringService {
    private static final double TEMPERATURE_THRESHOLD = 35.0;
    private static final double HUMIDITY_THRESHOLD = 50.0;

    public void handleSensorData(SensorData sensorData) {
        checkConstraintAndRaiseAlarmIfNeeded(sensorData);
    }

    private void checkConstraintAndRaiseAlarmIfNeeded(SensorData sensorData) {
        switch (sensorData.getType()) {
            case TEMPERATURE: {
                if (sensorData.getValue() > TEMPERATURE_THRESHOLD) {
                    raiseAlarm(sensorData);
                }
                break;
            }
            case HUMIDITY:
                if (sensorData.getValue() > HUMIDITY_THRESHOLD) {
                    raiseAlarm(sensorData);
                }
                break;
        }
    }

    void raiseAlarm(SensorData sensorData) {
        log.error("Alarm raised for {}", sensorData);
    }
}
