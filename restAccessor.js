

//function postAjax(url, data, success) {
//    var params = typeof data == 'string' ? data : Object.keys(data).map(
//            function(k){ return encodeURIComponent(k) + '=' + encodeURIComponent(data[k]) }
//        ).join('&');
//
//    var xhr = window.XMLHttpRequest ? new XMLHttpRequest() : new ActiveXObject("Microsoft.XMLHTTP");
//    xhr.open('POST', url);
//    xhr.onreadystatechange = function() {
//        if (xhr.readyState>3 && xhr.status==200) { success(xhr.responseText); }
//    };
//    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
//    xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
//    xhr.send(params);
//    return xhr;
//}
//
//function postAndUpdate(){
//    var json = document.forms["form"].elements[0].value;
//    postAjax('http://transportapi-env.984bzp88yp.us-east-2.elasticbeanstalk.com/itinerary',json, function(data){ console.log(data); document.getElementById("output").innerHTML = data;});
//}
//
//postAndUpdate();

console.log("js loaded")

function loadDoc(){
    var title = document.forms["form"].elements[0].value;
    var xhttp = new  XMLHttpRequest();
    var responses = {};
    var getData = function(item){
        console.log(item);
        xhttp.open("GET","http://localhost:8080/review?titles=" + item,true);
        try{
            xhttp.send();
        } catch(err){
            document.getElementById("output").innerHTML = "Error, Server not found"
        }
    }

    getData(title.replace(/\n/g,"~"));

    xhttp.onreadystatechange = function(){
        if (this.readyState == 4 && this.status == 200) {
            var responseObj = JSON.parse(this.responseText);
            for (var i = 0 ; i < responseObj.length  ; i++){
                document.getElementById("output").innerHTML = document.getElementById("output").innerHTML
                + responseObj[i]["title"]
                + " (" + responseObj[i]["year"] + ")"
                + " [" + responseObj[i]["imdbRating"] +"/10]" + "\n";
            }
        } else if (this.readyState == 403) {
            document.getElementById("output").innerHTML = "Error, Server not found"
        }
    };

    function sleep(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }
}