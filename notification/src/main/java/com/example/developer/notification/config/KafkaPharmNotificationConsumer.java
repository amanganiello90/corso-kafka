package com.example.developer.notification.config;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

public interface KafkaPharmNotificationConsumer {
    String CHANNELNAME = "binding-in-pharm-alert";

    @Input(CHANNELNAME)
    MessageChannel input();
}
