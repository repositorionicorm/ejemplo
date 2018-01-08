/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.TotalAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Asiento;
import pe.cajapaita.backerp.contabilidad.entidad.Detalle;
import pe.cajapaita.backerp.contabilidad.entidad.Moneda;
import pe.cajapaita.backerp.contabilidad.entidad.Periodo;
import pe.cajapaita.backerp.contabilidad.entidad.TipoAsiento;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Grupo;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIn;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionNoIgual;
import pe.cajapaita.backerp.contabilidad.consulta.Suma;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseBatchDao;
import pe.cajapaita.backerp.contabilidad.dto.AgenciaDTO;
import pe.cajapaita.backerp.contabilidad.dto.AsientoRenumeracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.CuentaBatchDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.SaldoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.UsuarioDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Agencia;
import pe.cajapaita.backerp.contabilidad.entidad.Cuenta;
import pe.cajapaita.backerp.contabilidad.entidad.Estado;
import pe.cajapaita.backerp.contabilidad.entidad.Procedencia;
import pe.cajapaita.backerp.contabilidad.entidad.Saldo;
import pe.cajapaita.backerp.contabilidad.entidad.Usuario;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.MonedaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.TipoCuentaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;

/**
 *
 * @author hnole
 */
@Service
@Transactional
public class AsientoServicioImpl implements IAsientoServicio {

    @Autowired
    private IRepositorioBaseDao<Asiento> asientoDAO;

    @Autowired
    private IRepositorioBaseDao<Detalle> detalleDAO;

    @Autowired
    private IRepositorioBaseDao<Periodo> periodoDAO;

    @Autowired
    private IRepositorioBaseDao<Moneda> monedaDAO;

    @Autowired
    private IRepositorioBaseDao<TipoAsiento> tipoAsientoDAO;

    @Autowired
    private IRepositorioBaseDao<Procedencia> procedenciaDAO;

    @Autowired
    private IRepositorioBaseDao<Estado> estadoDAO;

    @Autowired
    private IRepositorioBaseDao<Cuenta> cuentaDAO;

    @Autowired
    private IRepositorioBaseDao<Saldo> saldoDAO;

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IRepositorioBaseDao<Usuario> usuarioDAO;

    @Autowired
    private IRepositorioBaseDao<Agencia> agenciaDAO;

    @Autowired
    private ICuentaServicio cuentaServicio;

    @Autowired
    private IRepositorioBaseBatchDao repositorioBaseBatchDao;

    private int totalPaginas;
    private final Logger logger = Logger.getLogger(AsientoServicioImpl.class);

    private ProcedenciaEnum procedenciaEnum;

    @Override
    public boolean cuentaTieneMovimientos(int cuentaId) throws ExcepcionNegocio {
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("cuenta", "cuenta"));
            consulta.agregaAlias(new Alias("asiento", "asiento"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("cuenta.id", cuentaId));

            return (detalleDAO.contar(Detalle.class, consulta) > 0);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public List<AsientoDTO> traerAsientos(int periodoId, int tipoAsientoId, int monedaId, int procedenciaId, int pagina, int numero, int agenciaId,UsuarioDTO usuarioDTO) throws ExcepcionNegocio {
        try {

            Consulta consulta = new Consulta();
            consulta.setRegistrosPorPagina(utilitarioServicio.traerRegistrosPorPagina());
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));

            if (periodoId == 0) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_SELECCIONE_PERIODO, Mensaje.TIPO_ALERTA);
            }

            Periodo periodo = periodoDAO.traerPorId(Periodo.class, periodoId);
            if (periodo == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodo.getFechaInicial(), periodo.getFechaFinal()));

            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("editadoPor", "usuario"));
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaAlias(new Alias("moneda", "moneda"));
            consulta.agregaAlias(new Alias("agencia", "agencia"));

            if (tipoAsientoId > 0) {
                consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", tipoAsientoId));
            }

            if (monedaId > 0) {
                consulta.agregaRestriccionIgual(new RestriccionIgual("moneda.id", monedaId));
            }

            if (procedenciaId > 0) {
                consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", procedenciaId));
            }
            if (numero > 0) {
                consulta.agregaRestriccionIgual(new RestriccionIgual("numero", numero));
            }
            if (agenciaId > 0) {
                consulta.agregaRestriccionIgual(new RestriccionIgual("agencia.id", agenciaId));
            }

            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
            consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
            consulta.agregaEquivalencia(new Equivalencia("glosa", "glosa"));
            consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.descripcion", "tipoAsientoDescripcion"));
            consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.id", "tipoAsientoId"));
            consulta.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));
            consulta.agregaEquivalencia(new Equivalencia("procedencia.descripcion", "procedenciaDescripcion"));
            consulta.agregaEquivalencia(new Equivalencia("procedencia.id", "procedenciaId"));
            consulta.agregaEquivalencia(new Equivalencia("moneda.descripcion", "monedaDescripcion"));
            consulta.agregaEquivalencia(new Equivalencia("agencia.abreviatura", "agenciaAbreviatura"));
            consulta.agregaEquivalencia(new Equivalencia("agencia.id", "agenciaId"));

            consulta.agregaOrdenAscendente("fecha");
            consulta.agregaOrdenAscendente("numero");
            consulta.setPagina(pagina);

            List<AsientoDTO> asientos = asientoDAO.traerTodo(Asiento.class, consulta, AsientoDTO.class);

            if (asientos.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_ENCONTRADOS, Mensaje.TIPO_ALERTA);
            }            
            this.asignaTotalPaginas(consulta);
            this.actualizaTotales(asientos);
            //VARIABLES DE ACCIONES
             String eliminar = "<li><a   data-toggle='modal' data-target='#modalDetalleAsiento' href='#' class='detalle'>Eliminar</a></li>";
             String editar,comboLista;
             int estadoPeriodo=periodo.getEstado().getId();
             boolean pertenecePeriodo=false;
             if(estadoPeriodo==EstadoEnum.ACTIVO.ordinal()||estadoPeriodo==EstadoEnum.PENDIENTE.ordinal()){
                 pertenecePeriodo=true;
             }
             
             for(AsientoDTO asiento:asientos){
               editar= pertenecePeriodo && asiento.getProcedenciaId()==procedenciaEnum.CONTABILIDAD.ordinal()&&!usuarioDTO.getEditar().equals("S") ? "<li><a id='linkEditarAsiento' href='/contabilidad/asientos/editar/" + asiento.getId() + "'>Editar</a></li>":"" ;    
               eliminar=pertenecePeriodo&&!usuarioDTO.getEditar().equalsIgnoreCase("S") ? eliminar:"";
                comboLista="<div class='dropdown ' >"+
                "<button class='btn btn-xs btn-posicion btn-default dropdownMenu1 dropdown-toggle' type='button' id='dropdownMenu1' data-toggle='dropdown' aria-haspopup='true' >"+
                "<span class='glyphicon glyphicon-cog'></span>"+
                "<span class='caret'></span>"+
                "</button>"+
                "<ul class='dropdown-menu' style='position: absolute;left: -120px;'  aria-labelledby='dropdownMenu1'>"+
                "<li><a class='detalle'  data-toggle='modal'  data-target='#modalDetalleAsiento' href='#'>Detalle </a></li>"+
                editar+
                eliminar+
                "</ul>"+
                "</div>";
                
                asiento.setAccion(comboLista);
            }
            return asientos;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public int traerTotalPaginas() {
        return this.totalPaginas;
    }

    @Override
    public List<DetalleDTO> traerDetalle(int asientoId) throws ExcepcionNegocio {
        try {

            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("asiento", "asiento"));
            consulta.agregaAlias(new Alias("cuenta", "cuenta"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("asiento.id", asientoId));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("cuenta.cuenta", "cuentaCuenta"));
            consulta.agregaEquivalencia(new Equivalencia("debe", "debe"));
            consulta.agregaEquivalencia(new Equivalencia("haber", "haber"));
            consulta.agregaEquivalencia(new Equivalencia("cuenta.id", "cuentaId"));
            consulta.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));
            consulta.agregaOrdenAscendente("moneda.id");
            consulta.agregaOrdenAscendente("cuenta");

            List<DetalleDTO> detalle = detalleDAO.traerTodo(Detalle.class, consulta, DetalleDTO.class);

            if (detalle.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_DETALLE_NO_ENCONTRADO, Mensaje.TIPO_ALERTA);
            }

            return detalle;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void eliminar(int asientoId, int usuarioId) throws ExcepcionNegocio {
        try {

            Asiento asiento = asientoDAO.traerPorId(Asiento.class, asientoId);
            
            
            if (asiento == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            if (asiento.getProcedencia().getId() == ProcedenciaEnum.SYSONE.ordinal()) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NECESITA_CANCELAR_INTEGRACION, Mensaje.TIPO_ALERTA);
            }
            if (asiento.getProcedencia().getId() == ProcedenciaEnum.PROVISION.ordinal()) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NECESITA_CANCELAR_INTEGRACION_PROVISION, Mensaje.TIPO_ALERTA);
            }

            PeriodoDTO periodoVigenteDTO = utilitarioServicio.traerPeriodoVigente();
            if (!Helper.between(asiento.getFecha(), periodoVigenteDTO.getFechaInicial(), periodoVigenteDTO.getFechaLimite())) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_DE_PERIODO_NO_VIGENTE, Mensaje.TIPO_ALERTA);
            }

            Usuario usuario = usuarioDAO.traerPorId(Usuario.class, usuarioId);
            if (usuario == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_USUARIO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            asiento.setEditadoPor(usuario);
            asiento.setFechaEdicion(new Date());
            asiento.setEstado(estadoDAO.traerPorId(Estado.class, EstadoEnum.INACTIVO.ordinal()));

            asientoDAO.guardar(asiento);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void eliminarIntegracion(int asientoId, int usuarioId) throws ExcepcionNegocio {
        try {
            Asiento asiento = asientoDAO.traerPorId(Asiento.class, asientoId);

            if (asiento == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            PeriodoDTO periodoVigenteDTO = utilitarioServicio.traerPeriodoVigente();
            if (!Helper.between(asiento.getFecha(), periodoVigenteDTO.getFechaInicial(), periodoVigenteDTO.getFechaLimite())) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_DE_PERIODO_NO_VIGENTE, Mensaje.TIPO_ALERTA);
            }

            Usuario usuario = usuarioDAO.traerPorId(Usuario.class, usuarioId);
            if (usuario == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_USUARIO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            asiento.setEditadoPor(usuario);
            asiento.setFechaEdicion(new Date());
            asiento.setEstado(estadoDAO.traerPorId(Estado.class, EstadoEnum.INACTIVO.ordinal()));

            asientoDAO.guardar(asiento);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public AsientoDTO traerAsiento(int asientoId) throws ExcepcionNegocio {

        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("moneda", "moneda"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("id", asientoId));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
            consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
            consulta.agregaEquivalencia(new Equivalencia("glosa", "glosa"));
            consulta.agregaEquivalencia(new Equivalencia("tipoAsiento.id", "tipoAsientoId"));
            consulta.agregaEquivalencia(new Equivalencia("moneda.id", "monedaId"));

            AsientoDTO asientoDTO = (AsientoDTO) asientoDAO.traerUnico(Asiento.class, consulta, AsientoDTO.class);

            if (asientoDTO == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            asientoDTO.setFechaString(formato.format(asientoDTO.getFecha()));

            List<DetalleDTO> detalleDTO = this.traerDetalle(asientoId);

            asientoDTO.setDetalles(detalleDTO);
            return asientoDTO;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void guardar(AsientoDTO asientoDTO, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio {

        asientoDTO.validaPropiedades();
        if (asientoDTO.getId() == 0) {
            if (this.estaMonedaDentroDeCuenta(asientoDTO)) {
                this.nuevoDesdeExcel(asientoDTO, procedencia, usuarioId);
            } else {
                this.nuevo(asientoDTO, procedencia, usuarioId);
            }
        } else {
            this.actualizar(asientoDTO, usuarioId);
        }
    }

    @Override
    public void guardar(List<AsientoDTO> listaAsientos, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio {
        this.procedenciaEnum = procedencia;
        List<Asiento> asientos = new ArrayList<>();
        try {

            for (AsientoDTO asientoDTO : listaAsientos) {
                asientos.add(this.validaAsiento(asientoDTO, usuarioId));
            }

            asientoDAO.guardar(asientos);

            String mensaje = Mensaje.OPERACION_CORRECTA;
            String idGenerados = "", numerosGenerados = "";
            for (Asiento asiento : asientos) {
                idGenerados = idGenerados + asiento.getId() + ",";
                numerosGenerados = numerosGenerados + asiento.getNumero() + ",";
            }
            idGenerados = idGenerados.substring(0, idGenerados.length() - 1);
            numerosGenerados = numerosGenerados.substring(0, numerosGenerados.length() - 1);

            throw new ExcepcionNegocio(mensaje, Mensaje.TIPO_EXITO, idGenerados, numerosGenerados);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public void renumerarAsientos() throws ExcepcionNegocio {
        try {
            PeriodoDTO periodoVigente = utilitarioServicio.traerPeriodoVigente();
            Periodo periodoAnterior = periodoDAO.traerPorId(Periodo.class, periodoVigente.getPeriodoAnteriorId());

            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoVigente.getFechaInicial(), periodoVigente.getFechaLimite()));
            consulta.agregaEquivalencia(new Equivalencia("id", "id"));
            consulta.agregaEquivalencia(new Equivalencia("fecha", "fecha"));
            consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.agregaOrdenAscendente("fecha");
            consulta.agregaOrdenAscendente("procedencia.id");
            consulta.agregaOrdenAscendente("tipoAsiento.id");

            List<AsientoRenumeracionDTO> asientoDTO = asientoDAO.traerTodo(Asiento.class, consulta, AsientoRenumeracionDTO.class);
            int ultimoAsiento = periodoAnterior.getUltimoAsiento();
            boolean actualizarFechaRenumeracion = false;
            if (asientoDTO.size() <= 0) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_ENCONTRADOS_PARA_PERIODO, Mensaje.TIPO_ALERTA);
            }

            for (AsientoRenumeracionDTO asiento : asientoDTO) {
                ultimoAsiento++;
                if (asiento.getNumero() != ultimoAsiento) {
                    Asiento asientoRenumerar = asientoDAO.traerPorId(Asiento.class, asiento.getId());
                    asientoRenumerar.setNumero(ultimoAsiento);
                    asientoDAO.guardar(asientoRenumerar);
                    actualizarFechaRenumeracion = true;
                }
            }
            Periodo periodo = periodoDAO.traerPorId(Periodo.class, periodoVigente.getId());
            //int diferenciaAsientos = periodo.getUltimoAsiento() - periodoAnterior.getUltimoAsiento();
            periodo.setUltimoAsiento(ultimoAsiento);

            Periodo periodoActual = periodoDAO.traerPorId(Periodo.class, periodoVigente.getId());
            periodoActual.setFechaRenumeracion(new Date());
            periodoDAO.guardar(periodoActual);

            if ( actualizarFechaRenumeracion  ) {
                throw new ExcepcionNegocio(Mensaje.OPERACION_CORRECTA, Mensaje.TIPO_EXITO);
            }
            throw new ExcepcionNegocio(Mensaje.ASIENTO_YA_ESTA_RENUMERADO, Mensaje.TIPO_ALERTA);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public String ultimaFechaRenumeracion() throws ExcepcionNegocio {
        String respuesta = Mensaje.ASIENTO_RENUMERACION_NO_PROCESADA;
        int periodoVigenteDTOId = utilitarioServicio.traerPeriodoVigente().getId();
        try {
            Consulta consulta = new Consulta();
            consulta.agregaEquivalencia(new Equivalencia("fechaRenumeracion", "fechaRenumeracion"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("id", periodoVigenteDTOId));

            PeriodoDTO periodoDTO = (PeriodoDTO) periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class);

            if (periodoDTO == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            if (periodoDTO.getFechaRenumeracion() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mma");
                respuesta = Mensaje.ASIENTO_ULTIMA_FECHA_RENUMERACION + sdf.format(periodoDTO.getFechaRenumeracion());
            }
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
        return respuesta;
    }

    @Override
    public String ultimaFechaBalance() throws ExcepcionNegocio {
        String respuesta = Mensaje.ASIENTO_BALANCE_NO_PROCESADO;
        int periodoVigenteDTOId = utilitarioServicio.traerPeriodoVigente().getId();
        try {
            Consulta consulta = new Consulta();
            consulta.agregaEquivalencia(new Equivalencia("fechaBalance", "fechaBalance"));
            consulta.agregaRestriccionIgual(new RestriccionIgual("id", periodoVigenteDTOId));

            PeriodoDTO periodoDTO = (PeriodoDTO) periodoDAO.traerUnico(Periodo.class, consulta, PeriodoDTO.class);

            if (periodoDTO == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            if (periodoDTO.getFechaBalance() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mma");
                respuesta = Mensaje.ASIENTO_ULTIMA_FECHA_MAYORIZACION + sdf.format(periodoDTO.getFechaBalance());

            }
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
        return respuesta;
    }

    @Override
    public boolean existenAsientos(String fecha, int tipoAsientoId, int procedenciaId, int agenciaId) throws ExcepcionNegocio {
        try {

            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaAlias(new Alias("agencia", "agencia"));

            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha(fecha), Helper.convertirAFecha(fecha)));
            consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", tipoAsientoId));
            consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", procedenciaId));
            consulta.agregaRestriccionIgual(new RestriccionIgual("agencia.id", agenciaId));

            return asientoDAO.contar(Asiento.class, consulta) > 0;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public AsientoDTO cambiarTC(BigDecimal monto) throws ExcepcionNegocio {
        PeriodoDTO periodoDTO = utilitarioServicio.traerPeriodoVigente();
        int tipoAsientoId_MONEDA_EXTRANJERA = utilitarioServicio.traerTipoAsientoActualizacionME();
        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
        consulta.agregaAlias(new Alias("estado", "estado"));

        consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", periodoDTO.getFechaInicial(), periodoDTO.getFechaFinal()));
        consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", tipoAsientoId_MONEDA_EXTRANJERA));
        consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
        int totalActualizacionTC = asientoDAO.contar(Asiento.class, consulta);

        if (totalActualizacionTC > 0) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_SE_PUEDO_ACTUALIZAR, Mensaje.TIPO_ALERTA);
        }

        if (monto.equals(BigDecimal.ZERO)) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_NUMERO_MAYOR_CERO, Mensaje.TIPO_ALERTA);
        }

        Periodo periodoAnterior = periodoDAO.traerPorId(Periodo.class, periodoDTO.getPeriodoAnteriorId());

        if (periodoAnterior == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }

        if (monto.equals(periodoAnterior.getTcFijo())) {
            throw new ExcepcionNegocio(Mensaje.CIERRE_NUEVO_TC_IGUAL, Mensaje.TIPO_ALERTA);
        }

        return this.calcularNuevoSaldo(periodoDTO.getId(), periodoAnterior.getTcFijo(), monto, periodoDTO.getFechaFinal());
    }

    @Override
    public void grabarTipoCambio(BigDecimal nuevaTC) throws ExcepcionNegocio {
        try {
            if (nuevaTC.equals(BigDecimal.ZERO)) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NUMERO_MAYOR_CERO, Mensaje.TIPO_ALERTA);
            }
            PeriodoDTO periodoDTO = utilitarioServicio.traerPeriodoVigente();

            Periodo periodoAnterior = periodoDAO.traerPorId(Periodo.class, periodoDTO.getPeriodoAnteriorId());
            if (periodoAnterior == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_PERIODO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }
            Periodo periodo = periodoDAO.traerPorId(Periodo.class, periodoDTO.getId());
            periodo.setTcFijo(nuevaTC);
            periodoDAO.guardar(periodo);
            utilitarioServicio.actualizarPeriodo();
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

    }

    @Override
    public List<Integer> traerNumeroAsientos(List<Integer> asientosId) throws ExcepcionNegocio {
        List<Integer> listaNumeros = new ArrayList<>();

        Consulta consulta = new Consulta();
        consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));

        if (asientosId.size() == 0) {
            return listaNumeros;
        }

        switch (asientosId.size()) {
            case 1:
                consulta.agregaRestriccionSql("{alias}.id=" + asientosId.get(0));
                break;
            case 2:
                consulta.agregaRestriccionSql("({alias}.id=" + asientosId.get(0) + " or {alias}.id=" + asientosId.get(1) + ")");
                break;
        }

        List<AsientoDTO> asientos = asientoDAO.traerTodo(Asiento.class, consulta, AsientoDTO.class);

        for (AsientoDTO asiento : asientos) {
            listaNumeros.add(asiento.getNumero());
        }

        return listaNumeros;

    }

    @Override
    public void integrarTipoCambio(BigDecimal nuevaTC, AsientoDTO asientoDTO, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio {
        boolean exito = false;
        String mensaje = "";
        try {
            try {
                this.nuevo(asientoDTO, procedencia, usuarioId);

            } catch (ExcepcionNegocio excepcionNegocio) {
                if (excepcionNegocio.getTipoMensaje().equals(Mensaje.TIPO_EXITO)) {
                    exito = true;
                    mensaje = excepcionNegocio.getMessage();
                } else {
                    throw excepcionNegocio;
                }
            }
            if (exito) {
                this.grabarTipoCambio(nuevaTC);
                throw new ExcepcionNegocio(mensaje, Mensaje.TIPO_EXITO);
            }

        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

//<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private void nuevo(AsientoDTO asientoDTO, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio {
        this.procedenciaEnum = procedencia;
        try {
            Asiento asiento = this.validaAsiento(asientoDTO, usuarioId);
            asientoDAO.guardar(asiento);
            asientoDTO.setId(asiento.getId());
            asientoDTO.setNumero(asiento.getNumero());
            throw new ExcepcionNegocio(Mensaje.OPERACION_CORRECTA + " - Asiento generado : " + asiento.getNumero(), Mensaje.TIPO_EXITO);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);

        }
    }

    private void actualizar(AsientoDTO asientoDTO, int usuarioId) throws ExcepcionNegocio {
        try {
            Asiento asiento = asientoDAO.traerPorId(Asiento.class, asientoDTO.getId());
            List<MonedaDTO> listaMonedaDTO = utilitarioServicio.traerListaMonedaActiva();
            List<Integer> listaMonedasInteger = listaMonedaDTO.stream().map(x -> x.getId()).collect(Collectors.toList());
            if (asiento == null) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }

            if (asiento.getProcedencia().getId() != procedenciaEnum.CONTABILIDAD.ordinal()) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_NO_PUEDE_EDITAR, Mensaje.TIPO_ALERTA);
            }
            List<DetalleDTO> listaDetalleDTO = asientoDTO.getDetalles();
            List<Integer> listaMonedasEvaluar = listaDetalleDTO.stream().map(x -> x.getMonedaId()).collect(Collectors.toList());

            if (!Helper.existeDato(listaMonedasInteger, listaMonedasEvaluar)) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_MONEDA_NO_EXISTE, Mensaje.TIPO_ALERTA);
            }
            this.procedenciaEnum = ProcedenciaEnum.valueOf(asiento.getProcedencia().getDescripcion());
            Date ultimaFecha = new Date();
            this.validarMaestro(asiento, asientoDTO, utilitarioServicio.traerPeriodoVigente(), usuarioId, ultimaFecha);

            List<Detalle> listaDetalleOriginal = asiento.getDetalles();
            List<DetalleDTO> listaDetalleInsertar = asientoDTO.getDetalles();

            List<Detalle> listaBuscar = new ArrayList<>();
            listaBuscar = listaDetalleOriginal.stream().collect(Collectors.toList());

            for (Detalle detalle : listaBuscar) {
                DetalleDTO detalleDTO = listaDetalleInsertar.stream().filter(t -> t.getId() == detalle.getId()).findFirst().orElse(null);
                if (detalleDTO == null) {
                    listaDetalleOriginal.remove(detalle);
                }
            }

            Usuario usuario = new Usuario();
            usuario.setId(usuarioId);
            for (DetalleDTO detalleDTO : listaDetalleInsertar) {
                Detalle miDetalle = listaBuscar.stream().filter(t -> t.getId() == detalleDTO.getId()).findFirst().orElse(null);
                if (miDetalle == null) {
                    Detalle nuevoDetalle = new Detalle();
                    nuevoDetalle.setFechaCreacion(ultimaFecha);
                    nuevoDetalle.setFechaEdicion(ultimaFecha);
                    nuevoDetalle.setCreadoPor(usuario);
                    nuevoDetalle.setEditadoPor(usuario);
                    this.validarDetalle(nuevoDetalle, detalleDTO);
                    asiento.agregarDetalle(nuevoDetalle);

                } else {
                    this.verificarActualizacionDetalle(miDetalle, detalleDTO, ultimaFecha, usuario);
                    this.validarDetalle(miDetalle, detalleDTO);
                }
            }
            List<BigDecimal> debe = listaDetalleOriginal.stream().map(p -> p.getDebe()).collect(Collectors.toList());
            List<BigDecimal> haber = listaDetalleOriginal.stream().map(p -> p.getHaber()).collect(Collectors.toList());

            BigDecimal totalDebe = debe.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalHaber = haber.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalDebe.compareTo(totalHaber) != 0) {
                throw new ExcepcionNegocio(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA, Mensaje.TIPO_ALERTA);
            }

            asientoDAO.guardar(asiento);
            throw new ExcepcionNegocio(Mensaje.OPERACION_CORRECTA + " - Asiento editado : " + asiento.getNumero(), Mensaje.TIPO_EXITO);
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private void actualizaTotales(List<AsientoDTO> asientos) throws ExcepcionNegocio {
        RestriccionIn restriccionIn = new RestriccionIn("asiento.id");
        for (AsientoDTO asientoDTO : asientos) {
            restriccionIn.agregarRestriccion(asientoDTO.getId());
        }

        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("asiento", "asiento"));
        consulta.setRestriccionIn(restriccionIn);
        consulta.agregaSuma(new Suma("debe", "totalDebe"));
        consulta.agregaSuma(new Suma("haber", "totalHaber"));
        consulta.agregaGrupo(new Grupo("asiento.id", "id"));

        List<TotalAsientoDTO> totalAsientos = detalleDAO.traerAgrupado(Detalle.class, consulta, TotalAsientoDTO.class
        );
        if (!totalAsientos.isEmpty()) {
            for (AsientoDTO asiento : asientos) {
                try {
                    TotalAsientoDTO totalAsientoDTO = totalAsientos.stream().
                            filter(x -> x.getId() == asiento.getId()).findFirst().get();

                    if (totalAsientoDTO != null) {
                        asiento.setTotal(totalAsientoDTO.getTotalDebe().compareTo(totalAsientoDTO.getTotalHaber()) == 1
                                ? totalAsientoDTO.getTotalDebe() : totalAsientoDTO.getTotalHaber());
                    }
                } catch (Exception ex) {
                    logger.error(ex.getMessage() + "- Asiento " + asiento.getId());
                    asiento.setTotal(new BigDecimal(0));

                }
            }
        }

    }

    private void asignaTotalPaginas(Consulta parametro) throws ExcepcionNegocio {
        this.totalPaginas = (int) Math.ceil(asientoDAO.contar(Asiento.class, parametro) / (double) (utilitarioServicio.traerRegistrosPorPagina()));

    }

    private int traerNumeroDeAsiento(int periodoVigenteId) throws ExcepcionNegocio {
        Periodo periodo = periodoDAO.traerPorId(Periodo.class, periodoVigenteId);
        int numeroAsiento = periodo.getUltimoAsiento() + 1;
        periodo.setUltimoAsiento(numeroAsiento);

        return numeroAsiento;
    }

    private void validarMaestro(Asiento asiento, AsientoDTO asientoDTO, PeriodoDTO periodoVigenteDTO, int usuarioId, Date fecha) throws ExcepcionNegocio {
        int monedaCabecera;
        asiento.setGlosa(asientoDTO.getGlosa());

        asiento.setFecha(Helper.convertirAFecha(asientoDTO.getFechaString()));
        if (!Helper.between(asiento.getFecha(), periodoVigenteDTO.getFechaInicial(), periodoVigenteDTO.getFechaLimite())) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_DE_PERIODO_NO_VIGENTE, Mensaje.TIPO_ALERTA);

        }

        final List<MonedaDTO> listaMonedasActivas = utilitarioServicio.traerListaMonedaActiva();
        final List<TipoAsientoDTO> listaTipoAsiento = utilitarioServicio.traerListaTipoAsiento();
        final List<AgenciaDTO> listaAgencias = utilitarioServicio.traerListaAgencia();

        List<DetalleDTO> listaDetalleDTO = asientoDTO.getDetalles();
        List<Integer> listaMonedasDetalle = listaDetalleDTO.stream().map(x -> x.getMonedaId()).collect(Collectors.toList());
        List<Integer> listaMonedasActivasInteger = listaMonedasActivas.stream().map(x -> x.getId()).collect(Collectors.toList());
        Moneda moneda = new Moneda();
        if (!Helper.existeDato(listaMonedasActivasInteger, listaMonedasDetalle)) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_MONEDA_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }

        List<Integer> cantidadMonedas = listaMonedasDetalle.stream().distinct().collect(Collectors.toList());

        //SI LA CANTIDAD DE MONEDAS CONTENIDAS ES MAYOR A UNO EL TIPO DE MONEDA CABECERA SE ASIGNA COMO DOLAR
        if (cantidadMonedas.size() > 1) {
            monedaCabecera = MonedaEnum.DOLARES.ordinal();
        } else {
            monedaCabecera = cantidadMonedas.get(0);
        }

        moneda.setId(monedaCabecera);
        asiento.setMoneda(moneda);
        TipoAsientoDTO tipoAsientoDTO = listaTipoAsiento.stream().filter(x -> x.getId() == asientoDTO.getTipoAsientoId()).findFirst().orElse(null);

        if (tipoAsientoDTO == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_TIPO_ASIENTO_NO_EXISTE, Mensaje.TIPO_ALERTA);

        }
        TipoAsiento tipoAsiento = new TipoAsiento();
        tipoAsiento.setId(tipoAsientoDTO.getId());
        asiento.setTipoAsiento(tipoAsiento);

        asiento.setProcedencia(procedenciaDAO.traerPorId(Procedencia.class, this.procedenciaEnum.ordinal()));
        if (asiento.getProcedencia() == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_PROCEDENCIA_NO_EXISTE, Mensaje.TIPO_ALERTA);

        }

        Usuario usuario = usuarioDAO.traerPorId(Usuario.class, usuarioId);
        if (usuario == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_USUARIO_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }
        asiento.setEditadoPor(usuario);
        asiento.setFechaEdicion(fecha);
        if (asiento.getId() == 0) {
            asiento.setCreadoPor(usuario);
            asiento.setFechaCreacion(fecha);

        }
        AgenciaDTO agenciaDTO = listaAgencias.stream().filter(x -> x.getId() == asientoDTO.getAgenciaId()).findFirst().orElse(null);

        if (agenciaDTO == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_AGENCIA_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }
        Agencia agencia = new Agencia();
        agencia.setId(agenciaDTO.getId());

        asiento.setAgencia(agencia);

    }

    private void validarDetalle(Detalle detalle, DetalleDTO detalleDTO) throws ExcepcionNegocio {
        detalleDTO.validaPropiedades();

        detalle.setDebe(detalleDTO.getDebe());
        detalle.setHaber(detalleDTO.getHaber());

        CuentaBatchDTO cuentaDTO;
        if (detalleDTO.getCuentaId() != 0) {
            cuentaDTO = repositorioBaseBatchDao.traerCuentaPorId(detalleDTO.getCuentaId());

        } else {
            cuentaDTO = repositorioBaseBatchDao.traerCuentaPorCuenta(detalleDTO.getCuentaCuenta());
        }

        if (cuentaDTO == null) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_NO_EXISTE + " : " + detalleDTO.getCuentaCuenta(), Mensaje.TIPO_ALERTA);
        }

        if (cuentaDTO.getEsAnalitica().equals("N")) {
            throw new ExcepcionNegocio(cuentaDTO.getCuenta() + " " + Mensaje.CUENTA_NO_ES_ANALITICA, Mensaje.TIPO_ALERTA);
        }

        if (cuentaDTO.getEstadoId() != EstadoEnum.ACTIVO.ordinal()) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_NO_ACTIVA, Mensaje.TIPO_ALERTA);
        }
        Moneda moneda = new Moneda();
        moneda.setId(detalleDTO.getMonedaId());
        detalle.setMoneda(moneda);
        Cuenta cuenta = new Cuenta();
        cuenta.setId(cuentaDTO.getId());
        detalle.setCuenta(cuenta);
    }

    private AsientoDTO calcularNuevoSaldo(int periodoId, BigDecimal tcFijo, BigDecimal nuevoTcFijo, Date fechafinal) throws ExcepcionNegocio {
        Consulta consulta = new Consulta();
        consulta.agregaAlias(new Alias("periodo", "periodo"));
        consulta.agregaAlias(new Alias("moneda", "moneda"));
        consulta.agregaAlias(new Alias("cuenta", "cuenta"));

        consulta.agregaRestriccionIgual(new RestriccionIgual("periodo.id", periodoId));
        consulta.agregaRestriccionIgual(new RestriccionIgual("moneda.id", MonedaEnum.DOLARES.ordinal()));
        consulta.agregaRestriccionIgual(new RestriccionIgual("cuenta.esAnalitica", "S"));

        consulta.agregaRestriccionNoIgual(new RestriccionNoIgual("saldoFinal", BigDecimal.ZERO));
        consulta.agregaRestriccionSql("({alias}.totalDebe>0 or {alias}.totalHaber>0)");

        consulta.agregaEquivalencia(new Equivalencia("saldoFinal", "saldoFinal"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta.id", "cuentaId"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta.tipoCuenta.id", "tipoCuentaId"));
        consulta.agregaEquivalencia(new Equivalencia("cuenta.cuenta", "cuenta"));

        AsientoDTO asientoDTO = new AsientoDTO();

        List<SaldoDTO> saldos = saldoDAO.traerTodo(Saldo.class, consulta, SaldoDTO.class
        );

        if (saldos.isEmpty()) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_NO_SE_ENCONTRARON_SALDOS, Mensaje.TIPO_ALERTA);
        }

        List<DetalleDTO> detalleDTO = new ArrayList<>();
        for (SaldoDTO saldo : saldos) {
            BigDecimal saldoCambio = saldo.getSaldoFinal().multiply(nuevoTcFijo).divide(tcFijo, RoundingMode.HALF_UP);
            DetalleDTO detalleGenerar = this.asignarDebeHaber(saldo, saldoCambio.subtract(saldo.getSaldoFinal()));
            if (detalleGenerar != null) {
                detalleDTO.add(detalleGenerar);
            }
        }
        List<BigDecimal> debe = detalleDTO.stream().map(p -> p.getDebe()).collect(Collectors.toList());
        List<BigDecimal> haber = detalleDTO.stream().map(p -> p.getHaber()).collect(Collectors.toList());

        BigDecimal totalDebe = debe.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = haber.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        int diferencia = totalDebe.subtract(totalHaber).signum();
        DetalleDTO detalle = new DetalleDTO();
        detalle.setMonedaId(MonedaEnum.DOLARES.ordinal());
        switch (diferencia) {
            case 0:
                grabarTipoCambio(nuevoTcFijo);
                throw new ExcepcionNegocio(Mensaje.ASIENTO_TIPO_CAMBIO_ACTUALIZADO, Mensaje.TIPO_EXITO);

            case 1:
                detalle.setCuentaId(cuentaServicio.traerCuentaId(utilitarioServicio.traerCuentaIngreso()));
                detalle.setCuentaCuenta(utilitarioServicio.traerCuentaIngreso());
                detalle.setDebe(BigDecimal.ZERO);
                detalle.setHaber(totalDebe.subtract(totalHaber).abs());
                detalleDTO.add(detalle);
                break;
            case -1:
                detalle.setCuentaId(cuentaServicio.traerCuentaId(utilitarioServicio.traerCuentaEgresos()));
                detalle.setCuentaCuenta(utilitarioServicio.traerCuentaEgresos());
                detalle.setDebe(totalDebe.subtract(totalHaber).abs());
                detalle.setHaber(BigDecimal.ZERO);
                detalleDTO.add(detalle);
                break;
        }

        int tipoAsientoId_MONEDA_EXTRANJERA = utilitarioServicio.traerTipoAsientoActualizacionME();
        List<TipoAsientoDTO> listaTipoAsiento = utilitarioServicio.traerListaTipoAsiento();
        String glosa = listaTipoAsiento.stream().filter(x -> x.getId() == tipoAsientoId_MONEDA_EXTRANJERA).findFirst().get().getGlosa();
        asientoDTO.setGlosa(glosa);
        asientoDTO.setDetalles(detalleDTO);
        asientoDTO.setFechaString(Helper.convertirAFecha(fechafinal));
        asientoDTO.setMonedaId(MonedaEnum.DOLARES.ordinal());
        asientoDTO.setTipoAsientoId(tipoAsientoId_MONEDA_EXTRANJERA);
        return asientoDTO;

    }

    private DetalleDTO asignarDebeHaber(SaldoDTO saldo, BigDecimal nuevoSaldo) {
        nuevoSaldo = nuevoSaldo.setScale(2, RoundingMode.HALF_EVEN);
        DetalleDTO detalle = new DetalleDTO();
        detalle.setCuentaId(saldo.getCuentaId());
        detalle.setCuentaCuenta(saldo.getCuenta());
        detalle.setMonedaId(MonedaEnum.DOLARES.ordinal());

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        if (saldo.getTipoCuentaId() == TipoCuentaEnum.DEUDOR.ordinal()) {
            detalle.setDebe(nuevoSaldo.signum() == 1 ? nuevoSaldo : BigDecimal.ZERO);
            detalle.setHaber(nuevoSaldo.signum() == -1 ? nuevoSaldo.abs() : BigDecimal.ZERO);
        }
        if (saldo.getTipoCuentaId() == TipoCuentaEnum.ACREEDOR.ordinal()) {
            detalle.setDebe(nuevoSaldo.signum() == -1 ? nuevoSaldo.abs() : BigDecimal.ZERO);
            detalle.setHaber(nuevoSaldo.signum() == 1 ? nuevoSaldo : BigDecimal.ZERO);
        }
        return detalle;
    }

    private Asiento validaAsiento(AsientoDTO asientoDTO, int usuarioId) throws ExcepcionNegocio {
        Asiento asiento = new Asiento();
        PeriodoDTO periodoVigenteDTO = utilitarioServicio.traerPeriodoVigente();
        Date fechaActual = new Date();
        this.validarMaestro(asiento, asientoDTO, periodoVigenteDTO, usuarioId, fechaActual);

        BigDecimal totalDebe = new BigDecimal(0);
        BigDecimal totalHaber = new BigDecimal(0);

        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        for (DetalleDTO detalleDTO : asientoDTO.getDetalles()) {
            Detalle detalle = new Detalle();
            Moneda moneda = new Moneda();
            moneda.setId(detalleDTO.getMonedaId());
            this.validarDetalle(detalle, detalleDTO);
            detalle.setEditadoPor(usuario);
            detalle.setCreadoPor(usuario);
            detalle.setFechaCreacion(fechaActual);
            detalle.setFechaEdicion(fechaActual);
            detalle.setMoneda(moneda);
            totalDebe = totalDebe.add(detalleDTO.getDebe());
            totalHaber = totalHaber.add(detalleDTO.getHaber());
            asiento.agregarDetalle(detalle);
        }

        if (totalDebe.compareTo(totalHaber) != 0) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA, Mensaje.TIPO_ALERTA);

        }
        asiento.setEstado(estadoDAO.traerPorId(Estado.class, EstadoEnum.ACTIVO.ordinal()));
        if (asiento.getEstado() == null) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_ESTADO_NO_EXISTE, Mensaje.TIPO_ALERTA);
        }

        asiento.setNumero(this.traerNumeroDeAsiento(periodoVigenteDTO.getId()));
        return asiento;
    }

    private void nuevoDesdeExcel(AsientoDTO asientoDTO, ProcedenciaEnum procedencia, int usuarioId) throws ExcepcionNegocio {
        try {
            this.guardar(this.armarAsientosPorMoneda(asientoDTO), procedencia, usuarioId);
        } catch (ExcepcionNegocio excepcionNegocio) {
            if (excepcionNegocio.getTipoMensaje().equals(Mensaje.TIPO_EXITO)) {
                throw new ExcepcionNegocio(excepcionNegocio.getMessage() + ". Asientos generados : " + excepcionNegocio.getNumerosGenerados(), excepcionNegocio.getTipoMensaje(), excepcionNegocio.getIdGenerados(), excepcionNegocio.getNumerosGenerados());
            } else {
                throw excepcionNegocio;
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    private List<AsientoDTO> armarAsientosPorMoneda(AsientoDTO asientoDTO) throws ExcepcionNegocio {
        List<AsientoDTO> listaAsientoDTO = new ArrayList<>();
        int posicionMoneda = 2;
        char monedaConsolidada = '0';

        List<Integer> monedas = asientoDTO.getDetalles().stream().map(p -> Integer.parseInt(Character.toString(p.getCuentaCuenta().charAt(posicionMoneda)))).distinct().collect(Collectors.toList());
        List<Integer> monedasActivas = utilitarioServicio.traerListaMonedaActiva().stream().map(x -> x.getId()).distinct().collect(Collectors.toList());

        if (!Helper.existeDato(monedasActivas, monedas)) {
            throw new ExcepcionNegocio(Mensaje.INTEGRACION_SYSONE_MONEDA_INVALIDA, Mensaje.TIPO_ALERTA);
        }

        BigDecimal haber = asientoDTO.getDetalles().stream().map(p -> p.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal debe = asientoDTO.getDetalles().stream().map(p -> p.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);

        if ((debe.subtract(haber)).signum() != 0) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA, Mensaje.TIPO_ALERTA);
        }

        List<DetalleDTO> detalleSoles = asientoDTO.getDetalles().stream().filter(
                x -> Character.toString(x.getCuentaCuenta().charAt(posicionMoneda)).equalsIgnoreCase(String.valueOf(MonedaEnum.SOLES.ordinal())) && (x.getDebe().add(x.getHaber())).signum() != 0
        ).collect(Collectors.toList());

        detalleSoles.forEach(x -> {
            x.setMonedaId(MonedaEnum.SOLES.ordinal());
        });

        BigDecimal totalDebeAsiento = detalleSoles.stream().map(x -> x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaberAsiento = detalleSoles.stream().map(x -> x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean separarPorMoneda = true;

        if (totalDebeAsiento.compareTo(totalHaberAsiento) != 0) {
            separarPorMoneda = false;
        }

        List<DetalleDTO> detalleDolares = asientoDTO.getDetalles().stream().filter(x -> Character.toString(
                x.getCuentaCuenta().charAt(posicionMoneda)).equalsIgnoreCase(String.valueOf(MonedaEnum.DOLARES.ordinal())) && (x.getDebe().add(x.getHaber())).signum() != 0
        ).collect(Collectors.toList());

        detalleDolares.forEach(x -> {
            x.setMonedaId(MonedaEnum.DOLARES.ordinal());
        });

        totalDebeAsiento = detalleDolares.stream().map(x -> x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalHaberAsiento = detalleDolares.stream().map(x -> x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDebeAsiento.compareTo(totalHaberAsiento) != 0) {
            separarPorMoneda = false;
        }

        if (!separarPorMoneda) {
            List<DetalleDTO> detalleConsolodidado = new ArrayList<>();
            detalleConsolodidado.addAll(detalleSoles);
            detalleConsolodidado.addAll(detalleDolares);
            listaAsientoDTO.add(this.armarAsiento(asientoDTO, detalleConsolodidado, MonedaEnum.DOLARES.ordinal(), monedaConsolidada));
            return listaAsientoDTO;
        }

        if (detalleSoles.size() > 0) {
            listaAsientoDTO.add(this.armarAsiento(asientoDTO, detalleSoles, MonedaEnum.SOLES.ordinal(), monedaConsolidada));
        }
        if (detalleDolares.size() > 0) {
            listaAsientoDTO.add(this.armarAsiento(asientoDTO, detalleDolares, MonedaEnum.DOLARES.ordinal(), monedaConsolidada));
        }

        return listaAsientoDTO;
    }

    private AsientoDTO armarAsiento(AsientoDTO asientoDTO, List<DetalleDTO> detalles, int monedaId, char monedaConsolidada) throws ExcepcionNegocio {
        AsientoDTO localAsientoDTO = new AsientoDTO();
        localAsientoDTO.setFechaString(asientoDTO.getFechaString());
        localAsientoDTO.setMonedaId(monedaId);
        localAsientoDTO.setTipoAsientoId(asientoDTO.getTipoAsientoId());
        localAsientoDTO.setGlosa(utilitarioServicio.traerListaTipoAsiento().stream().filter(x -> x.getId() == asientoDTO.getTipoAsientoId()).findFirst().get().getGlosa());
        localAsientoDTO.setAgenciaId(asientoDTO.getAgenciaId());

        for (DetalleDTO detalle : detalles) {
            detalle.setCuentaId(cuentaServicio.traerCuentaId(Helper.transformaCuentaMoneda(detalle.getCuentaCuenta(), monedaConsolidada)));
        }

        localAsientoDTO.setDetalles(detalles);
        return localAsientoDTO;
    }

    /**
     * Validacion necesaria cuando se ingresan desde pantalla via excel
     *
     * @return
     */
    private boolean estaMonedaDentroDeCuenta(AsientoDTO asientoDTO) throws ExcepcionNegocio {
        Long cuentasVacias = asientoDTO.getDetalles().stream().filter(x -> x.getCuentaCuenta().length() == 0).count();
        int posicionMoneda = 2;
        if (cuentasVacias != 0) {
            throw new ExcepcionNegocio(Mensaje.CUENTA_CONTABLE_VACIA, Mensaje.TIPO_ALERTA);
        }
        Long cuentasMenosCuatroDigitos = asientoDTO.getDetalles().stream().filter(x -> x.getCuentaCuenta().length() < 4).count();
        if (cuentasMenosCuatroDigitos != 0) {
            throw new ExcepcionNegocio(Mensaje.ASIENTO_CUENTA_MENOS_DE_CUATRO_DIGITOS, Mensaje.TIPO_ALERTA);
        }

        Integer monedas = asientoDTO.getDetalles().stream().map(
                p -> Integer.parseInt(Character.toString(p.getCuentaCuenta().charAt(posicionMoneda)))
        ).reduce(0, Integer::sum);

        if (monedas == 0) {
            return false;
        }
        return true;
    }

    private void verificarActualizacionDetalle(Detalle detalleBase, DetalleDTO nuevoDetalle, Date fechaEdicion, Usuario usuario) {
        if (detalleBase.getDebe().compareTo(nuevoDetalle.getDebe()) != 0
                || detalleBase.getHaber().compareTo(nuevoDetalle.getHaber()) != 0
                || detalleBase.getCuenta().getId() != nuevoDetalle.getCuentaId()
                || detalleBase.getMoneda().getId() != nuevoDetalle.getMonedaId()) {

            detalleBase.setEditadoPor(usuario);
            detalleBase.setFechaEdicion(fechaEdicion);
        }
    }

    //</editor-fold>
}
