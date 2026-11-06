#!/bin/sh

set -eu

echo "=============================================="
echo " Carregando configurações no Consul"
echo "=============================================="

# Aguarda o Consul responder à API HTTP
until consul members >/dev/null 2>&1; do
  echo "Aguardando o Consul..."
  sleep 2
done

# Função auxiliar para cadastrar uma configuração
put() {
  consul kv put "$1" "$2"
}

# ============================================
# CONFIGURAÇÕES GERAIS - TODAS AS APLICAÇÕES
# ============================================
put "config/application/management.endpoints.web.exposure.include" "*"
put "config/application/management.endpoint.health.show-components" "always"
put "config/application/management.endpoint.health.show-details" "always"
put "config/application/management.health.livenessstate.enabled" "true"
put "config/application/management.health.readinessstate.enabled" "true"
put "config/application/management.health.db.enabled" "true"

# ============================================
# PRODUCT API
# ============================================
put "config/product-api/server.port" "8000"
put "config/product-api/spring.datasource.url" "jdbc:postgresql://product-db:5432/db_product"
put "config/product-api/spring.datasource.username" "postgres"
put "config/product-api/spring.datasource.password" "postgresql"
put "config/product-api/app.promotion.message" "Promoção Semana do Cliente"

# Resilience4j - Retry
put "config/product-api/resilience4j.retry.instances.currency-api.max-attempts" "3"
put "config/product-api/resilience4j.retry.instances.currency-api.wait-duration" "1s"
put "config/product-api/resilience4j.retry.instances.currency-api.ignore-exceptions" "io.github.resilience4j.circuitbreaker.CallNotPermittedException"

# Resilience4j - Circuit Breaker
put "config/product-api/resilience4j.circuitbreaker.instances.currency-api.sliding-window-type" "COUNT_BASED"
put "config/product-api/resilience4j.circuitbreaker.instances.currency-api.sliding-window-size" "5"
put "config/product-api/resilience4j.circuitbreaker.instances.currency-api.minimum-number-of-calls" "5"
put "config/product-api/resilience4j.circuitbreaker.instances.currency-api.failure-rate-threshold" "50"
put "config/product-api/resilience4j.circuitbreaker.instances.currency-api.wait-duration-in-open-state" "100s"

# Cache
put "config/product-api/spring.cache.type" "caffeine"
put "config/product-api/spring.cache.cache-names" "currency-api"
put "config/product-api/spring.cache.caffeine.spec" "maximumSize=1000,expireAfterWrite=10m"

# Ordem dos aspectos
put "config/product-api/resilience4j.retry.retry-aspect-order" "100"
put "config/product-api/resilience4j.circuitbreaker.circuit-breaker-aspect-order" "101"


# ============================================
# CURRENCY API
# ============================================
put "config/currency-api/server.port" "8100"
put "config/currency-api/spring.datasource.url" "jdbc:postgresql://currency-db:5432/db_currency"
put "config/currency-api/spring.datasource.username" "postgres"
put "config/currency-api/spring.datasource.password" "postgresql"

# Resilience4j - Retry (BCB)
put "config/currency-api/resilience4j.retry.instances.bcb.max-attempts" "3"
put "config/currency-api/resilience4j.retry.instances.bcb.wait-duration" "1s"
put "config/currency-api/resilience4j.retry.instances.bcb.ignore-exceptions" "io.github.resilience4j.circuitbreaker.CallNotPermittedException"

# Resilience4j - Circuit Breaker (BCB)
put "config/currency-api/resilience4j.circuitbreaker.instances.bcb.sliding-window-type" "COUNT_BASED"
put "config/currency-api/resilience4j.circuitbreaker.instances.bcb.sliding-window-size" "5"
put "config/currency-api/resilience4j.circuitbreaker.instances.bcb.minimum-number-of-calls" "5"
put "config/currency-api/resilience4j.circuitbreaker.instances.bcb.failure-rate-threshold" "50"
put "config/currency-api/resilience4j.circuitbreaker.instances.bcb.wait-duration-in-open-state" "100s"

# Cache
put "config/currency-api/spring.cache.type" "caffeine"
put "config/currency-api/spring.cache.cache-names" "bcb"
put "config/currency-api/spring.cache.caffeine.spec" "maximumSize=1000,expireAfterWrite=10m"

put "config/currency-api/resilience4j.retry.retry-aspect-order" "100"
put "config/currency-api/resilience4j.circuitbreaker.circuit-breaker-aspect-order" "101"

# ============================================
# AUTH API
# ============================================
put "config/auth-api/server.port" "8200"
put "config/auth-api/spring.datasource.url" "jdbc:postgresql://auth-db:5432/db_user"
put "config/auth-api/spring.datasource.username" "postgres"
put "config/auth-api/spring.datasource.password" "postgresql"

# ============================================
# ORDER API
# ============================================
put "config/order-api/server.port" "8300"
put "config/order-api/spring.datasource.url" "jdbc:postgresql://order-db:5432/db_order"
put "config/order-api/spring.datasource.username" "postgres"
put "config/order-api/spring.datasource.password" "postgresql"

# ============================================
# GATEWAY API
# ============================================
put "config/gateway-api/server.port" "8080"

echo "=============================================="
echo " Configurações carregadas com sucesso!"
echo "=============================================="