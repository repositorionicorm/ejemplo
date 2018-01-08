/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.util.List;

/**
 *
 * @author dev-out-02
 */
public class VariableComboDTO {
   private String descripcion;
   private List<Object> combo;

    public VariableComboDTO(String descripcion, List<Object> combo) {
        this.descripcion = descripcion;
        this.combo = combo;
    }

   
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Object> getCombo() {
        return combo;
    }

    public void setCombo(List<Object> combo) {
        this.combo = combo;
    }
   
}
