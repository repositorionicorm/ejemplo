
package pe.cajapaita.backerp.contabilidad.dto;

import java.util.Date;

/**
 *
 * @author dev-out-02
 */
public class AsientoRenumeracionDTO {
    private int id;
    private int numero;
    private Date fecha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
