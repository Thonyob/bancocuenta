package com.banco.cuenta.model.document;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    private String idClient;
    private String nroDocumento;
    private String tipoCliente;

}
