function postAndUpdate(){
    var titles = document.forms["form"].elements[0].value;
    titles = titles.replace(/\n/g,"~");
    postAjax('http://localhost:8080/review?sort=' + getSortingParameter() + getRatingFilter() + getVoteFilter() + getRuntimeFilter() + getYearFilter(), titles , function (data){updateOutput(data);});
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
    console.log(getRatingFilter())
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

function getRatingFilter(){
    if (document.getElementById("ratingFilterBox").checked){
        var lowerBound = document.getElementById("ratingLB").value;
        if (!empty(lowerBound)) return "&ratingFilter=gt:" + lowerBound;
        var upperBound = document.getElementById("ratingUB").value;
        if (!empty(upperBound)) return "&ratingFilter=lt:" + upperBound;
    }
    return ""
}

function getVoteFilter(){
    if (document.getElementById("votesFilterBox").checked){
        var lowerBound = document.getElementById("votesLB").value;
        if (!empty(lowerBound)) return "&votesFilter=gt:" + lowerBound;
        var upperBound = document.getElementById("votesUB").value;
        if (!empty(upperBound)) return "&votesFilter=lt:" + upperBound;
    }
    return ""
}

function getRuntimeFilter(){
    if (document.getElementById("runtimeFilterBox").checked){
        var lowerBound = document.getElementById("runtimeLB").value;
        if (!empty(lowerBound)) return "&runtimeFilter=gt:" + lowerBound;
        var upperBound = document.getElementById("runtimeUB").value;
        if (!empty(upperBound)) return "&runtimeFilter=lt:" + upperBound;
    }
    return ""
}

function getYearFilter(){
    if (document.getElementById("yearFilterBox").checked){
        var lowerBound = document.getElementById("yearLB").value;
        if (!empty(lowerBound)) return "&yearFilter=gt:" + lowerBound;
        var upperBound = document.getElementById("yearUB").value;
        if (!empty(upperBound)) return "&yearFilter=lt:" + upperBound;
    }
    return ""
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

function toggleRatingFilter(){
    toggleFilter("rating");
}

function toggleYearFilter(){
    toggleFilter("year");
}

function toggleRuntimeFilter(){
    toggleFilter("runtime");
}

function toggleVotesFilter(){
    toggleFilter("votes");
}

function toggleFilter(filterName){
    var x = document.getElementById(filterName + "Filters");
    if (document.getElementById(filterName + "FilterBox").checked) x.style.display = "block";
    else x.style.display = "none";
}

toggleRatingFilter();
toggleVotesFilter();
toggleRuntimeFilter();
toggleYearFilter();