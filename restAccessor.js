

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


function postAndUpdate(){
    var titles = document.forms["form"].elements[0].value;
    postAjax('http://localhost:8080/review', titles , function (data){updateOutput(data);});
}

function updateOutput(data){
    console.log(data);
    var responseObj = JSON.parse(data);
    for (var i = 0 ; i < responseObj.length  ; i++){
        document.getElementById("output").innerHTML = document.getElementById("output").innerHTML
        + responseObj[i]["title"]
        + " (" + responseObj[i]["year"] + ")"
        + " [" + responseObj[i]["imdbRating"] +"/10]" + "\n";
    }
}
