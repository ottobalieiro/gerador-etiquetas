package com.rastreio.geradoretiquetas.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class LojaIntegradaClient {

    @Value("${lojaintegrada.api.chave}")
    private String chaveApi;
    @Value("${lojaintegrada.api.aplicacao}")
    private String chaveAplicacao;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String authHeader = String.format("chave_api %s aplicacao %s", chaveApi, chaveAplicacao);
        headers.set("Authorization  ", authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public ResponseEntity<String> buscarPedidosPagos() {
        String url = "https://api.awsli.com.br/v1/pedido/search/?situacao_id=4";

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    public ResponseEntity<String> buscarDetalhesPedido(int numero){
        String url = String.format("https://api.awsli.com.br/v1/pedido/%d", numero);

        HttpEntity<Void> entity = new HttpEntity<>(createHeaders());

        return restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );
    }
}
