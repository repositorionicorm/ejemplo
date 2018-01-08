package pe.cajapaita.backerp.contabilidad.consulta;

/**
 *
 * @author dev-out-02
 */
public class RestriccionNoIgual {
    private String propiedad;
    private Object value;

    public RestriccionNoIgual(String propiedad, Object value) {
        this.propiedad = propiedad;
        this.value = value;
    }

    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
}
