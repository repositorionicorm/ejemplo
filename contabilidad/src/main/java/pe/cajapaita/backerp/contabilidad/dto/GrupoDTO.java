/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author dev-out-02
 */
public class GrupoDTO {
    private int id;
    private String descripcion;
    private BigDecimal monto;
    private String rango;
    private boolean esCabecera=false;
    private String operacionSoles;
    private String operacionDolares;
    
    List<ResultadoEncajePorFecha> listaPorFecha;

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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public boolean isEsCabecera() {
        return esCabecera;
    }

    public void setEsCabecera(boolean esCabecera) {
        this.esCabecera = esCabecera;
    }

    public String getOperacionSoles() {
        return operacionSoles;
    }

    public void setOperacionSoles(String operacionSoles) {
        this.operacionSoles = operacionSoles;
    }

    public String getOperacionDolares() {
        return operacionDolares;
    }

    public void setOperacionDolares(String operacionDolares) {
        this.operacionDolares = operacionDolares;
    }

    public List<ResultadoEncajePorFecha> getListaPorFecha() {
        return listaPorFecha;
    }

    public void setListaPorFecha(List<ResultadoEncajePorFecha> listaPorFecha) {
        this.listaPorFecha = listaPorFecha;
    }
    
}
