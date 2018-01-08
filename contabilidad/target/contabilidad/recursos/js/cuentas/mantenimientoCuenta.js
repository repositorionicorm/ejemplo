var $formInsertar = null;
var $formActualizar = null;
var tipoUsuario="S";
$(document).ready(cargarDatos);
var globalIdCuenta = "";
var cuentasSeleccionadas = {};
function cargarDatos() {
    $("#menuCuentas").addClass('active');
    $formInsertar = $("#formCrearCuenta").data('bootstrapValidator');
    $formActualizar = $("#formEditarCuenta").data('bootstrapValidator');
    $("#cuentasSeleccionadas").on("click", ".claseElegidas", darClickCuentaElegida);
    $("#btnAgregar").click(cargarNumCuenta);
    $("#btnEliminarCuenta").click(llamarEliminar);
    $("#btnEditarCuenta").click(actualizarCuentaPlan);
    $("#btnCrearCuenta").click(crearCuenta);
    $("#btnCancelarNueva").click(limpiarFormulario);
    $("#contenedor-tabs").on("click", "div.bhoechie-tab-menu>div.list-group>a", function (e) {
        e.preventDefault();
        var cuenta = $(this).data("dataCuenta");
        $(this).siblings('a.active').removeClass("active");
        $(this).addClass("active");
        $("#cuentaActualHtml").data("dataCuenta", cuenta);
        $("#cuentaActualHtml").html($(this).html());
        $(".spanCodigoCuenta").html(cuenta.cuenta);
        $("#cuentasSeleccionadas").html("");
        $("#cuentasSeleccionadas").hide();
        cuentasSeleccionadas = [];
        cuentaActual = cuenta;
        cargarTabla(cuenta.id, 1);
    });
    cuentasSeleccionadas = [];
    cargarPanelIzquierdo();
    
    $("#cuentasSeleccionadas").hide();
    $('#modalNuevaCuenta').on('shown.bs.modal', function () {
        $("#numCuentaCrear").focus();
    });
    $('#modalEditarCuenta').on('shown.bs.modal', function () {
        $("#descripcionEditar").focus();
    })
}
function cargarNumCuenta() {   
    $(".spanCodigoCuenta").html(cuentaActual.cuenta);
    $("#tipoCuenta").html('<option value="Deudora">' + cuentaActual.tipoCuentaDescripcion + '</option>')
}
function darClickCuentaElegida() {
    var cuenta = $(this).data("dataCuenta");
    $("#contenedorTabla").addClass('hide');
    var posicion = cuentasSeleccionadas.indexOf(cuenta);
    var tamanio = cuentasSeleccionadas.length;
    cuentaActual = cuenta;
    globalIdCuenta = cuenta.id;
    cuentasSeleccionadas.splice(posicion + 1, tamanio - posicion);
    $(".spanCodigoCuenta").html(cuenta.cuenta);
    $("#cuentaActualHtml").data("dataCuenta", cuenta);
    $("#cuentaActualHtml").html(cuenta.cuenta + " - " + cuenta.descripcion);
    if (cuentasSeleccionadas.length == 1)
        $("#cuentasSeleccionadas").hide();
    else
        $("#cuentasSeleccionadas").show();
    $("#cuentasSeleccionadas").html("");
    pintarCuentasSeleccionadas(cuentasSeleccionadas, "#cuentasSeleccionadas", "claseElegidas");
    cargarTabla(cuenta.id, 1);
}
function pintarCuentasSeleccionadas(data, contenedorCuentas, claseCuentas) {
    $.each(data, function (i, cuenta) {
        $(contenedorCuentas).append("<li><a  title='" + cuenta.descripcion + "' id='" + claseCuentas + i + "' class='" + claseCuentas + "'>" + cuenta.cuenta + "</a></li>");
        $("#" + claseCuentas + i).data("dataCuenta", cuenta);
    });
}
function pintarPanelIzquierdo(data, contenedorCuentas, claseCuentas) {
    $.each(data, function (i, cuenta) {
        $(contenedorCuentas).append("<a href='#' id='" + claseCuentas + i + "' class='" + claseCuentas + "'>" + cuenta.cuenta + "  " + cuenta.descripcion + "</a>");
        $("#" + claseCuentas + i).data("dataCuenta", cuenta);
    });
    $("." + claseCuentas).addClass("list-group-item");
    $("#" + claseCuentas + "0").addClass("active");
}
//******************************************************************************************
//*******************************************CRUD*******************************************
//******************************************************************************************
function cargarPanelIzquierdo() {
    $("#contenedorTabla").addClass("hide");
    $.getJSON("/contabilidad/cuentas/traerPrimerNivel", function (data1) {
        listaCuentas = data1.listaObjetos;
        mostrarMensajesContenedor(data1.tipoMensaje, data1.mensaje, "#errorConsulta");
        if (listaCuentas != null) {
            $("#contenedorTabla").removeClass("hide");
            pintarPanelIzquierdo(listaCuentas, "#panelIzquierdo", "opcionesPanelIzquierdo");
            $("#cuentaActualHtml").data(listaCuentas[0]);
            $("#cuentaActualHtml").html(listaCuentas[0].cuenta + "  " + listaCuentas[0].descripcion);
            $(".spanCodigoCuenta").html(listaCuentas[0].cuenta);
            cuentaActual = listaCuentas[0];
            cargarTabla(1, 1);
        }
    }).fail(function () {
        console.error("Ocurrio un error al realizar el pedido");
    }
    );
}
function actualizarCuentaPlan() {
    var padreId = cuentaActual.id;
    actualizarCuenta(padreId,1);
}
function llamarEliminar() {
    var padreId = cuentaActual.id;
    eliminarCuenta(padreId,1);
}
function crearCuenta() {
    var padreId = cuentaActual.id;
    var tipoCuenta = cuentaActual.tipoCuentaDescripcion;
    var Sbs = cuentaActual.esSbs;
    var analitica = cuentaActual.analitica;
    var numCuenta = $(".spanCodigoCuenta").html() + $("#numCuentaCrear").val();
    var descripcion = $("#descripcionCrear").val();
    $formInsertar.validate();
    if ($formInsertar.isValid()) {
        var cuentaDTO = new CuentaDTO(null, numCuenta, descripcion, padreId, tipoCuenta, Sbs, analitica);
        var cuentaJson = JSON.stringify(cuentaDTO);
        $.ajax({
            type: 'POST',
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            url: "grabarCuenta",
            data: cuentaJson,
            success: function (data) {
                mostrarMensajes(data.tipoMensaje, data.mensaje,  'body');
                cargarTabla(padreId, 1);
            },
            error: function (err) {
                mostrarMensajes("Ocurrio un error: " + err);
            }
        });
        $("#numCuentaCrear,#descripcionCrear").val("");
        $('#modalNuevaCuenta').modal('hide');        
        $formInsertar.resetForm();
    }
}
function cargarTabla(idCuenta, numPag) {
    $("#contenedorTabla").addClass('hide');
    var listaCuentas = null;
    $("#paginacion").off("page");
    $.getJSON("/contabilidad/cuentas/traerHijas/" + idCuenta + "/" + numPag, function (data) {
        listaCuentas = data.listaObjetos == null ? [] : data.listaObjetos;
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#errorConsulta");
        if (data.tipoMensaje == 'hide'){
            tipoUsuario=data.objeto;
            $("#contenedorTabla").removeClass('hide');
        $("#tablaCuentasMantenimiento").bootstrapTable({
            columns: [
                {
                    field: 'id',
                    title: '',
                    visible: false
                },
                {
                    field: 'cuenta',
                    title: 'Cuenta',
                    width: '25%',
                    formatter: formatoCuenta,
                    events: operacionCuenta
                },
                {
                    field: 'descripcion',
                    title: 'Descripcion',
                    events: operacionCuenta,
                    formatter: formatoDescripcion
                },
                {
                    field: 'tipoCuentaDescripcion',
                    visible: false
                },
                {
                    field: 'esSbs',
                    visible: false
                },
                {
                    field: 'padreId',
                    visible: false
                },
                {
                    title:'Analítica',
                    field: 'esAnalitica',
                    visible: true,
                    align:'center'
                    
                },
                {
                    field: 'operate',
                    title: '',
                    formatter: formatoCombo,
                    align: 'center',
                    events: operacionesCombo,
                    width: '5%'
                }
            ],
            uniqueId: 'id',
            search: false,
            classes: 'table table-hover table-no-bordered',
            height: 500
        });
        $("#tablaCuentasMantenimiento").bootstrapTable('load', listaCuentas);
        $("#tablaCuentasMantenimiento").bootstrapTable('refresh');
        $("#paginacion").bootpag({
            total: data.totalPaginas,
            maxVisible: 10,
            page: numPag
        }
        ).on("page", function (event, num) {
            cargarTabla(idCuenta, num);
        });}
    }).fail(function () {
        console.error("Ocurrio un error al realizar el pedido");
    }
    );
}
function formatoCombo(value, row, index) {
    var eliminar;
    if(tipoUsuario=='S'){
        return '';
    }
    
    if (row.esAnalitica == 'S' && row.esSbs!="S") {
        eliminar = '<li><a data-toggle="modal" data-target="#modalEliminarCuenta" href="#" class="eliminar"  >Eliminar</a></li>';
    } else
        eliminar = ''
    return [
        '<div class="dropdown " >',
        '<button class="btn btn-xs btn-posicion btn-default dropdownMenu1 dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" >',
        '<span class="glyphicon glyphicon-cog"></span>',
        '<span class="caret"></span>',
        '</button>',
        '<ul class="dropdown-menu" style="position: absolute;left: -120px;"  aria-labelledby="dropdownMenu1">',
        '<li><a  class="editar" data-toggle="modal"  data-target="#modalEditarCuenta" href="#">Editar </a></li>',
        eliminar,
        '</ul>',
        '</div>'
    ].join('');
}
;
function formatoDescripcion(value, row, index) {
    return "<p style='margin: 0 0 0' class='lista'>" + row.descripcion + "</p>";
}
function formatoCuenta(value, row, index) {
    return "<p style='margin: 0 0 0' class='lista'>" + row.cuenta + "</p>";
}
var operacionesCombo = {
    'click .editar': function (e, value, row, index) {
        cuentaOperacion = row;
        $("#numCuentaEditar").attr("disabled", "disabled");
        $(".spanCodigoCuenta").html(row.cuenta);
        $(".spanCodigoCuenta").data("id", row.id);
        $("#numCuentaEditar").val(row.cuenta);
        $("#descripcionEditar").val(row.descripcion);

    },
    'click .eliminar': function (e, value, row, index) {
        cuentaOperacion = row;
        $(".spanCodigoCuenta").html(row.cuenta + ' - ' + row.descripcion);
        $(".spanCodigoCuenta").data("id", row.id);
    }
};
var operacionCuenta = {
    'click .lista': function (e, value, row, index) {
        $("#cuentaActualHtml").html(row.cuenta + " - " + row.descripcion);
        cuentaActual = row;
        cuentasSeleccionadas.push(row);
        cuentasSeleccionadas.length == 1 ?
                $("#cuentasSeleccionadas").hide()
                :
                $("#cuentasSeleccionadas").show();
        $("#cuentasSeleccionadas").html("");
        pintarCuentasSeleccionadas(cuentasSeleccionadas, "#cuentasSeleccionadas", "claseElegidas");
        $(".spanCodigoCuenta").html(row.cuenta);
        cargarTabla(row.id, 1);
    }
};
//Validacion  para que acepte UN SOLO numero de cuenta
$("#numCuentaCrear").keyup(function () {
    var numCuenta = $("#numCuentaCrear").val();
    if (numCuenta.length == 1   && cuentaActual.id == cuentaActual.padreId){
        $formInsertar.updateStatus('numCuentaNueva', 'VALID','stringLength');
        $formInsertar.updateStatus('numCuentaNueva', 'VALID', 'notEmpty');
        if(Number.isInteger(numCuenta))
            $formInsertar.updateStatus('numCuentaNueva', 'VALID', 'numeric');
    }
    if (numCuenta.length == 2 && cuentaActual.id == cuentaActual.padreId) {
        $formInsertar.updateStatus('numCuentaNueva', 'INVALID');
        $formInsertar.updateStatus('numCuentaNueva', 'VALID', 'notEmpty');
        $formInsertar.updateStatus('numCuentaNueva', 'VALID', 'numeric');
        $formInsertar.updateMessage('numCuentaNueva', "stringLength", "Debe ingresar solo un dígito para esta cuenta.");
    }
});
function limpiarFormulario() {
    $("#formCrearCuenta")[0].reset();
}