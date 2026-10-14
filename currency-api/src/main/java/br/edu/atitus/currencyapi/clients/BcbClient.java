package br.edu.atitus.currencyapi.clients;

import br.edu.atitus.currencyapi.dtos.BcbResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface BcbClient {

    @GetExchange("/olinda/servico/PTAX/versao/v1/odata/CotacaoMoedaDia(moeda='{moeda}',dataCotacao='{data}')")
    BcbResponse getCotacao(
            @PathVariable String moeda,
            @PathVariable String data,
            @RequestParam("$format") String format
    );
}