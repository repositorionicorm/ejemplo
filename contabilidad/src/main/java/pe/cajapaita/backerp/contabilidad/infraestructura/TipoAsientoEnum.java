/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.infraestructura;

/**
 *
 * @author hnole
 */
public enum TipoAsientoEnum {
    CompraVenta(5);

    private final int valor;

    private TipoAsientoEnum(int valor) {
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
