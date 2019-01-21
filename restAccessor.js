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
    console.log(data);
    document.getElementById("output").innerHTML = ""
    var responseObj = JSON.parse(data);

    var outputTable = document.getElementById("outputTable");
    var headerRow = outputTable.insertRow(0);

    var headerCell = document.createElement("TH");
    headerCell.innerHTML = "Title";
    headerRow.appendChild(headerCell);

    for (var i = 0 ; i < responseObj.length  ; i++){
        document.getElementById("output").innerHTML = document.getElementById("output").innerHTML
            + responseObj[i]["title"]
            + getAdditionalInfo("year", responseObj[i])
            + " [" + responseObj[i]["imdbRating"] +"/10]"
            + getAdditionalInfo("runtime", responseObj[i])
            + getAdditionalInfo("imdbVotes", responseObj[i])
            + "\n";


        var row = outputTable.insertRow(i + 1);
        row.insertCell(0).innerHTML = responseObj[i]["title"];


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
    var sortingParameter = "";
    if (document.getElementById('rating').checked){
        sortingParameter = "rating"
    } else if (document.getElementById('name').checked){
        sortingParameter = "name"
    } else if (document.getElementById('year').checked){
        sortingParameter = "year";
    } else if (document.getElementById('votes').checked){
        sortingParameter = "votes";
    } else if (document.getElementById('runtime').checked){
        sortingParameter = "runtime";
    }
    console.log(sortingParameter)
    return sortingParameter;
}