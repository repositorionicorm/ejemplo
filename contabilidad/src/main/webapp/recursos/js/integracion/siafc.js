$(document).ready(inciarIntegracion);
var $tablaIntegracion;
var $tablaDetalle;
var totalDebe;
var totalHaber;
var listaModulos;
function inciarIntegracion() {
    traerModulos();
    traerArchivos();
    $tablaIntegracion = $("#tablaIntegracionSiafc");
    $tablaDetalle = $("#tblMostrarDetalle");
    $("#menuIntegracion").addClass('active');
    $("#archivos").on('click', '.checkArchivo', verificarCheck);
    $("#btnExportarExcel").click(exportarExcel);
    $("#btnIntegrar").click(respuestaIntegracion);
    $("#btnNuevo").click(function () {
        $tablaIntegracion.bootstrapTable('removeAll');
        $('.checkArchivo').attr('checked', false);
        $('#btnIntegrar').attr('disabled', 'disabled');
        $("#marcarTodos").prop('checked',false);
        traerArchivos();
    });
    $("#tblCuenta").off('click-row.bs.table');
    $("#marcarTodos").click(function () {
        var estado = $(this).is(':checked');
        if (estado) {
            $(".checkArchivo").each(function (i, element) {
                if (!$(this).is(':checked'))
                    $(this).trigger('click');
            });
        } else {
            $(".checkArchivo").each(function (i, element) {
                if ($(this).is(':checked'))
                    $(this).trigger('click');
            });
        }
    });
    $tablaIntegracion.bootstrapTable({
        columns: [
            {
                formatter: mostrarIndex,
                title: 'N°',
                width: '3%',
                halign: 'center'
            },
            {
                field: 'idTipo',
                visible: false
            },
            {
                field: 'nombre',
                title: 'Archivo',
                width: '10',
                halign: 'center'
            },
            {
                field: 'descripcion',
                title: 'Descripcion',
                halign: 'center'
            },
            {
                field: 'fecha',
                title: 'Fecha',
                width: '80',
                halign: 'center'
            },
            {
                field: 'proc',
                title: 'Proc',
                width: '4%',
                halign: 'center',
                visible: false
            },
            {
                field: 'resultado',
                title: 'Resultado',
                halign: 'center'
            }
        ],
        uniqueId: 'nombre',
        classes: 'table table-hover table-condensed',
        height: '360'

    }).on('click-row.bs.table', function (e, row, $element) {
        var fecha=row.fecha;
        if (fecha == undefined) {
            mostrarDetalle(row);
            $("#btnExportarExcel").removeAttr('disabled');
        }
        else{
            if( fecha.length==0){
                mostrarDetalle(row);
                $("#btnExportarExcel").removeAttr('disabled'); 
            }
        }
        

    });

}
function mostrarDetalle(row) {
    var nombreArchivo = row.nombre;
    $("#lblArchivo").html(nombreArchivo);

    cargarDetalleArchivo(nombreArchivo);
    $("#modalDetalleArchivo").modal('show');
}
function cargarDetalleArchivo(nombreArchivo) {
    var listaDetalle = null;
    $("#tablaExcel").hide();
    mostrarMensajeCargando("Cargando datos...", '.mensaje', 'h4');
    $.getJSON("traerDetalle/" + nombreArchivo, function (data) {
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, ".mensaje");
        if (data.tipoMensaje === "hide") {
            $("#tablaExcel").show();
            $("#btnExportarExcel").show();
            listaDetalle = data.listaObjetos;
            $tablaDetalle.bootstrapTable({
                columns: [
                    {
                        field: 'oficina',
                        title: 'Ofic',
                        width: '5%',
                        halign: 'center',
                        align: 'center'
                    },
                    {
                        field: 'monedaId',
                        title: 'Moneda',
                        width: '10%',
                        halign: 'center',
                        align: 'center'
                    },
                    {
                        field: 'fechaString',
                        title: 'Fecha',
                        width: '10%',
                        halign: 'center',
                        align: 'center'
                    },
                    {
                        field: 'cuentaCuenta',
                        title: 'Cuenta',
                        halign: 'center',
                        align: 'left'
                    },
                    {
                        field: 'debe',
                        title: 'Debe',
                        width: '17%',
                        halign: 'center',
                        align: 'right',
                        formatter: formatoMoneda,
                        footerFormatter: sumFormatter
                    },
                    {
                        field: 'haber',
                        title: 'Haber',
                        width: '17%',
                        halign: 'center',
                        align: 'right',
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
        }
    });
}
function mostrarIndex(value, row, index) {
    return index + 1;
}
function verificarCheck() {
    var estado = $(this).is(':checked');
    var archivo = $(this).data('datos');

    $.each(listaModulos, function (i, item) {
        var nombre = archivo.nombre;
        if (item.modulo == nombre.toUpperCase().charAt(0)) {
            archivo.descripcion = item.descripcion;
            archivo.idTipo = item.id;
            return false;
        }
    })

    var conArchivos = null;
    var actual = $tablaIntegracion.bootstrapTable('getRowByUniqueId', archivo.nombre);
    if (estado && actual == null)
        $tablaIntegracion.bootstrapTable('append', archivo);
    else
        $tablaIntegracion.bootstrapTable('removeByUniqueId', archivo.nombre);

    $tablaIntegracion.bootstrapTable('resetView');
    conArchivos = $tablaIntegracion.bootstrapTable('getData').length;
    if (conArchivos <= 0)
        $("#btnIntegrar").attr('disabled', 'disabled');
    else
        $("#btnIntegrar").removeAttr('disabled');
}
function exportarExcel() {
    var dataDetalle;
    var dataTypes = {
        oficina: "String",
        monedaId: "String",
        fechaString: "String",
        cuentaCuenta: "String",
        debe: "Number",
        haber: "Number"
    };
    var titulos = {
        Oficina: "String",
        Moneda: "String",
        Fecha: "String",
        Cuenta: "String",
        Debe: "Number",
        Haber: "Number"
    };
    dataDetalle = $tablaDetalle.bootstrapTable('getData');
    dataDetalle.push({oficina: '', monedaId: '', fechaString: '', cuentaCuenta: 'Total', debe: parseFloat(totalDebe.replace(",", "").replace("´", "")), haber: parseFloat(totalHaber.replace(",", "").replace("´", ""))});
    $("#btnExportarExcel").attr("disabled", "disabled");
    descargarExcel(dataDetalle, $("#lblArchivo").html() + ".xls", dataTypes, titulos);
}
function respuestaIntegracion() {
    $("#btnIntegrar").attr('disabled', 'disabled');
    var listaTablaIntengracion = $tablaIntegracion.bootstrapTable('getData');
    var listaAsientos = [];
    $.each(listaTablaIntengracion, function (i, item) {
        var archivoDTO = new ArchivoSIAFCDTO(item.idTipo, item.nombre);
        listaAsientos.push(archivoDTO);
    });
    var listaAsientosJSON = JSON.stringify(new listaAsientoJSON(listaAsientos));
    $("#integrando").removeClass('hide');
    $("#contenedorTablaArchivos").hide();
    $.ajax({
        url: "integrarArchivos",
        type: 'POST',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        data: listaAsientosJSON,
        success: function (data, textStatus, jqXHR) {
            $("#integrando").addClass('hide');
            $("#contenedorTablaArchivos").show();
            $.each(data, function (i, item) {
                $tablaIntegracion.bootstrapTable('updateRow', {index: i, row: data[i]});
            });

        },
        error: function (jqXHR, textStatus, errorThrown) {
            $("#integrando").hide();
            $("#errorIntegracion").removeClass('hide');
            $("#errorIntegracion").html(jqXHR.responseText);
            $("#btnNuevo").attr('disabled', 'disabled');
            $("#btnResultado").attr('disabled', 'disabled');
        }
    });
    $("#archivos").html("");
    $("#mostrarTodos").hide();
}
$(window).resize(function () {
    $('#tablaIntegracionSiafc').bootstrapTable('resetView');
});
function traerArchivos() {
    $("#cargandoArchivos").show();
    $.ajax({
        type: 'GET',
        url: "traerArchivos",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (data, textStatus, jqXHR) {
            $("#mostrarTodos").show();
            if (data.tipoMensaje != 'hide') {
                $("#mostrarTodos").hide();
            }
            $("#cargandoArchivos").hide();
            mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeArchivos")
            $("#archivos").html("");
            var actuales = $tablaIntegracion.bootstrapTable('getData');
            $.each(data.listaObjetos, function (index, archivo) {
                $("#archivos").append('<li class=""><input id="archivo' + index + '" class="checkArchivo"  type="checkbox" />&nbsp;' + archivo.nombre + '</li>');
                $("#archivo" + index).data('datos', archivo);

            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("ERROR +++");
        }

    });
}
function traerModulos() {
    $.getJSON("../utilitario/modulos", function (data) {
        listaModulos = data;
    });
}
var listaAsientoJSON = function (listaAsientos) {
    this.listaAsientos = listaAsientos;
};
var AsientoDTO = function (tipoId) {
    this.id = null;
    this.fechaString = null;
    this.monedaId = null;
    this.tipoAsientoId = tipoId;
    this.glosa = null;
    this.detalles = null;
};
var ArchivoSIAFCDTO = function (tipoAsientoId, nombre) {
    this.nombre = nombre;
    this.tipoAsientoId = tipoAsientoId;
}