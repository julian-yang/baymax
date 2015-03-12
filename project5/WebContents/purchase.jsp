<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<html>
    <head>
        <title>Purchase Item!</title>
    </head>

    <body>
		<a href="${homeLink}">Home</a>
		
        <c:choose>
            <c:when test="${not isSecure}">
                <c:redirect url="${secureLink}"/>
            </c:when>
            <c:when test="${empty ItemResult}">
                <h1>Please access this form through the "Buy Now" link on the 
                    item's info page. </h1>
                <p>(ItemResult was not found)</p>
            </c:when>
            <c:when test="${empty ItemResult.buyPrice}">
                <h1>Not Available for Purchasing</h1>
                <p>Sorry, this item is not available for immediate purchase.</p>
            </c:when>
            <c:otherwise>
                <h1>Purchasing: ${ItemResult.name}</h1>
                <%--
                <p> redirect link: ${secureLink}</p>
                <p>Is secure: ${isSecure}</p>
                --%>
                <p>Item ID: ${ItemResult.itemId}</p>
                <p>Item Name: ${ItemResult.name}</p>
                <p>Buy Price: ${ItemResult.buyPrice}</p>
                <form action="/eBay/confirm" method="POST">
                    <span>Credit Card #: </span> 
                    <input name="creditCard" type="text">
                    <input value="Submit" type="submit">
                </form>
            </c:otherwise>
        </c:choose>
    </body>
</html>
