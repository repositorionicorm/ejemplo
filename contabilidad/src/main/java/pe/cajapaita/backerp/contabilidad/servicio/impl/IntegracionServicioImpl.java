/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.IntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Estado;
import pe.cajapaita.backerp.contabilidad.entidad.Integracion;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.MonedaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IWebServicio;

/**
 *
 * @author hnole
 */
@Service
@Transactional
public class IntegracionServicioImpl implements IIntegracionServicio {

    @Autowired
    private IRepositorioBaseDao<Integracion> integracionDAO;

    @Autowired
    private IAsientoServicio asientoServicio;

    @Autowired
    private IRepositorioBaseDao<Estado> estadoDAO;

    @Autowired
    private IRepositorioBaseDao<Procedencia> procedenciaDAO;

    @Autowired
    private IRepositorioBaseDao<TipoAsiento> tipoAsientoDAO;

    @Autowired
    private ICuentaServicio cuentaServicio;

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IRepositorioBaseDao<Periodo> periodoDAO;
    
    @Autowired
    private IWebServicio webServicio;

    private final Logger logger = Logger.getLogger(IntegracionServicioImpl.class);

    @Override
    public List<IntegracionDTO> traerIntegraciones(int tipoAsientoId, int agenciaId, ProcedenciaEnum procedenciaEnum) throws ExcepcionNegocio {
        try {
            PeriodoDTO periodoVigenteDTO = utilitarioServicio.traerPeriodoVigente();
            Periodo periodo= periodoDAO.traerPorId(Periodo.class, periodoVigenteDTO.getId());
            Date fechaFin;
            
            if(periodo.getFechaFinal().after(periodo.getFechaIntegracion())){
                fechaFin=periodo.getFechaFinal();
            }
            else{
                fechaFin=periodo.getFechaIntegracion();
            }
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("agencia", "agencia"));

            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha",
                    periodoVigenteDTO.getFechaInicial(), fechaFin));

            consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", procedenciaEnum.ordinal()));

            if (tipoAsientoId > 0) {
                consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", tipoAsientoId));
            }
            if (agenciaId > 0) {
                consulta.agregaRestriccionIgual(new RestriccionIgual("agencia.id", agenciaId));
            }

            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("procedencia.id", "procedenciaId"));
            consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.id", "tipoAsientoId"));
            consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.descripcion", "tipoAsientoDescripcion"));
            consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
            consulta.agregaEquivalencia(new Equivalencia("observacion", "observacion"));
            consulta.agregaEquivalencia(new Equivalencia("estado.id", "estadoId"));
            consulta.agregaEquivalencia(new Equivalencia("estado.descripcion", "estadoDescripcion"));
            consulta.agregaEquivalencia(new Equivalencia("estado.color", "estadoColor"));
            consulta.agregaEquivalencia(new Equivalencia("agencia.abreviatura", "agenciaAbreviatura"));
            consulta.agregaEquivalencia(new Equivalencia("agencia.id", "agenciaId"));

            consulta.agregaOrdenAscendente("fecha");
            consulta.agregaOrdenAscendente("tipoAsiento.descripcion");
            consulta.agregaOrdenAscendente("agencia.abreviatura");

            List<IntegracionDTO> integracionesDTO = integracionDAO.traerTodo(Integracion.class, consulta, IntegracionDTO.class);
            if (integracionesDTO.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_NO_ENCONTRADAS, Mensaje.TIPO_ALERTA);
            }

            return integracionesDTO;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void integrarAsiento(int tipoAsientoId, String fecha, int agenciaId, int usuarioId, ProcedenciaEnum procedenciaEnum) throws ExcepcionNegocio {
        if (asientoServicio.existenAsientos(fecha, tipoAsientoId, procedenciaEnum.ordinal(), agenciaId)) {
            throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_ASIENTO_YA_INTEGRADO, Mensaje.TIPO_ALERTA);
        }

        Integracion integracion = this.traerIntegracion(tipoAsientoId, fecha, agenciaId, procedenciaEnum);
        if (integracion.getEstado().getId() != EstadoEnum.PENDIENTE.ordinal()) {
            throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_NO_PENDIENTE, Mensaje.TIPO_ALERTA);
        }
        try {

            List<DetalleSysoneDTO> asientoSysone = new ArrayList<>();
            String mensaje = "";
            String tipoMensaje = "";
            try {
                if (procedenciaEnum == ProcedenciaEnum.SYSONE) {
                    asientoSysone = webServicio.traerAsientoSysone(tipoAsientoId, fecha, agenciaId);
                } else {
                    asientoSysone = webServicio.traerAsientoProvision(tipoAsientoId, fecha);
                }
            } catch (ExcepcionNegocio en) {
                mensaje = en.getMessage();
                tipoMensaje = en.getTipoMensaje();
            }
            List<AsientoDTO> asientosDTO = new ArrayList<>();
            String idGenerados = "";
            if (mensaje.isEmpty()) {
                Map respuestaAsiento = this.validacionesAsiento(asientoSysone, tipoAsientoId, fecha, agenciaId);
                asientosDTO = (List<AsientoDTO>) (Object) respuestaAsiento.get("asientos");
                mensaje = (String) respuestaAsiento.get("mensaje");
                tipoMensaje = (String) respuestaAsiento.get("tipo");

            }

            boolean exito = false;

            if (asientosDTO.size() > 0) {
                try {
                    asientoServicio.guardar(asientosDTO, procedenciaEnum, usuarioId);
                } catch (ExcepcionNegocio excepcionNegocio) {
                    tipoMensaje = excepcionNegocio.getTipoMensaje();
                    if (tipoMensaje.equals(Mensaje.TIPO_EXITO)) {
                        exito = true;
                        idGenerados = excepcionNegocio.getIdGenerados();
                    }
                    mensaje = mensaje + excepcionNegocio.getMessage();
                }
            }

            integracion.setObservacion(mensaje);
            if (exito) {
                integracion.setAsientoId(idGenerados);
                integracion.setEstado(estadoDAO.traerPorId(Estado.class, EstadoEnum.INTEGRADO.ordinal()));
            }
            integracionDAO.guardar(integracion);

            throw new ExcepcionNegocio(mensaje, tipoMensaje);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getCause() + " - " + ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<Integer> traerNumeroVoucher(int integracionId) throws ExcepcionNegocio {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("id", integracionId));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.INTEGRADO.ordinal()));
            consulta.agregaEquivalencia(new Equivalencia("asientoId", "asientoId"));

            IntegracionDTO integracion = (IntegracionDTO) integracionDAO.traerUnico(Integracion.class, consulta, IntegracionDTO.class);

            if (integracion == null) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }
            String listaAsientos = integracion.getAsientoId();
            String[] asientos = listaAsientos.split(",");

            List<Integer> asientosId = new ArrayList<>();
            for (String asiento : asientos) {
                asientosId.add(Integer.parseInt(asiento));
            }

            List<Integer> numerosAsientos = asientoServicio.traerNumeroAsientos(asientosId);

            return numerosAsientos;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void cambiarEstado(int id, int usuarioId) throws ExcepcionNegocio {
        try {
            Integracion integracion = integracionDAO.traerPorId(Integracion.class, id);

            if (integracion == null) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }
            PeriodoDTO periodoDTO = utilitarioServicio.traerPeriodoVigente();
            if (!Helper.between(integracion.getFecha(), periodoDTO.getFechaInicial(), periodoDTO.getFechaLimite())) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_OTRO_PERIODO, Mensaje.TIPO_ALERTA);
            }

            Estado estado = null;
            if (integracion.getEstado().getId() == EstadoEnum.PENDIENTE.ordinal()) {
                estado = estadoDAO.traerPorId(Estado.class, EstadoEnum.INACTIVO.ordinal());
            }
            if (integracion.getEstado().getId() == EstadoEnum.INACTIVO.ordinal()) {
                estado = estadoDAO.traerPorId(Estado.class, EstadoEnum.PENDIENTE.ordinal());
            }
            if (integracion.getEstado().getId() == EstadoEnum.INTEGRADO.ordinal()) {
                this.eliminarAsientos(integracion, usuarioId);
                estado = estadoDAO.traerPorId(Estado.class, EstadoEnum.PENDIENTE.ordinal());
                integracion.setObservacion("");
                integracion.setAsientoId("");
            }
            if (estado == null) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_ESTADO_NO_PERMITIDO, Mensaje.TIPO_ALERTA);
            }
            integracion.setEstado(estado);
            integracionDAO.guardar(integracion);

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados">
    private Integracion traerIntegracion(int tipoAsientoId, String fecha, int agenciaId, ProcedenciaEnum procedenciaEnum) throws ExcepcionNegocio {
        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("procedencia", "procedencia"));
        consulta.agregaAlias(new Alias("agencia", "agencia"));

        consulta.agregaRestriccionIgual(new RestriccionIgual("fecha", Helper.convertirAFecha(fecha)));
        consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", tipoAsientoId));
        consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", procedenciaEnum.ordinal()));
        consulta.agregaRestriccionIgual(new RestriccionIgual("agencia.id", agenciaId));
        Integracion integracion = (Integracion) integracionDAO.traerUnico(Integracion.class, consulta);
        if (integracion == null) {
            integracion = new Integracion();
            integracion.setEstado(estadoDAO.traerPorId(Estado.class, EstadoEnum.PENDIENTE.ordinal()));
            integracion.setFecha(Helper.convertirAFecha(fecha));
            integracion.setObservacion("");
            integracion.setProcedencia(procedenciaDAO.traerPorId(Procedencia.class, procedenciaEnum.ordinal()));
            integracion.setTipoAsiento(tipoAsientoDAO.traerPorId(TipoAsiento.class, tipoAsientoId));
        }

        return integracion;
    }

    private Map validacionesAsiento(List<DetalleSysoneDTO> asientoSysone, int tipoAsientoId, String fecha, int agenciaId) {
        List<AsientoDTO> asientosDTO = new ArrayList<>();
        Map map = new HashMap();

        try {

            List<Integer> monedasActivas = utilitarioServicio.traerListaMonedaActiva().stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
            List<String> monedasAVerificarString = asientoSysone.stream().map(x -> x.getMoneda()).distinct().collect(Collectors.toList());
            List<Integer> monedasAVerificarInteger = new ArrayList<>();
            char monedaConsolidada = '0';
            for (String monedaString : monedasAVerificarString) {
                monedasAVerificarInteger.add(Integer.parseInt(monedaString));
            }

            if (!Helper.existeDato(monedasActivas, monedasAVerificarInteger)) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_MONEDA_INVALIDA, Mensaje.TIPO_ALERTA);
            }

            boolean generarDosAsientos = true;

            BigDecimal totalDebeAsiento = asientoSysone.stream().map(x -> x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalHaberAsiento = asientoSysone.stream().map(x -> x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalDebeAsiento.compareTo(totalHaberAsiento) != 0) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA, Mensaje.TIPO_ALERTA);
            }

            for (String moneda : monedasAVerificarString) {
                totalDebeAsiento = asientoSysone.stream().filter(x -> x.getMoneda().equals(moneda)).map(x -> x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
                totalHaberAsiento = asientoSysone.stream().filter(x -> x.getMoneda().equals(moneda)).map(x -> x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalDebeAsiento.compareTo(totalHaberAsiento) != 0) {
                    generarDosAsientos = false;
                    break;
                }
            }

            if (!generarDosAsientos) {                
                AsientoDTO asientoDTO=this.armarAsientos(MonedaEnum.DOLARES.ordinal(), tipoAsientoId, fecha, agenciaId, asientoSysone, monedaConsolidada);
                asientosDTO.add(asientoDTO);

                map.put("asientos", asientosDTO);
                map.put("mensaje", "");
                map.put("tipo", "");
                return map;

            }

            for (String monedaId : monedasAVerificarString) {
                List<DetalleSysoneDTO> detalleSysone = asientoSysone.stream().filter(x -> x.getMoneda().equals(monedaId)).collect(Collectors.toList());
                
               AsientoDTO asientoDTO =  this.armarAsientos(Integer.parseInt(monedaId), tipoAsientoId, fecha, agenciaId, detalleSysone, monedaConsolidada);
                asientosDTO.add(asientoDTO);
            }
            map.put("asientos", asientosDTO);
            map.put("mensaje", "");
            map.put("tipo", "");
            return map;
        } catch (ExcepcionNegocio excepcionNegocio) {
            asientosDTO = new ArrayList<>();
            map.put("asientos", asientosDTO);
            map.put("mensaje", excepcionNegocio.getMessage());
            map.put("tipo", excepcionNegocio.getTipoMensaje());
            return map;
        } catch (Exception ex) {
            asientosDTO = new ArrayList<>();
            logger.error(ex.getMessage());
            map.put("asientos", asientosDTO);
            map.put("mensaje", Mensaje.ERROR_GENERAL);
            map.put("tipo", Mensaje.TIPO_ERROR);
            return map;
        }
    }

    private void eliminarAsientos(Integracion integracion, int usuarioId) throws ExcepcionNegocio {
        String listaAsientos = integracion.getAsientoId();
        String[] asientos = listaAsientos.split(",");

        for (String asiento : asientos) {
            asientoServicio.eliminarIntegracion(Integer.parseInt(asiento), usuarioId);
        }
    }

    private AsientoDTO armarAsientos(int monedaId, int tipoAsientoId, String fecha, int agenciaId, List<DetalleSysoneDTO> listaDetalle,char monedaConsolidada) throws ExcepcionNegocio{
        AsientoDTO asientoDTO = new AsientoDTO();
        asientoDTO.setMonedaId(monedaId);
        asientoDTO.setTipoAsientoId(tipoAsientoId);
        asientoDTO.setFechaString(fecha);
        asientoDTO.setGlosa(utilitarioServicio.traerListaTipoAsiento().stream().filter(x -> x.getId() == tipoAsientoId).findFirst().get().getGlosa());
        asientoDTO.setAgenciaId(agenciaId);

        List<DetalleDTO> detallesDTO = new ArrayList<>();
        for (DetalleSysoneDTO detalleSysoneDTO : listaDetalle) {
            DetalleDTO detalleDTO = new DetalleDTO();
            detalleDTO.setDebe(detalleSysoneDTO.getDebe());
            detalleDTO.setHaber(detalleSysoneDTO.getHaber());
            detalleDTO.setCuentaId(cuentaServicio.traerCuentaId(
                    Helper.transformaCuentaMoneda(detalleSysoneDTO.getCuenta(), monedaConsolidada)));

            detalleDTO.setMonedaId(Integer.parseInt(detalleSysoneDTO.getMoneda()));
            detallesDTO.add(detalleDTO);
        }
        asientoDTO.setDetalles(detallesDTO);

        return asientoDTO;
    }
    //</editor-fold>

}
