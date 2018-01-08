/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.util.Date;

/**
 *
 * @author hnole
 */
public class IntegracionDTO {
    private int id;    
    private int procedenciaId;
    private int tipoAsientoId;
    private String tipoAsientoDescripcion;
    private Date fecha;
    private String fechaString;
    private String observacion;
    private int estadoId;
    private String estadoDescripcion; 
    private String estadoColor;
    private String agenciaAbreviatura;
    private String asientoId;
    private int agenciaId;
        
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
   

    public int getProcedenciaId() {
        return procedenciaId;
    }

    public void setProcedenciaId(int procedenciaId) {
        this.procedenciaId = procedenciaId;
    }

    public int getTipoAsientoId() {
        return tipoAsientoId;
    }

    public void setTipoAsientoId(int tipoAsientoId) {
        this.tipoAsientoId = tipoAsientoId;
    }
       

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getFechaString() {
        return fechaString;
    }

    public void setFechaString(String fechaString) {
        this.fechaString = fechaString;
    }   
    

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }        

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    public String getTipoAsientoDescripcion() {
        return tipoAsientoDescripcion;
    }

    public void setTipoAsientoDescripcion(String tipoAsientoDescripcion) {
        this.tipoAsientoDescripcion = tipoAsientoDescripcion;
    }

    public String getEstadoColor() {
        return estadoColor;
    }

    public void setEstadoColor(String estadoColor) {
        this.estadoColor = estadoColor;
    }

    public String getAgenciaAbreviatura() {
        return agenciaAbreviatura;
    }

    public void setAgenciaAbreviatura(String agenciaAbreviatura) {
        this.agenciaAbreviatura = agenciaAbreviatura;
    }

    public int getAgenciaId() {
        return agenciaId;
    }

    public void setAgenciaId(int agenciaId) {
        this.agenciaId = agenciaId;
    }

    public String getAsientoId() {
        return asientoId;
    }

    public void setAsientoId(String asientoId) {
        this.asientoId = asientoId;
    }
}
