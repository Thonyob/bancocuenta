package com.banco.cuenta.model.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "cuenta_cliente")
public class AccountClient {

    @Id
    private String id;
    private String nroCuenta;
    private AccountType tipoCuenta;
    private Client cliente;
    private AccountMovement movimientos;
    private float saldo;



}
