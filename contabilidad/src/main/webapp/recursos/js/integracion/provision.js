$(document).ready(cargar);
var $tablaDetalle;
var rowActivo;
var modalElementos = ["lblMensaje", "mensajeActivarInactivar"];

function cargar() {   
    $tablaDetalle = $("#tblMostrarDetalleProvision");        
    $("#btnIntegrarAsiento").click(integrarAsiento);
    $("#btnExportarExcel").click(exportarExcel);
    $("#btnAceptar").click(activarInactivar);    
    traerIntegraciones();
}

// listar integraciones
function traerIntegraciones() {
    var listaIntegracion;    
    
    $("#contenedorTabla").hide();
    mostrarMensajeCargando(Mensajes.general_cargando, '.mensajeSysone', 'h4');
    $.getJSON("traerIntegracion", function (data) {
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, '.mensajeSysone');
        if (data.tipoMensaje === 'hide') {
            listaIntegracion = data.listaObjetos;
            $("#contenedorTabla").show();
            $("#tablaListaIntegracion").bootstrapTable({
                columns: [
                    {
                        field: 'tipoAsientoId',
                        title: 'Estado',
                        align: 'center',
                        visible: false
                    },
                    {
                        field: 'estadoId',
                        title: 'Estado',
                        align: 'center',
                        visible: false
                    },
                    {
                        field: 'agenciaId',
                        title: 'Agencia',
                        align: 'center',
                        visible: false
                    },
                    {
                        field: 'fechaString',
                        title: 'Fecha',
                        width: '7%',
                        align: 'center'
                    },
                    {
                        field: 'tipoAsientoDescripcion',
                        title: 'Tipo',
                        width: '35%',
                        align: 'left'
                    },                
                    {
                        field: 'observacion',
                        title: 'Observacion',
                        align: 'left',
                        halign: 'center'
                    },
                    {
                        title: 'Estado',
                        align: 'center',
                        width: '8%',
                        halign: 'center',
                        formatter: muestraEstado,
                    },
                    {
                        field: 'operate',
                        title: '',
                        align: 'center',
                        formatter: muestraBotones,
                        width: '8%',
                        events: eventos
                    }
                ],
                search: false,
                height: 460
            });
            $("#tablaListaIntegracion").bootstrapTable('load', listaIntegracion);
        }
    });
}

// integrar asiento
function integrarAsiento() {    
    $("#provisionIntegrando").removeClass('hide');
    $("#btnIntegrarAsiento").attr('disabled', 'disabled');
    $("#btnExportarExcel").attr('disabled', 'disabled');
    $("#btnCerrar,#btnExportarExcel,#btnCerrar,#btnCerrarModalDetalleSysone").attr('disabled', 'disabled');
    
    $.getJSON("integrarAsiento?tipoAsientoId=" + rowActivo.tipoAsientoId + "&fecha=" + rowActivo.fechaString + "&agenciaId=" + rowActivo.agenciaId, function (data) {
        $("#provisionIntegrando").addClass('hide');
        $("#modalDetalleProvision").modal('hide');
        traerIntegraciones();
        mostrarMensajes(data.tipoMensaje, data.mensaje, "body");
    });
}

// exportar a excel
function exportarExcel() {
    var dataDetalle;
    var dataTypes = {
        moneda: "String",
        cuenta: "String",
        debe: "Number",
        haber: "Number"
    };
    var titulos = {
        Moneda: "String",
        Cuenta: "String",
        Debe: "Number",
        Haber: "Number"
    };
    dataDetalle = $tablaDetalle.bootstrapTable('getData');
    dataDetalle.push({moneda: '', cuenta: 'Total', debe: parseFloat(totalDebe.replace(",", "").replace("´", "")), haber: parseFloat(totalHaber.replace(",", "").replace("´", ""))});
    $("#btnExportarExcel").attr("disabled", "disabled");
    descargarExcel(dataDetalle, $("#lblAsiento").html() + ".xls", dataTypes, titulos);
}

// Estado
function muestraEstado(value, row, index) {
    return row.estadoColor;
}

// Botones
function muestraBotones(value, row, index) {
    if (row.estadoId == estadosSistema.INTEGRADO)
        return [
            '<button style="margin: 2px 5px" class="btn btn-xs btn-default verNumero" href="javascript:void(0)" title="Ver número asiento">',
            '<i class="text-success glyphicon glyphicon-eye-open"></i>',
            '</button>',
            '<button style="margin: 2px 5px" class="btn btn-xs btn-default cancelar" href="javascript:void(0)" title="Cancelar Integración">',
            '<i class="text-danger glyphicon glyphicon-remove-circle"></i>',
            '</button>'
        ].join('');
    if (row.estadoId == estadosSistema.PENDIENTE) {
        return [
            '<button style="margin:2px 5px" class="btn btn-xs btn-default verAsiento" href="javascript:void(0)" title="Traer Asiento">',
            '<i class="text-success glyphicon glyphicon-share"></i>',
            '</button>',
            '<button style="margin: 2px 5px" class="btn btn-xs btn-default inactivar" href="javascript:void(0)" title="Desactivar">',
            '<i class="text-danger glyphicon glyphicon-ban-circle"></i>',
            '</button>'
        ].join('');
    } else {
        return [
            '&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
            '<button style="margin: 2px 5px" class="btn btn-xs btn-default activar" href="javascript:void(0)" title="Activar">',
            '<i class="text-success glyphicon glyphicon-ok-circle"></i>',
            '</button>'
        ].join('');
    }
}

// Eventos
var eventos = {
    'click .verAsiento': function (e, value, row, index) {
        e.preventDefault();
        mostrarDetalle(row);
    },
    'click .inactivar': function (e, value, row, index) {
        $("#lblMensaje").html(Mensajes.sysone_desactivar);
        $("#lblMensaje").data("asiento", row);
        $("#modalActivarInactivar").modal('show');
        ocultarMostrarElementos(["lblMensaje"], modalElementos);
    },
    'click .activar': function (e, value, row, index) {
        $("#lblMensaje").html(Mensajes.sysone_activar);
        $("#lblMensaje").data("asiento", row);
        $("#modalActivarInactivar").modal('show');
        ocultarMostrarElementos(["lblMensaje"], modalElementos);
    },
    'click .cancelar': function (e, value, row, index) {
        $("#lblMensaje").html(Mensajes.sysone_cancelar);
        $("#lblMensaje").data("asiento", row);
        $("#modalActivarInactivar").modal('show');
        ocultarMostrarElementos(["lblMensaje"], modalElementos);
    },
    'click .verNumero':function(e,value,row,index){
        cargarNumeros(row);
        $("#modalMostarNumeroAsiento").modal('show');
    }
};

// traer asiento provision
function mostrarDetalle(row) {
    rowActivo = row;
    var listaDetalle = null;
    var tipoAsiento = row.tipoAsientoId;
    var fecha = row.fechaString;    
    var nombreAsiento = row.tipoAsientoDescripcion + ' del ' + fecha;    
        
    $("#modalDetalleProvision").modal({
            backdrop: 'static',
            keyboard: false
    });
    $("#tablaExcel").show();
    $("#btnExportarExcel").show();
    $("#btnIntegrarAsiento").show();
    $("#lblAsiento").html(nombreAsiento);    
    $("#btnIntegrarAsiento").removeAttr('disabled');
    $("#btnExportarExcel").removeAttr('disabled');
    $("#btnCerrar").removeAttr('disabled');
    $("#detalleProvisionProcesando").removeClass('hide');

    $.getJSON("traerAsiento?tipoAsientoId=" + tipoAsiento + "&fecha=" + fecha, function (data) {
        $("#detalleProvisionProcesando").addClass('hide');
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, ".mensaje");
        if (data.tipoMensaje === "hide") {
            listaDetalle = data.listaObjetos;
            $tablaDetalle.bootstrapTable({
                columns: [
                    {
                        field: 'moneda',
                        title: 'Moneda',
                        width: '3%',
                        halign: 'center',
                        align: 'center'
                    },
                    {
                        field: 'cuenta',
                        title: 'Cuenta',
                        width: '40%',
                        halign: 'left',
                        align: 'left'
                    },
                    {
                        field: 'debe',
                        title: 'Debe',
                        width: '10%',
                        halign: 'center',
                        align: 'center',
                        formatter: formatoMoneda,
                        footerFormatter: sumFormatter
                    },
                    {
                        field: 'haber',
                        title: 'Haber',
                        width: '10%',
                        halign: 'center',
                        formatter: formatoMoneda,
                        footerFormatter: sumFormatter
                    }
                ],
                showFooter: true,
                classes: 'table table-condensed',
                height: '550'
            });
            $tablaDetalle.bootstrapTable('load', listaDetalle);
            $tablaDetalle.bootstrapTable('resetView');
        } else {
            $("#tablaExcel").hide();
            $("#btnExportarExcel").hide();
            $("#btnIntegrarAsiento").hide();
        }
    });
    $("#modalDetalleProvision").modal('show');
}

function activarInactivar() {
    var asiento = $("#lblMensaje").data("asiento");
    ocultarMostrarElementos(["mensajeActivarInactivar"], modalElementos);
    mostrarMensajeCargando("Procesando por favor espere...", "mensajeActivarInactivar", "h4");
    $.ajax({
        url: 'cambiarEstadoAsiento/' + asiento.id,
        type: 'POST',
        success: function (data) {
            successActivarDesactivar(data);
        },
        error: function (jqXHR, textStatus, errorThrown) {
            respuestaError(jqXHR, "#mensajeActivarInactivar");
        }
    });
}
function successActivarDesactivar(data) {
    mostrarMensajes(data.tipoMensaje, data.mensaje, "body");
    ocultarMostrarElementos(["lblMensaje"], modalElementos);
    traerIntegraciones();
    $("#modalActivarInactivar").modal('hide');
}

function cargarNumeros(row){
    $.ajax({
       url:'traerNumerosAsientos/'+row.id,
        type: 'GET',
        success: function (data, textStatus, jqXHR) {
            var asientos="";
            $.each(data.objeto,function(i,item){
                asientos=asientos+","+item;
            });
            var numeroAsientos=asientos.substring(1,asientos.length);
            $("#listaNumeros").html(numeroAsientos);
        },
        error: function (jqXHR, textStatus, errorThrown) {
             respuestaError(jqXHR, "#errorMostrarNumero");
        }
       
    });
}

function respuestaError(data, contenedor) {
    if (data.status == '404')
        mostrarMensajesContenedor(tipoMensaje.ERROR, Mensajes.general_url_no_existe, contenedor);
    else
        mostrarMensajesContenedor(tipoMensaje.ERROR, data.statusText, contenedor);
}
$(window).resize(function () {
    $('#tblMostrarDetalleProvision').bootstrapTable('resetView');
});