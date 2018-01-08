/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.infraestructura;

/**
 *
 * @author hnole
 */
public class ExcepcionNegocio extends Exception{

    private String tipoMensaje;
    private String idGenerados;
    private String numerosGenerados;
    
    public ExcepcionNegocio(String mensaje, String tipoMensaje) {
        super(mensaje);
        this.tipoMensaje = tipoMensaje;
    }

    public ExcepcionNegocio(String mensaje, String tipoMensaje, String idGenerados, String numerosGenerados) {
        super(mensaje);
        this.tipoMensaje = tipoMensaje;
        this.idGenerados = idGenerados;
        this.numerosGenerados=numerosGenerados;
    }
    
    public String getTipoMensaje() {
        return tipoMensaje;
    }   

    public String getIdGenerados() {
        return idGenerados;
    }

    public String getNumerosGenerados() {
        return numerosGenerados;
    }    
}
