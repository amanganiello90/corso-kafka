package com.example.developer.pharmacy.service;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;

import com.example.developer.pharmacy.config.KafkaPharmAlertProducer;
import com.example.developer.pharmacy.service.dto.PharmacyAlertDTO;
import com.example.developer.pharmacy.service.dto.PharmacyDTO;

/**
 * Service Implementation for managing kafka producer message to alert microservice.
 */
@Service
@Transactional
public class KafkaPharmAlertProducerService {

	private final MessageChannel output;
    private final Logger log = LoggerFactory.getLogger(KafkaPharmAlertProducerService.class);


    public KafkaPharmAlertProducerService(@Qualifier(KafkaPharmAlertProducer.CHANNELNAME) MessageChannel output) {
        this.output=output;
    }

    /**
     * Send a message to store alert entity status
     *
     * @param store the entity to send message.
     */
    public void pharmacyAlertStatus(PharmacyDTO pharmacyDTO) {
        
    	PharmacyAlertDTO pharmacyAlertDTO = new PharmacyAlertDTO(pharmacyDTO);
        log.debug("Build message pharmacyAlertDTO of previous saved store entity : {}", pharmacyAlertDTO);
        Map<String, Object> map = new HashMap<>();
        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
        MessageHeaders headers = new MessageHeaders(map);
        output.send(new GenericMessage<>(pharmacyAlertDTO, headers));
        
    }

  
}
