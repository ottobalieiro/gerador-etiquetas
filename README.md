## Overview
**Gerador de Etiquetas** é uma aplicação web em produção que **automatiza o cadastro e emissão de etiquetas e declarações de conteúdo** para envios via Correios.  
Foi criada para reduzir drasticamente o tempo e o esforço na geração manual de pedidos, eliminando etapas repetitivas e erros comuns.

💡 **Impacto real:** em uso em produção, este sistema reduziu o tempo da equipe de expedição em pelo menos 83% ao automatizar a geração de etiquetas, declarações e pré-postagens.

## Funcionalidades
- Geração automática da **Pré-Postagem** do Correios.
- Geração automática e fácil impressão da **Declaração de Conteúdo**.
- Geração automática e fácil impressão da **Etiqueta**.

## Tecnologias
- **Backend:** Java 21 + Spring Boot 3  
- **Frontend:** React + Vite + TailwindCSS  

## Integrações e Bibliotecas
**Backend** (Java 21 + Spring Boot 3)  
- **Spring Boot Starter Web** → Controllers REST. 
- **Jackson Databind** → JSON ↔ objetos Java. 
- **OpenHTMLtoPDF + Apache PDFBox** → gerar/manipular PDFs.
- **SLF4J** → logs.

**Frontend** (React + Vite)  
- **React** – interface do usuário.  

## Demonstração
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

## Executar Localmente

### Requisitos
- Loja Integrada (paga).
- Contrato com oo Correios.
- Algumas chaves e informações (incorporar no ```application.properties```):
```
lojaintegrada.api.chave=CHAVE_LJ
lojaintegrada.api.aplicacao=CHAVE_APLICACAO_LJ

correios.api.codigo.acesso=CHAVE_UNICA_CWS
correios.api.cartao=NUMERO_CARTAO_POSTAGEM
meu.correios.username=CNPJ_USADO_NO_CONTRATO
```
### Backend
```bash
cd backend-gerador-etiquetas
mvn spring-boot:run
```
### Frontend
```bash
cd frontend-gerador-etiquetas
npm install
npm run dev
```
### Dica Rápida
Use o script: ```start-projetos.bat``` na pasta principal

## Contato
[![Email](https://img.shields.io/badge/Email-otto.bfa%40gmail.com-red?logo=gmail)](mailto:otto.bfa@gmail.com)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Otto%20Balieiro-blue?logo=linkedin)](https://www.linkedin.com/in/otto-balieiro)

