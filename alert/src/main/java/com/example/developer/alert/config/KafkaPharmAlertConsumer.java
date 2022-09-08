package com.example.developer.alert.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface KafkaPharmAlertConsumer {
    String CHANNELNAME = "binding-in-pharm-alert";

    @Input(CHANNELNAME)
    MessageChannel input();
}
