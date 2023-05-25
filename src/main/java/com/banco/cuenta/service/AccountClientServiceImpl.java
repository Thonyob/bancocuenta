package com.banco.cuenta.service;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.repository.AccountClientRepository;
import com.banco.cuenta.model.service.AccountClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountClientServiceImpl implements AccountClientService {

    @Autowired
    private AccountClientRepository accountClientRepository;

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
}
