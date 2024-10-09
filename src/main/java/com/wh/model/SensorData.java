package com.wh.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.StringJoiner;

@JsonDeserialize(builder = SensorData.SensorDataBuilder.class)
@Getter
public class SensorData {
    private SensorType type;
    private String sensorId;
    private double value;

    private SensorData(SensorType type, String sensorId, double value) {
        this.type = type;
        this.sensorId = sensorId;
        this.value = value;
    }

    public static SensorDataBuilder builder() {
        return new SensorDataBuilder();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SensorData.class.getSimpleName() + "[", "]")
                .add("type=" + type)
                .add("sensorId='" + sensorId + "'")
                .add("value=" + value)
                .toString();
    }

    @JsonPOJOBuilder(withPrefix = "set")
    public static class SensorDataBuilder {
        private SensorType type;
        private String sensorId;
        private double value;

        public SensorDataBuilder setType(SensorType type) {
            this.type = type;
            return this;
        }

        public SensorDataBuilder setSensorId(String sensorId) {
            this.sensorId = sensorId;
            return this;
        }

        public SensorDataBuilder setValue(double value) {
            this.value = value;
            return this;
        }

        public SensorData build() {
            return new SensorData(type, sensorId, value);
        }
    }
}
