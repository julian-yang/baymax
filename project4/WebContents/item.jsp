<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
    <head>
        <title>Item Results!</title>
    </head>
    <body>
		<a href="/eBay">Home</a>
		
		<form action="/eBay/item" method="GET">
            <span>Enter item ID:</span>
            <input name="id" type="text">
            <input type="submit">
        </form>
		
		${result}
    </body>
</html>