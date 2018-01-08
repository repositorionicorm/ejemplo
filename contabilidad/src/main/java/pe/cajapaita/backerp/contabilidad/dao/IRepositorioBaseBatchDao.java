package pe.cajapaita.backerp.contabilidad.dao;

import java.util.Date;
import java.util.List;
import pe.cajapaita.backerp.contabilidad.dto.CuentaBatchDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Integracion;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;

/**
 *
 * @author dev-out-02
 */
public interface IRepositorioBaseBatchDao{
    void insertarBatchSaldo(List<Saldo> listaSaldos) throws ExcepcionNegocio;
    void insertarBatchIntegracion(List<Integracion> listaIntegracion)throws ExcepcionNegocio;
    
    CuentaBatchDTO traerCuentaPorId(int id);
    CuentaBatchDTO traerCuentaPorCuenta(String cuenta);
    List<DetalleDTO> traerListaDetalle(int cuenta,Date fechaInicial,Date fechaFinal, int estadoId) throws ExcepcionNegocio;
    
}
