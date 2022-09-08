package com.example.developer.alert.service;


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

import com.example.developer.alert.config.KafkaPharmAlertConsumer;
import com.example.developer.alert.service.dto.AlertMessDTO;
import com.example.developer.alert.web.rest.errors.BadRequestAlertException;


/**
 * Service Implementation for managing kafka consumer message from pharmacy microservice.
 */
@Service
@Transactional
public class KafkaPharmAlertConsumerService {

	private static final String ENTITY_NAME = "alertAlertMess";
	private final AlertMessService alertMessService;
    private final Logger log = LoggerFactory.getLogger(KafkaPharmAlertConsumerService.class);


    public KafkaPharmAlertConsumerService(AlertMessService alertMessService) {
        this.alertMessService= alertMessService;
    }

    /**
     * consume an alert store message to save related entity 
     *
     * @param store the entity to send message.
     */
    @StreamListener(value = KafkaPharmAlertConsumer.CHANNELNAME, copyHeaders = "false")
    public void consumeStoreAlertSave(Message<AlertMessDTO> message) {
    	AlertMessDTO alertMessDTO = message.getPayload();
        log.debug("consuming message AlertMessDTO to persist entity : {}", alertMessDTO);
        if (alertMessDTO.getId() != null) {
            throw new BadRequestAlertException("A new storeAlert message cannot already have an ID", ENTITY_NAME, "idexists");
        }
        alertMessDTO.setTimestamp(Instant.now());
        AlertMessDTO result = alertMessService.save(alertMessDTO);
        log.debug("stored {} entity : {}", ENTITY_NAME, result);

        
    }

  
}
