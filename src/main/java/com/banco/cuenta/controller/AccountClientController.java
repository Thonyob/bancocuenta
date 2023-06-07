package com.banco.cuenta.controller;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.document.AccountType;
import com.banco.cuenta.model.service.AccountClientService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
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
    public ResponseEntity<Maybe<AccountClient>> registerAccountClient(@RequestBody AccountClient accountClient) {
        return new ResponseEntity<>(accountClientService.save(accountClient), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Flux<AccountClient>> getAllAccountClient(){
                return new ResponseEntity<>(accountClientService.getAll(),HttpStatus.OK);
    }

    @GetMapping("/client/nroCuenta/{nroCuenta}")
    public ResponseEntity<Maybe<AccountClient>> getClientAccountByNroCuenta(@PathVariable("nroCuenta") String nroCuenta){
        return new ResponseEntity<>(accountClientService.getClientAccountByNroCuenta(nroCuenta),HttpStatus.OK);
    }

    @GetMapping("/client/nroDocumento/{nroDocumento}")
    public ResponseEntity<Flowable<AccountClient>> getClientAccountByNroDocumento(@PathVariable("nroDocumento") String nroDocumento){
        return new ResponseEntity<>(accountClientService.getClientAccountByNroDocumento(nroDocumento),HttpStatus.OK);
    }

    @PostMapping("/client/deposit")
    public ResponseEntity<Maybe<AccountClient>> getUpdateDepositBalance(@RequestParam("nroCuenta")String nroCuenta,
                                                                        @RequestParam("amount") float amount){
        return new ResponseEntity<>(accountClientService.getUpdateDepositBalance(nroCuenta,amount),HttpStatus.OK);
    }

    @PostMapping("/client/withdrawa")
    public ResponseEntity<Maybe<AccountClient>> getUpdateWithdrawalBalance(@RequestParam("nroCuenta")String nroCuenta,
                                                                        @RequestParam("amount") float amount){
        return new ResponseEntity<>(accountClientService.getUpdateWithdrawalBalance(nroCuenta,amount),HttpStatus.OK);
    }

    @GetMapping("/account")
    public ResponseEntity<Flowable<AccountType>> getAllAccountTye(){
        return new ResponseEntity<>(accountClientService.getAllAccountType(),HttpStatus.OK);
    }


}

