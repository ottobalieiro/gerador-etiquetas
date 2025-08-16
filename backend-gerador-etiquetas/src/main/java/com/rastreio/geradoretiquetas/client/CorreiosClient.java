package com.rastreio.geradoretiquetas.client;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.rastreio.geradoretiquetas.dto.EnderecoDTO;
import com.rastreio.geradoretiquetas.dto.PedidoDTO;
import com.rastreio.geradoretiquetas.store.PrePostagemStore;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
public class CorreiosClient {

    @Value("${correios.api.codigo.acesso}")
    private String codigoAcesso;

    @Value("${correios.api.cartao}")
    private String cartaoPostagem;

    @Value("${meu.correios.username}")
    private String usernameMC;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CorreiosClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String gerarToken() {
        String url = "https://api.correios.com.br/token/v1/autentica/cartaopostagem";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(usernameMC, codigoAcesso);

        String body = String.format("{\"numero\": \"%s\"}", cartaoPostagem);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, JsonNode.class
            );

            return response.getBody().path("token").asText();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Erro ao gerar token dos Correios: " + e.getMessage(), e);
        }
    }

    public String criarPrePostagem(PedidoDTO pedido, String token) {
        String url = "https://api.correios.com.br/prepostagem/v1/prepostagens";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> body = montarBodyPrePostagem(pedido);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ObjectMapper mapperr = new ObjectMapper();
            String jsonFormatado = mapperr.writerWithDefaultPrettyPrinter().writeValueAsString(body);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            log.info("Erro ao converter o body para JSON: " + e.getMessage());
        }

        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, JsonNode.class
            );

            return response.getBody().path("id").asText();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar pré-postagem: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> montarBodyPrePostagem(PedidoDTO pedido) {

        List<Map<String, Object>> itensDeclaracao = pedido.getItens().stream()
                .map(item -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("conteudo", Optional.ofNullable(item.getNomeProduto()).orElse("Produto"));
                    map.put("quantidade", String.valueOf(item.getQuantidade()));
                    map.put("valor", String.format(Locale.US, "%.2f", item.getQuantidade() * item.getPrecoUnitario()));
                    return map;
                })
                .toList();

        double valorTotal = pedido.getItens().stream()
                .mapToDouble(item -> item.getQuantidade() * item.getPrecoUnitario())
                .sum();
        double valorDeclaradoDouble = Math.max(valorTotal, 25.50);
        String valorDeclaradoStr = String.format(Locale.forLanguageTag("pt-BR"), "%.2f", valorDeclaradoDouble)
                .replace(".", ",");

        double pesoKg = pedido.getItens().stream()
                .mapToDouble(item -> item.getQuantidade() * item.getPesoUnitario())
                .sum();
        int pesoEmGramas = (int) Math.ceil(pesoKg * 1000);

        Map<String, Object> remetenteEndereco = new HashMap<>();
        remetenteEndereco.put("cep", "04045002");
        remetenteEndereco.put("logradouro", "Avenida Jabaquara");
        remetenteEndereco.put("numero", "1469");
        remetenteEndereco.put("bairro", "Mirandópolis");
        remetenteEndereco.put("cidade", "São Paulo");
        remetenteEndereco.put("uf", "SP");

        Map<String, Object> remetente = new HashMap<>();
        remetente.put("nome", "Coroa Propolis");
        remetente.put("cpfCnpj", "31892164000102");
        remetente.put("dddCelular", "11");
        remetente.put("celular", "911862828");
        remetente.put("endereco", remetenteEndereco);

        EnderecoDTO e = pedido.getCliente().getEndereco();
        Map<String, Object> destinatarioEndereco = new HashMap<>();
        destinatarioEndereco.put("cep", Optional.ofNullable(e.getCep()).orElse("00000000"));
        destinatarioEndereco.put("logradouro", Optional.ofNullable(e.getLogradouro()).orElse(""));
        destinatarioEndereco.put("numero", Optional.ofNullable(e.getNumero()).orElse(""));
        destinatarioEndereco.put("bairro", Optional.ofNullable(e.getBairro()).orElse(""));
        destinatarioEndereco.put("cidade", Optional.ofNullable(e.getCidade()).orElse(""));
        destinatarioEndereco.put("uf", Optional.ofNullable(e.getEstado()).orElse("SP"));
        destinatarioEndereco.put("complemento", Optional.ofNullable(e.getComplemento()).orElse(""));

        String telefoneCliente = pedido.getCliente().getTelefone() != null
                ? pedido.getCliente().getTelefone().replaceAll("\\D", "")
                : "11999999999";
        String dddCliente = telefoneCliente.length() >= 10 ? telefoneCliente.substring(0, 2) : "11";
        String numeroCliente = telefoneCliente.length() > 2 ? telefoneCliente.substring(2) : "999999999";

        Map<String, Object> destinatario = new HashMap<>();
        destinatario.put("nome", pedido.getCliente().getNome());
        destinatario.put("dddCelular", dddCliente);
        destinatario.put("celular", numeroCliente);
        destinatario.put("endereco", destinatarioEndereco);
        destinatario.put("cpfCnpj", pedido.getCliente().getCpf());

        Map<String, Object> body = new HashMap<>();
        body.put("numeroCartaoPostagem", "0079307663");
        body.put("codigoServico", pedido.getFormaEnvio().equalsIgnoreCase("sedex") ? "03220" : "03298");
        body.put("pesoInformado", pesoEmGramas);
        body.put("codigoFormatoObjetoInformado", 2);
        body.put("comprimentoInformado", 20);
        body.put("alturaInformada", 10);
        body.put("larguraInformada", 15);
        body.put("diametroInformado", 0);
        body.put("valorDeclarado", valorDeclaradoStr);
        body.put("cienteObjetoNaoProibido", 1);
        body.put("itensDeclaracaoConteudo", itensDeclaracao);
        body.put("remetente", remetente);
        body.put("destinatario", destinatario);

        return body;
    }

    public String solicitarRotulo(String prePostagemId, String token) {
        String url = "https://api.correios.com.br/prepostagem/v1/prepostagens/rotulo/assincrono/pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> body = Map.of(
                "idsPrePostagem", List.of(prePostagemId),
                "tipoRotulo", "P",
                "formatoRotulo", "A4"
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, JsonNode.class
        );

        return response.getBody().path("idRecibo").asText();
    }

    public byte[] baixarRotuloPdfBytes(String token, String idRecibo) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://api.correios.com.br/prepostagem/v1/prepostagens/rotulo/download/assincrono/" + idRecibo;

        int tentativas = 0;
        while (tentativas < 20) {
            try {
                ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
                    JsonNode dadosNode = jsonNode.get("dados");

                    if (dadosNode != null && !dadosNode.isNull()) {
                        return Base64.getDecoder().decode(dadosNode.asText());
                    }
                }
            } catch (HttpClientErrorException e) {
                String body = e.getResponseBodyAsString();

                if (body.contains("PPN-295") || body.contains("PPN-291")) {
                    log.info("⏳ Rótulo ainda não pronto. Tentando novamente...");
                } else {
                    throw new RuntimeException("Erro ao baixar rótulo: " + body, e);
                }
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            tentativas++;
        }

        throw new RuntimeException("Não foi possível baixar o rótulo após várias tentativas.");
    }

    public String baixarDeclaracaoConteudoHtml(String token, String prePostagemId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.TEXT_HTML));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.correios.com.br/prepostagem/v1/prepostagens/declaracaoconteudo/" + prePostagemId,
                HttpMethod.GET,
                entity,
                String.class
        );

        return response.getBody();
    }

    private final PrePostagemStore store = new PrePostagemStore(objectMapper);

    public String obterOuCriarPrePostagem(PedidoDTO pedido, String token) {
        String existente = store.getId(pedido.getNumero());
        if (existente != null) {
            return existente;
        }
        String novoId = criarPrePostagem(pedido, token);
        store.putId(pedido.getNumero(), novoId);
        return novoId;
    }

    private void salvarPdfLocal(byte[] pdfBytes, String idRecibo) {
        try (FileOutputStream fos = new FileOutputStream("etiqueta_" + idRecibo + ".pdf")) {
            fos.write(pdfBytes);
            log.info("✅ PDF salvo em: etiqueta_" + idRecibo + ".pdf");
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar PDF localmente", e);
        }
    }

    public byte[] baixarDeclaracaoConteudoPdf(String token, String prePostagemId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.TEXT_HTML));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "https://api.correios.com.br/prepostagem/v1/prepostagens/declaracaoconteudo/" + prePostagemId;

        ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Erro ao baixar declaração de conteúdo: " + response.getStatusCode());
        }

        String html = response.getBody()
                .replace("&atilde;", "ã")
                .replace("&Atilde;", "Ã")
                .replace("&ccedil;", "ç")
                .replace("&Ccedil;", "Ç")
                .replace("&aacute;", "á")
                .replace("&Aacute;", "Á")
                .replace("&eacute;", "é")
                .replace("&Eacute;", "É")
                .replace("&iacute;", "í")
                .replace("&Iacute;", "Í")
                .replace("&oacute;", "ó")
                .replace("&Oacute;", "Ó")
                .replace("&uacute;", "ú")
                .replace("&Uacute;", "Ú")
                .replace("&nbsp;", " ");

        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter declaração de conteúdo para PDF: " + e.getMessage(), e);
        }
    }

    public byte[] gerarPdfComEtiquetaEDeclaracao(String token, String prePostagemId) {
        try {
            String idRecibo = solicitarRotulo(prePostagemId, token);
            byte[] etiquetaPdf = baixarRotuloPdfBytes(token, idRecibo);

            byte[] declaracaoPdf = baixarDeclaracaoConteudoPdf(token, prePostagemId);

            PDFMergerUtility merger = new PDFMergerUtility();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            merger.addSource(new ByteArrayInputStream(etiquetaPdf));
            merger.addSource(new ByteArrayInputStream(declaracaoPdf));
            merger.setDestinationStream(outputStream);
            merger.mergeDocuments(null);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF combinado: " + e.getMessage(), e);
        }
    }

    public String getUrlDeclaracaoConteudo(String prePostagemId) {
        return "https://api.correios.com.br/prepostagem/v1/prepostagens/declaracaoconteudo/" + prePostagemId;
    }
}
