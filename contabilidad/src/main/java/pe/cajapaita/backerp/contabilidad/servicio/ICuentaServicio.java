/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.CuentaDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author hnole
 */
public interface ICuentaServicio {
    List<CuentaDTO> traerPorCuenta(String cuenta, int pagina) throws ExcepcionNegocio;
    List<CuentaDTO> traerHijas(int cuentaPadreId, int pagina) throws ExcepcionNegocio;
    List<CuentaDTO> traerPrimerNivel() throws ExcepcionNegocio;    
    
    void guardar(CuentaDTO cuentaDTO, int usuarioId) throws ExcepcionNegocio;
    void actualizar(CuentaDTO cuentaDTO) throws ExcepcionNegocio;
    void eliminar(int cuentaId) throws ExcepcionNegocio;
    
    int traerTotalPaginas();    
    int traerCuentaId(String numeroCuenta) throws ExcepcionNegocio;
}