function loadLibroMayor(){
    $(".barraMenu").removeClass('active');
    $("#menuReportes").addClass('active');
    $("#btnBuscarMayor").click(buscarMayor);

    $("#cmbPeriodo").change(function(){
       $("#btnExportarExcel").addClass('hide') ;
    });
    $("#cmbMoneda").change(function(){
       $("#btnExportarExcel").addClass('hide') ;
    });
    $("#txtNumCuenta").keydown(function(){
         $("#btnExportarExcel").addClass('hide') ;
    });
}


function buscarMayor() {
    var periodo=$("#cmbPeriodo").val();
    var numCuenta = $("#txtNumCuenta").val();
    var monedaId = $("#cmbMoneda").val();
    var url="/contabilidad/reportes/reporteMayor?periodo=" + periodo +"&numCta=" + numCuenta+"&monedaId=" + monedaId + "&tipo=";
    var id="rptMayor";
    buscarReporte(url,id,"#btnBuscarMayor","#btnExportarExcel");    
}