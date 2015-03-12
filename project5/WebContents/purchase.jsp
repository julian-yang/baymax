<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Purchase Item!</title>
    </head>

    <body>
        <c:choose>
            <c:when test="${empty ItemResult}">
                <h1>Please access this form through the "Buy Now" link on the 
                    item's info page. </h1>
            </c:when>
            <c:when test="${empty ItemResult.buyPrice}">
                <h1>Not Available for Purchasing</h1>
                <p>Sorry, this item is not available for immediate purchase.</p>
            </c:when>
            <c:otherwise>
                <h1>Purchasing: ${ItemResult.name}</h1>
                <p>Is secure: ${isSecure}</p>
                
                <p>ItemID: ${ItemResult.itemId}</p>
                <p>ItemName: ${ItemResult.name}</p>
                <p>BuyPrice: ${ItemResult.buyPrice}</p>
                <form action="/eBay/confirm" method="POST">
                    <span>Credit Card #: </span> 
                    <input name="creditCard" type="text">
                    <input name="Purchase" type="submit">
                </form>
            </c:otherwise>
        </c:choose>
    </body>
</html>
