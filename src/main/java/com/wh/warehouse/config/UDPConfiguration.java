package com.wh.warehouse.config;


import com.wh.model.SensorType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "service")
public class UDPConfiguration {
    private Map<SensorType, Integer> udpPortMap;

    public Map<SensorType, Integer> getUdpPortMap() {
        return udpPortMap;
    }

    public void setUdpPortMap(Map<SensorType, Integer> udpPortMap) {
        this.udpPortMap = udpPortMap;
    }
}
