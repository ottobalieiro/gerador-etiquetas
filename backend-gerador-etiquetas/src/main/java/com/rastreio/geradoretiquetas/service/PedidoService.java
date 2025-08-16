package com.rastreio.geradoretiquetas.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rastreio.geradoretiquetas.client.LojaIntegradaClient;
import com.rastreio.geradoretiquetas.dto.ClienteDTO;
import com.rastreio.geradoretiquetas.dto.EnderecoDTO;
import com.rastreio.geradoretiquetas.dto.ItemPedidoDTO;
import com.rastreio.geradoretiquetas.dto.PedidoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final LojaIntegradaClient lojaIntegradaClient;
    private final ObjectMapper objectMapper;

    public PedidoService(LojaIntegradaClient lojaIntegradaClient, ObjectMapper objectMapper) {
        this.lojaIntegradaClient = lojaIntegradaClient;
        this.objectMapper = objectMapper;
    }

    public PedidoDTO buscarPedidoPorNumero(int numero) {
        try {
            ResponseEntity<String> response = lojaIntegradaClient.buscarDetalhesPedido(numero);
            JsonNode detalheNode = objectMapper.readTree(response.getBody());

            return montarPedidoDTO(detalheNode, numero);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao buscar pedido número " + numero, e);
        }
    }

    private PedidoDTO montarPedidoDTO(JsonNode detalheNode, int numeroPedido) {

        PedidoDTO pedido = new PedidoDTO();
        pedido.setNumero(numeroPedido);

        ClienteDTO cliente = new ClienteDTO();
        JsonNode clienteNode = detalheNode.path("cliente");
        cliente.setCpf(clienteNode.path("cpf").asText());
        cliente.setNome(clienteNode.path("nome").asText());
        cliente.setTelefone(clienteNode.path("telefone_celular").asText());

        EnderecoDTO endereco = new EnderecoDTO();
        JsonNode enderecoNode = detalheNode.path("endereco_entrega");
        endereco.setBairro(enderecoNode.path("bairro").asText());
        endereco.setNumero(enderecoNode.path("numero").asText());
        endereco.setCep(enderecoNode.path("cep").asText());
        endereco.setCidade(enderecoNode.path("cidade").asText());
        endereco.setLogradouro(enderecoNode.path("endereco").asText());
        endereco.setEstado(enderecoNode.path("estado").asText());
        endereco.setComplemento(enderecoNode.path("complemento").asText());

        cliente.setEndereco(endereco);
        pedido.setCliente(cliente);

        pedido.setFormaEnvio(detalheNode.path("envios").get(0).path("forma_envio").path("nome").asText());

        List<ItemPedidoDTO> itens = new ArrayList<>();
        JsonNode itensNode = detalheNode.path("itens");
        for (JsonNode itemNode : itensNode) {
            ItemPedidoDTO item = new ItemPedidoDTO();
            item.setNomeProduto(itemNode.path("nome").asText());
            item.setQuantidade(itemNode.path("quantidade").asInt());
            item.setPrecoUnitario(itemNode.path("preco_venda").asDouble());
            item.setPreco(itemNode.path("preco_subtotal").asDouble());
            item.setPesoUnitario(itemNode.path("peso").asDouble());

            itens.add(item);
        }
        pedido.setItens(itens);

        return pedido;
    }
}
