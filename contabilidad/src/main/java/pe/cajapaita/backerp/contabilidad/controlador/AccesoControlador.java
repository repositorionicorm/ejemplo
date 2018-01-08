/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.controlador;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.servicio.IAccesoServicio;

/**
 *
 * @author hnole
 */
@Controller
@RequestMapping("acceso")
public class AccesoControlador {

    @Autowired
    private IAccesoServicio accesoServicio;
    
    private final Logger logger = Logger.getLogger(AccesoControlador.class);

    @RequestMapping(value = "usuario", method = RequestMethod.GET)
    public ResponseEntity<UsuarioDTO> usuario() {
        List<UsuarioDTO> listausuario = new ArrayList<>();
        try {
            listausuario = accesoServicio.traerUsuarios();
            return new ResponseEntity(listausuario, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return new ResponseEntity(listausuario, HttpStatus.CONFLICT);
        }
    }
}
