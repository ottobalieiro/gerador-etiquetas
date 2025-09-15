// src/App.jsx
import React, { useState } from "react";
import Pedidos from "./Pedidos";
import coroaLogo from "./assets/coroa.png"; // coloque o logo real da Coroa
import salomonLogo from "./assets/salomon.png"; // coloque o logo real da Salomon

function App() {
  const [empresa, setEmpresa] = useState("coroa");

  return (
    <div className="min-h-screen min-w-screen bg-gray-50 flex flex-col items-center">
      {/* Switch de Empresa */}
      <div className="flex items-center justify-center mt-8 space-x-6">
        {/* Coroa */}
        <div
          className={`cursor-pointer transition-transform duration-300 ${
            empresa === "coroa" ? "scale-110 drop-shadow-lg" : "opacity-60 hover:opacity-100"
          }`}
          onClick={() => setEmpresa("coroa")}
        >
          <img src={coroaLogo} alt="Coroa Própolis" className="w-28 h-28 object-contain" />
        </div>

        {/* Linha divisória */}
        <div className="h-20 w-px bg-gray-400"></div>

        {/* Salomon */}
        <div
          className={`cursor-pointer transition-transform duration-300 ${
            empresa === "salomon" ? "scale-110 drop-shadow-lg" : "opacity-60 hover:opacity-100"
          }`}
          onClick={() => setEmpresa("salomon")}
        >
          <img src={salomonLogo} alt="Salomon Própolis" className="w-28 h-28 object-contain" />
        </div>
      </div>

      {/* Conteúdo dinâmico */}
      <div className="mt-10 w-full px-4">
        {empresa === "coroa" && <Pedidos />}
        {empresa === "salomon" && (
          <div className="text-center text-gray-600">
            <h2 className="text-xl font-semibold mb-4">Área da Salomon</h2>
            <p>Em breve aqui vai entrar o CRUD de clientes e produtos.</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;
