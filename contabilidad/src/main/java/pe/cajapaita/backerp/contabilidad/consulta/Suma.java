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
public class Suma {
    private String propiedadSumar;
    private String aliasDeSuma;

    public Suma(String propiedadSumar, String aliasDeSuma) {
        this.propiedadSumar = propiedadSumar;
        this.aliasDeSuma = aliasDeSuma;
    }

    public String getPropiedadSumar() {
        return propiedadSumar;
    }   

    public String getAliasDeSuma() {
        return aliasDeSuma;
    }
}
