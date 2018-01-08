/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author hnole
 */
public interface IMayorizacionServicio {
    void mayorizar() throws ExcepcionNegocio;
}
