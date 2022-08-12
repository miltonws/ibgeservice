package com.com.mws.kafkaline.ibgeservice.gateway.resource;


import com.com.mws.kafkaline.ibgeservice.gateway.json.CidadeList;
import com.com.mws.kafkaline.ibgeservice.gateway.json.EstadoList;
import com.com.mws.kafkaline.ibgeservice.service.estado.ConsultarCidadeService;
import com.com.mws.kafkaline.ibgeservice.service.estado.ConsultarEstadoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/estados")
public class EstadoResource {

    @Autowired
    private ConsultarEstadoService consultarEstadoService;

    @Autowired
    private ConsultarCidadeService consultarCidadeService;

    @GetMapping("/")
    public EstadoList consultarEstados() throws ExecutionException, JsonProcessingException, InterruptedException {
        return consultarEstadoService.execute();
    }

    @GetMapping("/{uf}/cidades")
    public CidadeList consultarEstados(@PathVariable("uf") String uf) throws ExecutionException, JsonProcessingException, InterruptedException {
        return consultarCidadeService.execute(uf);
    }
}
