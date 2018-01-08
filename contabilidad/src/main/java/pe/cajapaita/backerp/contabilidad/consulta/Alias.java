/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.consulta;

/**
 *
 * @author hnole
 */
public class Alias {
    private String propiedadEntidad;
    private String alias;

    public Alias(String propiedadEntidad, String alias) {
        this.propiedadEntidad = propiedadEntidad;
        this.alias = alias;
    }

    public Alias() {
    }    
    
    public String getPropiedadEntidad() {
        return propiedadEntidad;
    }

    public void setPropiedadEntidad(String propiedadEntidad) {
        this.propiedadEntidad = propiedadEntidad;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
    
    
}
