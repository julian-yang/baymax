<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Search Results!</title>
    </head>
    <body>
		<a href="/eBay">Home</a>
		
		<form action="/eBay/search" method="GET">
            <span>Enter search text:</span>
            <input name="q" type="text">
            <!-- TODO: set these hidden fields with Javascript or something -->
            <input name="numResultsToSkip" type="hidden" value="0">
            <input name="numResultsToReturn" type="hidden" value="10">
            <input type="submit">
        </form>
	
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
			var q = "${q}";
			var numResultsToSkip = parseInt("${numResultsToSkip}");
			var numResultsToReturn = parseInt("${numResultsToReturn}");
			var numResultsReturned = parseInt("${numResultsReturned}");
			
			//var url = "search?q=${q}&numResultsToSkip=" + 0 + "&numResultsToReturn=${numResultsToReturn}";

			var previous = document.getElementById("previous");
			previous.innerHTML = "Previous"			
			previous.href = "search?q=${q}&numResultsToSkip=" + (numResultsToSkip-numResultsToReturn) + "&numResultsToReturn=${numResultsToReturn}";
			
			var next = document.getElementById("next");
			next.innerHTML = "Next";
			next.href = "search?q=${q}&numResultsToSkip=" + (numResultsToSkip+numResultsToReturn) + "&numResultsToReturn=${numResultsToReturn}";
			
			if(numResultsToSkip <= 0) {
				previous.innerHTML = "";
			}
			
			if(numResultsReturned < numResultsToReturn) {
				next.innerHTML = "";
			}
			
		</script>
    </body>
</html>