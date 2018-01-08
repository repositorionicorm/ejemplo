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
public class RestriccionLike {
    private String propiedad;
    private String valor;

    public RestriccionLike(String propiedad, String valor) {
        this.propiedad = propiedad;
        this.valor = valor;
    }

    public RestriccionLike() {
    }    
    
    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    
    
}
