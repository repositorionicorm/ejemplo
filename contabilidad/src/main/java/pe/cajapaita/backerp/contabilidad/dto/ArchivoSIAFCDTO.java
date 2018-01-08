/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

/**
 *
 * @author dev-out-02
 */
public class ArchivoSIAFCDTO {
    private String nombre;
    private String descripcion;
    private int tipoAsientoId;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getTipoAsientoId() {
        return tipoAsientoId;
    }

    public void setTipoAsientoId(int tipoAsientoId) {
        this.tipoAsientoId = tipoAsientoId;
    }
    
    
}
