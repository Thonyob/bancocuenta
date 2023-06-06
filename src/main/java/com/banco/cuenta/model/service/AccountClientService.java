package com.banco.cuenta.model.service;

import com.banco.cuenta.model.document.AccountClient;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountClientService {

    Flux<AccountClient> getAll();
    Maybe<AccountClient>save(AccountClient accountClient);
    Mono<AccountClient>findById(String id);
    Mono<Boolean>existsById(String id);
    Flowable<AccountClient> getClientAccountByNroCuenta(String nroCuenta);
    Flowable<AccountClient> getClientAccountByNroDocumento(String nroDocumento);


}
