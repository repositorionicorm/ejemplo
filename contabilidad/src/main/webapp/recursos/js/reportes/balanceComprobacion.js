function loadReporteBalanceComprobacion(){
     $(".barraMenu").removeClass('active');
     $("#menuReportes").addClass('active');
      $("#cmbPeriodo,#cmbMoneda").change(function(){
        $("#btnExportarExcel").addClass('hide');
    })
    $("#btnBuscarBalanceComprobacion").click(buscarBalanceComprobacion);
}

function buscarBalanceComprobacion(){
    var periodo= $("#cmbPeriodo").val();
    var moneda=$("#cmbMoneda").val();
    var url= "/contabilidad/reportes/reporteBalance/"+ periodo +"/"+moneda+ "/";
    var id="rptBalance";
    buscarReporte(url,id,"#btnBuscarBalanceComprobacion","#btnExportarExcel");
}