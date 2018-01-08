
package pe.cajapaita.backerp.contabilidad.dto;

import java.math.BigDecimal;

/**
 *
 * @author dev-out-02
 */
public class ReporteVariosDTO {
    private String descripcion;
    private BigDecimal monto;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }
    
}
