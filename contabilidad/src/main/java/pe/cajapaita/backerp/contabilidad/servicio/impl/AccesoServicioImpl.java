/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Usuario;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAccesoServicio;

/**
 *
 * @author hnole
 */
@Service
@Transactional
public class AccesoServicioImpl implements IAccesoServicio {

    @Autowired    
    private IRepositorioBaseDao<Usuario> usuarioDao;
    
    private final Logger logger = Logger.getLogger(AccesoServicioImpl.class);

    @Override
    public UsuarioDTO traerUsuario(String identificador) {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("agencia", "agencia"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("identificador", identificador.trim().toUpperCase()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("nombre", "nombre"));
            consulta.agregaEquivalencia(new Equivalencia("identificador", "identificador")); 
            consulta.agregaEquivalencia(new Equivalencia("agencia.id", "agenciaId"));
            consulta.agregaEquivalencia(new Equivalencia("editar", "editar"));

            return (UsuarioDTO) usuarioDao.traerUnico(Usuario.class, consulta, UsuarioDTO.class);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    @Override
    public List<UsuarioDTO> traerUsuarios() {        
        try {
            Consulta consulta = new Consulta();
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("nombre", "nombre"));
            consulta.agregaEquivalencia(new Equivalencia("identificador", "identificador"));
            consulta.agregaOrdenAscendente("identificador");
            
            return usuarioDao.traerTodo(Usuario.class, consulta, UsuarioDTO.class);            
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

}
