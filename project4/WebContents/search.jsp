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
	
        <ol>
            <c:forEach var="result" items="${results}">
                <li>
                    <p>ItemID: <span><a href="item?id=${result.getItemId()}">${result.getItemId()}</a></span>
                       Name: <span>${result.getName()}</span>
                    </p>
                </li>
            </c:forEach>
        </ol>
    </body>
</html>