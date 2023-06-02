package com.banco.cuenta.controller;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.service.AccountClientService;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/account-client")
public class AccountClientController {

    @Autowired
    AccountClientService accountClientService;

    @PostMapping
    public ResponseEntity<Mono<AccountClient>> registerAccountClient(@RequestBody AccountClient accountClient) {
        return new ResponseEntity<>(accountClientService.save(accountClient), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<AccountClient>> getAllAccountClient(){
                return new ResponseEntity<>(accountClientService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/client/nroCuenta/{nroCuenta}")
    public ResponseEntity<Flowable<AccountClient>> getClientAccountByNroCuenta(@PathVariable("nroCuenta") String nroCuenta){
        return new ResponseEntity<>(accountClientService.getClientAccountByNroCuenta(nroCuenta),HttpStatus.OK);
    }

    @GetMapping("/client/nroDocumento/{nroDocumento}")
    public ResponseEntity<Flowable<AccountClient>> getClientAccountByNroDocumento(@PathVariable("nroDocumento") String nroDocumento){
        System.out.println("nro Documento ."+nroDocumento);
        return new ResponseEntity<>(accountClientService.getClientAccountByNroDocumento(nroDocumento),HttpStatus.OK);
    }



}

