package com.banco.cuenta.model.service;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.document.AccountType;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountClientService {

    Flux<AccountClient> getAll();
    Maybe<AccountClient>save(AccountClient accountClient);
    Mono<AccountClient>findById(String id);
    Mono<Boolean>existsById(String id);
    Maybe<AccountClient> getClientAccountByNroCuenta(String nroCuenta);
    Flowable<AccountClient> getClientAccountByNroDocumento(String nroDocumento);
    Maybe<AccountClient> getUpdateDepositBalance(String nroCuenta,float amount);
    Maybe<AccountClient> getUpdateWithdrawalBalance(String nroCuenta,float amount);
    Flowable<AccountType> getAllAccountType();

}
