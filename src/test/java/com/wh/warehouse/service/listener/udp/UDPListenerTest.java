package com.wh.warehouse.service.listener.udp;

import com.wh.model.SensorData;
import com.wh.model.SensorType;
import com.wh.warehouse.service.listener.udp.UDPListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UDPListenerTest {
    private final int port = 3344;
    private final SensorType type = SensorType.TEMPERATURE;
    @InjectMocks
    private UDPListener udpListener = new UDPListener(port, type);
    @Mock
    private DatagramSocket datagramSocket;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        udpListener = spy(udpListener);
    }

    @Test
    public void testListenEmitsSensorData() throws Exception {
        String simulatedData = "sensorId=1;value=23.5";
        byte[] dataBytes = simulatedData.getBytes(StandardCharsets.UTF_8);

        doAnswer(invocation -> {
            DatagramPacket receivedPacket = invocation.getArgument(0);
            System.arraycopy(dataBytes, 0, receivedPacket.getData(), 0, dataBytes.length);
            receivedPacket.setLength(dataBytes.length);
            return null;
        }).when(datagramSocket).receive(any(DatagramPacket.class));

        doReturn(datagramSocket).when(udpListener).createSocket();

        Flux<SensorData> sensorDataFlux = udpListener.listen();

        StepVerifier.create(sensorDataFlux)
                .expectNextMatches(sensorData ->
                        sensorData.getSensorId().equals("1") && sensorData.getValue() == 23.5)
                .thenCancel()
                .verify();
    }

    @Test
    public void testListenHandlesMalformedData() throws Exception {
        String malformedData = "sensorId=;value=";
        byte[] dataBytes = malformedData.getBytes(StandardCharsets.UTF_8);

        doAnswer(invocation -> {
            DatagramPacket receivedPacket = invocation.getArgument(0);
            System.arraycopy(dataBytes, 0, receivedPacket.getData(), 0, dataBytes.length);
            receivedPacket.setLength(dataBytes.length);
            return null;
        }).doAnswer(invocation -> {
            throw new IOException("End of test");
        }).when(datagramSocket).receive(any(DatagramPacket.class));

        doReturn(datagramSocket).when(udpListener).createSocket();

        Flux<SensorData> sensorDataFlux = udpListener.listen();

        StepVerifier.create(sensorDataFlux)
                .expectErrorMatches(throwable -> throwable instanceof IOException)
                .verify();
    }

    @Test
    public void testListenHandlesNonNumericValue() throws Exception {
        String malformedData = "sensorId=1;value=abc";
        byte[] dataBytes = malformedData.getBytes(StandardCharsets.UTF_8);

        doAnswer(invocation -> {
            DatagramPacket receivedPacket = invocation.getArgument(0);
            System.arraycopy(dataBytes, 0, receivedPacket.getData(), 0, dataBytes.length);
            receivedPacket.setLength(dataBytes.length);
            return null;
        }).doAnswer(invocation -> {
            throw new IOException("End of test");
        }).when(datagramSocket).receive(any(DatagramPacket.class));

        doReturn(datagramSocket).when(udpListener).createSocket();

        Flux<SensorData> sensorDataFlux = udpListener.listen();

        StepVerifier.create(sensorDataFlux)
                .expectErrorMatches(throwable -> throwable instanceof IOException)
                .verify();
    }

    @Test
    public void testListenHandlesIOException() throws Exception {
        doThrow(new IOException("Test Exception")).when(datagramSocket).receive(any(DatagramPacket.class));

        doReturn(datagramSocket).when(udpListener).createSocket();

        Flux<SensorData> sensorDataFlux = udpListener.listen();

        StepVerifier.create(sensorDataFlux)
                .expectErrorMatches(throwable -> throwable instanceof IOException && throwable.getMessage().equals("Test Exception"))
                .verify();
    }
}
