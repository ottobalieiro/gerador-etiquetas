@echo off
REM ============================================================
REM Script para iniciar backend (Spring Boot) e frontend (Vite)
REM Projeto: Gerador de Etiquetas
REM ============================================================

REM Define o diretório base do projeto (a pasta onde está este .bat)
set BASE_DIR=%~dp0

REM Caminhos relativos para backend e frontend
set BACKEND_DIR=%BASE_DIR%backend-gerador-etiquetas
set FRONTEND_DIR=%BASE_DIR%frontend-gerador-etiquetas

REM === Subir backend Spring Boot ===
start cmd /k "cd /d %BACKEND_DIR% && mvnd spring-boot:run"

REM === Subir frontend Vite ===
start cmd /k "cd /d %FRONTEND_DIR% && npm run dev"


