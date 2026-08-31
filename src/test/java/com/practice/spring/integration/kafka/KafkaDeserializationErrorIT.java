package com.practice.spring.integration.kafka;

import com.practice.spring.support.config.AbstractSpringBootIT;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.kafka.KafkaContainer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

public class KafkaDeserializationErrorIT extends AbstractSpringBootIT {

    private KafkaProducer<String, String> producer;

    private KafkaConsumer<String, String> consumer;

    @Autowired
    private KafkaContainer kafkaContainer;

    @BeforeEach
    void setUp(){
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "dlt-test-" + UUID.randomUUID());
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        producer = new KafkaProducer<>(producerProperties);
        consumer = new KafkaConsumer<>(consumerProperties);
        consumer.subscribe(List.of("note-events.DLT"));
        consumer.poll(Duration.ofSeconds(1));
    }

    @AfterEach
    void tearDownKafka(){
        if(producer!=null){
            producer.close();
        }
        if (consumer!=null){
            consumer.close();
        }
    }

    @Test
    void shouldMoveDeserializationFailedMessageToDlt(){
        ProducerRecord<String, String> record = new ProducerRecord<>("note-events", "test-key", "{incorrect_value");
        producer.send(record);

        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
            assertThat(records.count()).isEqualTo(1);
        });
    }
}
