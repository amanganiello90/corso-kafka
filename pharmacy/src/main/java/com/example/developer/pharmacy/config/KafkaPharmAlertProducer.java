package com.example.developer.pharmacy.config;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KafkaPharmAlertProducer {
    String CHANNELNAME = "binding-out-pharm-alert";

    @Output(CHANNELNAME)
    MessageChannel output();
}
