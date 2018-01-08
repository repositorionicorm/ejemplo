/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.infraestructura;

/**
 *
 * @author dev-out-02
 */
public enum CuentasOrdenEnum {
    
    CUENTA1(71,72),
    CUENTA2(81,82),
    CUENTA3(83,84);
    
    
    private int cuentaDebe;
    private int cuentaHaber;
    
    private CuentasOrdenEnum(int cuentaDebe,int cuentaHaber){
        this.cuentaDebe=cuentaDebe;
        this.cuentaHaber=cuentaHaber;
    }

    public int getCuentaDebe() {
        return cuentaDebe;
    }

    public void setCuentaDebe(int cuentaDebe) {
        this.cuentaDebe = cuentaDebe;
    }

    public int getCuentaHaber() {
        return cuentaHaber;
    }

    public void setCuentaHaber(int cuentaHaber) {
        this.cuentaHaber = cuentaHaber;
    }
    
    
    

    
}
