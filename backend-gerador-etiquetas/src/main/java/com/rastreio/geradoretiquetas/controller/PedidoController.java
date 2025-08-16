package com.rastreio.geradoretiquetas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rastreio.geradoretiquetas.client.CorreiosClient;
import com.rastreio.geradoretiquetas.client.LojaIntegradaClient;
import com.rastreio.geradoretiquetas.dto.PedidoDTO;
import com.rastreio.geradoretiquetas.service.PedidoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
public class PedidoController {

    private final LojaIntegradaClient lojaIntegradaClient;
    private final CorreiosClient correiosClient;
    private final PedidoService pedidoService;

    public PedidoController(LojaIntegradaClient lojaIntegradaClient, CorreiosClient correiosClient, PedidoService pedidoService) {
        this.lojaIntegradaClient = lojaIntegradaClient;
        this.correiosClient = correiosClient;
        this.pedidoService = pedidoService;
    }

    @GetMapping("/pedidos")
    public ResponseEntity<String> getPedidos() {
        return lojaIntegradaClient.buscarPedidosPagos();
    }

    @GetMapping("/{numero}")
    public ResponseEntity<PedidoDTO> buscarPorNumero(@PathVariable int numero) {
        PedidoDTO pedido = pedidoService.buscarPedidoPorNumero(numero);
        return ResponseEntity.ok(pedido);
    }

    @GetMapping("/{numero}/declaracao/url")
    public ResponseEntity<String> gerarDeclaracaoUrl(@PathVariable int numero) {
        PedidoDTO pedido = pedidoService.buscarPedidoPorNumero(numero);

        String token = correiosClient.gerarToken();
        String prePostagemId = correiosClient.obterOuCriarPrePostagem(pedido, token);

        String url = correiosClient.getUrlDeclaracaoConteudo(prePostagemId);
        return ResponseEntity.ok(url);
    }

    @GetMapping("/{numero}/download")
    public ResponseEntity<?> gerarDownload(
            @PathVariable int numero,
            @RequestParam String tipo) throws JsonProcessingException {

        if (tipo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tipo inválido. Use: etiqueta, declaracao ou ambos.");
        }

        String tipoLower = tipo.toLowerCase();
        if (!tipoLower.equals("etiqueta") && !tipoLower.equals("declaracao") && !tipoLower.equals("ambos")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Tipo inválido. Use: etiqueta, declaracao ou ambos.");
        }

        PedidoDTO pedido = pedidoService.buscarPedidoPorNumero(numero);
        String token = correiosClient.gerarToken();
        String prePostagemId = correiosClient.obterOuCriarPrePostagem(pedido, token);

        switch (tipoLower) {
            case "etiqueta": {
                String idRecibo = correiosClient.solicitarRotulo(prePostagemId, token);
                byte[] pdfEtiqueta = correiosClient.baixarRotuloPdfBytes(token, idRecibo);

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=etiqueta_" + numero + ".pdf")
                        .body(pdfEtiqueta);
            }
            case "declaracao": {
                String htmlDeclaracao = correiosClient.baixarDeclaracaoConteudoHtml(token, prePostagemId);

                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(htmlDeclaracao);
            }
            case "ambos": {
                String idRecibo = correiosClient.solicitarRotulo(prePostagemId, token);
                byte[] pdfEtiqueta = correiosClient.baixarRotuloPdfBytes(token, idRecibo);
                String htmlDeclaracao = correiosClient.baixarDeclaracaoConteudoHtml(token, prePostagemId);

                String base64Etiqueta = Base64.getEncoder().encodeToString(pdfEtiqueta);
                String base64Declaracao = Base64.getEncoder().encodeToString(htmlDeclaracao.getBytes(StandardCharsets.UTF_8));

                String html = """
            <html>
            <head>
                <script>
                    var pdfEtiqueta = 'data:application/pdf;base64,%s';
                    var w1 = window.open();
                    w1.document.write('<iframe src="' + pdfEtiqueta + '" style="width:100%%;height:100%%;"></iframe>');

                    var htmlDeclaracao = decodeURIComponent(escape(window.atob('%s')));
                    var w2 = window.open();
                    w2.document.write(htmlDeclaracao);

                    window.close();
                </script>
            </head>
            <body></body>
            </html>
            """.formatted(base64Etiqueta, base64Declaracao);

                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(html);
            }
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Tipo inválido");
        }
    }
}