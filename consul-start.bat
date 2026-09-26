@echo off
echo Iniciando Consul...
start "Consul" consul agent -dev
echo Aguardando Consul iniciar...
timeout /t 3 /nobreak > nul
echo Carregando configuracoes...

consul kv put config/currency-api/server.port "8100"
consul kv put config/currency-api/spring.datasource.url "jdbc:postgresql://localhost/db_currency"
consul kv put config/currency-api/spring.datasource.username "postgres"
consul kv put config/currency-api/spring.datasource.password "postgresql"

echo.
echo Consul iniciado e configuracoes carregadas!
pause