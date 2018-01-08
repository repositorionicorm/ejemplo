/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author hnole
 */
public class PeriodoDTO {
    private int id;
    private int periodoAnteriorId;
    private String descripcion;
    private Date fechaInicial;
    private Date fechaFinal;
    private int ultimoAsiento;
    private String fechaInicialString;
    private String fechaFinalString;
    private Date fechaRenumeracion;
    private Date fechaBalance;
    private Date fechaIntegracion;
    private BigDecimal tipoCambio;
    private BigDecimal tipoCambioAnterior;
    private String descripcionAnterior;
    private Date fechaLimite;
    private String fechaLimiteString;
    private int estadoId;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicial() {
        return fechaInicial;
    }

    public void setFechaInicial(Date fechaInicial) {
        this.fechaInicial = fechaInicial;
    }

    public Date getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(Date fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public int getUltimoAsiento() {
        return ultimoAsiento;
    }

    public void setUltimoAsiento(int ultimoAsiento) {
        this.ultimoAsiento = ultimoAsiento;
    }

    public String getFechaInicialString() {
        return fechaInicialString;
    }

    public void setFechaInicialString(String fechaInicialString) {
        this.fechaInicialString = fechaInicialString;
    }

    public String getFechaFinalString() {
        return fechaFinalString;
    }

    public void setFechaFinalString(String fechaFinalString) {
        this.fechaFinalString = fechaFinalString;
    }
    
    public int getPeriodoAnteriorId() {
        return periodoAnteriorId;
}

    public void setPeriodoAnteriorId(int periodoAnteriorId) {
        this.periodoAnteriorId = periodoAnteriorId;
    }

    public Date getFechaRenumeracion() {
        return fechaRenumeracion;
    }

    public void setFechaRenumeracion(Date fechaRenumeracion) {
        this.fechaRenumeracion = fechaRenumeracion;
    }

    public Date getFechaBalance() {
        return fechaBalance;
    }

    public void setFechaBalance(Date fechaBalance) {
        this.fechaBalance = fechaBalance;
    }

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public BigDecimal getTipoCambioAnterior() {
        return tipoCambioAnterior;
    }

    public void setTipoCambioAnterior(BigDecimal tipoCambioAnterior) {
        this.tipoCambioAnterior = tipoCambioAnterior;
    }

    public String getDescripcionAnterior() {
        return descripcionAnterior;
    }

    public void setDescripcionAnterior(String descripcionAnterior) {
        this.descripcionAnterior = descripcionAnterior;
    }

    public Date getFechaIntegracion() {
        return fechaIntegracion;
    }

    public void setFechaIntegracion(Date fechaIntegracion) {
        this.fechaIntegracion = fechaIntegracion;
    }

    public Date getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(Date fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getFechaLimiteString() {
        return fechaLimiteString;
    }

    public void setFechaLimiteString(String fechaLimiteString) {
        this.fechaLimiteString = fechaLimiteString;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }
    
    

}
