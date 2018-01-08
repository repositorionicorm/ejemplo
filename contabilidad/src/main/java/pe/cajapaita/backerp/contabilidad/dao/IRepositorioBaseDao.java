/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.dao;

import java.util.List;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;

/**
 *
 * @author hnole
 * @param <Entidad>
 */
public interface IRepositorioBaseDao<Entidad> {
    Entidad traerPorId(Class<Entidad> claseEntidad, int id) throws ExcepcionNegocio;
    List traerTodo(Class<Entidad> claseEntidad, Consulta consulta, Class claseDTO) throws ExcepcionNegocio;
    List traerTodo(Class<Entidad> claseEntidad,Consulta consulta) throws ExcepcionNegocio;
    List traerAgrupado(Class<Entidad> claseEntidad, Consulta consulta, Class claseDTO) throws ExcepcionNegocio;
    Object traerUnico(Class<Entidad> claseEntidad, Consulta consulta, Class claseDTO) throws ExcepcionNegocio;
    Object traerUnico(Class<Entidad> claseEntidad, Consulta consulta) throws ExcepcionNegocio;
    Object traerMaximo(Class<Entidad> claseEntidad, Consulta consulta)throws ExcepcionNegocio;
    int contar(Class<Entidad> claseEntidad, Consulta parametro) throws ExcepcionNegocio;
    void guardar(Entidad entidad) throws ExcepcionNegocio;
    void guardar(List<Entidad> entidad) throws ExcepcionNegocio;
    void eliminar(Entidad entidad) throws ExcepcionNegocio;
    void ejecutarConsulta(String consulta) throws ExcepcionNegocio;
}
