/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import pe.cajapaita.backerp.contabilidad.dto.DetalleIntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.IntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TransaccionDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Integracion;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionProvisionServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionSysoneServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IWebServicio;

/**
 *
 * @author hnole
 */
@Service
@Transactional
public class IntegracionSysoneServicioImpl extends IntegracionServicioImpl implements IIntegracionSysoneServicio {

    @Autowired
    private IWebServicio webServicio;

    @Autowired
    private IRepositorioBaseDao<Integracion> integracionDAO;

    @Autowired
    private IIntegracionProvisionServicio integracionProvisionServicio;

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IRepositorioBaseDao<Periodo> periodoDAO;

    private final Logger logger = Logger.getLogger(IntegracionSysoneServicioImpl.class);

    @Override
    public List<IntegracionDTO> traerIntegraciones(int tipoAsientoId, int agenciaId) throws ExcepcionNegocio {
        return super.traerIntegraciones(tipoAsientoId, agenciaId, ProcedenciaEnum.SYSONE);
    }

    @Override
    public List<DetalleSysoneDTO> traerAsiento(int tipoAsientoId, String fecha, int agenciaId) throws ExcepcionNegocio {
        return webServicio.traerAsientoSysone(tipoAsientoId, fecha, agenciaId);
    }

    @Override
    public void integrarAsiento(int tipoAsientoId, String fecha, int agenciaId, int usuarioId) throws ExcepcionNegocio {
        super.integrarAsiento(tipoAsientoId, fecha, agenciaId, usuarioId, ProcedenciaEnum.SYSONE);
    }

    @Override
    public List<DetalleIntegracionDTO> traerDetalleIntegracion(int tipoAsientoId, String fecha, String cuenta, char moneda, int agenciaId) throws ExcepcionNegocio {
        try {
            String cuentaEnMoneda = Helper.transformaCuentaMoneda(cuenta, moneda);
            List<DetalleIntegracionDTO> detalleIntegracion = webServicio.traerDetalleIntegracionSysone(fecha, cuentaEnMoneda, tipoAsientoId, agenciaId);
            if (detalleIntegracion.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO, Mensaje.TIPO_ALERTA);
            }

            return detalleIntegracion;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<TransaccionDTO> traerDetalleTransaccion(BigDecimal transaccion) throws ExcepcionNegocio {
        try {
            List<TransaccionDTO> transacciones = webServicio.traerDetalleTransaccion(transaccion);
            if (transacciones.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_TRANSACCION_NO_ENCONTRADA, Mensaje.TIPO_ALERTA);
            }

            return transacciones;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void ejecutarIntegracionAutomatica() {
        try {
            PeriodoDTO periodoDTO = utilitarioServicio.traerPeriodoVigente();

            Periodo periodo = periodoDAO.traerPorId(Periodo.class, periodoDTO.getId());
            Calendar fecha = Calendar.getInstance();
            fecha.setTime(periodo.getFechaIntegracion());
            Date fechaFormato;
            String fechaString = Helper.convertirAFecha(fecha.getTime());

            if (!Helper.between(fecha.getTime(), periodo.getFechaInicial(),periodoDTO.getFechaLimite())) {  
                logger.error(Mensaje.INTEGRACION_AUTOMATICA_NO_CORRESPONDE_PERIODO + " - " + fechaString);
            } else {
                int idUsuario = utilitarioServicio.traerIdUsuarioIntegracion();
                fechaFormato = Helper.convertirAFecha(fechaString);

                List<IntegracionDTO> listaIntegracion = this.traerIntegracionesPendientes(ProcedenciaEnum.SYSONE.ordinal(), fechaFormato);

                listaIntegracion.stream().forEach((integrar) -> {
                    try {
                        this.integrarAsiento(integrar.getTipoAsientoId(), fechaString, integrar.getAgenciaId(), idUsuario);
                    } catch (ExcepcionNegocio ex) {
                    }
                });

                Calendar finMes = Calendar.getInstance();
                finMes.setTime(fechaFormato);
                int ultimoDiaMes = finMes.getActualMaximum(Calendar.DAY_OF_MONTH);
                int diaActual = finMes.get(Calendar.DAY_OF_MONTH);

                if (ultimoDiaMes == diaActual) {
                    List<IntegracionDTO> listaProvision = this.traerIntegracionesPendientes(ProcedenciaEnum.PROVISION.ordinal(), fechaFormato);
                    listaProvision.stream().forEach((integrar) -> {
                        try {
                            integracionProvisionServicio.integrarAsiento(integrar.getTipoAsientoId(), fechaString, integrar.getAgenciaId(), idUsuario);
                        } catch (ExcepcionNegocio ex) {
                        }
                    });
                }
                fecha.add(Calendar.DAY_OF_YEAR, 1);
                periodo.setFechaIntegracion(fecha.getTime());
                periodoDAO.guardar(periodo);
            }

        } catch (ExcepcionNegocio en) {
            logger.error(en.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
//<editor-fold defaultstate="collapsed" desc="metodos privados"> 

    private List<IntegracionDTO> traerIntegracionesPendientes(int tipoProcedencia, Date fecha) {
        List<IntegracionDTO> listaIntegracion = new ArrayList<>();
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("agencia", "agencia"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("fecha", fecha));
            consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", tipoProcedencia));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));
            consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.id", "tipoAsientoId"));
            consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
            consulta.agregaEquivalencia(new Equivalencia("agencia.id", "agenciaId"));

            listaIntegracion = integracionDAO.traerTodo(Integracion.class, consulta, IntegracionDTO.class);
        } catch (ExcepcionNegocio en) {
            logger.error(en.getMessage());
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return listaIntegracion;
    }
    //</editor-fold>
}
