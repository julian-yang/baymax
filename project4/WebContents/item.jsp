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
		
        <h1>${ItemResult.name}</h1>
        <p>ID#${ItemResult.itemId}</p>
        <p>Description: ${ItemResult.description} </p>
        <p>Categories: <br/>
            <ul>
                <c:forEach var="category" items="${ItemResult.categories}">
                    <li>${category}</li>
                </c:forEach>
            </ul>
        </p>
        
        <p>Current Price: ${ItemResult.currently} </p>
        <p>BuyPrice: 
            <c:choose>
                <c:when test="${empty ItemResult.buyPrice}">N/A</c:when>
                <c:otherwise>${ItemResult.buyPrice}</c:otherwise>
            </c:choose>
        </p>
        <p>Minimum Bid: 
            <c:choose>
                <c:when test="${empty ItemResult.firstBid}">N/A</c:when>
                <c:otherwise>${ItemResult.firstBid}</c:otherwise>
            </c:choose>
        </p>
        <p>Bids (${ItemResult.numberOfBids}): <br/>
            <ol>
                <c:forEach var="bid" items="${ItemResult.bids}">
                    <li>
                        <p>${bid.bidTime}: ${bid.bidAmount}</p>
                        <p>User: ${bid.bidderId}</p>
                        <p>Rating: ${bid.bidderRating}</p>
                        <p>Bidder Location: ${bid.bidderLocation}</p>
                        <p>Bidder Country: ${bid.bidderCountry}</p>
                    </li>
                </c:forEach>
            </ol>
        </p>
        <p>Location: ${ItemResult.location} </p>
        <p>Country: ${ItemResult.country} </p>
        <p>Started: ${ItemResult.started} </p>
        <p>Ends: ${ItemResult.ends} </p>
        <p>SellerId: ${ItemResult.sellerId} </p>
        <p>SellerRating: ${ItemResult.sellerRating} </p>
        
    </body>
</html>
