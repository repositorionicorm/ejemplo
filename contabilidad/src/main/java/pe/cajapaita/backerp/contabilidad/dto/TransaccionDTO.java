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
public class TransaccionDTO {
    private BigDecimal transaccion;
    private String operacion;
    private String moneda;
    private BigDecimal montoMovimiento;
    private String codigoReferencia;
    private String nombreReferencia;
    private BigDecimal correlativo;
    private String cuenta;
    private String descripcion;
    private BigDecimal debeOriginal;
    private BigDecimal haberOriginal;
    private BigDecimal debeSoles;
    private BigDecimal haberSoles;

    public BigDecimal getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(BigDecimal transaccion) {
        this.transaccion = transaccion;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public BigDecimal getMontoMovimiento() {
        return montoMovimiento;
    }

    public void setMontoMovimiento(BigDecimal montoMovimiento) {
        this.montoMovimiento = montoMovimiento;
    }

    public String getCodigoReferencia() {
        return codigoReferencia;
    }

    public void setCodigoReferencia(String codigoReferencia) {
        this.codigoReferencia = codigoReferencia;
    }

    public String getNombreReferencia() {
        return nombreReferencia;
    }

    public void setNombreReferencia(String nombreReferencia) {
        this.nombreReferencia = nombreReferencia;
    }

    public BigDecimal getCorrelativo() {
        return correlativo;
    }

    public void setCorrelativo(BigDecimal correlativo) {
        this.correlativo = correlativo;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
