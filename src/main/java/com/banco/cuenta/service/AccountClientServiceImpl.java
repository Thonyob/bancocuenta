package com.banco.cuenta.service;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.repository.AccountClientRepository;
import com.banco.cuenta.model.service.AccountClientService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountClientServiceImpl implements AccountClientService {

    @Autowired
    private AccountClientRepository accountClientRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;


    @Override
    public Flux<AccountClient> getAll() {
        return this.accountClientRepository.findAll();
    }

    @Override
    public Mono<AccountClient> save(AccountClient accountClient) {
        return this.accountClientRepository.save(accountClient);
    }

    @Override
    public Mono<AccountClient> findById(String id) {
        return this.accountClientRepository.findById(id);
    }

    @Override
    public Mono<Boolean> existsById(String id) {
        return this.accountClientRepository.existsById(id);
    }

    @Override
    public Flowable<AccountClient> getClientAccountByNroCuenta(String nroCuenta) {

        Query query=new Query();
        query.addCriteria(Criteria.where("nroCuenta").in(nroCuenta));

        return mongoTemplate
                .find(query,AccountClient.class)
                .as(RxJava3Adapter::fluxToFlowable);

    }

    @Override
    public Flowable<AccountClient> getClientAccountByNroDocumento(String nroDocumento) {

        System.out.println("Service :"+nroDocumento);

        Query query=new Query();
        query.addCriteria(Criteria.where("cliente.idClient").in(nroDocumento));

        return mongoTemplate
                .find(query,AccountClient.class)
                .as(RxJava3Adapter::fluxToFlowable);

    }
}
