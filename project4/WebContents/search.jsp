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
		
		${suggestions}

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