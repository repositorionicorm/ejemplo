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
public enum MesEnum {
    ENERO(0),
    FEBRERO(1),
    MARZO(2),
    ABRIL(3),
    MAYO(4),
    JUNIO(5),
    JULIO(6),
    AGOSTO(7),
    SETIEMBRE(8),
    OCTUBRE(9),
    NOVIEMBRE(10),
    DICIEMBRE(11);
    
    private int id;
    private MesEnum(int id) {    
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
}
