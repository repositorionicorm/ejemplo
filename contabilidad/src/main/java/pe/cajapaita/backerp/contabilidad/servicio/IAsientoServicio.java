/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio;

import java.math.BigDecimal;
import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;

/**
 *
 * @author hnole
 */
public interface IAsientoServicio {

    List<AsientoDTO> traerAsientos(int periodoId, int tipoAsientoId, int usuarioId, int procedenciaId, int pagina,int numero,int agenciaId,UsuarioDTO usuarioDTO) throws ExcepcionNegocio;    
    List<DetalleDTO> traerDetalle(int asientoId) throws ExcepcionNegocio;
    AsientoDTO traerAsiento(int asientoId) throws ExcepcionNegocio;
    
    void eliminar(int asientoId, int usuarioId) throws ExcepcionNegocio;
    void eliminarIntegracion(int asientoId, int usuarioId) throws ExcepcionNegocio;
    void guardar(AsientoDTO asientoDTO, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio;
    void guardar(List<AsientoDTO> listaAsientos, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio;
    void renumerarAsientos() throws ExcepcionNegocio;    
    void grabarTipoCambio(BigDecimal nuevaTC) throws ExcepcionNegocio;
    void integrarTipoCambio(BigDecimal nuevaTC,AsientoDTO asientoDTO, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio;
    AsientoDTO cambiarTC(BigDecimal monto) throws ExcepcionNegocio;
    
    boolean cuentaTieneMovimientos(int cuentaId) throws ExcepcionNegocio;    
    boolean existenAsientos(String fecha, int tipoAsientoId, int procedenciaId,int agenciaId) throws ExcepcionNegocio;
    int traerTotalPaginas();
    String ultimaFechaRenumeracion() throws ExcepcionNegocio;
    String ultimaFechaBalance() throws ExcepcionNegocio;
    List<Integer> traerNumeroAsientos(List<Integer> asientoId) throws ExcepcionNegocio;
    
    
}
