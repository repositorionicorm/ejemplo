package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import pe.cajapaita.backerp.contabilidad.consulta.Maximo;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionLike;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionNoIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseBatchDao;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AgenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.AsientoRenumeracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.EstadoCierreDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.SaldoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Agencia;
import pe.cajapaita.backerp.contabilidad.entidad.Asiento;
import pe.cajapaita.backerp.contabilidad.entidad.Clave;
import pe.cajapaita.backerp.contabilidad.entidad.Cuenta;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Estado;
import pe.cajapaita.backerp.contabilidad.entidad.Integracion;
import pe.cajapaita.backerp.contabilidad.entidad.Moneda;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.infraestructura.CuentasOrdenEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcesosEnum;
import pe.cajapaita.backerp.contabilidad.servicio.ICierreServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author dev-out-02
 */
@Service
public class CierreServicioImpl implements ICierreServicio {

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IRepositorioBaseDao<Periodo> periodoDAO;

    @Autowired
    private IRepositorioBaseDao<Asiento> asientoDAO;

    @Autowired
    private IRepositorioBaseDao<Estado> estadoDAO;

    @Autowired
    private IRepositorioBaseDao<Integracion> integracionDAO;

    @Autowired
    private IRepositorioBaseDao<Saldo> saldoDAO;

    @Autowired
    private IRepositorioBaseBatchDao repositorioBaseBatchDao;

    @Autowired
    private IRepositorioBaseDao<Clave> claveDAO;

    @Autowired
    private IRepositorioBaseDao<Detalle> detalleDAO;

    private final Logger logger = Logger.getLogger(CierreServicioImpl.class);

    @Transactional
    @Override
    public List<EstadoCierreDTO> validacionCierre() throws ExcepcionNegocio {
        List<EstadoCierreDTO> estadoCierre = new ArrayList<>();
        PeriodoDTO periodoVigente = utilitarioServicio.traerPeriodoVigente();
        Periodo periodoActual = null;
        try {
            periodoActual = periodoDAO.traerPorId(Periodo.class, periodoVigente.getId());
            if (periodoActual == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }
            estadoCierre.add(this.verificarRenumeracion(periodoActual));
            estadoCierre.add(this.verificarBalance(periodoActual));
            estadoCierre.add(this.verificarAsientosSysone(periodoActual));
            estadoCierre.add(this.verificarAsientosProvision(periodoActual));
            estadoCierre.add(this.verificarAsientosActualizacionME(periodoActual));
            estadoCierre.add(this.verificarAsientosCuentasOrden(periodoActual));
            return estadoCierre;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage() + " - " + ex.toString());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = ExcepcionNegocio.class)
    public void ejecutarCierre() throws ExcepcionNegocio {
        try {
            if (ProcesosEnum.CIERRE.estaProcesando()) {
                throw new ExcepcionNegocio(Mensaje.CIERRE_PROCESANDO, Mensaje.TIPO_ALERTA);
            }
            List<EstadoCierreDTO> listValidacion = this.validacionCierre();

            if (listValidacion.stream().anyMatch(p -> p.isEstado() == false)) {
                throw new ExcepcionNegocio(Mensaje.CIERRE_NO_PUEDE_PROCESAR, Mensaje.TIPO_ALERTA);
            }
            ProcesosEnum.CIERRE.setProcesando(true);
            Periodo periodoActual = periodoDAO.traerPorId(Periodo.class, utilitarioServicio.traerPeriodoVigente().getId());
            Periodo nuevoPeriodo = this.activarNuevoPeriodo(periodoActual);
            
            periodoDAO.guardar(nuevoPeriodo);

            periodoActual.setEstado(estadoDAO.traerPorId(Estado.class, EstadoEnum.INACTIVO.ordinal()));
            periodoDAO.guardar(periodoActual);
            
            Periodo proxPeriodoPendiente=this.generarNuevoPeriodo(nuevoPeriodo);
            
            periodoDAO.guardar(proxPeriodoPendiente);

            this.trasladarSaldos(periodoActual, nuevoPeriodo);
            this.actualizaraTablaIntegracion(proxPeriodoPendiente);
            utilitarioServicio.actualizarPeriodo();
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        } finally {
            ProcesosEnum.CIERRE.setProcesando(false);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados">
    private EstadoCierreDTO verificarRenumeracion(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            EstadoCierreDTO estado = new EstadoCierreDTO();
            estado.setProceso("Renumeración");
            PeriodoDTO periodoVigente = utilitarioServicio.traerPeriodoVigente();
            Periodo periodoAnterior = periodoDAO.traerPorId(Periodo.class, periodoVigente.getPeriodoAnteriorId());

            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoVigente.getFechaInicial(), periodoVigente.getFechaFinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

            int totalAsientos = asientoDAO.contar(Asiento.class, consulta);

            if (totalAsientos == 0) {
                estado.setRespuesta(Mensaje.ASIENTO_NO_ENCONTRADOS_PARA_PERIODO);
                estado.setEstado(false);
                return estado;
            }

            consulta = new Consulta();
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoVigente.getFechaInicial(), periodoVigente.getFechaFinal()));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
            consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.agregaOrdenAscendente("fecha");
            consulta.agregaOrdenAscendente("numero");
            consulta.agregaOrdenAscendente("procedencia.id");
            consulta.agregaOrdenAscendente("tipoAsiento.id");

            List<AsientoRenumeracionDTO> asientoDTO = asientoDAO.traerTodo(Asiento.class, consulta, AsientoRenumeracionDTO.class);
            int ultimoAsiento = periodoAnterior.getUltimoAsiento();
            boolean actualizarFechaRenumeracion = false;

            for (AsientoRenumeracionDTO asiento : asientoDTO) {
                ultimoAsiento++;
                if (asiento.getNumero() != ultimoAsiento) {
                    actualizarFechaRenumeracion = true;
                    break;
                }
            }

            //if (totalAsientos != (ultAsientoActual - ultAsientoAnterior) || actualizarFechaRenumeracion) {
            if (actualizarFechaRenumeracion) {
                estado.setRespuesta(Mensaje.ASIENTO_NECESITA_RENUMERACION);
                estado.setEstado(false);
                return estado;
            }
            if (periodoActual.getFechaRenumeracion() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mma");
                estado.setRespuesta(Mensaje.ASIENTO_ULTIMA_FECHA_RENUMERACION + sdf.format(periodoActual.getFechaRenumeracion()));
            } else {
                estado.setRespuesta(Mensaje.ASIENTO_RENUMERACION_NO_PROCESADA);
            }
            estado.setEstado(true);
            return estado;

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);

        }
    }

    private EstadoCierreDTO verificarBalance(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            EstadoCierreDTO estado = new EstadoCierreDTO();
            estado.setProceso("Balance");
            if (periodoActual.getFechaBalance() == null) {
                estado.setEstado(false);
                estado.setRespuesta(Mensaje.CIERRE_BALANCE_NO_PROCESADO);
                return estado;
            }
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoActual.getFechaInicial(), periodoActual.getFechaFinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.ObtenerMaximoDe(new Maximo("fechaEdicion"));

            Date maxFechaEdicion = (Date) asientoDAO.traerMaximo(Asiento.class, consulta);

            Date ultimaFechaBalance = periodoActual.getFechaBalance();
            if (maxFechaEdicion == null) {
                estado.setEstado(false);
                estado.setRespuesta(Mensaje.ASIENTO_NO_ENCONTRADOS_PARA_PERIODO);
                return estado;
            }
            if (ultimaFechaBalance.before(maxFechaEdicion)) {
                estado.setEstado(false);
                estado.setRespuesta(Mensaje.CIERRE_ASIENTOS_NO_PROCESADOS_BALANCE);
                return estado;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mma");
            estado.setEstado(true);
            estado.setRespuesta(Mensaje.CIERRE_BALANCE_PROCESADO + sdf.format(periodoActual.getFechaBalance()));
            return estado;

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    private EstadoCierreDTO verificarAsientosSysone(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            EstadoCierreDTO estado = new EstadoCierreDTO();

            estado.setProceso("Asientos Sysone");
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoActual.getFechaInicial(), periodoActual.getFechaFinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", ProcedenciaEnum.SYSONE.ordinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));

            int pendientes = integracionDAO.contar(Integracion.class, consulta);
            if (pendientes > 0) {
                estado.setEstado(false);
                estado.setRespuesta(Mensaje.INTEGRACION_SYSONE_INCOMPLETA);
                return estado;
            }
            estado.setRespuesta(Mensaje.CIERRE_PROCESADO);
            estado.setEstado(true);
            return estado;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private EstadoCierreDTO verificarAsientosProvision(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            EstadoCierreDTO estado = new EstadoCierreDTO();

            estado.setProceso("Asientos Provisión");
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoActual.getFechaInicial(), periodoActual.getFechaFinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", ProcedenciaEnum.PROVISION.ordinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));

            int pendientes = integracionDAO.contar(Integracion.class, consulta);
            if (pendientes > 0) {
                estado.setEstado(false);
                estado.setRespuesta(Mensaje.INTEGRACION_SYSONE_INCOMPLETA);
                return estado;
            }
            estado.setRespuesta(Mensaje.CIERRE_PROCESADO);
            estado.setEstado(true);
            return estado;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private EstadoCierreDTO verificarAsientosActualizacionME(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            EstadoCierreDTO estado = new EstadoCierreDTO();

            estado.setProceso("Asientos Actualizacion ME");
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoActual.getFechaInicial(), periodoActual.getFechaFinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", utilitarioServicio.traerTipoAsientoActualizacionME()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

            int asientosME = asientoDAO.contar(Asiento.class, consulta);
            if (asientosME == 0) {
                estado.setEstado(false);
                estado.setRespuesta(Mensaje.CIERRE_ASIENTO_ME_NO_EXISTE);
                return estado;
            } else if (asientosME > 1) {
                estado.setEstado(false);
                estado.setRespuesta(Mensaje.CIERRE_ASIENTO_ME_MAS_DE_UNO);
                return estado;
            }
            estado.setRespuesta(Mensaje.CIERRE_PROCESADO);
            estado.setEstado(true);
            return estado;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private EstadoCierreDTO verificarAsientosCuentasOrden(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            EstadoCierreDTO estado = new EstadoCierreDTO();
            boolean correcto = true;
            String respuesta = Mensaje.CIERRE_VALIDADO;

            estado.setProceso("Cuentas de Orden");

            CuentasOrdenEnum listaCuentas[] = CuentasOrdenEnum.values();
            List<DetalleDTO> listaDetalle;
            BigDecimal totalDebe;
            BigDecimal totalHaber;
            BigDecimal diferenciaDebe;
            BigDecimal diferenciaHaber;
            int totalAsientos = 0;
            for (CuentasOrdenEnum cuenta : listaCuentas) {
                listaDetalle = this.traerListaCuentaOrden(periodoActual, cuenta.getCuentaDebe());
                totalAsientos = totalAsientos + listaDetalle.size();
                totalDebe = listaDetalle.stream().map(x -> x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
                totalHaber = listaDetalle.stream().map(x -> x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);

                diferenciaDebe = totalDebe.subtract(totalHaber);

                listaDetalle = this.traerListaCuentaOrden(periodoActual, cuenta.getCuentaHaber());
                totalAsientos = totalAsientos + listaDetalle.size();
                totalDebe = listaDetalle.stream().map(x -> x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
                totalHaber = listaDetalle.stream().map(x -> x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);

                diferenciaHaber = totalHaber.subtract(totalDebe);

                if (diferenciaDebe.compareTo(diferenciaHaber) != 0) {
                    correcto = false;
                    respuesta = Mensaje.CIERRE_CUENTAS_ORDEN_NO_CUADRA;
                    break;
                }
            }
            if (totalAsientos == 0) {
                respuesta = Mensaje.CIERRE_NO_EXISTE_CUENTAS_ORDEN;
            }
            estado.setEstado(correcto);
            estado.setRespuesta(respuesta);

            return estado;

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private List<DetalleDTO> traerListaCuentaOrden(Periodo periodoActual, int cuenta) throws ExcepcionNegocio {
        try {
            
           return  repositorioBaseBatchDao.traerListaDetalle(cuenta,periodoActual.getFechaInicial(), periodoActual.getFechaFinal(), EstadoEnum.ACTIVO.ordinal());

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    private void trasladarSaldos(Periodo periodoAnterior, Periodo periodoActual) throws ExcepcionNegocio {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("periodo", "periodo"));
            consulta.agregaAlias(new Alias("cuenta", "cuenta"));
            consulta.agregaAlias(new Alias("moneda", "moneda"));

            consulta.agregaEquivalencia(new Equivalencia("cuenta.id", "cuentaId"));
            consulta.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));
            consulta.agregaEquivalencia(new Equivalencia("saldoFinal", "saldoFinal"));
            consulta.agregaRestriccionNoIgual(new RestriccionNoIgual("saldoFinal", BigDecimal.ZERO));
            consulta.agregaRestriccionIgual(new RestriccionIgual("periodo.id", periodoAnterior.getId()));

            List<SaldoDTO> listaSaldos = saldoDAO.traerTodo(Saldo.class, consulta, SaldoDTO.class);

            Clave clave = utilitarioServicio.traerSiguienteIdSaldo();

            int siguienteId = clave.getSiguienteid();

            if (listaSaldos.size() > 0) {
                int nuevaClave = siguienteId + listaSaldos.size();
                clave.setSiguienteid(nuevaClave);
                claveDAO.guardar(clave);
                List<Saldo> listaSaldosJDBC = new ArrayList<>();
                for (SaldoDTO saldoDTO : listaSaldos) {
                    Saldo saldoNuevo = new Saldo();

                    Moneda moneda = new Moneda();
                    moneda.setId(saldoDTO.getMonedaId());
                    Cuenta cuenta = new Cuenta();
                    cuenta.setId(saldoDTO.getCuentaId());

                    saldoNuevo.setId(siguienteId);
                    saldoNuevo.setCuenta(cuenta);
                    saldoNuevo.setMoneda(moneda);
                    saldoNuevo.setPeriodo(periodoActual);
                    saldoNuevo.setSaldoInicial(saldoDTO.getSaldoFinal());
                    saldoNuevo.setTotalDebe(BigDecimal.ZERO);
                    saldoNuevo.setTotalHaber(BigDecimal.ZERO);
                    saldoNuevo.setSaldoFinal(saldoDTO.getSaldoFinal());

                    listaSaldosJDBC.add(saldoNuevo);
                    siguienteId++;
                }
                repositorioBaseBatchDao.insertarBatchSaldo(listaSaldosJDBC);

            }

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private Periodo generarNuevoPeriodo(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            Periodo nuevoPeriodoPendiente = new Periodo();
            Date fechaInicialAnterior = periodoActual.getFechaInicial();

            Calendar calendarI = Calendar.getInstance();
            calendarI.setTime(fechaInicialAnterior);
            calendarI.add(Calendar.MONTH, 1);
            Date nuevaFechaInicio = calendarI.getTime();

            Calendar calendarF = Calendar.getInstance();
            calendarF.setTime(nuevaFechaInicio);
            int ultimoDiaMes = calendarF.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendarF.set(Calendar.DATE, ultimoDiaMes);
            Date nuevaFechaFinal = calendarF.getTime();

            String[] descripcionTemp = Helper.convertirAFecha(nuevaFechaInicio).split("/");
            String descripcion = descripcionTemp[2] + descripcionTemp[1];

            nuevoPeriodoPendiente.setDescripcion(descripcion);
            nuevoPeriodoPendiente.setEstado(estadoDAO.traerPorId(Estado.class, EstadoEnum.PENDIENTE.ordinal()));
            nuevoPeriodoPendiente.setFechaFinal(nuevaFechaFinal);
            nuevoPeriodoPendiente.setFechaInicial(nuevaFechaInicio);
            nuevoPeriodoPendiente.setFechaIntegracion(nuevaFechaInicio);
            nuevoPeriodoPendiente.setPeriodoAnterior(periodoActual);
            nuevoPeriodoPendiente.setUltimoAsiento(periodoActual.getUltimoAsiento());
            nuevoPeriodoPendiente.setTcFijo(periodoActual.getTcFijo());
            nuevoPeriodoPendiente.setBaseSoles(periodoActual.getBaseSoles());
            nuevoPeriodoPendiente.setBaseDolares(periodoActual.getBaseDolares());
            nuevoPeriodoPendiente.setTasaImplicitaME(periodoActual.getTasaImplicitaME());
            nuevoPeriodoPendiente.setTasaObligacion(periodoActual.getTasaObligacion());
            nuevoPeriodoPendiente.setTasaDeduccion(periodoActual.getTasaDeduccion());
            nuevoPeriodoPendiente.setTasaImplicitaMN(periodoActual.getTasaImplicitaMN());
            nuevoPeriodoPendiente.setTasaAdicionalMN(periodoActual.getTasaAdicionalMN());
            return nuevoPeriodoPendiente;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    private Periodo activarNuevoPeriodo(Periodo periodoActual) throws ExcepcionNegocio {
        int idPeriodoActual = periodoActual.getId();

        Consulta consultaPeriodo = new Consulta();
        consultaPeriodo.agregaAlias(new Alias("estado", "estado"));
        consultaPeriodo.agregaAlias(new Alias("periodoAnterior", "periodoAnterior"));
        consultaPeriodo.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.PENDIENTE.ordinal()));
        consultaPeriodo.agregaRestriccionIgual(new RestriccionIgual("periodoAnterior.id", idPeriodoActual));

        Periodo periodoActivar = (Periodo) periodoDAO.traerUnico(Periodo.class, consultaPeriodo);

        if (periodoActivar == null) {
            throw new ExcepcionNegocio(Mensaje.CIERRE_ERROR_ACTIVAR_PERIODO, Mensaje.TIPO_ERROR);
        }
        this.renumerarPeriodoActivar(periodoActual, periodoActivar);

        Estado estado = new Estado();
        estado.setId(EstadoEnum.ACTIVO.ordinal());
        periodoActivar.setEstado(estado);
        periodoActivar.setTcFijo(periodoActual.getTcFijo());
        periodoActivar.setFechaIntegracion(periodoActual.getFechaIntegracion());
        periodoActivar.setTotalCaja(periodoActual.getTotalCaja());
        periodoActivar.setBaseSoles(periodoActual.getBaseSoles());
        periodoActivar.setBaseDolares(periodoActual.getBaseDolares());
        periodoActivar.setTasaImplicitaME(periodoActual.getTasaImplicitaME());
        periodoActivar.setTasaObligacion(periodoActual.getTasaObligacion());
        periodoActivar.setTasaDeduccion(periodoActual.getTasaDeduccion());
        periodoActivar.setTasaImplicitaMN(periodoActual.getTasaImplicitaMN());
        periodoActivar.setTasaAdicionalMN(periodoActual.getTasaAdicionalMN());
        return periodoActivar;
    }

    private void renumerarPeriodoActivar(Periodo periodoActual, Periodo periodoActivar) throws ExcepcionNegocio {
        Consulta consultaNumero = new Consulta();
        consultaNumero.agregaEquivalencia(new Equivalencia("estado", "estado"));
        consultaNumero.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoActual.getFechaInicial(), periodoActual.getFechaFinal()));
        consultaNumero.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consultaNumero.ObtenerMaximoDe(new Maximo("numero"));

        int ultimoAsiento = (int) asientoDAO.traerMaximo(Asiento.class, consultaNumero);
        periodoActual.setUltimoAsiento(ultimoAsiento);
        Consulta consultaAsientos = new Consulta();
        consultaAsientos.agregaAlias(new Alias("estado", "estado"));
        consultaAsientos.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoActivar.getFechaInicial(), periodoActivar.getFechaFinal()));
        consultaAsientos.agregaEquivalencia(new Equivalencia("id", "id"));
        consultaAsientos.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
        consultaAsientos.agregaEquivalencia(new Equivalencia("numero", "numero"));
        consultaAsientos.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        consultaAsientos.agregaOrdenAscendente("fecha");
        consultaAsientos.agregaOrdenAscendente("numero");

        List<AsientoRenumeracionDTO> asientoDTO = asientoDAO.traerTodo(Asiento.class, consultaAsientos, AsientoRenumeracionDTO.class);

        if (!asientoDTO.isEmpty()) {
            for (AsientoRenumeracionDTO asiento : asientoDTO) {
                ultimoAsiento++;
                if (asiento.getNumero() != ultimoAsiento) {
                    Asiento asientoRenumerar = asientoDAO.traerPorId(Asiento.class, asiento.getId());
                    asientoRenumerar.setNumero(ultimoAsiento);
                    asientoDAO.guardar(asientoRenumerar);
                }
            }
        }

        periodoActivar.setUltimoAsiento(ultimoAsiento);

    }

    private void actualizaraTablaIntegracion(Periodo periodoActual) throws ExcepcionNegocio {
        try {
            List<TipoAsientoDTO> tiposSysone = utilitarioServicio.traerListaTipoAsientoSysone();
            List<TipoAsientoDTO> tiposProvision = utilitarioServicio.traerListaTipoAsientoProvision();
            List<AgenciaDTO> listaAgencias = utilitarioServicio.traerListaAgencia();
            List<Integracion> listaIntegracion = new ArrayList<>();

            Clave clave = utilitarioServicio.traerSiguienteIdIntegracion();

            int siguienteId = clave.getSiguienteid()+1;

            Procedencia procedencia = new Procedencia();
            procedencia.setId(ProcedenciaEnum.SYSONE.ordinal());
            Estado estado = new Estado();
            estado.setId(EstadoEnum.PENDIENTE.ordinal());
            Calendar fechaInicio = Calendar.getInstance();
            fechaInicio.setTime(periodoActual.getFechaInicial());

            Calendar fechaFin = Calendar.getInstance();
            fechaFin.setTime(periodoActual.getFechaFinal());

            int dias = fechaFin.get(Calendar.DATE) - fechaInicio.get(Calendar.DATE) + 1;

            int nuevaClave = tiposSysone.size() * dias * listaAgencias.size() + tiposProvision.size() + siguienteId;
            clave.setSiguienteid(nuevaClave);
            claveDAO.guardar(clave);

            for (TipoAsientoDTO tipo : tiposSysone) {
                TipoAsiento tipoAsiento = new TipoAsiento();
                tipoAsiento.setId(tipo.getId());
                fechaInicio.setTime(periodoActual.getFechaInicial());

                for (int i = 1; i <= dias; i++) {
                    fechaInicio.set(Calendar.DATE, i);

                    for (AgenciaDTO agencia : listaAgencias) {

                        Agencia agenciaAgregar = new Agencia();
                        agenciaAgregar.setId(agencia.getId());
                        Integracion integracion = new Integracion();
                        integracion.setId(siguienteId);
                        integracion.setEstado(estado);
                        integracion.setFecha(fechaInicio.getTime());
                        integracion.setProcedencia(procedencia);
                        integracion.setTipoAsiento(tipoAsiento);
                        integracion.setAgencia(agenciaAgregar);
                        listaIntegracion.add(integracion);
                        siguienteId++;
                    }
                }
            }
            Procedencia procedenciaProvision = new Procedencia();
            procedenciaProvision.setId(ProcedenciaEnum.PROVISION.ordinal());
            int oficinaPrincipal = 1;
            for (TipoAsientoDTO tipo : tiposProvision) {

                TipoAsiento tipoAsiento = new TipoAsiento();
                tipoAsiento.setId(tipo.getId());
                Agencia agenciaAgregar = new Agencia();
                agenciaAgregar.setId(oficinaPrincipal);
                Integracion integracion = new Integracion();
                integracion.setId(siguienteId);
                integracion.setEstado(estado);
                integracion.setFecha(fechaFin.getTime());
                integracion.setProcedencia(procedenciaProvision);
                integracion.setTipoAsiento(tipoAsiento);
                integracion.setAgencia(agenciaAgregar);
                listaIntegracion.add(integracion);
                siguienteId++;
            }
            repositorioBaseBatchDao.insertarBatchIntegracion(listaIntegracion);

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }
    //</editor-fold>
}
