package com.banco.cuenta.model.repository;

import com.banco.cuenta.model.document.AccountClient;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountClientRepository extends ReactiveMongoRepository<AccountClient,String> {
}
