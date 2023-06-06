package com.banco.cuenta.service;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.document.Client;
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

import java.util.List;
import java.util.function.Predicate;

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
    public Maybe<AccountClient> save(AccountClient accountClient) {

            return this.accountClientRepository.save(accountClient)
                    .filter(valid->this.isValidCreateCuenta (accountClient.getCliente().getNroDocumento(),accountClient.getTipoCuenta().getAccountName()))
                    .flatMap(accountClientRepository::save)
                    .as(RxJava3Adapter::monoToMaybe);
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

        Query query=new Query();
        query.addCriteria(Criteria.where("cliente.nroDocumento").in(nroDocumento));

        return mongoTemplate
                .find(query,AccountClient.class)
                .as(RxJava3Adapter::fluxToFlowable);

    }

    private boolean isValidCreateCuenta(String nroDocumento,String tipoCuenta){

        boolean estado=false;

        Long ahorros = this.getClientAccountByNroDocumento(nroDocumento).filter(filter->filter.getTipoCuenta().getAccountName().equals("Ahorros")).count().blockingGet();
        Long corriente = this.getClientAccountByNroDocumento(nroDocumento).filter(filter->filter.getTipoCuenta().getAccountName().equals("Corriente")).count().blockingGet();
        Long plazoFijo = this.getClientAccountByNroDocumento(nroDocumento).filter(filter->filter.getTipoCuenta().getAccountName().equals("Plazo Fijo")).count().blockingGet();

        Predicate<Boolean> predicatePersonal = x->(
                        (tipoCuenta.equals("Ahorros")   && ahorros==0   || ahorros<=1) &&
                        (tipoCuenta.equals("Corriente") && corriente==0 || corriente<=1)&&
                        (tipoCuenta.equals("Plazo Fijo") && plazoFijo==0 || plazoFijo<=1));


        Predicate<Boolean> predicateEmpresarial = x->(
                        (!tipoCuenta.equals("Ahorros")) &&
                        (tipoCuenta.equals("Corriente")) &&
                        (!tipoCuenta.equals("Plazo Fijo")));

        if(Integer.parseInt(nroDocumento)==8) {

            if (predicatePersonal.test(false)) {
                estado=true;
            }

        }else{

            if (predicateEmpresarial.test(false)) {
                estado=true;
            }

        }


        return estado;

    }

}
