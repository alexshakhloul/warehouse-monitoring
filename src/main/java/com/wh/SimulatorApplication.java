package com.wh;

import java.math.RoundingMode;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SimulatorApplication {
    private static final Random random = new Random();
    private static final int TEMPERATURE_PORT = 3344;
    private static final int HUMIDITY_PORT = 3355;
    private static double humidity = 40.0;

    public static void simulateData() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                DatagramSocket temperatureSocket = new DatagramSocket();
                DatagramSocket humiditySocket = new DatagramSocket();
                InetAddress address = InetAddress.getByName("localhost");
                while (true) {
                    double temperature = fluctuateTemperature();
                    sendSensorData(temperatureSocket, TEMPERATURE_PORT, address, "t" + random.nextInt(), temperature);

                    humidity = fluctuateHumidity();
                    sendSensorData(humiditySocket, HUMIDITY_PORT, address, "h" + random.nextInt(), humidity);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void sendSensorData(DatagramSocket socket, int port, InetAddress address, String sensorId, double value) throws Exception {
        String message = "sensor_id=" + sensorId + ";value=" + value;
        byte[] buffer = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
        System.out.println("Sent to port " + port + ": " + message);
    }

    private static double fluctuateTemperature() {
        double sineWave = 5 * Math.sin(System.currentTimeMillis() / 10000.0);
        double spike = random.nextDouble() > 0.9 ? random.nextDouble() * 10 : 0;
        double v = 30.0 + sineWave + spike;
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.UP);
        String truncated = df.format(v);
        return Double.parseDouble(truncated.replace(",", "."));
    }

    private static double fluctuateHumidity() {
        double drift = random.nextDouble() * 2 - 1;
        double spike = random.nextDouble() > 0.95 ? random.nextDouble() * 10 : 0;
        humidity += drift + spike;
        humidity = Math.max(20.0, Math.min(80.0, humidity));
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.UP);
        String truncated = df.format(humidity);
        return Double.parseDouble(truncated.replace(",", "."));
    }
}