/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.consulta;

import java.util.Date;

/**
 *
 * @author hnole
 */
public class RestriccionBetweenDate {    
    private String propiedad;
    private Date valorInicial;
    private Date valorFinal;

    public RestriccionBetweenDate() {
    }
        
    public RestriccionBetweenDate(String propiedad, Date valorInicial, Date valorFinal) {
        this.propiedad = propiedad;
        this.valorInicial = valorInicial;
        this.valorFinal = valorFinal;
    }

    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }

    public Date getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(Date valorInicial) {
        this.valorInicial = valorInicial;
    }

    public Date getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(Date valorFinal) {
        this.valorFinal = valorFinal;
    }
    
    
    
}
