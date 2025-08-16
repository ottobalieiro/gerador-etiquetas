@echo off
REM === Subir backend Spring Boot ===
start cmd /k "cd /d C:\Users\ADM\IdeaProjects\PESSOAL\gerador-etiquetas && mvn spring-boot:run"

REM === Subir frontend Vite ===
start cmd /k "cd /d C:\Users\ADM\frontend && npm run dev"

REM Evita que a janela feche imediatamente
pause
