
package pe.cajapaita.backerp.contabilidad.dto;

/**
 *
 * @author dev-out-02
 */
public class ReporteDTO {
    private int id;
    private String descripcion;
    private int tipo;
  

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }
    
    
}
