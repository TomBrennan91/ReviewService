function postAndUpdate(){
    var sortingParameter = getSortingParameter();
    var titles = document.forms["form"].elements[0].value;
    titles = titles.replace(/\n/g,"~");
    postAjax('http://localhost:8080/review?sort=' + sortingParameter, titles , function (data){updateOutput(data);});
}

function postAjax(url, data, success) {
    var params = typeof data == 'string' ? data : Object.keys(data).map(
            function(k){ return encodeURIComponent(k) + '=' + encodeURIComponent(data[k]) }
        ).join('&');

    var xhr = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
    xhr.open('POST', url);
    xhr.onreadystatechange = function() {
        if (xhr.readyState>3 && xhr.status==200) { success(xhr.responseText); }
    };
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
    xhr.send(params);
    return xhr;
}

function updateOutput(data){
    getColumnHeaders();
    console.log(getSortingParameter);
    console.log(data);
    var responseObj = JSON.parse(data);
    var outputTable = document.getElementById("outputTable");
    printColumnHeaders();
    var columnHeaders = getColumnHeaders();

    for (var i = 0 ; i < responseObj.length  ; i++){
        var row = outputTable.insertRow(i + 1);
        for (columnIdx in columnHeaders){
            row.insertCell(columnIdx).innerHTML = responseObj[i][columnHeaders[columnIdx]];
        }
    }
}

function getAdditionalInfo(parameterName, review){
    if(document.getElementById(parameterName + "Box").checked){
        return " (" + review[parameterName] + ")"
    } else {
        return ""
    }
}

function getSortingParameter(){
    var sortingParameters = document.getElementsByClassName("sorting");
    for (let item of sortingParameters){
        if(item.checked){
            return item.id;
        }
    }
    return "";
}

function getColumnHeaders(){
    var columnHeaders = ["title", "imdbRating"];
    var optionalHeaders = document.getElementsByClassName("info");
    for (let item of optionalHeaders){
        console.log(item);
        if(item.checked){
            columnHeaders.push(item.id.replace("Box",""));
        }
    }
    console.log(columnHeaders);
    return columnHeaders;
}

function printColumnHeaders(){
    var columnHeaders = getColumnHeaders();
    var outputTable = document.getElementById("outputTable");
    var headerRow = outputTable.insertRow(0);
    for (columnIdx in columnHeaders){
        var headerCell = document.createElement("TH");
        headerCell.innerHTML = upperFirstLetter(columnHeaders[columnIdx]);
        headerRow.appendChild(headerCell);
    }
}

function upperFirstLetter(string){
    return string.charAt(0).toUpperCase() + string.slice(1);
}