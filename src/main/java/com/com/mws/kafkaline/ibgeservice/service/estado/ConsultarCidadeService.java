package com.com.mws.kafkaline.ibgeservice.service.estado;

import com.com.mws.kafkaline.ibgeservice.gateway.json.CidadeList;
import com.com.mws.kafkaline.ibgeservice.gateway.json.EstadoList;
import com.com.mws.kafkaline.ibgeservice.gateway.json.EstadoRequestTopicJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class ConsultarCidadeService {

    @Autowired
    private ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate;

    //Fila que vai ser enviado para o servico ibgewrapper
    @Value("${kafka.topic.request-topic-cidade}")
    private String requestTopic;

    //Fila que vai receber a resposta
    @Value("${kafka.topic.requestreply-topic-cidade}")
    private String requestReplyTopic;

    public CidadeList execute(String uf) throws JsonProcessingException, ExecutionException, InterruptedException {

        //convertendo objeto para string
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = mapper.writeValueAsString(EstadoRequestTopicJson.builder().uf(uf).build());

        //montando o producer que vai ser enviado para o kafka
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(requestTopic, jsonString);

        //informa no header especifico do producer o topico de reply
        producerRecord.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));

        //enviado
        RequestReplyFuture<String, String, String> sendAndReceive = replyingKafkaTemplate.sendAndReceive(producerRecord);

        //recebendo retorno
        SendResult<String, String> sendResult = sendAndReceive.getSendFuture().get();
        sendResult.getProducerRecord()
                .headers().forEach(header -> System.out.printf(header.key() +" : " + header.value().toString()));

        ConsumerRecord<String, String> consumerRecord = sendAndReceive.get();

        CidadeList cidadeList = mapper.readValue(consumerRecord.value(), CidadeList.class);

        return cidadeList;
    }
}
