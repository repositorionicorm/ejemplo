/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

/**
 *
 * @author hnole
 */
public class MonedaDTO {
    private int id;
    private String descripcion;
    private String denominacionIso;
    private String simbolo;
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

    public String getDenominacionIso() {
        return denominacionIso;
    }

    public void setDenominacionIso(String denominacionIso) {
        this.denominacionIso = denominacionIso;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }
    
    
}
