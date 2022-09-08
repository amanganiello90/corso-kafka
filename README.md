# Corso kafka

> Tutorial di un caso d'uso di kafka in un architettura a microservizi utilizzando jhipster 7.8.1

Sostanzialmente viene simulato un passaggio di messaggi kafka tra 3 microservizi (**alert, notification e pharmacy**).

Alla creazione di un entità pharmacy (**mediante restController**) nel relativo microservizio, un producer genera un messaggio che verrà consumato dagli altri due microservizi in modo da storare su ciascun database le informazioni sul nome e la data di notifica del farmaco creato.

Quindi abbiamo:

- Microservizio **pharmacy producer**
- Microservizi **alert e notification consumer**

Inoltre trovi [kafka ppt](kafka-slides.pptx) che spiega alcuni concetti su kafka.

# Tecnologie

Per poter avviare il progetto, bisogna generare dal jdl di jhipster l'intera architettura.

I microservizi utilizzeranno **spring cloud stream kafka** (astrazione di alto livello del messaging) con relative configurazioni, e non i kafka client di basso livello.


# Struttura

Il tutorial è costituito da un jdl (**stack.jdl**), contenente **3 microservizi** (ciascuno con un entity) e un **gateway** (backend+dashboard Angular).

Nel dettaglio le customizzazioni sono:

- 3 cartelle per ogni microservizio, dove sono presenti i servizi producer e consumer di un caso di business
- un file dove ci sono [comandi cli](kafka-commands.md) per gestire i messaggi kafka, e cioè visualizzare topic e messaggi (produrli e consumarli)
- I microservizi comunicano con il topic **pharm-alert-topic** (creato automaticamente dai microservizi al loro avvio)
- I messaggi sono inviati tramite **JSON** (non avro, altrimenti servirebbe anche uno schema-registry)
- Viene utilizzato il serializzatore/deserializzatore di default di Jackson


## File

### microservizio pharmacy - producer

- [KafkaPharmAlertProducer.java](pharmacy/src/main/java/com/example/developer/pharmacy/config/KafkaPharmAlertProducer.java) per fare il binding del relativo canale
- Modifica della classe di config [AsyncConfiguration.java](pharmacy/src/main/java/com/example/developer/pharmacy/config/AsyncConfiguration.java) per importare ed usare la precedente classe del canale
- [PharmacyAlertDTO.java](pharmacy/src/main/java/com/example/developer/pharmacy/service/dto/PharmacyAlertDTO.java) per serializzare il messaggio JSON da inviare
- [KafkaPharmAlertProducerService.java](pharmacy/src/main/java/com/example/developer/pharmacy/service/KafkaPharmAlertProducerService.java) per registrare il producer asincrono del messaggio di pharmacy alla chiamata della **rest api di creazione pharmacy**
- Modifica della classe web controller [PharmacyResource.java](pharmacy/src/main/java/com/example/developer/pharmacy/web/rest/PharmacyResource.java) per chiamare il servizio producer kafka precedente nel metodo di creazione entity
- Configurazione nel [application.yml](pharmacy/src/main/resources/config/application.yml) delle property per kafka consumer:


```
 binding-out-pharm-alert:
          destination: pharm-alert-topic
          content-type: application/json
          group: pharmacy
```

### microservizio alert - consumer

- [KafkaPharmAlertConsumer.java](alert/src/main/java/com/example/developer/alert/config/KafkaPharmAlertConsumer.java) per fare il binding del relativo canale
- Modifica della classe di config [AsyncConfiguration.java](alert/src/main/java/com/example/developer/alert/config/AsyncConfiguration.java) per importare ed usare la precedente classe del canale
- [KafkaPharmAlertConsumerService.java](alert/src/main/java/com/example/developer/alert/service/KafkaPharmAlertConsumerService.java) per registrare il consumer asincrono del messaggio di pharmacy e storare le info
- Configurazione nel [application.yml](alert/src/main/resources/config/application.yml) delle property per kafka consumer:


```
 binding-in-pharm-alert:
          destination: pharm-alert-topic
          content-type: application/json
          group: alert
```

### microservizio notification - consumer

- [KafkaPharmNotificationConsumer.java](notification/src/main/java/com/example/developer/notification/config/KafkaPharmNotificationConsumer.java) per fare il binding del relativo canale
- Modifica della classe di config [AsyncConfiguration.java](notification/src/main/java/com/example/developer/notification/config/AsyncConfiguration.java) per importare ed usare la precedente classe del canale
- [KafkaPharmNotificationConsumerService.java](notification/src/main/java/com/example/developer/notification/service/KafkaPharmNotificationConsumerService.java) per registrare il consumer asincrono del messaggio di pharmacy e storare le info
- Configurazione nel [application.yml](notification/src/main/resources/config/application.yml) delle property per kafka consumer:


```
    binding-in-pharm-alert:
        destination: pharm-alert-topic
        content-type: application/json
        group: notification
```

C'è commentata una property per l'offset (auto-offset-reset  di default **latest**)


# Avvio

1. Esegui un run da questa folder tramite riga di comando:

```
jhipster import-jdl stack
```

**N. B.: Non sovrascrivere i file già presenti in questo progetto**

2. Lancia i servizi dockerizzati per keycloak, kafka e jhipster registry, da qualsiasi folder di microservizio generato:

```
docker-compose -f src/main/docker/keycloak.yml up -f
```

```
docker-compose -f src/main/docker/jhipster-registry.yml up -f
```

```
docker-compose -f src/main/docker/kafka.yml up -f
```

3. Esegui il run per ogni progetto, aprendo una differente shell dei comandi in ciascuna folder:

```
mvnw
```
**N. B.: Sui sistemi UNIX-like il comando è ./mvnw**

4. Il gateway si troverà deployato su **localhost:8080**

5. Dopo la login vai sull'entity pharmacy e creane una. cambia tab entity cliccando su notification e alert mess, noterai che una nuova entity con le stesse info dei campi è stata creata anche qui. Su ogni prompt dei comandi troverai anche delle loggate dei consumer e producer

6. In dev mode alcuni processi possono rimanere in sospeso. Nel file [comandi cli](kafka-commands.md) troverai utility per killare processi su determinate porte, oltre ai comandi per accedere alle info sui topic kafka e messaggi creati.