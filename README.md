## OVERVIEW
**Gerador de Etiquetas** é uma aplicação web em produção que **automatiza o cadastro e emissão de etiquetas e declarações de conteúdo** para envios via Correios.  
Foi criada para reduzir drasticamente o tempo e o esforço na geração manual de pedidos, eliminando etapas repetitivas e erros comuns.

💡 **Impacto real:** este sistema já está em uso no ambiente de produção, otimizando o fluxo de expedição e aumentando a produtividade.

## FUNCIONALIDADES
- Geração automática da **Pré-Postagem** do Coreios.
- Geração automática e facil impressão da **Declaração de Conteúdo**.
- Geração automática e facil impressão da **Etiqueta**.

## TECNOLOGIAS
**Backend:** Java 21 + Spring Boot 3  
**Frontend:** React + Vite + TailwindCSS  

## INTEGRAÇÕES E BIBLIOTECAS

**Backend (Java 21 + Spring Boot 3)**  
- **Spring Boot Starter Web** → Controllers REST. 
- **Jackson Databind** → JSON ↔ objetos Java. 
- **OpenHTMLtoPDF + Apache PDFBox** → gerar/manipular PDFs.
- **SLF4J** → logs.

**Frontend (React + Vite)**  
- **React** – interface do usuário.  

## DEMONSTRAÇÃO
### 1. Lista de pedidos pagos:
<img src="https://github.com/user-attachments/assets/e129bad9-5144-4667-8aa8-b0a874fdfb47"
     alt="Lista de pedidos pagos para gerar a etiqueta e declaração"
     width="730"/>
### 2. Etiqueta e declaração geradas:     
<img src="https://github.com/user-attachments/assets/fee9aaf9-fe05-4a09-b8e1-be455832fad4"
     alt="Etiqueta e declaração geradas"
     width="730"/>
### 3. Pré-postagem gerada:     
<img src="https://github.com/user-attachments/assets/c43c90d4-b351-429c-a515-4375977e80e4"
     alt="Pré-postagem gerada na interface do Correios"
     width="730"/>

## EXECULTAR LOCALMENTE



### Backend
```bash
cd backend
mvn spring-boot:run
```
### Frontend
```bash
cd frontend
npm install
npm run dev
```

📧 [ottobfa@gmail.com]
🔗 [www.linkedin.com/in/otto-balieiro]

