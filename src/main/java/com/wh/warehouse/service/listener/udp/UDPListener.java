package com.wh.warehouse.service.listener.udp;

import com.wh.model.SensorData;
import com.wh.model.SensorType;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

@Slf4j
public class UDPListener {
    private final int port;
    private final SensorType type;

    public UDPListener(int port, SensorType type) {
        this.port = port;
        this.type = type;
    }

    public Flux<SensorData> listen() {
        return Flux.create(this::setupListener)
                .subscribeOn(Schedulers.fromExecutor(Executors.newSingleThreadExecutor()));
    }

    private void setupListener(FluxSink<SensorData> sink) {
        try (DatagramSocket socket = createSocket()) {
            log.info("Started UDP listener to port: {}", port);
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                String data = null;
                try {
                    socket.receive(packet);
                    data = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                    String[] parts = data.split(";");
                    if (parts.length < 2) {
                        log.error("Malformed packet received: {}", data);
                        continue;
                    }
                    String sensorId = parts[0].split("=")[1];
                    double value = Double.parseDouble(parts[1].split("=")[1]);
                    SensorData sensorData = SensorData.builder()
                            .setSensorId(sensorId)
                            .setValue(value)
                            .setType(type)
                            .build();
                    sink.next(sensorData);
                } catch (IOException e) {
                    log.error("IOException occurred during packet reception", e);
                    sink.error(e);
                    break;
                } catch (Exception e) {
                    log.error("Error parsing packet: {}", data, e);
                }
            }
        } catch (IOException e) {
            log.error("Error while creating UDP listener", e);
        } finally {
            sink.complete();
        }
    }

    DatagramSocket createSocket() throws IOException {
        return new DatagramSocket(port);
    }
}
