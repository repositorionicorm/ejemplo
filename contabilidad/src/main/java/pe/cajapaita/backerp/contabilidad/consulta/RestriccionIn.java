/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.consulta;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author hnole
 */
public class RestriccionIn {

    private String propiedad;
    private List<Integer> restricciones;

    public RestriccionIn(String propiedad) {
        this.propiedad = propiedad;
         this.restricciones = new ArrayList<>();
    }
    
    

    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }

    public List<Integer> getRestricciones() {
        return restricciones;
    }

    public void agregarRestriccion(int restriccion) {
        this.restricciones.add(restriccion);
    }

}
