package com.banco.cuenta.controller;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.service.AccountClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;
import reactor.core.publisher.Mono;

import javax.swing.text.html.parser.Entity;

@RestController
@RequestMapping(path = "/account-client")
public class BancoAccount {

    @Autowired
    AccountClientService accountClientService;

    @PostMapping
    public ResponseEntity<Mono<AccountClient>> register(@RequestBody AccountClient accountClient) {
        return new ResponseEntity<>(accountClientService.save(accountClient), HttpStatus.CREATED);
    }

}

