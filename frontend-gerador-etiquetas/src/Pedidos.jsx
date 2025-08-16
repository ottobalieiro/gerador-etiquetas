import React, { useEffect, useState } from "react";
import "./Pedidos.css";

export default function Pedidos() {
  const [pedidos, setPedidos] = useState([]);

  useEffect(() => {
    fetch("/api/pedidos")
      .then((res) => res.json())
      .then((data) => {
        const pedidosResumidos = data.objects || [];
        Promise.all(
          pedidosResumidos.map((p) =>
            fetch(`/api/${p.numero}`).then((res) => res.json())
          )
        ).then((pedidosDetalhados) => {
          console.log("✅ Pedidos detalhados prontos:", pedidosDetalhados);
          setPedidos(pedidosDetalhados);
        });
      })
      .catch((err) => console.error("Erro ao carregar pedidos:", err));
  }, []);

  const abrirPdf = (numero, tipo) => {
    window.open(`/api/${numero}/download?tipo=${tipo}`, "_blank");
  };

  return (
    <div className="pedidos-container"> 

      <table className="pedidos-tabela">
        <thead>
          <tr>
            <th>Pedido</th>
            <th>Cliente</th>
            <th>Produto</th>
            <th>Envio</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {pedidos.map((pedido) => (
            <tr key={pedido.numero}>
              <td>#{pedido.numero}</td>
              <td>{pedido.cliente?.nome}</td>
              <td>{pedido.itens?.map((item) => item.nomeProduto).join(", ")}</td>
              <td>{pedido.formaEnvio}</td>
              <td className="acoes">
                <button className="btn btn-etiqueta" onClick={() => abrirPdf(pedido.numero, "etiqueta")}>
                  Etiqueta
                </button>
                <button className="btn btn-declaracao" onClick={() => abrirPdf(pedido.numero, "declaracao")}>
                  Declaração
                </button>
                <button className="btn btn-combo" onClick={() => abrirPdf(pedido.numero, "ambos")}>
                  Etiqueta + Declaração
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}