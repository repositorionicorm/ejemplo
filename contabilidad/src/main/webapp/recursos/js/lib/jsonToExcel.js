emitXmlHeader = function (dataTypes) {
    var headerRow = '<ss:Row>\n';
    for (var colName in dataTypes) {
        headerRow += '  <ss:Cell>\n';
        headerRow += '    <ss:Data ss:Type="String">';
        headerRow += colName + '</ss:Data>\n';
        headerRow += '  </ss:Cell>\n';
    }
    headerRow += '</ss:Row>\n';
    return '<?xml version="1.0"?>\n' +
            '<ss:Workbook xmlns:ss="urn:schemas-microsoft-com:office:spreadsheet">\n' +
            '<ss:Worksheet ss:Name="Sheet1">\n' +
            '<ss:Table>\n\n' + headerRow;
};

emitXmlFooter = function () {
    return '\n</ss:Table>\n' +
            '</ss:Worksheet>\n' +
            '</ss:Workbook>\n';
};

function llenarFila(item, dataTypes) {
    var xml = "";
    for (var data in dataTypes) {
        xml += '  <ss:Cell>\n';
        xml += '    <ss:Data ss:Type="' + dataTypes[data] + '">';
        xml += item[data] + '</ss:Data>\n';
        xml += '  </ss:Cell>\n';
    }
    return xml;
}

jsonToSsXml = function (jsonObject, dataTypes, titulos) {
//    var row;
//    var col;
    var xml;
    var data = typeof jsonObject != "object" ? JSON.parse(jsonObject) : jsonObject;

    xml = emitXmlHeader(titulos);

    $.each(data, function (i, item) {
        xml += '<ss:Row>\n';
        xml += llenarFila(item, dataTypes)
        xml += '</ss:Row>\n';
    });
//
//    for (row = 0; row < data.length; row++) {
//        xml += '<ss:Row>\n';
//
//        for (col in data[row]) {
//            xml += '  <ss:Cell>\n';
//            xml += '    <ss:Data ss:Type="' + dataTypes[col] + '">';
//            xml += data[row][col] + '</ss:Data>\n';
//            xml += '  </ss:Cell>\n';
//        }
//
//        xml += '</ss:Row>\n';
//    }

    xml += emitXmlFooter();
    return xml;
};

download = function (content, filename, contentType) {
    $('body').append('<a id="exportarn" href="">link</a>');
    if (!contentType)
        contentType = 'application/octet-stream';
    var a = document.getElementById('exportarn');
    var blob = new Blob([content], {
        'type': contentType
    });
    a.href = window.URL.createObjectURL(blob);
    a.download = filename;
    a.click();
    document.body.removeChild(a);
};

function descargarExcel(dataJson, nombreArchivo, dataTypes, titulos) {
    download(jsonToSsXml(dataJson, dataTypes, titulos), nombreArchivo, 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
}
