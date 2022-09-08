package com.example.developer.pharmacy.web.rest;

import com.example.developer.pharmacy.repository.PharmacyRepository;
import com.example.developer.pharmacy.service.KafkaPharmAlertProducerService;
import com.example.developer.pharmacy.service.PharmacyService;
import com.example.developer.pharmacy.service.dto.PharmacyDTO;
import com.example.developer.pharmacy.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.example.developer.pharmacy.domain.Pharmacy}.
 */
@RestController
@RequestMapping("/api")
public class PharmacyResource {

    private final Logger log = LoggerFactory.getLogger(PharmacyResource.class);

    private static final String ENTITY_NAME = "pharmacyPharmacy";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PharmacyService pharmacyService;

    private final PharmacyRepository pharmacyRepository;
    
    private final KafkaPharmAlertProducerService kafkaPharmAlertProducerService;

    public PharmacyResource(PharmacyService pharmacyService, PharmacyRepository pharmacyRepository, KafkaPharmAlertProducerService kafkaPharmAlertProducerService) {
        this.pharmacyService = pharmacyService;
        this.pharmacyRepository = pharmacyRepository;
        this.kafkaPharmAlertProducerService = kafkaPharmAlertProducerService;
    }

    /**
     * {@code POST  /pharmacies} : Create a new pharmacy.
     *
     * @param pharmacyDTO the pharmacyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pharmacyDTO, or with status {@code 400 (Bad Request)} if the pharmacy has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pharmacies")
    public ResponseEntity<PharmacyDTO> createPharmacy(@Valid @RequestBody PharmacyDTO pharmacyDTO) throws URISyntaxException {
        log.debug("REST request to save Pharmacy : {}", pharmacyDTO);
        if (pharmacyDTO.getId() != null) {
            throw new BadRequestAlertException("A new pharmacy cannot already have an ID", ENTITY_NAME, "idexists");
        }
        PharmacyDTO result = pharmacyService.save(pharmacyDTO);
        
        // send message
        kafkaPharmAlertProducerService.pharmacyAlertStatus(pharmacyDTO);
        //
        return ResponseEntity
            .created(new URI("/api/pharmacies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /pharmacies/:id} : Updates an existing pharmacy.
     *
     * @param id the id of the pharmacyDTO to save.
     * @param pharmacyDTO the pharmacyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pharmacyDTO,
     * or with status {@code 400 (Bad Request)} if the pharmacyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pharmacyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pharmacies/{id}")
    public ResponseEntity<PharmacyDTO> updatePharmacy(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PharmacyDTO pharmacyDTO
    ) throws URISyntaxException {
        log.debug("REST request to update Pharmacy : {}, {}", id, pharmacyDTO);
        if (pharmacyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pharmacyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pharmacyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        PharmacyDTO result = pharmacyService.update(pharmacyDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pharmacyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /pharmacies/:id} : Partial updates given fields of an existing pharmacy, field will ignore if it is null
     *
     * @param id the id of the pharmacyDTO to save.
     * @param pharmacyDTO the pharmacyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pharmacyDTO,
     * or with status {@code 400 (Bad Request)} if the pharmacyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pharmacyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pharmacyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pharmacies/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PharmacyDTO> partialUpdatePharmacy(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PharmacyDTO pharmacyDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pharmacy partially : {}, {}", id, pharmacyDTO);
        if (pharmacyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pharmacyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pharmacyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PharmacyDTO> result = pharmacyService.partialUpdate(pharmacyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pharmacyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pharmacies} : get all the pharmacies.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pharmacies in body.
     */
    @GetMapping("/pharmacies")
    public List<PharmacyDTO> getAllPharmacies() {
        log.debug("REST request to get all Pharmacies");
        return pharmacyService.findAll();
    }

    /**
     * {@code GET  /pharmacies/:id} : get the "id" pharmacy.
     *
     * @param id the id of the pharmacyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pharmacyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pharmacies/{id}")
    public ResponseEntity<PharmacyDTO> getPharmacy(@PathVariable Long id) {
        log.debug("REST request to get Pharmacy : {}", id);
        Optional<PharmacyDTO> pharmacyDTO = pharmacyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pharmacyDTO);
    }

    /**
     * {@code DELETE  /pharmacies/:id} : delete the "id" pharmacy.
     *
     * @param id the id of the pharmacyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pharmacies/{id}")
    public ResponseEntity<Void> deletePharmacy(@PathVariable Long id) {
        log.debug("REST request to delete Pharmacy : {}", id);
        pharmacyService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
