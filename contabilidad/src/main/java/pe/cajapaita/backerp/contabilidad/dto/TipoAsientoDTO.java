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
public class TipoAsientoDTO {
    private int id;
    private String descripcion;
    private String glosa;    
    private int procedenciaId;
    private String modulo;

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

    public String getGlosa() {
        return glosa;
    }

    public void setGlosa(String glosa) {
        this.glosa = glosa;
    }
    
    public int getProcedenciaId() {
        return procedenciaId;
    }

    public void setProcedenciaId(int procedenciaId) {
        this.procedenciaId = procedenciaId;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }
    
    
}
