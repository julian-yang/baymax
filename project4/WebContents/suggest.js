var xmlHttp = new XMLHttpRequest();

function getSuggestions(input) {
    var request = "suggest?q="+encodeURI(input);

    xmlHttp.open("GET", request);
    xmlHttp.onreadystatechange = loadSuggestion;
    xmlHttp.send();
}

function loadSuggestion() {
    if (xmlHttp.readyState == 4 && xmlHttp.responseXML !== null) {
        var s = xmlHttp.responseXML.getElementsByTagName('CompleteSuggestion');
        var suggestionHtml = "";
        for(i = 0; i < s.length; i++) {
            var text = s[i].childNodes[0].getAttribute("data");
            suggestionHtml += "<p ";
            suggestionHtml += "class=\"suggestion\" ";
            suggestionHtml += "onclick=\"autoFill(this.innerText);\">";
            suggestionHtml += text + "</p>";
        }
        
        document.getElementById("suggestions").innerHTML = suggestionHtml;
    }
}

function autoFill(suggestion) {
    document.getElementById("queryBox").value = suggestion;
}

