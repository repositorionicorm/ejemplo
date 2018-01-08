/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dto;

/**
 *
 * @author hnole
 */
public class TipoCuentaDTO {
    private int id;
    private String descripcion;
    private String signoDebe;
    private String signoHaber;

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
    
    
}
