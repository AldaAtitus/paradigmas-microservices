package br.edu.atitus.currencyapi.services;

import br.edu.atitus.currencyapi.clients.BcbClient;
import br.edu.atitus.currencyapi.dtos.BcbResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BcbClientService {

    private final BcbClient bcbClient;

    public BcbClientService(BcbClient bcbClient) {
        this.bcbClient = bcbClient;
    }

    @Cacheable(value = "bcb", key = "#moeda + '-' + #data")
    @Retry(name = "bcb")
    @CircuitBreaker(name = "bcb")
    public BcbResponse getCotacao(String moeda, String data) {
        return bcbClient.getCotacao(moeda, data, "json");
    }
}