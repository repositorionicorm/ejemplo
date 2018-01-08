var globalTipo = 0;
var globalMonedaId = 0;
var globalProcedencia = 0;
var globalPeriodo = 0;
var globalTotalDebe = 0;
var globalTotalHaber = 0;
var globalAgencia = 0;
var cuentaActual;
var asientoActual;
var esSysone = false;
var numero = "0";
var tipoUsuario="S";

var todosItem = ['tablaDetalleAsientos',
    'listaCuentas',
    'listaTransacciones',
    'contenedorTblCuenta',
    'contenedorTblTransaccion',
    'cabeceraTransacciones',
    'cargandoDatosTransacciones',
    'mensajeError'];
$(document).ready(cargar);
function cargar() {
    $("#menuAsientos").addClass('active');
    $("#atrasCuentas").click(function () {
        var mostrar = ['tablaDetalleAsientos'];
        ocultarMostrarElementos(mostrar, todosItem);
    });
    $("#atrasTransacciones").click(function () {
        var mostrar = ['listaCuentas', 'contenedorTblCuenta'];
        ocultarMostrarElementos(mostrar, todosItem);
    });
    $("#btnEliminarAsiento").click(eliminarAsiento);
    llenaCombos("#cmbTipoAsiento", "../utilitario/todosTipoAsiento", "id", "descripcion");
    llenaCombos("#cmbMonedaAsiento", "../utilitario/monedaActiva", "id", "descripcion");
    llenaCombos("#cmbProcedenciaAsiento", "../utilitario/procedencia", "id", "descripcion");
    llenaCombos("#cmbAgencia", "../utilitario/agencias", "id", "abreviatura");
    llenaCombosPeriodo();

    $("#btnFiltrarListaAsientos").click(function () {
        globalTipo = $("#cmbTipoAsiento").val();
        globalMonedaId = $("#cmbMonedaAsiento").val();
        globalProcedencia = $("#cmbProcedenciaAsiento").val();
        globalPeriodo = $("#cmbPeriodo").val();
        globalAgencia = $("#cmbAgencia").val();
        numero = $("#numero").val();
        traerListaAsientos(1);
    });
    $('#modalDetalleAsiento').on('shown.bs.modal', function () {
        $("#tblMostrarDetalle").bootstrapTable('resetView');
    });
}
$("#btnAgregarAsiento").click(function () {
    location.href = "/contabilidad/asientos/crear";
});
function traerListaAsientos(pagina) {
    if (numero.length > 0 && isNaN(numero)) {
        mostrarMensajes(tipoMensaje.ALERTA, Mensajes.general_url_no_es_numero, 'body');
        $("#numero").focus();
        return;
    }
    if (numero.length == 0) {
        numero = 0;
    }
    var listaAsientos;
    $("#contenedorTabla").hide();
    $.getJSON("traerAsientos/" + globalPeriodo + "/" + globalTipo + "/" + globalMonedaId + "/" + globalProcedencia + "/" + numero + "/" + globalAgencia + "/" + pagina, function (data) {
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, '.mensaje');
        if (data.tipoMensaje === 'hide') {
            tipoUsuario=data.objeto;
            listaAsientos = data.listaObjetos;
            $("#paginacion").off('page');
            $("#tablaListaAsientos").bootstrapTable({
                columns: [
                    {
                        field: 'id',
                        title: '',
                        visible: false
                    },
                    {
                        field: 'agenciaId',
                        title: '',
                        visible: false
                    },
                    {
                        field: 'fechaString',
                        title: 'Fecha',
                        width: '7%',
                        align: 'center'
                    },
                    {
                        field: 'numero',
                        title: 'N°',
                        align: 'center',
                        halign: 'center'
                    },
                    {
                        field: 'agenciaAbreviatura',
                        title: 'Agencia',
                        align: 'center'
                    },
                    {
                        field: 'tipoAsientoDescripcion',
                        title: 'Tipo',
                        align: 'left',
                        halign: 'center'
                    },
                    {
                        field: 'procedenciaDescripcion',
                        title: 'Procedencia',
                        align: 'center'
                    },
                    {
                        field: 'monedaDescripcion',
                        title: 'Moneda',
                        align: 'center'
                    },
                    {
                        field: 'total',
                        title: 'Monto',
                        halign: 'center',
                        formatter: formatoMoneda
                    },
                    {
                        //field: 'operate',
                        title: '',
                        field:'accion',
                        //formatter: operateListarAsientos,
                        align: 'center',
                        events: operaciones
                    }
                ],
                search: false,
                height: 460
            });
            $("#tablaListaAsientos").bootstrapTable('load', listaAsientos);
            $("#paginacion").bootpag({
                total: data.totalPaginas,
                maxVisible: 10,
                page: pagina
            }).on("page", function (event, pag) {
                traerListaAsientos(pag);
            });
            $("#contenedorTabla").show();



        }
    });
}
function traerDetalleAsiento(asiento) {
    $("#tblMostrarDetalle").addClass('punterotabla');
    var detalleAsiento = null;
    $("#contenedorTablaDetalleAsiento").hide()
    $('#tblMostrarDetalle').bootstrapTable('resetView');

    mostrarMensajeCargando("Cargando datos...", '#mensajeDetalle', 'h4');
    esSysone = false;
    if (asiento.procedenciaDescripcion == enumProcedencia.SYSONE)
    {
        esSysone = true;
    }
    $.getJSON("traerDetalle/" + asiento.id, function (data) {
        var procedencia = asientoActual.procedenciaDescripcion;
        if (procedencia.toUpperCase() != enumProcedencia.SYSONE) {
            $("#tblMostrarDetalle").removeClass('punterotabla');
        }
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, '#mensajeDetalle');
        if (data.tipoMensaje == "hide") {
            var mostrar = ['tablaDetalleAsientos'];
            ocultarMostrarElementos(mostrar, todosItem);
            $("#listaCuentas").addClass('hide');
            $("#tblMostrarDetalle").off("click-row.bs.table");
            $("#tblMostrarDetalle").bootstrapTable({
                columns: [
                    {
                        field: 'cuentaCuenta',
                        title: 'Cuenta',
                        halign: 'center',
                        align: 'left',
                        width: '56%',
                        formatter: formatoTitulo
                    },
                    {
                        field: 'monedaId',
                        title:'Moneda',
                        align:'center',
                        width:'4%'
                    },
                    {
                        field: 'debe',
                        title: 'Debe',
                        align: 'right',
                        halign: 'center',
                        formatter: formatoMoneda,
                        footerFormatter: sumFormatter,
                        width: '20%'

                    },
                    {
                        field: 'haber',
                        title: 'Haber',
                        align: 'right',
                        halign: 'center',
                        formatter: formatoMoneda,
                        footerFormatter: sumFormatter,
                        width: '20%'
                    }
                ],
                showFooter: true,
                height: '600'
            }).on('click-row.bs.table', function (e, row, $element) {
                if (procedencia.toUpperCase() == enumProcedencia.SYSONE) {
                    cargarTablaTransacciones(row, asiento.agenciaId);
                }

            });
            detalleAsiento = data.listaObjetos;
            $("#tblMostrarDetalle").bootstrapTable('load', detalleAsiento);
            $('#tblMostrarDetalle').bootstrapTable('resetView');
            $('#tblMostrarDetalle').bootstrapTable('refresh');
            $("#contenedorTablaDetalleAsiento").show();
        }
        $('#tblMostrarDetalle').bootstrapTable('resetView');
    });

}
function eliminarAsiento() {
    var idAsiento = $("#lblNumero").data("idAsiento");
    $.ajax({
        type: 'DELETE',
        url: "eliminar/" + idAsiento,
        success: function (data, textStatus, jqXHR) {
            mostrarMensajes(data.tipoMensaje, data.mensaje, 'body');
            traerListaAsientos(1);
        }
    });

    $("#modalDetalleAsiento").modal("hide");
}

function cargarTablaTransacciones(row, agenciaId) {

    cuentaActual = row;
    var tipoAsientoId = asientoActual.tipoAsientoId;
    var fecha = asientoActual.fechaString;
    var cuenta = cuentaActual.cuentaCuenta;
    var moneda = row.monedaId;    

    $("#cargandoDatosTransacciones").show();
    mostrarMensajeCargando("Cargando datos...", "#cargandoDatosTransacciones", "h4")
    var mostrar = ["cargandoDatosTransacciones"];
    ocultarMostrarElementos(mostrar, todosItem);
    $.ajax({
        url: urlBaseGeneral + 'integracionSysone/traerDetalleIntegracion?tipoAsientoId=' + tipoAsientoId + '&fecha=' + fecha + '&cuenta=' + cuenta + '&moneda=' + moneda + '&agencia=' + agenciaId,
        type: 'GET',
        success: function (data, textStatus, jqXHR) {
            $("#cargandoDatosTransacciones").hide();
            $("#cuentaActual").html("<strong>Cuenta: </strong>" + cuentaActual.cuentaCuenta);
            $("#debeActual").html("<strong>Debe: </strong>" + formatoMoneda(cuentaActual.debe));
            $("#haberActual").html("<strong>Haber:</strong> " + formatoMoneda(cuentaActual.haber));
            if (data.tipoMensaje == 'hide') {
                var mostrar = ['listaCuentas', 'contenedorTblCuenta'];

                ocultarMostrarElementos(mostrar, todosItem);
                $("#tblCuenta").off('click-row.bs.table');
                //TABLA CUENTA
                $("#tblCuenta").bootstrapTable({
                    columns: [
                        {
                            field: 'transaccion',
                            title: 'Transacción',
                            halign: 'center',
                            width: 70,
                            align: 'left',
                            formatter: formatoTransaccion
                        },
                        {
                            field: 'moneda',
                            title: 'Moneda',
                            align: 'center',
                            width: 50
                        },
                        {
                            field: 'debeOriginal',
                            title: 'Debe ',
                            halign: 'center',
                            formatter: formatoMoneda
                        },
                        {
                            field: 'haberOriginal',
                            title: 'Haber ',
                            halign: 'center',
                            formatter: formatoMoneda
                        },
                        {
                            field: 'debeSoles',
                            title: 'Debe S/',
                            halign: 'center',
                            formatter: formatoMoneda,
                            footerFormatter: sumFormatter
                        },
                        {
                            field: 'haberSoles',
                            title: 'Haber S/',
                            halign: 'center',
                            formatter: formatoMoneda,
                            footerFormatter: sumFormatter
                        }
                    ],
                    search: false,
                    showFooter: true,
                    classes: 'table table-condensed',
                    height: '400'
                }).on('click-row.bs.table', function (e, row, $element) {
                    cargarDetalleTransaccion(row);
                });
                $("#tblCuenta").bootstrapTable('load', data.listaObjetos);
            } else {
                var mostrar = ['mensajeError'];
                mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeError");
                ocultarMostrarElementos(mostrar, todosItem);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log("ERROR+");
        }
    });

}

function cargarDetalleTransaccion(row) {
    $.ajax({
        url: urlBaseGeneral + 'integracionSysone/traerDetalleTransaccion/' + row.transaccion,
        type: 'GET',
        success: function (data, textStatus, jqXHR) {
            if (data.tipoMensaje == 'hide') {
                var mostrar = ['contenedorTblTransaccion', 'listaCuentas', 'cabeceraTransacciones', 'listaTransacciones'];
                ocultarMostrarElementos(mostrar, todosItem);
                $("#transNum").html(data.listaObjetos[0].transaccion);
                $("#transMoneda").html(data.listaObjetos[0].moneda);
                $("#codigoRef").html(data.listaObjetos[0].codigoReferencia);
                $("#transCliente").html(data.listaObjetos[0].nombreReferencia.toLowerCase());
                $("#transOpe").html(data.listaObjetos[0].operacion);

                $("#tblDetalleTransaccion").bootstrapTable({
                    columns: [
                        {
                            field: 'cuenta',
                            title: 'Cuenta',
                            halign: 'center'
                        },
                        {
                            field: 'descripcion',
                            title: 'Descripcion',
                            halign: 'center'
                        },
                        {
                            field: 'debeOriginal',
                            title: 'Deber',
                            formatter: formatoMoneda,
                            halign: 'center',
                            align: 'right'
                        },
                        {
                            field: 'haberOriginal',
                            title: 'Haber ',
                            formatter: formatoMoneda,
                            halign: 'center',
                            align: 'right'
                        },
                        {
                            field: 'debeSoles',
                            title: 'Debe S/',
                            formatter: formatoMoneda,
                            halign: 'center',
                            align: 'right'
                        },
                        {
                            field: 'haberSoles',
                            title: 'Haber S/',
                            formatter: formatoMoneda,
                            halign: 'center',
                            align: 'right'
                        }
                    ],
                    search: false,
                    height: 400
                });
                $("#tblDetalleTransaccion").bootstrapTable('load', data.listaObjetos);
            } else {
                var mostrar = ['mensajeError', 'listaCuentas', 'contenedorTblCuenta'];
                mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeError");
                ocultarMostrarElementos(mostrar, todosItem);
            }

        },
        error: function (jqXHR, textStatus, errorThrown) {

        }
    });


}
//ALINEAR CELDAS AL CAMBIAR LAS DIMENSIONES DE LAS TABLAS
$(window).resize(function () {
    $('#tablaListaAsientos').bootstrapTable('resetView');
    $('#tblMostrarDetalle').bootstrapTable('resetView');
});
var operaciones = {
    'click .detalle': function (e, value, row, index) {
        var mostrarEliminar = $(this).text() == "Eliminar" ? 'inline' : 'none'; //MOSTRAR BOTON ELIMINAR
        $("#btnEliminarAsiento").css('display', mostrarEliminar);

        $("#lblNumero").html('N° ' + row.numero);
        $("#glosaDetalle").html(row.glosa);
        $("#lblNumero").data('idAsiento', row.id);
        asientoActual = row;
        traerDetalleAsiento(row);
    }
};
//function operateListarAsientos(value, row, index) {
//    var eliminar = "";
//    var editar = "";
//    var tipoAsiento_MONEDA_EXTRANJERA = 12;
//    if (periodoVigente == globalPeriodo || globalPeriodo) {
//        eliminar = '<li><a   data-toggle="modal" data-target="#modalDetalleAsiento" href="#" class="detalle">Eliminar</a></li>';
//        editar = '<li><a id="linkEditarAsiento" href="/contabilidad/asientos/editar/' + row.id + '">Editar</a></li>';
//    }
//    if (row.procedenciaDescripcion == enumProcedencia.SYSONE || 
//            row.procedenciaDescripcion == enumProcedencia.SIAFC
//            ||row.procedenciaDescripcion == enumProcedencia.PROVISION) {
//        editar = "";
//    }
//    if (row.tipoAsientoId == tipoAsiento_MONEDA_EXTRANJERA) {
//        editar = "";
//       eliminar = '<li><a   data-toggle="modal" data-target="#modalDetalleAsiento" href="#" class="detalle">Eliminar</a></li>';
//    }
//    if(tipoUsuario=='S'){
//        editar="";
//        eliminar="";
//    }
//    
//    return [
//        '<div class="dropdown " >',
//        '<button class="btn btn-xs btn-posicion btn-default dropdownMenu1 dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" >',
//        '<span class="glyphicon glyphicon-cog"></span>',
//        '<span class="caret"></span>',
//        '</button>',
//        '<ul class="dropdown-menu" style="position: absolute;left: -120px;"  aria-labelledby="dropdownMenu1">',
//        '<li><a class="detalle"  data-toggle="modal"  data-target="#modalDetalleAsiento" href="#">Detalle </a></li>',
//        editar,
//        eliminar,
//        '</ul>',
//        '</div>'
//    ].join('');
//}
var pruebaEliminarAsiento = {
    tipoMensaje: "danger",
    mensaje: "Se elimino correctamente correctamente",
    listaObjetos: null,
    objeto: null,
    totalPaginas: 0
};
function llenaCombosPeriodo() {
    var seleccionado="";
    $.getJSON("../utilitario/periodo", function (data) {
        $.each(data, function (i, item) {
            seleccionado="";
            if(item['estadoId']==estadosSistema.ACTIVO){
                seleccionado='selected="selected"';
            }
            $("#cmbPeriodo").append('<option '+seleccionado+'  value="' + item['id'] + '">' + item['descripcion'] + '</option>');
        });
        globalPeriodo = $("#cmbPeriodo").val();
        traerListaAsientos(1);
    });
}

function formatoTitulo(value) {
    if (esSysone) {
        return "<p style='margin: 0' title='Ver Origen'>" + value + "</p>";
    }
    return value;
}

function formatoTransaccion(value) {
    return "<p style='margin: 0' title='Ver Transacción'>" + value + "</p>";
}