$(document).ready(inicioTipoCambio);
var asiento=null;

function inicioTipoCambio() {
    $("#formularioTipoCambio").unbind('submit').submit(actualizarTipoCambio);
    $("#btnIntegrarTC").click(integrarTC);
    $("#btnHistorico").click(verHistorico);
}
function actualizarTipoCambio(e) {
    e.preventDefault();
    var nuevaTC = $("#nuevoTipoCambio").val();
    var actualTC = $("#tipoCambio").html();
    try {
        if (nuevaTC.length == 0)
            throw new Error("Campo no debe estar vacio");
        if (isNaN(nuevaTC))
            throw new Error("Debe ingresar un número");
        if (nuevaTC <= 0)
            throw new Error("El valor ingresado debe ser mayor a cero");

        $("#btnModificarTC").attr("disabled", "disabled");
        $("#nuevoTipoCambio").attr("disabled", "disabled");
        mostrarMensajeCargando("Procesando...", "#mensajeTC", "h4");

        $.ajax({
            url: urlBaseGeneral + 'proceso/cambiarTC?nuevoTC=' + nuevaTC,
            method: 'POST',
            success: function (data, textStatus, jqXHR) {
                if(data.objeto==null&&data.tipoMensaje==tipoMensaje.EXITO){
                    $("#tipoCambio").html(nuevaTC);
                    mostrarMensajesContenedor(data.tipoMensaje,data.mensaje,"#mensajeTC");
                    return;
                }
                asiento=JSON.stringify(data.objeto);
                if (data.tipoMensaje == tipoMensaje.EXITO) {
                    console.log(data);
                    $("#tblTablaTC").bootstrapTable({
                        columns: [
                            {
                                field: 'cuentaCuenta',
                                title: 'Cuenta',
                                halign: 'center',
                                align: 'left',
                                width: '60%'
                            },
                            {
                                field: 'monedaId',
                                title :'Moneda',
                                align:'center'
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
                        height: '400'
                    });
                    $("#tblTablaTC").bootstrapTable('load', data.objeto.detalles);
                    $('#tblTablaTC').bootstrapTable('refresh');
                    $('#tblTablaTC').bootstrapTable('resetView');
                    $("#mensajeTC").html("");
                    $("#contenedorGrabado").removeClass('hide');
                } else{
                     $("#btnModificarTC").removeAttr('disabled')
                    $("#nuevoTipoCambio").removeAttr("disabled");
                    $("#nuevoTipoCambio").focus();
                    mostrarMensajesContenedor(data.tipoMensaje, data.mensaje, "#mensajeTC");
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                mostrarMensajesContenedor(tipoMensaje.ERROR, Mensajes.general_error_inesperado, "#mensajeTC");
            }

        });
    } catch (err) {
        var mensaje = err.toString();
        mostrarMensajesContenedor(tipoMensaje.ALERTA, mensaje.substring(6, err.length), "#mensajeTC");
        $("#nuevoTipoCambio").focus();
    }

}
var AsientoDTO=function(asiento){
    this.asiento=asiento;
}
function integrarTC(){
    var nuevaTC = $("#nuevoTipoCambio").val();
    $("#btnIntegrarTC").attr('disabled','disabled');
    $.ajax({
        type: 'POST',
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        url: "integrarTipoCambio?nuevoTC="+nuevaTC,
        data: asiento,
        success: function (data) {
            mostrarMensajes(data.tipoMensaje,data.mensaje,'body');
            if(data.tipoMensaje==tipoMensaje.EXITO){
                setTimeout(function(){
                    location.href = "/contabilidad/proceso/datosTCambio";
                },4000);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
           mostrarMensajes(tipoMensaje.ERROR,Mensajes.general_error_inesperado,'body');
        }
        
    });
}
function verHistorico(e){
    e.preventDefault();
    
    $.ajax({
        type: 'GET',
       url: 'traerHistoricoTC',
        success: function (data, textStatus, jqXHR) {
            $("#tblMostrarDetalleHistorico").bootstrapTable('destroy');
            if(data.tipoMensaje!=tipoMensaje.EXITO){
                mostrarMensajes(data.tipoMensaje,data.mensaje,"#mensajeTC");
                return;
            }
            $("#modalHistoricoTC").modal("show");
            $("#tblMostrarDetalleHistorico").bootstrapTable({data:data.listaObjetos});
        },
        error: function (jqXHR, textStatus, errorThrown) {
             mostrarMensajes(tipoMensaje.ERROR,Mensajes.general_error_inesperado,'body');
        }
    });
}