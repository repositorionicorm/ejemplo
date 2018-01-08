$(document).ready(inicioCierre);

function inicioCierre() {

    $("#btnCierre").attr('disabled', 'disabled');
    $("#linkCierre").click(function (e) {
        e.preventDefault();
        $("#encabezadoPeriodo").html("Periodo");
        $("#btnCancelar").html("Cancelar");
        $("#colorTituloPeriodo").removeClass('text-danger');
        $("#estados").html("");
        $("#btnCierre").attr('disabled', 'disabled');
        $("#modalCierre").modal({
            backdrop: 'static',
            keyboard: false
        });
        $("#modalCierre").modal('show');
        traerEstadoPeriodo();
    });
    $("#btnCierre").click(procesarCierre);
}
function traerEstadoPeriodo() {
    $("#btnCierre").attr('disabled', 'disabled');
    $("#cierreProcesando").removeClass('hide');
    $("#cargandoCierre").html("Espere, cargando datos...");
    $("#mensajeCierre").removeClass('hide');
    $.ajax({
        url: urlBaseGeneral+'proceso/validarCierre',
        type: 'POST',
        success: function (data, textStatus, jqXHR) {
            $("#cierreProcesando").addClass('hide');
           
            mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeCierre");
            if (data.objeto == true) {
                $("#btnCierre").removeAttr('disabled');
            }
            $("#estados").html("<ul class='list-group'></ul>");
            $.each(data.listaObjetos, function (i, respuesta) {
                var estadoProceso = "<span class='badge badge-success'><span class='glyphicon glyphicon-ok'></span></span>";
                if (respuesta.respuesta != null) {
                    if (respuesta.estado == false)
                        estadoProceso = "<span class='badge badge-danger'><span class='glyphicon glyphicon-remove'></span> </span>";

                    $("#estados").append("<li class='list-group-item'>" +
                            estadoProceso +
                            "<strong>" + respuesta.proceso + ": </strong>" + respuesta.respuesta + " \n\
                                         </li>");
                }
            })
        },
        error: function (jqXHR, textStatus, errorThrown) {
            mostrarMensajesContenedor("danger", errores.eGeneral, "#errorCierre");
        }
    });
}
function procesarCierre() {
    $("#cierreProcesando").removeClass('hide');
    $("#btnCierre,#btnCancelar").attr('disabled', 'disabled');
    $("#cerrarProcesandoCierre").data('dismiss', '');
    $("#cerrarProcesandoCierre").hide();
    $("#cargandoCierre").html("Procesando Cierre...");
    $.ajax({
        url: urlBaseGeneral+'proceso/procesarCierre',
        type: 'POST',
        success: function (data, textStatus, jqXHR) {
            $("#btnCancelar").html("Salir");
            mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeCierre");
            $("#btnCancelar").removeAttr("disabled", "disabled");
            $("#cerrarProcesandoCierre").data('dismiss', 'modal');
            $("#cerrarProcesandoCierre").show();
            $("#cierreProcesando").addClass('hide');
            if (data.tipoMensaje == "success") {
                cargarPeriodo();
                $("#encabezadoPeriodo").html("Nuevo Periodo");
                $("#colorTituloPeriodo").addClass('text-danger');
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {

        }
    });
}