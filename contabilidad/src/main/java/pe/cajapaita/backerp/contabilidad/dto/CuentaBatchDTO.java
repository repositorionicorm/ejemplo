/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

/**
 *
 * @author dev-out-02
 */
public class CuentaBatchDTO {

    private int id;
    private String esAnalitica;
    private int estadoId;
   private String cuenta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEsAnalitica() {
        return esAnalitica;
    }

    public void setEsAnalitica(String esAnalitica) {
        this.esAnalitica = esAnalitica;
    }

    public int getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(int estadoId) {
        this.estadoId = estadoId;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }
    

}
