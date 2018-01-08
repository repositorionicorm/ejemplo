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
public class RestriccionSql {
    private String sentenciaSql;

    public RestriccionSql(String sentenciaSql) {
        this.sentenciaSql = sentenciaSql;
    }

    public String getSentenciaSql() {
        return sentenciaSql;
    }

    public void setSentenciaSql(String sentenciaSql) {
        this.sentenciaSql = sentenciaSql;
    }   
    
}
