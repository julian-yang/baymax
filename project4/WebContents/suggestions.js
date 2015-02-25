
/**
 * Provides suggestions for state names (USA).
 * @class
 * @scope public
 */
var xmlHttp = new XMLHttpRequest();
var oAutoSuggestControl_var;
var bTypeAhead_var;
function GoogleSuggestions() {
}

/**
 * Request suggestions for the given autosuggest control. 
 * @scope protected
 * @param oAutoSuggestControl The autosuggest control to provide suggestions for.
 */
GoogleSuggestions.prototype.requestSuggestions = function (oAutoSuggestControl /*:AutoSuggestControl*/,
                                                          bTypeAhead /*:boolean*/) {
    var aSuggestions = [];
    var sTextboxValue = oAutoSuggestControl.textbox.value;
    
    if (sTextboxValue.length > 0){

        var request = "suggest?q="+encodeURI(sTextboxValue);
        
        oAutoSuggestControl_var = oAutoSuggestControl;
        bTypeAhead_var = bTypeAhead;
        xmlHttp.open("GET", request);
        xmlHttp.onreadystatechange = processSuggestions;
        xmlHttp.send();
   }

    //provide suggestions to the control
};

function processSuggestions() {
    if (xmlHttp.readyState == 4 && xmlHttp.responseXML !== null) {
        var aSuggestions = [];
        var s = xmlHttp.responseXML.getElementsByTagName('CompleteSuggestion');
        var suggestionHtml = "";
        for(i = 0; i < s.length; i++) {
            var text = s[i].childNodes[0].getAttribute("data");
            aSuggestions.push(text);
        }
        
        //determine suggestions for the control
        oAutoSuggestControl_var.autosuggest(aSuggestions, bTypeAhead_var);
        //document.getElementById("suggestions").innerHTML = suggestionHtml;
    }
}

