package com.example.developer.notification.service;


import java.time.Instant;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.example.developer.notification.config.KafkaPharmNotificationConsumer;
import com.example.developer.notification.service.dto.NotificationDTO;
import com.example.developer.notification.web.rest.errors.BadRequestAlertException;


/**
 * Service Implementation for managing kafka consumer message from pharmacy microservice.
 */
@Service
@Transactional
public class KafkaPharmNotificationConsumerService {

	private static final String ENTITY_NAME = "notificationNotification";
	private final NotificationService notificationService;
    private final Logger log = LoggerFactory.getLogger(KafkaPharmNotificationConsumerService.class);


    public KafkaPharmNotificationConsumerService(NotificationService notificationService) {
        this.notificationService= notificationService;
    }

    /**
     * consume a pharmacy message to save related entity 
     *
     * @param notificationDTO from entity that sends message.
     */
    @StreamListener(value = KafkaPharmNotificationConsumer.CHANNELNAME, copyHeaders = "false")
    public void consumePharmacyAlertSave(Message<NotificationDTO> message) {
    	NotificationDTO notificationDTO = message.getPayload();
        log.debug("consuming message notificationDTO to persist entity : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new storeAlert message cannot already have an ID", ENTITY_NAME, "idexists");
        }
        notificationDTO.setNotificatedTimestamp(Instant.now());
        NotificationDTO result = notificationService.save(notificationDTO);
        log.debug("stored {} entity : {}", ENTITY_NAME, result);

        
    }

  
}
