package com.rastreio.geradoretiquetas.dto;

import java.util.List;

public class PedidoDTO {
    private int numero;
    private ClienteDTO cliente;
    private List<ItemPedidoDTO> itens;
    private String formaEnvio;

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public List<ItemPedidoDTO> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoDTO> itens) {
        this.itens = itens;
    }

    public String getFormaEnvio() {
        return formaEnvio;
    }

    public void setFormaEnvio(String formaEnvio) {
        this.formaEnvio = formaEnvio;
    }
}


