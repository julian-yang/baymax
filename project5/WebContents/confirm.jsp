<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Purchase Confirmation!</title>
    </head>

    <body>
		<a href="${homeLink}">Home</a>
		
        <c:choose>
            <c:when test="${empty ItemResult.buyPrice}">
                <h1>Could Not Purchase</h1>
                <p>Sorry, this item is not available for buying directly</p>
            </c:when>
            <c:otherwise>
                <h1>Purchase Confirmation for ${ItemResult.name}</h1>
                <p>Is secure: ${isSecure}</p>
                
                <p>Item ID: ${ItemResult.itemId}</p>
                <p>Item Name: ${ItemResult.name}</p>
                <p>Buy Price: ${ItemResult.buyPrice}</p>
				
				<p>Credit Card: ${creditCard}</p>
				<p>Transaction Time: ${transactionTime}</p>
				
            </c:otherwise>
        </c:choose>
    </body>
</html>
