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
public class Grupo {
    private String propiedadAgrupar;
    private String aliasDeGrupo;

    public Grupo(String propiedadAgrupar, String aliasDeGrupo) {
        this.propiedadAgrupar = propiedadAgrupar;
        this.aliasDeGrupo = aliasDeGrupo;
    }

    public String getPropiedadAgrupar() {
        return propiedadAgrupar;
    }

    public String getAliasDeGrupo() {
        return aliasDeGrupo;
    }
    
    
}
