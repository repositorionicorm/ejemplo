package pe.cajapaita.backerp.contabilidad.dto;

/**
 *
 * @author dev-out-02
 */
public class EstadoCierreDTO {
    private String proceso;
    private String respuesta;
    private boolean estado;

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getProceso() {
        return proceso;
    }

    public void setProceso(String proceso) {
        this.proceso = proceso;
    }
    
    
}
