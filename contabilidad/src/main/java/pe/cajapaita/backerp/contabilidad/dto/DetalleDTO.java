/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import java.math.BigDecimal;
import java.util.Date;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;

/**
 *
 * @author dev-out-02
 */
public class DetalleDTO {

    private int id;
    private int cuentaId;
    private String cuentaCuenta;
    private BigDecimal debe;
    private BigDecimal haber;
    private String signoDebe;
    private String signoHaber;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;
    private int tipoCuentaId;
    private Date fechaAsiento;
    private int monedaId;

    public DetalleDTO(int cuentaId, String cuentaCuenta, BigDecimal debe, BigDecimal haber,int monedaId) {
        this.cuentaId = cuentaId;
        this.cuentaCuenta = cuentaCuenta;
        this.debe = debe;
        this.haber = haber;
        this.monedaId=monedaId;
    }

    public DetalleDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCuentaId() {
        return cuentaId;
    }

    public void setCuentaId(int cuentaId) {
        this.cuentaId = cuentaId;
    }
    
    
    
    public String getCuentaCuenta() {
        return cuentaCuenta;
    }

    public void setCuentaCuenta(String cuentaCuenta) {
        this.cuentaCuenta = cuentaCuenta;
    }

    public BigDecimal getDebe() {
        return debe;
    }

    public void setDebe(BigDecimal debe) {
        this.debe = debe;
    }

    public BigDecimal getHaber() {
        return haber;
    }

    public void setHaber(BigDecimal haber) {
        this.haber = haber;
    }

    public String getSignoDebe() {
        return signoDebe;
    }

    public void setSignoDebe(String signoDebe) {
        this.signoDebe = signoDebe;
    }

    public String getSignoHaber() {
        return signoHaber;
    }

    public void setSignoHaber(String signoHaber) {
        this.signoHaber = signoHaber;
    }

    public BigDecimal getTotalDebe() {
        return totalDebe;
    }

    public void setTotalDebe(BigDecimal totalDebe) {
        this.totalDebe = totalDebe;
    }

    public BigDecimal getTotalHaber() {
        return totalHaber;
    }

    public void setTotalHaber(BigDecimal totalHaber) {
        this.totalHaber = totalHaber;
    }

    public int getTipoCuentaId() {
        return tipoCuentaId;
    }

    public void setTipoCuentaId(int tipoCuentaId) {
        this.tipoCuentaId = tipoCuentaId;
    }

    public Date getFechaAsiento() {
        return fechaAsiento;
    }

    public void setFechaAsiento(Date fechaAsiento) {
        this.fechaAsiento = fechaAsiento;
    }

    public int getMonedaId() {
        return monedaId;
    }

    public void setMonedaId(int monedaId) {
        this.monedaId = monedaId;
    }
    
    
    

    public void validaPropiedades() throws ExcepcionNegocio {
        if (this.cuentaId == 0 && Helper.esNuloVacio(this.cuentaCuenta)) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_VACIA, Mensaje.TIPO_ALERTA);
        }

        if (this.debe.signum() == -1 || this.haber.signum() == -1) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_DEBE_HABER_INVALIDO, Mensaje.TIPO_ALERTA);
        }
        
        if (this.debe.signum() == 0 && this.haber.signum() == 0) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_DEBE_HABER_INVALIDO, Mensaje.TIPO_ALERTA);
        }

    }

}
