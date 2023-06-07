package com.banco.cuenta.service;

import com.banco.cuenta.model.document.AccountClient;
import com.banco.cuenta.model.document.AccountMovement;
import com.banco.cuenta.model.document.AccountType;
import com.banco.cuenta.model.document.Client;
import com.banco.cuenta.model.repository.AccountClientRepository;
import com.banco.cuenta.model.repository.AccountTypeRepository;
import com.banco.cuenta.model.service.AccountClientService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.adapter.rxjava.RxJava3Adapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class AccountClientServiceImpl implements AccountClientService {

    @Autowired
    private AccountClientRepository accountClientRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;


    @Override
    public Flux<AccountClient> getAll() {
        return this.accountClientRepository.findAll();
    }

    @Override
    public Maybe<AccountClient> save(AccountClient accountClient) {

        boolean isValid = this.isValidCreateAccount(accountClient.getCliente().getNroDocumento()
                ,accountClient.getTipoCuenta().getAccountName()
                ,accountClient.getCliente().getTipoCliente());

        if(isValid) {
            return this.accountClientRepository.save(accountClient)
                    .flatMap(accountClientRepository::save)
                    .as(RxJava3Adapter::monoToMaybe);
        }
        return null;
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
    public Maybe<AccountClient> getClientAccountByNroCuenta(String nroCuenta) {

        Query query=new Query();
        query.addCriteria(Criteria.where("nroCuenta").in(nroCuenta));

        return mongoTemplate
                .findOne(query,AccountClient.class)
                .as(RxJava3Adapter::monoToMaybe);

    }

    @Override
    public Flowable<AccountClient> getClientAccountByNroDocumento(String nroDocumento) {

        Query query=new Query();
        query.addCriteria(Criteria.where("cliente.nroDocumento").in(nroDocumento));

        return mongoTemplate
                .find(query,AccountClient.class)
                .as(RxJava3Adapter::fluxToFlowable);

    }

        @Override
    public Maybe<AccountClient> getUpdateDepositBalance(String nroCuenta,float amount) {

        return this.getClientAccountByNroCuenta(nroCuenta)
                .filter(filter->isValidDepositBalance(filter.getTipoCuenta().getAccountName(),filter.getMovimientos(),amount, filter.getSaldo(),"Deposito"))
                .map(cl-> {
                    cl.setSaldo(cl.getSaldo()+amount);
                    List<AccountMovement> movimientosList=cl.getMovimientos();
                    movimientosList.add(new AccountMovement("Deposiito",amount,getFechaActual()));
                    cl.setMovimientos(movimientosList);
                    return cl;
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(accountClientRepository::save)
                .as(RxJava3Adapter::monoToMaybe);

    }

    @Override
    public Maybe<AccountClient> getUpdateWithdrawalBalance(String nroCuenta, float amount) {

        return this.getClientAccountByNroCuenta(nroCuenta)
                .filter(filter->isValidDepositBalance(filter.getTipoCuenta().getAccountName(),filter.getMovimientos(),amount, filter.getSaldo(),"Retiro"))
                .map(cl-> {
                    cl.setSaldo(cl.getSaldo()-amount);
                    List<AccountMovement> movimientosList=cl.getMovimientos();
                    movimientosList.add(new AccountMovement("Retiro",amount,getFechaActual()));
                    cl.setMovimientos(movimientosList);
                    return cl;
                })
                .to(RxJava3Adapter::maybeToMono)
                .flatMap(accountClientRepository::save)
                .as(RxJava3Adapter::monoToMaybe);
    }

    @Override
    public Flowable<AccountType> getAllAccountType() {
        return this.accountTypeRepository.findAll().as(RxJava3Adapter::fluxToFlowable);
    }

    private boolean isValidCreateAccount(String nroDocumento,String tipoCuenta,String tipoCliente){

        boolean estado=false;

        Long ahorros = this.getClientAccountByNroDocumento(nroDocumento).filter(filter->filter.getTipoCuenta().getAccountName().equals("Cuenta Ahorros")).count().blockingGet();
        Long corriente = this.getClientAccountByNroDocumento(nroDocumento).filter(filter->filter.getTipoCuenta().getAccountName().equals("Cuenta Corriente")).count().blockingGet();
        Long plazoFijo = this.getClientAccountByNroDocumento(nroDocumento).filter(filter->filter.getTipoCuenta().getAccountName().equals("Cuenta Plazo Fijo")).count().blockingGet();

        Predicate<Boolean> predicatePersonal = x->(
                        (tipoCuenta.equals("Cuenta Ahorros")   && ahorros==0  ) ||
                        (tipoCuenta.equals("Cuenta Corriente") && corriente==0 )||
                        (tipoCuenta.equals("Cuenta Plazo Fijo") && plazoFijo==0 ));


        Predicate<Boolean> predicateEmpresarial = x->(
                        (!tipoCuenta.equals("Cuenta Ahorros")) &&
                        (tipoCuenta.equals("Cuenta Corriente")) &&
                        (!tipoCuenta.equals("Cuenta Plazo Fijo")));


        Predicate<Boolean> predicateReturn = x->
                                                        (tipoCliente.equals("Personal") && predicatePersonal.test(false)) ||
                                                        (tipoCliente.equals("Empresarial") && predicateEmpresarial.test(false));


        return predicateReturn.test(false);
    }

    private boolean isValidDepositBalance(String accountType,List<AccountMovement>movimientosCuenta,double amount,double saldo,String tipoTransaccion){

        List<AccountType> listaTipoCuenta = this.getAllAccountType().toList().blockingGet();
        List<AccountType> listaCuenta= listaTipoCuenta.stream().filter(f->f.getNombre().equals(accountType)).collect(Collectors.toList());

        //Fecha
        String formattedDate=getFechaActual();

        //MOVIMIENTOS CUENTA
        long movimientoContador=movimientosCuenta.stream().filter(x-> {
            String fechaActual=formattedDate;
            return x.getFecha().substring(0,10).equals(fechaActual);
        }).count();

        //Predicate
        Predicate<Boolean> predicateCuentaAhorro = f->
                (movimientosCuenta.size()<listaCuenta.get(0).getCondiciones().getLimiteMovimiento()) &&
                        ((tipoTransaccion.equals("Retiro") && saldo>=amount) || (tipoTransaccion.equals("Deposito")));

        Predicate<Boolean> predicateCuentaCorriente = f->
                ((tipoTransaccion.equals("Retiro") && saldo>=amount) || (tipoTransaccion.equals("Deposito")));

        Predicate<Boolean> predicateCuentaPlazoFijo = f-> movimientoContador==0 &&
                ((tipoTransaccion.equals("Retiro") && saldo>=amount) || (tipoTransaccion.equals("Deposito")));


        Predicate<Boolean> predicateFinal = f->
                ((accountType.equals("Cuenta Ahorros") && predicateCuentaAhorro.test(false)) ||
                        (accountType.equals("Cuenta Corriente") && predicateCuentaCorriente.test(false)) ||
                        (accountType.equals("Cuenta Plazo Fijo") && predicateCuentaPlazoFijo.test(false)));


        return predicateFinal.test(false);
    }

    private String getFechaActual(){
        //Fecha
        SimpleDateFormat dtf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendarr = Calendar.getInstance();
        Date dateObj = calendarr.getTime();
        String formattedDate = dtf.format(dateObj);
        return formattedDate;
    }

}
