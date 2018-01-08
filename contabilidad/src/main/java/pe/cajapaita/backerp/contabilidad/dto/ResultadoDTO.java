/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.util.List;
import org.springframework.stereotype.Component;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;

/**
 *
 * @author hnole
 */
@Component
public class ResultadoDTO {

    private String tipoMensaje;
    private String mensaje;
    private List<Object> listaObjetos;
    private Object objeto;
    private int totalPaginas;

    public ResultadoDTO() {
        this.tipoMensaje = Mensaje.TIPO_OCULTO;
        this.totalPaginas=1;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje, String tipoMensaje) {
        this.mensaje = mensaje;
        this.tipoMensaje = tipoMensaje;
    }

    public List<Object> getListaObjetos() {
        return listaObjetos;
    }

    public void setListaObjetos(List<Object> listaObjetos, int totalPaginas) {
        this.listaObjetos = listaObjetos;
        this.totalPaginas = totalPaginas;        
    }

    public Object getObjeto() {
        return objeto;
    }

    public void setObjeto(Object objeto) {
        this.objeto = objeto;        
    }    

    public int getTotalPaginas() {
        return totalPaginas;
    } 
  
}
