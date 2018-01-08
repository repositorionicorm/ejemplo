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
public class RestriccionIgual {
    private String propiedad;
    private Object valor;

    public RestriccionIgual(String propiedad, Object valor) {
        this.propiedad = propiedad;
        this.valor = valor;        
    }      

    public RestriccionIgual() {
    }  

    public String getPropiedad() {
        return propiedad;
    }   

    public Object getValor() {
        return valor;
    }    
}
