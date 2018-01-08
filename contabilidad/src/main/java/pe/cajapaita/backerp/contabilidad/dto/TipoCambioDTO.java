package pe.cajapaita.backerp.contabilidad.dto;

import java.math.BigDecimal;

/**
 *
 * @author dev-out-02
 */
public class TipoCambioDTO {
    private BigDecimal tipoCambio;
    private String periodoDescripcion;

    public BigDecimal getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(BigDecimal tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public String getPeriodoDescripcion() {
        return periodoDescripcion;
    }

    public void setPeriodoDescripcion(String periodoDescripcion) {
        this.periodoDescripcion = periodoDescripcion;
    }
    
    
}
