import React, { useEffect, useState } from "react";

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
    <div className="flex justify-center py-10 px-5">
      <table className="table-auto border-collapse w-full shadow-sm rounded-lg overflow-hidden">
        <thead>
          <tr className="bg-gray-200 text-gray-700 text-sm uppercase">
            <th className="px-4 py-2 text-left">Pedido</th>
            <th className="px-4 py-2 text-left">Cliente</th>
            <th className="px-4 py-2 text-left">Produto</th>
            <th className="px-4 py-2 text-left">Envio</th>
            <th className="px-4 py-2 text-left">Ações</th>
          </tr>
        </thead>
        <tbody>
          {pedidos.map((pedido) => (
            <tr key={pedido.numero} className="border-b hover:bg-gray-50">
              <td className="px-4 py-2 text-gray-600">#{pedido.numero}</td>
              <td className="px-4 py-2 text-gray-600">{pedido.cliente?.nome}</td>
              <td className="px-4 py-2 text-gray-600">
                {pedido.itens?.map((item) => item.nomeProduto).join(", ")}
              </td>
              <td className="px-4 py-2 text-gray-600">{pedido.formaEnvio}</td>
              <td className="px-4 py-2 flex gap-2">
                <button
                  className="px-3 py-1 rounded-full border border-gray-600 text-white text-sm hover:bg-gray-100"
                  onClick={() => abrirPdf(pedido.numero, "etiqueta")}
                >
                  Etiqueta
                </button>
                <button
                  className="px-3 py-1 rounded-full border border-gray-600 text-white text-sm hover:bg-gray-100"
                  onClick={() => abrirPdf(pedido.numero, "declaracao")}
                >
                  Declaração
                </button>
                <button
                  className="px-3 py-1 rounded-full bg-yellow-400 text-white text-sm hover:bg-yellow-500"
                  onClick={() => abrirPdf(pedido.numero, "ambos")}
                >
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
