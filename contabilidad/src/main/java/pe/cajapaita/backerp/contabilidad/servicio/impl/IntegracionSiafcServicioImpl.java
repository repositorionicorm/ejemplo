/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import net.sf.jasperreports.engine.util.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import pe.cajapaita.backerp.contabilidad.consulta.Alias;
import pe.cajapaita.backerp.contabilidad.consulta.Consulta;
import pe.cajapaita.backerp.contabilidad.consulta.Equivalencia;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionBetweenDate;
import pe.cajapaita.backerp.contabilidad.consulta.RestriccionIgual;
import pe.cajapaita.backerp.contabilidad.dao.IRepositorioBaseDao;
import pe.cajapaita.backerp.contabilidad.dto.ArchivoSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.AsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.PeriodoDTO;
import pe.cajapaita.backerp.contabilidad.dto.RespuestaIntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.entidad.Asiento;
import pe.cajapaita.backerp.contabilidad.infraestructura.EstadoEnum;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Helper;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.infraestructura.ProcedenciaEnum;
import pe.cajapaita.backerp.contabilidad.servicio.IAsientoServicio;
import pe.cajapaita.backerp.contabilidad.servicio.ICuentaServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IIntegracionSiafcServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IWebServicio;

/**
 *
 * @author dev-out-02
 */
@Service
@Transactional
@PropertySource(value = {"file:/opt/aplicaciones/contabilidad/sistema.properties"})
public class IntegracionSiafcServicioImpl implements IIntegracionSiafcServicio {

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    @Autowired
    private IAsientoServicio asientoServicio;

    @Autowired
    private IWebServicio webServicio;

    @Autowired
    private IRepositorioBaseDao<Asiento> asientoDAO;

    private final Logger logger = Logger.getLogger(IntegracionSiafcServicioImpl.class);

    @Override
    public List<ArchivoSIAFCDTO> listaArchivosSIAFC() throws ExcepcionNegocio {
        List<ArchivoSIAFCDTO> misArchivos = new ArrayList<ArchivoSIAFCDTO>();
        List<TipoAsientoDTO> tiposAsientos = utilitarioServicio.traerListaTipoAsientoSiafc();
        try {
            List<String> listaArchivos = webServicio.traerArchivosSiafc();
            if (listaArchivos.isEmpty()) {
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SIAFC_ARCHIVOS_DISPONIBLES, Mensaje.TIPO_ALERTA);
            }
            listaArchivos.stream().forEach(
                    p -> {
                        String nombre = String.valueOf(p.toUpperCase().charAt(0));
                        if (tiposAsientos.stream().anyMatch(x -> x.getModulo().toUpperCase().equals(nombre) && x.getProcedenciaId() == ProcedenciaEnum.SIAFC.ordinal())) {
                            ArchivoSIAFCDTO archivo = new ArchivoSIAFCDTO();
                            archivo.setNombre(p);
                            misArchivos.add(archivo);
                        }
                    }
            );
            return misArchivos;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<DetalleSIAFCDTO> detalleArchivoSIAFC(String nombreArchivo) throws ExcepcionNegocio {
        try {
            List<DetalleSIAFCDTO> detalle = webServicio.traerDetalleArchivoSiafc(nombreArchivo);
            return detalle;
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<RespuestaIntegracionDTO> integrarArchivosSIAFC(List<ArchivoSIAFCDTO> listaAsientos, int usuarioId) throws ExcepcionNegocio {
        List<RespuestaIntegracionDTO> listaRespuesta = new ArrayList<>();
        for (ArchivoSIAFCDTO archivo : listaAsientos) {
            String fecha = "";
            String mensajeRespuesta = "";
            boolean errorEjecucion = false;

            try {
                //TRAE DETALLE DEL ARCHIVO DESDE EL SERVICIOREST
                List<DetalleSIAFCDTO> detalle = webServicio.traerDetalleArchivoSiafc(archivo.getNombre());

                //VALIDA LA FECHA,MONEDA Y QUE NO ESTE VACIO
                List resultadoValidacion = this.validaArchivo(detalle, archivo.getNombre());

                List<AsientoDTO> misAsientos = new ArrayList<>();
                List<List<DetalleSIAFCDTO>> listasDetalles = new ArrayList<>();

                //VERIFICA QUE TODAS LAS VALIDACIONES SE HAYAN CUMPLIDO
                if (resultadoValidacion.get(0).equals(true)) {
                    List<Integer> listaMonedasContenidas = detalle.stream().map(p -> p.getMonedaId()).distinct().collect(Collectors.toList());

                    for (Integer monedaId : listaMonedasContenidas) {
                        List<DetalleSIAFCDTO> subDetalle = new ArrayList<>();
                        subDetalle = detalle.stream().filter(x -> x.getMonedaId() == monedaId).collect(Collectors.toList());
                        if (subDetalle.size() <= 0 || !this.validaTotales(subDetalle)) {
                            mensajeRespuesta = Mensaje.INTEGRACION_SIAFC_ARCHIVO_TOTALES_NO_COINCIDEN;
                            //errorEjecucion = true;
                            listasDetalles= new ArrayList<>();
                            listasDetalles.add(detalle);
                            break;
                        }
                        listasDetalles.add(subDetalle);
                    }
                    if (errorEjecucion) {
                        listaRespuesta.add(this.construirRespuesta(fecha, mensajeRespuesta));
                        continue;
                    }

                    for (List<DetalleSIAFCDTO> miDetalle : listasDetalles) {

                        fecha = miDetalle.stream().findAny().get().getFechaString();
                        /**
                         * *ARMANDO EL ASIENTODTO**
                         */
                        Pair<RespuestaIntegracionDTO, AsientoDTO> rptaArmarAsiento = this.armarAsiento(miDetalle, fecha, archivo);
                        RespuestaIntegracionDTO respuesta = rptaArmarAsiento.first();
                        AsientoDTO asientoDTO = rptaArmarAsiento.second();

                        if (asientoDTO == null) {
                            listaRespuesta.add(respuesta);
                            errorEjecucion = true;
                            break;
                        }
                        misAsientos.add(asientoDTO);
                    }
                    if (errorEjecucion) {
                        continue;
                    }

                    if (misAsientos.size() > 0) {
                        String mensajeError = "";
                        AsientoDTO asientoTemp = misAsientos.get(0);
                        String archivoRegistrado = this.verificarRegistroArchivo(asientoTemp);
                        if (archivoRegistrado.length() == 0) {
                            try {                                
                                asientoServicio.guardar(misAsientos, ProcedenciaEnum.SIAFC, usuarioId);
                            } catch (ExcepcionNegocio en) {
                                if (!en.getTipoMensaje().equals(Mensaje.TIPO_EXITO)) {
                                    errorEjecucion = true;
                                    logger.error(en.getMessage());
                                    listaRespuesta.add(this.construirRespuesta("", en.getMessage()));
                                }
                                mensajeError = mensajeError + " " + en.getMessage()+". Asientos generados : "+en.getNumerosGenerados();
                            }
                        } else {
                            listaRespuesta.add(this.construirRespuesta("", archivoRegistrado));
                            continue;
                        }
                        if (errorEjecucion) {
                            continue;
                        }

                        listaRespuesta.add(this.construirRespuesta(fecha, mensajeError));

                        webServicio.renombrarArchivo(archivo.getNombre());
                    }
                } else {
                    listaRespuesta.add(this.construirRespuesta(fecha, resultadoValidacion.get(1).toString()));
                }

            } catch (HttpStatusCodeException statusException) {
                listaRespuesta.add(this.construirRespuesta(fecha, statusException.getResponseBodyAsString()));
            }
        }
        return listaRespuesta;
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados"> 
    private List validaArchivo(List<DetalleSIAFCDTO> detalleArchivo, String nombre) throws ExcepcionNegocio {
        List<Integer> moneda = utilitarioServicio.traerListaMonedaActiva().stream().map(p -> p.getId()).collect(Collectors.toList());
        List<Integer> agencias=utilitarioServicio.traerListaAgencia().stream().map(p->p.getId()).collect(Collectors.toList());
        List<Integer> agenciasContenidas=detalleArchivo.stream().map(p->Integer.parseInt(p.getOficina())).distinct().collect(Collectors.toList());
        List<Integer> listaMonedasContenidas = detalleArchivo.stream().map(p -> p.getMonedaId()).distinct().collect(Collectors.toList());
        List<String> listaFechas = detalleArchivo.stream().map(p -> p.getFechaString()).distinct().collect(Collectors.toList());
        PeriodoDTO periodoDTO = utilitarioServicio.traerPeriodoVigente();
        List resultado = new ArrayList();

        List<TipoAsientoDTO> tiposAsientos = utilitarioServicio.traerListaTipoAsientoSiafc();

        nombre = String.valueOf(nombre.toUpperCase().charAt(0));
        final String nombreTemp = nombre;
        if (!tiposAsientos.stream().anyMatch(x -> x.getModulo().toUpperCase().equals(nombreTemp) && x.getProcedenciaId() == ProcedenciaEnum.SIAFC.ordinal())) {
            resultado.add(false);
            resultado.add(Mensaje.INTEGRACION_SIAFC_ARCHIVO_NO_PERTENECE_SIAFC);
            return resultado;
        }

        /*Verifica que el archivo no este vacio*/
        if (detalleArchivo.size() <= 0) {
            resultado.add(false);
            resultado.add(Mensaje.INTEGRACION_SIAFC_ARCHIVO_VACIO);
            return resultado;
        }
        /*Verificar que contenga una sola fecha*/
        if (listaFechas.size() != 1) {
            resultado.add(false);
            resultado.add(Mensaje.INTEGRACION_SIAFC_ARCHIVO_DIFERENTES_FECHAS);
            return resultado;
        }
        Date fecha = Helper.convertirAFecha(listaFechas.get(0));
        if (!Helper.between(fecha, periodoDTO.getFechaInicial(), periodoDTO.getFechaLimite())) {
            resultado.add(false);
            resultado.add(Mensaje.INTEGRACION_SIAFC_ARCHIVO_PERIODO_VIGENTE);
            return resultado;
        }
        /*Verifica que contenga las monedas validas*/
        for (Integer monedaId : listaMonedasContenidas) {
            if (moneda.stream().filter(p -> p == monedaId).findFirst().orElse(null) == null) {
                resultado.add(false);
                resultado.add(Mensaje.INTEGRACION_SIAFC_ARCHIVO_MONEDA_NO_VALIDA);
                return resultado;
            }
        }
        /*Verifica que solo contenga una agencia*/
        if(agenciasContenidas.size()>1){
            resultado.add(false);
            resultado.add(Mensaje.INTEGRACION_SIAFC_MAS_DE_UNA_AGENCIA);
            return resultado;
        }
        if(agenciasContenidas.size()<1){
            resultado.add(false);
            resultado.add(Mensaje.INTEGRACION_SIAFC_NO_CONTIENE_AGENCIA);
            return resultado;
        }
        /*Verifica que el archivo tenga una agencia correcta*/
        if(agencias.stream().filter(p->p==agenciasContenidas.get(0)).findFirst().orElse(null)==null){
            resultado.add(false);
            resultado.add(Mensaje.INTEGRACION_SIAFC_NO_CONTIENE_AGENCIA);
            return resultado;
        }
        /*Verifica que cuadren los totales del debe y haber del archivo*/
        BigDecimal totalDebe=detalleArchivo.stream().map(x->x.getDebe()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber=detalleArchivo.stream().map(x->x.getHaber()).reduce(BigDecimal.ZERO, BigDecimal::add);
        if(totalDebe.compareTo(totalHaber)!=0){
            resultado.add(false);
            resultado.add(Mensaje.ASIENTO_DEBE_HABER_NO_CUADRA);
            return resultado;
        }
        resultado.add(true);
        resultado.add("Correcto");
        return resultado;
    }

    private boolean validaTotales(List<DetalleSIAFCDTO> subDetalle) {
        List<BigDecimal> debe = subDetalle.stream().map(p -> p.getDebe()).collect(Collectors.toList());
        List<BigDecimal> haber = subDetalle.stream().map(p -> p.getHaber()).collect(Collectors.toList());

        BigDecimal totalDebe = debe.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = haber.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        if (!totalDebe.equals(totalHaber)) {
            return false;
        }
        return true;
    }

    private String verificarRegistroArchivo(AsientoDTO asiento) throws ExcepcionNegocio {
        String mensaje = "";
        try {
            Consulta consulta = new Consulta();
            consulta.agregaAlias(new Alias("tipoAsiento", "tipoAsiento"));
            consulta.agregaAlias(new Alias("estado", "estado"));
            consulta.agregaAlias(new Alias("procedencia", "procedencia"));
            consulta.agregaAlias(new Alias("agencia", "agencia"));
            consulta.agregaEquivalencia(new Equivalencia("numero", "numero"));
            consulta.agregaRestriccionBetweenDate(new RestriccionBetweenDate("fecha", Helper.convertirAFecha(asiento.getFechaString()), Helper.convertirAFecha(asiento.getFechaString())));
            consulta.agregaRestriccionIgual(new RestriccionIgual("tipoAsiento.id", asiento.getTipoAsientoId()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("estado.id", EstadoEnum.ACTIVO.ordinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("procedencia.id", ProcedenciaEnum.SIAFC.ordinal()));
            consulta.agregaRestriccionIgual(new RestriccionIgual("agencia.id", asiento.getAgenciaId()));
            List<AsientoDTO> asientosEnBase = asientoDAO.traerTodo(Asiento.class, consulta, AsientoDTO.class);

            if (asientosEnBase.size() > 0) {
                mensaje = mensaje + Mensaje.INTEGRACION_SIAFC_ARCHIVO_YA_PROCESADO;
                for (AsientoDTO a : asientosEnBase) {
                    mensaje = mensaje + " - " + a.getNumero();
                }
            }
        } catch (ExcepcionNegocio excepcionNegocio) {
            throw excepcionNegocio;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }

        return mensaje;
    }

    private RespuestaIntegracionDTO construirRespuesta(String fecha, String mensaje) {
        RespuestaIntegracionDTO respuesta = new RespuestaIntegracionDTO();
        respuesta.setFecha(fecha);
        respuesta.setResultado(mensaje);
        return respuesta;
    }

    private Pair<RespuestaIntegracionDTO, AsientoDTO> armarAsiento(List<DetalleSIAFCDTO> miDetalle, String fecha, ArchivoSIAFCDTO archivo) {
        List<DetalleDTO> listaDetalleDTO = new ArrayList<>();
        List<TipoAsientoDTO> listaTipoAsiento = utilitarioServicio.traerListaTipoAsiento();
        AsientoDTO asientoDTO = new AsientoDTO();
        RespuestaIntegracionDTO respuesta = null;

        boolean errorEjecucion = false;

        for (DetalleSIAFCDTO forDetalle : miDetalle) {
            try {
                String numCuenta = forDetalle.getCuentaCuenta();
                if (numCuenta.length() > 3) {
                    char[] temp = numCuenta.toCharArray();
                    temp[2] = '0';
                    numCuenta = new String(temp);
                }
                DetalleDTO detalleDTO = new DetalleDTO();
                detalleDTO.setDebe(forDetalle.getDebe());
                detalleDTO.setHaber(forDetalle.getHaber());
                detalleDTO.setCuentaCuenta(numCuenta);
                detalleDTO.setMonedaId(forDetalle.getMonedaId());
                listaDetalleDTO.add(detalleDTO);

            } catch (Exception ex) {
                logger.error(ex.getMessage());
                respuesta = this.construirRespuesta(fecha, Mensaje.ERROR_GENERAL);
                errorEjecucion = true;
                asientoDTO = null;
                break;
            }
        }
        if (errorEjecucion) {
            return new Pair<>(respuesta, asientoDTO);
        }

        try {
            fecha = miDetalle.stream().findAny().get().getFechaString();
            int agenciaId=Integer.parseInt(miDetalle.stream().findAny().get().getOficina());
            int monedaId = miDetalle.stream().findAny().get().getMonedaId();
            String glosa = listaTipoAsiento.stream().filter(x -> x.getId() == archivo.getTipoAsientoId()).findFirst().get().getGlosa();
            asientoDTO.setFechaString(fecha);
            asientoDTO.setTipoAsientoId(archivo.getTipoAsientoId());
            asientoDTO.setMonedaId(monedaId);
            asientoDTO.setGlosa(glosa);
            asientoDTO.setDetalles(listaDetalleDTO);
            asientoDTO.setAgenciaId(agenciaId);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            respuesta = this.construirRespuesta(fecha, Mensaje.ERROR_GENERAL);;
            asientoDTO = null;

        }
        return new Pair<>(respuesta, asientoDTO);
    }
//</editor-fold>
}
