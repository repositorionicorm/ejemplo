/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;

/**
 *
 * @author hnole
 */
public interface IAccesoServicio {    
    UsuarioDTO traerUsuario(String identificador);
    List<UsuarioDTO> traerUsuarios();
}
