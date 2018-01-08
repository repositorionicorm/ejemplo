/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;

/**
 *
 * @author hnole
 */
public class CuentaDTO {

    private int id;
    private String cuenta;
    private String descripcion;
    private String tipoCuentaDescripcion;
    private String esSbs;
    private int padreId;
    private String esAnalitica;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipoCuentaDescripcion() {
        return tipoCuentaDescripcion;
    }

    public void setTipoCuentaDescripcion(String tipoCuentaDescripcion) {
        this.tipoCuentaDescripcion = tipoCuentaDescripcion;
    }

    public String getEsSbs() {
        return esSbs;
    }

    public void setEsSbs(String esSbs) {
        this.esSbs = esSbs;
    }

    public int getPadreId() {
        return padreId;
    }

    public void setPadreId(int padreId) {
        this.padreId = padreId;
    }

    public String getEsAnalitica() {
        return esAnalitica;
    }

    public void setEsAnalitica(String esAnalitica) {
        this.esAnalitica = esAnalitica;
    }

    public void validarPropiedades() throws ExcepcionNegocio {

        if (Helper.esNuloVacio(this.cuenta)) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_VACIA, Mensaje.TIPO_ALERTA);
        }

        if (!Helper.esNumerico(this.cuenta)) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_SOLO_DIGITOS, Mensaje.TIPO_ALERTA);
        }

        if (!Helper.esPar(this.cuenta)) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_SOLO_PARES, Mensaje.TIPO_ALERTA);
        }

        if (this.cuenta.length() >= 4) {            
            if (!this.cuenta.substring(2, 3).trim().equals("0")) {
                throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_TERCER_DIGITO_CERO, Mensaje.TIPO_ALERTA);
            }
        }

        if (Helper.esNuloVacio(this.descripcion)) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_DESCRIPCION_VACIA, Mensaje.TIPO_ALERTA);
        }
    }
}
