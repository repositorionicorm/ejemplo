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
public class Equivalencia {
    private String propiedadEntidad;
    private String propiedadDto;

    public Equivalencia(String propiedadEntidad, String propiedadDto) {
        this.propiedadEntidad = propiedadEntidad;
        this.propiedadDto = propiedadDto;
    }

    public Equivalencia() {
    }

    public String getPropiedadEntidad() {
        return propiedadEntidad;
    }

    public String getPropiedadDto() {
        return propiedadDto;
    }
    
    
}
