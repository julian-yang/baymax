<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Search Results!</title>
        <link rel="stylesheet" type="text/css" href="eBay.css">
    </head>
    <body>
		<a href="/eBay">Home</a>
		
		<form action="/eBay/search" method="GET">
            <div class="queryContainer">
                <div>
                    <span>Enter search text:</span><br/>
                    <input id="queryBox" name="q" type="text">
                    <input id="submit" type="submit">
                </div>
            </div>
            <!-- TODO: set these hidden fields with Javascript or something -->
            <input name="numResultsToSkip" type="hidden" value="0">
            <input name="numResultsToReturn" type="hidden" value="10">
            
        </form>
        <script src="autosuggest.js"></script>
        <script src="suggestions.js"></script>
        <script type="text/javascript">
            window.onload = function () {
                var oTextbox = 
                    new AutoSuggestControl(document.getElementById("queryBox"),
                new GoogleSuggestions());
            }
        </script>
        
		<c:if test="${empty results}">
			<h1>No results found!</h1>
			<p>No items matched your search query</p>
		</c:if>
		
        <!--- Results --->
        <ul>
            <c:forEach var="result" items="${results}">
                <li>
                    <p>ItemID: <span><a href="item?id=${result.getItemId()}">${result.getItemId()}</a></span>
                       Name: <span>${result.getName()}</span>
                    </p>
                </li>
            </c:forEach>
        </ul>
		

		<br>
		<a id="previous" href="search?q=test&numResultsToSkip=0&numResultsToReturn=10">Previous</a>
		<a id="next" href="search?q=test&numResultsToSkip=0&numResultsToReturn=10">Next</a>
		
		<script type="text/javascript">
		
			// grab request data
			var q = "${q}";
			var numResultsToSkip = parseInt("${numResultsToSkip}");
			var numResultsToReturn = parseInt("${numResultsToReturn}");
			var numResultsReturned = parseInt("${numResultsReturned}");
			var hasMore = ${hasMore};

			// set up "previous" link
			var previous = document.getElementById("previous");
			previous.innerHTML = "Previous"			
			previous.href = "search?q=${q}&numResultsToSkip=" + (numResultsToSkip-numResultsToReturn) + "&numResultsToReturn=${numResultsToReturn}";
			
			// set up "next" link
			var next = document.getElementById("next");
			next.innerHTML = "Next";
			next.href = "search?q=${q}&numResultsToSkip=" + (numResultsToSkip+numResultsToReturn) + "&numResultsToReturn=${numResultsToReturn}";
			
			// if no results skipped, hide "previous" link
			if(numResultsToSkip <= 0) {
				previous.innerHTML = "";
			}
			
			// if no results remaining, hide "next" link
			if(!hasMore) {
				next.innerHTML = "";
			}
			
		</script>
    </body>
</html>
