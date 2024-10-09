package com.wh.warehouse.service.kafka;

public interface Publisher<T> {
    void publish(T data);
}
