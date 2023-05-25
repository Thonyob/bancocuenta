package com.banco.cuenta.model.service;

import com.banco.cuenta.model.document.AccountClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountClientService {

    Flux<AccountClient> getAll();
    Mono<AccountClient>save(AccountClient accountClient);
    Mono<AccountClient>findById(String id);
    Mono<Boolean>existsById(String id);

}
