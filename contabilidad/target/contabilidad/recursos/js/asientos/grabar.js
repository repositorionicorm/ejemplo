$(window).resize(function () {
    $tablaDetalle.bootstrapTable('resetView');
    $("#formAgregarDetalle").bootstrapTable('resetView');

});
$(document).ready(alCargar);
var checkId = 0;
var globalNumCta = "";
var $tablaCuenta;
var $tablaDetalle;
var $btnAgregarDetalle;
var urlGeneral = "";
var debeEditar = "";
var haberEditar = "";
var idAsiento = $("#idAsiento").data("id");

var operateEvents = {
    'click .editar': function (e, value, row, index) {
        e.preventDefault();
        $('#formAgregarDetalle').bootstrapValidator('resetForm', true);
        $btnAgregarDetalle.data('btnGrabar', row);
        $btnAgregarDetalle.data('btnGrabarIndex', index);
        $("#spanNumCuenta").val("");
        $('#txtNumCtaElegida').val(row.cuentaCuenta);
        buscarCuenta(row.cuentaCuenta, 1);
        debeEditar = row.debe;
        haberEditar = row.haber;
        checkId = row.cuentaId;
        $('input:radio[name=moneda]')[row.monedaId-1].checked=true;
        $("#modalBuscarCuenta").modal("show");
    },
    'click .eliminar': function (e, value, row, index) {
        $tablaDetalle.bootstrapTable('removeRow', {index: index});
    }
};
var AsientoDTO = function (id, fecha, tipo, glosa, detalle) {
    this.id = id;
    this.fechaString = fecha;
    //this.monedaId = moneda;
    this.tipoAsientoId = tipo;
    this.glosa = glosa;
    this.detalles = detalle;
};
function alCargar() {
    $("#fileArchivoExcel").change(cargarArchivo);
    $("#fileArchivoExcel").click(function () {
        $(this).val(null);
    });
    urlGeneral = typeof idAsiento == "undefined" ? "" : "../";
    $("#menuAsientos").addClass('active');
    $formBuscarCta = $("#formBuscarCtaPlan").data('bootstrapValidator');
    $btnAgregarDetalle = $('#btnAgregarDetalleAsiento');
    $tablaCuenta = $("#tblBuscarCta");
    $tablaDetalle = $("#tblDetalleAsiento");
    $("#btnCancelarAsiento").click(function () {
        location.href = "/contabilidad/asientos/listar";
    });
//    llenaCombosExtraData("#cmbTipoMoneda", urlGeneral + "../utilitario/monedaActiva", "id", "descripcion", "simbolo", cargarTipoAsiento);
    cargarTipoAsiento();
    configurarFecha();
    $("#cmbTipoGrabar").change(cambiarGlosa);
    $("#btnBuscarCuenta").click(validaBusqueda);
    $btnAgregarDetalle.click(grabarDetalle);
    $("#btnGrabarAsiento").click(grabarAsiento);
    $("#btnAgregarDetalle").click(function () {
        $("#txtNumCtaElegida").val("");
        $("#spanNumCuenta").val("");
        $(".divDetalleAsiento").addClass('hidden');
        $btnAgregarDetalle.data('btnGrabar', null);
        $('#formAgregarDetalle').bootstrapValidator('resetForm', true);
        $("#modalBuscarCuenta").modal("show");
    });
    //Al mostrar modal para agregar detalle
    $('#modalBuscarCuenta').on('shown.bs.modal', function () {
        $formBuscarCta.resetForm();
        if (typeof debeEditar != 'number')
            debeEditar = 0;
        if (typeof haberEditar != 'number')
            haberEditar = 0;
        $("#debe" + checkId).val(debeEditar.toFixed(2));
        $("#haber" + checkId).val(haberEditar.toFixed(2));
        $("#txtNumCtaElegida").focus();
        $tablaCuenta.bootstrapTable('checkBy', {field: 'id', values: [checkId]});
    });
    //Al ocultar modal para agregar detalle
    $('#modalBuscarCuenta').on('hidden.bs.modal', function () {
        $(".alter-mensaje").alert('close');
    });
    $("#formAgregarDetalle").on('keypress', '.debe_haber', function (e) {
        if (!e)
            e = window.event;
        var keyCode = e.keyCode || e.which;
        if (keyCode == '13') {
            grabarDetalle();
            return false;
        }
    });
}
function cargarTipoAsiento() {
    llenaCombosExtraData("#cmbTipoGrabar", urlGeneral + "../utilitario/tipoAsiento", "id", "descripcion", "glosa", cargarGlosa);
}
function cargarGlosa() {
    var glosaInicial = $("#cmbTipoGrabar").find(":selected").data("glosa");
    $("#txtGlosa").val(glosaInicial);
    cargarDetalle();
}
function cargarDetalle() {
    if (idAsiento != undefined)
        cargarDatosEditar();
}
function cargarDatosEditar() {
    var listaDetalle = null;
    var asiento = null;
    $.getJSON("../traerAsiento/" + idAsiento, function (data) {
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeAsiento");
        if (data.tipoMensaje === 'hide') {
            asiento = data.objeto;
            listaDetalle = asiento.detalles;
            $tablaDetalle.bootstrapTable('load', listaDetalle);
            $("#txtFecAsiento").val(asiento.fechaString);
            $("#cmbTipoMoneda").val(asiento.monedaId);
            $("#cmbTipoGrabar").val(asiento.tipoAsientoId);
            $("#txtGlosa").val(asiento.glosa);
            $("#spanNumAsientoEditar").html(asiento.numero);
            $("#contenedorTabla").show();
            $("#btnAgregarDetalle").show();
            $("#btnGrabarAsiento").show();
        } else {
            $("#contenedorTabla").hide();
            $("#btnAgregarDetalle").hide();
            $("#btnGrabarAsiento").hide();
        }

    });
}
function grabarAsiento() {
    var $formulario = $("#formGrabarAsiento").data("bootstrapValidator");
    var tablaDetalle = $tablaDetalle.bootstrapTable('getData');
    var fecha = $("#txtFecAsiento").val();
    var moneda = $("#cmbTipoMoneda").val();
    var tipo = $("#cmbTipoGrabar").val();
    var glosa = $("#txtGlosa").val();
    var asientoJSON = JSON.stringify(new AsientoDTO(idAsiento, fecha, tipo, glosa, tablaDetalle));
    $formulario.validate();
    if ($formulario.isValid()) {
        try {
            if (tablaDetalle.length <= 0)
                throw new Error("Debe ingresar un detalle de Asiento");
            if (totalDebe != totalHaber)
                throw new Error("El total debe y total haber no coinciden");
            $("#mensajeProcesando").removeClass('hide');
            $("#btnGrabarAsiento").attr("disabled", "disabled")
            $.ajax({
                type: 'POST',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                url: urlGeneral + "guardar",
                data: asientoJSON,
                success: function (data) {
                    mostrarMensajes(data.tipoMensaje, data.mensaje, 'body');
                    if (data.tipoMensaje == 'success') {

                        $("#btnGrabarAsiento").attr("disabled", "disabled")
                        setTimeout(function () {
                            location.href = "/contabilidad/asientos/listar";
                        }, 2500);
                    } else {
                        $("#btnGrabarAsiento").removeAttr('disabled');
                    }
                    $("#mensajeProcesando").addClass('hide');
                },
                error: function (err) {
                    console.log("errrorEDITAR");
                }
            });
        } catch (error) {
            mostrarMensajes('warning', error, 'body');
        }
    } else {
        $("#contenedorMoneda").parent().css({position: 'relative'});
        $("#contenedorMoneda").css({
            position: "absolute",
            top: 0
        });
    }
}
function cambiarGlosa() {
    var glosa = $(this).find(":selected").data("glosa");
    $("#txtGlosa").val(glosa);
    $('#formGrabarAsiento').bootstrapValidator('revalidateField', 'txtGlosa');
    $("#formGrabarAsiento").data("bootstrapValidator").validate();
}
function validaBusqueda() {
    $formBuscarCta.validate();
    if ($formBuscarCta.isValid()) {
        globalNumCta = $("#txtNumCtaElegida").val();
        $("#spanNumCuenta").val("");
        buscarCuenta(globalNumCta, 1);
    }
}
function buscarCuenta(numCta, pagina) {

    var listaCuentas = null;
    $("#paginacionCuenta").off("page");
    $.getJSON("/contabilidad/cuentas/traerPorCuenta/" + numCta + "/" + pagina, function (data) {
        mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, '#mensajeCta');
        if (data.tipoMensaje == 'hide') {
            $("#formAgregarDetalle").show();
            listaCuentas = data.listaObjetos == null ? [] : data.listaObjetos;
            $tablaCuenta.bootstrapTable({
                columns: [
                    {
                        field: 'state',
                        radio: true,
                        formatter: showRadio
                    },
                    {
                        field: 'id',
                        title: '',
                        visible: false
                    },
                    {
                        field: 'esAnalitica',
                        title: 'S',
                        visible: false
                    },
                    {
                        field: 'cuenta',
                        title: 'N°',
                        halign: 'center',
                        align: 'left'
                    },
                    {
                        field: 'descripcion',
                        title: 'Descripción',
                        halign: 'center',
                        align: 'left'
                    },
                    {
                        field: '',
                        title: 'Debe',
                        halign: 'center',
                        align: 'left',
                        formatter: formatoDebe,
                        width: '20%'
                    },
                    {
                        field: '',
                        title: 'Haber',
                        halign: 'center',
                        align: 'left',
                        formatter: formatoHaber,
                        width: '20%'
                    }
                ],
                height: 455,
                classes: 'table table-condensed'
            }).on('check.bs.table', function (e, row) {//EVENTOS AL SELECCIONAR UN RADIO
                if (row.esAnalitica == "S") {
                    $(".debe_haber").addClass('hide');
                    $("#spanNumCuenta").val(row.cuenta);
                    $("#debe" + row.id).removeClass('hide');
                    $("#debe" + row.id).focus();
                    $("#haber" + row.id).removeClass('hide');
                }
            });
            $tablaCuenta.bootstrapTable('load', listaCuentas);
            $("#paginacionCuenta").bootpag({
                total: data.totalPaginas,
                maxVisible: 10,
                page: pagina
            }).on("page", function (event, num) {
                $("#spanNumCuenta").val("");
                buscarCuenta(numCta, num);
            });
            $(".divDetalleAsiento").removeClass("hidden");
        } else
            $("#formAgregarDetalle").hide();
    });
}
function grabarDetalle() {
    var formulario = $("#formAgregarDetalle").data("bootstrapValidator");
    var totalSeleccionado = $tablaCuenta.bootstrapTable('getSelections').length;
    var cuenta;
    var index = $btnAgregarDetalle.data('btnGrabarIndex');
    formulario.validate();
    /*
     * idFila= 
     * null : Es un nuevo detalle
     * Object : Es un detalle a editar
     * */
    cuenta = $tablaCuenta.bootstrapTable('getSelections')[0];

    var idFila = $btnAgregarDetalle.data('btnGrabar');
    if (formulario.isValid()) {

        try {
            if (totalSeleccionado <= 0)
                throw new Error('Debe seleccionar una cuenta');
            var montoDebe = $("#debe" + cuenta.id).val();
            var montoHaber = $("#haber" + cuenta.id).val();
            var monedaId = $("input:radio[name=moneda]:checked").val();
            
            if (monedaId==undefined) throw new Error('Debe seleccionar una moneda');

            var montoDebeNuevo = isNaN(parseFloat(montoDebe)) ? 0 : parseFloat(montoDebe);
            var montoHaberNuevo = isNaN(parseFloat(montoHaber)) ? 0 : parseFloat(montoHaber);
            montoDebeNuevo = montoDebeNuevo.toFixed(2);
            montoHaberNuevo = montoHaberNuevo.toFixed(2);

            if (montoDebeNuevo <= 0 && montoHaberNuevo <= 0)
                throw new Error('Uno de los montos debe ser mayor a cero');

            $(".divDetalleAsiento").addClass('hidden');
            $("#modalBuscarCuenta").modal("hide");

            var filaDetalle = {
                cuentaId: cuenta.id,
                cuentaCuenta: cuenta.cuenta,
                debe: parseFloat(montoDebeNuevo),
                haber: parseFloat(montoHaberNuevo),
                monedaId: monedaId

            };
            if (idFila == null) //NUEVO
                $tablaDetalle.bootstrapTable('insertRow', {index: 0, row: filaDetalle});
            else //ACTUALIZAR
                $tablaDetalle.bootstrapTable('updateRow', {index: index, row: filaDetalle});

            $(".divDetalleAsiento").addClass('hidden');
            $("#modalBuscarCuenta").modal("hide");
            $tablaDetalle.bootstrapTable('refresh');
            $tablaCuenta.bootstrapTable('destroy');

        } catch (error) {
            mostrarMensajes('warning', error.message, '#alertSeleccioneCta');
        }

    }
}

$("#tblDetalleAsiento").bootstrapTable({
    columns: [
        {
            field: 'id',
            title: '',
            visible: false
        },
        {
            formatter: mostrarIndex,
            title: 'N°',
            width: '4%',
            halign: 'center'
        },
       
        {
            field: 'cuentaCuenta',
            title: 'N° Cuenta',
            width: '51%',
            halign: 'center',
            align: 'left'
        },
         {
            title: 'Moneda',
            width: '4%',
            field: 'monedaId',
            align: 'center'
        },
        {
            field: 'debe',
            title: 'Debe',
            width: '15%',
            align: 'right',
            halign: 'center',
            formatter: formatoMoneda,
            footerFormatter: sumFormatter
        },
        {
            field: 'haber',
            title: 'Haber',
            width: '15%',
            align: 'right',
            halign: 'center',
            formatter: formatoMoneda,
            footerFormatter: sumFormatter
        },
        {
            field: 'operate',
            title: '',
            width: '15%',
            align: 'center',
            formatter: operateFormatter,
            events: operateEvents
        }
    ],
    classes: 'table table-condensed',
    uniqueId: 'id',
    showFooter: true,
    height: '355'
});
$("#tblDetalleAsiento").bootstrapTable("removeAll");
//BOTONES PARA LA TABLA 
function operateFormatter(value, row, index) {
    return [
        '<button style="margin:2px 5px" class="btn btn-xs btn-default editar" href="javascript:void(0)" title="Editar">',
        '<i class="glyphicon glyphicon-pencil"></i>',
        '</button>',
        '<button style="margin: 2px 5px" class="eliminar btn btn-xs btn-default " href="javascript:void(0)" title="Eliminar">',
        '<i class="text-danger glyphicon glyphicon-trash"></i>',
        '</button>'
    ].join('');
}
//FUNCIONES PARA LOS RADIOS
function showRadio(value, row, index) {
    if (row.esAnalitica == 'N') {
        this.radio = false;
        return '';
    } else {
        this.radio = true;
        return this;
    }
}

function formatoDebe(value, row, index) {
    return '<input id="debe' + row.id + '" type="text" align="rigth" autocomplete="off" class="  debe_haber hide input-sm form-control"></input>';

}
function formatoHaber(value, row, index) {
    return'<input id="haber' + row.id + '" type="text" align="right" autocomplete="off" class="text-rigth debe_haber hide input-sm form-control"></input>';
}
function configurarFecha() {
    $.getJSON("/contabilidad/utilitario/periodoVigente", function (data) {
        $("#datePicker-asiento").datepicker({
            language: 'es-ES',
            autoclose: true,
            startDate: data.fechaInicialString,
            endDate: data.fechaLimiteString
        });
    });
}
function verificaTecla(e) {
    if (!e)
        e = window.event;
    var keyCode = e.keyCode || e.which;
    if (keyCode == '13') {
        validaBusqueda();
        return false;
    }
}
function cargarArchivo(e) {
    e.preventDefault();
    var formData = new FormData();
    formData.append("fileArchivoExcel", fileArchivoExcel.files[0]);
    mostrarMensajeCargando(Mensajes.general_cargando, "#mensajeCargarExcel", "h4");
    $("#contenedorTabla").addClass('hide');
    $.ajax({
        dataType: 'json',
        url: urlBaseGeneral + 'integracionExcel/leerExcel',
        type: 'POST',
        data: formData,
        cache: false,
        contentType: false,
        processData: false,
        enctype: 'multipart/form-data',
        success: function (data, textStatus, jqXHR) {
            if (data.listaObjetos != null) {
                $tablaDetalle.bootstrapTable('load', data.listaObjetos);
                $tablaDetalle.bootstrapTable('hideColumn', 'operate');

                $("#contenedorTabla").removeClass('hide');
                $("#mensajeCargarExcel").addClass('hide');
                $tablaDetalle.bootstrapTable('resetView');
            } else {
                $("#mensajeCargarExcel").removeClass('hide');
                mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeCargarExcel");
            }
            $("#btnAgregarDetalle").attr('disabled', 'disabled');
        }
        ,
        error: function (jqXHR, textStatus, errorThrown) {
            mostrarMensajes(tipoMensaje.ERROR, "Error: " + jqXHR.statusText, "#mensajeCargarExcel");
        }

    });
}
