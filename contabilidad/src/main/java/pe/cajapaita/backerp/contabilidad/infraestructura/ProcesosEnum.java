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
public enum ProcesosEnum {
    CIERRE(false);
    
    private boolean procesando;
    
    private ProcesosEnum(boolean procesando){
        this.procesando=procesando;
    }

    public boolean estaProcesando() {
        return procesando;
    }

    public void setProcesando(boolean procesando) {
        this.procesando = procesando;
    }
    
}
