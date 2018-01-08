/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.math.BigDecimal;

/**
 *
 * @author hnole
 */
public class DetalleIntegracionDTO {
    private int transaccion;
    private String moneda;
    private BigDecimal debeOriginal;
    private BigDecimal haberOriginal;
    private BigDecimal debeSoles;
    private BigDecimal haberSoles;

    public int getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(int transaccion) {
        this.transaccion = transaccion;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getDebeOriginal() {
        return debeOriginal;
    }

    public void setDebeOriginal(BigDecimal debeOriginal) {
        this.debeOriginal = debeOriginal;
    }

    public BigDecimal getHaberOriginal() {
        return haberOriginal;
    }

    public void setHaberOriginal(BigDecimal haberOriginal) {
        this.haberOriginal = haberOriginal;
    }

    public BigDecimal getDebeSoles() {
        return debeSoles;
    }

    public void setDebeSoles(BigDecimal debeSoles) {
        this.debeSoles = debeSoles;
    }

    public BigDecimal getHaberSoles() {
        return haberSoles;
    }

    public void setHaberSoles(BigDecimal haberSoles) {
        this.haberSoles = haberSoles;
    }    
}
