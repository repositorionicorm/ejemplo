/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.servicio.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import pe.cajapaita.backerp.contabilidad.dto.DetalleIntegracionDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSIAFCDTO;
import pe.cajapaita.backerp.contabilidad.dto.DetalleSysoneDTO;
import pe.cajapaita.backerp.contabilidad.dto.MonedaDTO;
import pe.cajapaita.backerp.contabilidad.dto.TipoAsientoDTO;
import pe.cajapaita.backerp.contabilidad.dto.TransaccionDTO;
import pe.cajapaita.backerp.contabilidad.infraestructura.ExcepcionNegocio;
import pe.cajapaita.backerp.contabilidad.infraestructura.Mensaje;
import pe.cajapaita.backerp.contabilidad.servicio.IUtilitarioServicio;
import pe.cajapaita.backerp.contabilidad.servicio.IWebServicio;

/**
 *
 * @author hnole
 */
@Service
@Transactional
@PropertySource(value = {"file:/opt/aplicaciones/contabilidad/sistema.properties"})
public class WebServicioImpl implements IWebServicio {

    @Autowired
    private Environment environment;

    @Autowired
    private IUtilitarioServicio utilitarioServicio;

    private final Logger logger = Logger.getLogger(WebServicioImpl.class);

    @Override
    public List<DetalleSysoneDTO> traerAsientoSysone(int tipoAsientoId, String fecha, int agenciaId) throws ExcepcionNegocio {
        RestTemplate restTemplate = new RestTemplate();
        List<DetalleSysoneDTO> asiento = new ArrayList();
        String urlTotal = "";
        try {
            String urlBase = environment.getProperty("servicioSysone.url");
            urlTotal = urlBase + "asientos/listar?" + 
                    "fecha=" + fecha + 
                    "&modulo=" + this.traerModulo(tipoAsientoId) +
                    "&agencia="+ agenciaId;

            DetalleSysoneDTO[] listaWebService = restTemplate.getForObject(urlTotal, DetalleSysoneDTO[].class);
            asiento = Arrays.asList(listaWebService);

            return asiento;
        } catch (HttpStatusCodeException exceptionCode) {
            if (exceptionCode.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("URL Servicio Sysone: " + urlTotal);
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SERVICIO_NO_DISPONIBLE, Mensaje.TIPO_ERROR);
            }

            throw new ExcepcionNegocio(exceptionCode.getResponseBodyAsString(), Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error( ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }
    
    @Override
    public List<DetalleSysoneDTO> traerAsientoProvision(int tipoAsientoId, String fecha) throws ExcepcionNegocio {
        RestTemplate restTemplate = new RestTemplate();
        List<DetalleSysoneDTO> asiento = new ArrayList();
        String urlTotal = "";
        try {
            String urlBase = environment.getProperty("servicioSysone.url");
            urlTotal = urlBase + "asientos/listarRCO?" + 
                    "fecha=" + fecha + 
                    "&modulo=" + this.traerModulo(tipoAsientoId);

            DetalleSysoneDTO[] listaWebService = restTemplate.getForObject(urlTotal, DetalleSysoneDTO[].class);
            asiento = Arrays.asList(listaWebService);

            return asiento;
        } catch (HttpStatusCodeException exceptionCode) {
            if (exceptionCode.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("URL Servicio Sysone: " + urlTotal);
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SERVICIO_NO_DISPONIBLE, Mensaje.TIPO_ERROR);
            }

            throw new ExcepcionNegocio(exceptionCode.getResponseBodyAsString(), Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error( ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<String> traerArchivosSiafc() throws ExcepcionNegocio {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = environment.getProperty("servicioDBF.url") + "listaArchivos";
            String[] lista = restTemplate.getForObject(url, String[].class);

            return Arrays.asList(lista);
        } catch (HttpStatusCodeException exceptionCode) {
            if (exceptionCode.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("URL Servicio SIAFC:" + environment.getProperty("servicioDBF.url") + "listaArchivos");
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SERVICIO_NO_DISPONIBLE, Mensaje.TIPO_ERROR);
            }
            throw new ExcepcionNegocio(exceptionCode.getResponseBodyAsString(), Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error( ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<DetalleSIAFCDTO> traerDetalleArchivoSiafc(String nombreArchivo) throws ExcepcionNegocio {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = environment.getProperty("servicioDBF.url") + "listaDetalle/" + nombreArchivo;
            DetalleSIAFCDTO[] lista = restTemplate.getForObject(url, DetalleSIAFCDTO[].class);

            return Arrays.asList(lista);
        } catch (HttpStatusCodeException exceptionCode) {
            if (exceptionCode.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("URL Servicio SIAFC:" + environment.getProperty("servicioDBF.url") + "listaDetalle/" + nombreArchivo);
            }
            throw exceptionCode;

        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public String renombrarArchivo(String nombreArchivo) throws ExcepcionNegocio {
        RestTemplate restTemplate = new RestTemplate();
        try {
            String url = environment.getProperty("servicioDBF.url") + "renombrarArchivo/" + nombreArchivo;
            String respuesta = restTemplate.getForObject(url, String.class);

            return respuesta;
        } catch (HttpStatusCodeException exceptionCode) {
            if (exceptionCode.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("URL Servicio SIAFC:" + environment.getProperty("servicioDBF.url") + "renombrarArchivo/" + nombreArchivo);
            }
            throw exceptionCode;
        } catch (Exception ex) {
            logger.error( ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<DetalleIntegracionDTO> traerDetalleIntegracionSysone(String fecha, String cuenta, int tipoAsientoId, int agenciaId) throws ExcepcionNegocio {
        RestTemplate restTemplate = new RestTemplate();
        List<DetalleIntegracionDTO> detalleAsiento = new ArrayList();
        String urlTotal = "";
        try {
            String urlBase = environment.getProperty("servicioSysone.url");
            urlTotal = urlBase + "asientos/detallarAsiento?"
                    + "fecha=" + fecha
                    + "&modulo=" + this.traerModulo(tipoAsientoId)
                    + "&cuenta=" + cuenta
                    + "&agencia="+ agenciaId;

            DetalleIntegracionDTO[] listaWebService = restTemplate.getForObject(urlTotal, DetalleIntegracionDTO[].class);
            detalleAsiento = Arrays.asList(listaWebService);
            
            for (DetalleIntegracionDTO detalleIntegracionDTO : detalleAsiento) {
                detalleIntegracionDTO.setMoneda(this.traerDenominacionIso(detalleIntegracionDTO.getMoneda()));
            }

            return detalleAsiento;
        } catch (HttpStatusCodeException exceptionCode) {
            if (exceptionCode.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("URL Servicio Sysone: " + urlTotal);
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SERVICIO_NO_DISPONIBLE, Mensaje.TIPO_ERROR);
            }

            throw new ExcepcionNegocio(exceptionCode.getResponseBodyAsString(), Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error( ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    @Override
    public List<TransaccionDTO> traerDetalleTransaccion(BigDecimal transaccion) throws ExcepcionNegocio {
        RestTemplate restTemplate = new RestTemplate();
        List<TransaccionDTO> transacciones = new ArrayList();
        String urlTotal = "";
        try {
            String urlBase = environment.getProperty("servicioSysone.url");
            urlTotal = urlBase + "asientos/detallarTransaccion?"
                    + "transaccion=" + transaccion;

            TransaccionDTO[] listaWebService = restTemplate.getForObject(urlTotal, TransaccionDTO[].class);
            transacciones = Arrays.asList(listaWebService);
            
            for (TransaccionDTO transaccionDTO : transacciones) {
                transaccionDTO.setMoneda(this.traerDenominacionIso(transaccionDTO.getMoneda()));
            }

            return transacciones;
        } catch (HttpStatusCodeException exceptionCode) {
            if (exceptionCode.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("URL Servicio Sysone: " + urlTotal);
                throw new ExcepcionNegocio(Mensaje.INTEGRACION_SERVICIO_NO_DISPONIBLE, Mensaje.TIPO_ERROR);
            }

            throw new ExcepcionNegocio(exceptionCode.getResponseBodyAsString(), Mensaje.TIPO_ERROR);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcepcionNegocio(Mensaje.ERROR_GENERAL, Mensaje.TIPO_ERROR);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="metodos privados">
    private String traerModulo(int tipoAsientoId) {
        try {
            List<TipoAsientoDTO> detalle = utilitarioServicio.traerListaTipoAsiento();
            return detalle.stream().filter(x -> x.getId() == tipoAsientoId)
                    .findFirst().get().getModulo().toUpperCase();
        } catch (Exception ex) {
            logger.error(ex.getMessage() + "- No se encontró tipo de asiento");
            return "";
        }
    }

    private String traerDenominacionIso(String moneda) {
         try {
            List<MonedaDTO> monedas = utilitarioServicio.traerListaMonedaActiva();
            return monedas.stream().filter(x -> x.getId() == Integer.parseInt(moneda))
                    .findFirst().get().getDenominacionIso();
        } catch (Exception ex) {
            logger.error(ex.getMessage()+ "- No se encontró moneda");
            return "";
        }
    }

    //</editor-fold>
}
