@echo off
echo Iniciando Consul...
start "Consul" cmd /c "consul agent -dev"
echo Aguardando Consul iniciar...
timeout /t 10 /nobreak > nul
echo Carregando configuracoes...

REM ============================================
REM Configuracoes do product-api
REM ============================================
consul kv put config/product-api/server.port "8000"
consul kv put config/product-api/spring.datasource.url "jdbc:postgresql://localhost/db_product"
consul kv put config/product-api/spring.datasource.username "postgres"
consul kv put config/product-api/spring.datasource.password "postgresql"
consul kv put config/product-api/app.promotion.message "Customer Week Promotion"
consul kv put config/product-api,it/app.promotion.message "Promozione Settimana del Cliente"

REM ============================================
REM Configuracoes do currency-api
REM ============================================
consul kv put config/currency-api/server.port "8100"
consul kv put config/currency-api/spring.datasource.url "jdbc:postgresql://localhost/db_currency"
consul kv put config/currency-api/spring.datasource.username "postgres"
consul kv put config/currency-api/spring.datasource.password "postgresql"

echo.
echo Consul iniciado e configuracoes carregadas!
pause