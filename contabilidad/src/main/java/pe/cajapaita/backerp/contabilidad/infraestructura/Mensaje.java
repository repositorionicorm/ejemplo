/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.cajapaita.backerp.contabilidad.infraestructura;

/**
 *
 * @author hnole
 */
public class Mensaje {

    public static final String TIPO_EXITO = "success";
    public static final String TIPO_ERROR = "danger";
    public static final String TIPO_ALERTA = "warning";
    public static final String TIPO_OCULTO = "hide";

    public static final String ERROR_CONEXION_BASE = "Se produjo un error de conexión a la Base de Datos";
    public static final String ERROR_GENERAL = "Se produjo un error en la aplicación. Comunicarse con TI";
    public static final String ERROR_PAGINA_NO_ENCONTRADA = "No se encontro la ruta solicitada";
    public static final String VARIABLES_NO_CARGADAS = "Variables no han sido cargadas. Comunicarse con TI";
    public static final String OPERACION_CORRECTA = "Operación procesada correctamente";
            
    public static final String CUENTA_NO_EXISTEN = "No existen cuentas";
    public static final String CUENTA_NO_EXISTE = "No existe cuenta";
    public static final String CUENTA_CONTABLE_VACIA = "Cuenta contable vacía";
    public static final String CUENTA_CONTABLE_SOLO_DIGITOS = "Ingrese sólo dígitos en cuenta contable";
    public static final String CUENTA_CONTABLE_SOLO_PARES = "Sólo se acepta cuentas pares";
    public static final String CUENTA_CONTABLE_TERCER_DIGITO_CERO = "Tercer dígito de cuenta debe ser cero";
    public static final String CUENTA_DESCRIPCION_VACIA = "Descripción de cuenta vacía";
    public static final String CUENTA_CONTABLE_EXISTE = "Cuenta contable ya existe";
    public static final String CUENTA_CONTABLE_NO_EXISTE = "Cuenta contable no existe";
    public static final String CUENTA_PADRE_NO_EXISTE = "Cuenta superior no existe";
    public static final String CUENTA_NO_COINCIDE_CON_PADRE = "Cuenta contable no coincide con cuenta superior";
    public static final String CUENTA_ES_SBS = "Cuenta considerada en Plan SBS";
    public static final String CUENTA_NO_ES_ANALITICA = " Cuenta no es analítica";
    public static final String CUENTA_TIENE_MOVIMIENTOS = "Cuenta tiene movimientos";

    public static final String ASIENTO_SELECCIONE_PERIODO = "Seleccione periodo";
    public static final String ASIENTO_PERIODO_NO_EXISTE = "Periodo no existe";
    public static final String ASIENTO_NO_ENCONTRADOS = "No se encontraron asientos con estos criterios";
    public static final String ASIENTO_NO_ENCONTRADOS_PARA_PERIODO = "No se encontraron asientos para este periodo";
    public static final String ASIENTO_NO_EXISTE = "El asiento no existe";
    public static final String ASIENTO_DETALLE_NO_ENCONTRADO = "No se encontraron detalles";
    public static final String ASIENTO_SELECCIONE_FECHA = "Seleccione fecha de asiento";
    public static final String ASIENTO_FORMATO_FECHA_INCORRECTA = "Formato de fecha incorrecto";
    public static final String ASIENTO_SELECCIONE_MONEDA = "Seleccione moneda";
    public static final String ASIENTO_MONEDA_NO_EXISTE = "Moneda no existe";
    public static final String ASIENTO_SELECCIONE_TIPO_ASIENTO = "Seleccione tipo de asiento";
    public static final String ASIENTO_TIPO_ASIENTO_NO_EXISTE = "Tipo de asiento no existe";
    public static final String ASIENTO_INGRESE_GLOSA = "Ingrese glosa";
    public static final String ASIENTO_PROCEDENCIA_NO_EXISTE = "Procedencia no existe";
    public static final String ASIENTO_ESTADO_NO_EXISTE = "Estado no existe";
    public static final String ASIENTO_USUARIO_NO_EXISTE = "Usuario no existe";
    public static final String ASIENTO_DEBE_HABER_INVALIDO = "Debe y/o haber deben ser mayor a cero";
    public static final String ASIENTO_DEBE_HABER_NO_CUADRA = "Debe y haber no cuadra";
    public static final String CUENTA_NO_ACTIVA = "Cuenta no esta activa";
    public static final String ASIENTO_DE_PERIODO_NO_VIGENTE = "No se puede alterar asientos de un periodo cerrado";
    public static final String ASIENTO_YA_ESTA_RENUMERADO = "El periodo actual no necesita ser renumerado";
    public static final String ASIENTO_RENUMERACION_NO_PROCESADA = "No se ha procesado la renumeración en este periodo";
    public static final String ASIENTO_BALANCE_NO_PROCESADO = "No se ha procesado balance para este periodo";
    public static final String ASIENTO_ULTIMA_FECHA_RENUMERACION = "La última fecha de renumeración fue el ";
    public static final String ASIENTO_ULTIMA_FECHA_MAYORIZACION = "La última fecha que se proceso el balanace fue el ";
    public static final String ASIENTO_NECESITA_RENUMERACION = "El periodo actual necesita ser renumerado";
    public static final String ASIENTO_NECESITA_CANCELAR_INTEGRACION = "Este asiento procede de una integración del Sysone; cancele la integración";
    public static final String ASIENTO_NECESITA_CANCELAR_INTEGRACION_PROVISION="Este asiento procede de una integración de la Provisión; cancele la integración";
    public static final String ASIENTO_NUMERO_MAYOR_CERO = "El monto ingresado debe ser mayor a cero";
    public static final String ASIENTO_NO_SE_ENCONTRARON_SALDOS = "No se encontraron saldos en dólares";
    public static final String ASIENTO_NO_SE_PUEDO_ACTUALIZAR = "No se puede actualizar Tipo de Cambio, ya existe un asiento por Tipo de Cambio";
    public static final String ASIENTO_TIPO_CAMBIO_ACTUALIZADO = "Tipo de cambio actualizado correctamente";
    public static final String ASIENTO_NO_CONTIENE_AGENCIA = "Asiento no contiene agencia";
    public static final String ASIENTO_AGENCIA_NO_EXISTE = "Agencia no existe";
    public static final String ASIENTO_CUENTA_MENOS_DE_CUATRO_DIGITOS="Una de las cuentas tiene menos de cuatro dígitos";

    public static final String INTEGRACION_SIAFC_ARCHIVO_VACIO = "El archivo esta vacio";
    public static final String INTEGRACION_SIAFC_ARCHIVO_MONEDA_NO_VALIDA = "El archivo contiene moneda no valida";
    public static final String INTEGRACION_SIAFC_ARCHIVO_DIFERENTES_FECHAS = "El archivo contiene diferente fechas";
    public static final String INTEGRACION_SIAFC_ARCHIVO_TOTALES_NO_COINCIDEN = "Debe y/o haber no cuadra";
    public static final String INTEGRACION_SERVICIO_NO_DISPONIBLE = "El servicio no se encuentra disponible";
    public static final String INTEGRACION_SIAFC_ARCHIVO_YA_PROCESADO = "No procesado. Tipo de asiento ya existe, números: ";
    public static final String INTEGRACION_NO_ENCONTRADAS = "No se encontraron integraciones";
    public static final String INTEGRACION_SYSONE_INCOMPLETA = "Falta integrar asientos";
    public static final String INTEGRACION_SIAFC_ARCHIVO_NO_PERTENECE_SIAFC="El archivo no corresponde al SIAFC";
    public static final String INTEGRACION_SIAFC_ARCHIVOS_DISPONIBLES="No se encontraron archivos disponibles";
    public static final String INTEGRACION_SIAFC_ARCHIVO_PERIODO_VIGENTE="Archivo no corresponde al periodo vigente";
    public static final String INTEGRACION_SIAFC_MAS_DE_UNA_AGENCIA="El archivo contiene más de una agencia";
    public static final String INTEGRACION_SIAFC_NO_CONTIENE_AGENCIA="El archivo no contiene agencia";
    public static final String INTEGRACION_SIAFC_AGENCIA_NO_VALIDA="El archivo contiene una agencia no válida";
    
    public static final String CIERRE_BALANCE_NO_PROCESADO = "No se ha procesado el balance para el periodo";
    public static final String CIERRE_BALANCE_PROCESADO = "El balance fue procesado el ";
    public static final String CIERRE_CORRECTO = "El cierre se ejecutó correctamente, nuevo periodo: ";
    public static final String CIERRE_NO_PUEDE_PROCESAR = "No se puede ejecutar el cierre";
    public static final String CIERRE_NUEVO_TC_IGUAL="El nuevo tipo de cambio es igual al anterior";
    public static final String CIERRE_PROCESANDO="No puede ejecutar esta operación. En estos momentos otro usuario está procesando el cierre";
    public static final String CIERRE_ASIENTOS_NO_PROCESADOS_BALANCE="No se puede ejecutar el cierre, hay asientos que no han sido procesados en el balance. Debe procesar Balance";
    public static final String CIERRE_ASIENTO_ME_NO_EXISTE="No se encontró Asiento de Actualización ME";
    public static final String CIERRE_ASIENTO_ME_MAS_DE_UNO="Se encontró mas de 1 Asiento de Actualización ME";
    public static final String CIERRE_PROCESADO="Procesado";
    public static final String CIERRE_VALIDADO="Validado";
    public static final String CIERRE_NO_EXISTE_CUENTAS_ORDEN="No existen cuentas de orden en el periodo";
    public static final String CIERRE_CUENTAS_ORDEN_NO_CUADRA="Existen cuentas de orden que no cuadran";
    public static final String CIERRE_ERROR_ACTIVAR_PERIODO="Ocurrió un error al activar el nuevo periodo";
    
    public static final String INTEGRACION_SYSONE_NO_ENCONTRADAS = "No se encontraron integraciones";
    public static final String INTEGRACION_SYSONE_ASIENTO_YA_INTEGRADO = "Asiento ya fue integrado";
    public static final String INTEGRACION_SYSONE_NO_PENDIENTE = "Integración no se encuentra pendiente";
    public static final String INTEGRACION_SYSONE_MONEDA_INVALIDA = "Asiento contiene moneda inválida";
    public static final String INTEGRACION_SYSONE_TRANSACCION_NO_ENCONTRADA = "Transacción no encontrada";
    public static final String INTEGRACION_SYSONE_ESTADO_NO_PERMITIDO = "El estado del asiento no esta permitido para esta operación";
    public static final String INTEGRACION_SYSONE_NO_EXISTE = "No existe la integración solicitada";
    public static final String INTEGRACION_SYSONE_OTRO_PERIODO = "No se puede cancelar integración de otro periodo";
    public static final String INTEGRACION_SYSONE_NO_PUEDE_EDITAR = "No puede editar asiento generado por una integración";
    public static final String INTEGRACION_SYSONE_ASIENTOS_GENERADOS = "Operación correcta. Se generaron los siguientes asientos: ";

    public static final String INTEGRACION_EXCEL_CUENTA_VACIA="FALTA CUENTA EN LA FILA ";
    public static final String INTEGRACION_EXCEL_DATO_INCORRECTO="HAY UN DATO INCORRECTO EN LA FILA ";
    public static final String INTEGRACION_EXCEL_ARCHIVO_NO_EXCEL="El archivo seleccionado no es un excel,debe elegir un archivo con extensión .xlsx";
    public static final String INTEGRACION_EXCEL_CAMPO_NO_NUMERICO="Hay un dato incorrecto en ";
    
    public static final String INTEGRACION_AUTOMATICA_NO_CORRESPONDE_PERIODO="La fecha de integracion no corresponde al periodo vigente";
    
    public static final String REPORTE_NO_EXISTE="El reporte solicitado no existe";
    
    public static final String REPORTE_ERROR_ZIP = "Error al generar el archivo zip";
    
    public static final String FECHA_NO_PERTENECE_AL_PERIODO="Fecha no pertenece al periodo vigente";

    public static final String LOGEO_ERROR_CREDENCIALES = "Usuario y/o contraseña inválidos";
    
    //PARA REPORTE ENCAJE
    public static final String ENCAJE_BANCO="BANCO CENTRAL DE RESERVA DEL PERÚ - DEPARTAMENTO DE ADMINISTRACIÓN DE ENCAJES";
    public static final String ENCAJE_DESCRIPCION="OBLIGACIONES SUJETAS A ENCAJE";
    public static final String CMAC_PAITA="CAJA MUNICIPAL DE AHORRO Y CRÉDITO DE PAITA SA";
    

}
