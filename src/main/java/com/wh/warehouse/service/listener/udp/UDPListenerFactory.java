package com.wh.warehouse.service.listener.udp;

import com.wh.model.SensorType;
import org.springframework.stereotype.Component;

@Component
public class UDPListenerFactory {
    public UDPListener create(int port, SensorType type) {
        return new UDPListener(port, type);
    }
}
