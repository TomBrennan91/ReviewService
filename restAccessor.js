function postAndUpdate(){
    var titles = document.forms["form"].elements[0].value;
    titles = titles.replace(/\n/g,"~");
    postAjax('http://localhost:8080/review?sort=' + getSortingParameter() + getFilterParameter(), titles , function (data){updateOutput(data);});
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
    var spinner = document.getElementById("spinner");
    spinner.style.display = "block";
    console.log(ascOrDesc());
    getColumnHeaders();
    clearOutputTable();
    printColumnHeaders();

    var responseObj = JSON.parse(data);
    var columnHeaders = getColumnHeaders();
    for (var i = 0 ; i < responseObj.length  ; i++){
        var row = outputTable.insertRow(i + 1);
        for (columnIdx in columnHeaders){
            row.insertCell(columnIdx).innerHTML = responseObj[i][columnHeaders[columnIdx]];
        }
    }
    spinner.style.display = "none";
}

function getAdditionalInfo(parameterName, review){
    if(document.getElementById(parameterName + "Box").checked){
        return " (" + review[parameterName] + ")"
    } else {
        return ""
    }
}

function getSortingParameter(){
    return document.getElementById("sorting").value + ":" + ascOrDesc();
}

function ascOrDesc(){
    if (document.getElementById("asc").checked){
        return "asc";
    } else {
        return "desc";
    }
}

function getFilterParameter(){
    return document.getElementById("filtering").value + ":" + greaterThanOrLessThan() + ":" + document.getElementById("filterValue").value
}

function greaterThanOrLessThan(){
    if (document.getElementById("greaterThan").checked){
        return "gt"
    } else {
        return "lt"
    }
}

function getColumnHeaders(){
    var columnHeaders = ["title", "imdbRating"];
    var optionalHeaders = document.getElementsByClassName("info");
    for (let item of optionalHeaders){
        if(item.checked){
            columnHeaders.push(item.id.replace("Box",""));
        }
    }
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

function clearOutputTable(){
    var Parent = document.getElementById("outputTable");
    while(Parent.hasChildNodes()){
       Parent.removeChild(Parent.firstChild);
    }
}

function empty(data){
    if(typeof(data) == 'number' || typeof(data) == 'boolean'){
        return false;
    }
    if(typeof(data) == 'undefined' || data === null){
        return true;
    }
    if(typeof(data.length) != 'undefined'){
        return data.length == 0;
    }
    var count = 0;
    for(var i in data){
        if(data.hasOwnProperty(i)){
            count ++;
        }
    }
    return count == 0;
}

var spinner = document.getElementById("spinner");
spinner.style.display = "none";

document.getElementById("desc").checked = true;