<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>

    <head>
        <title>Item Results!</title>
		<meta name="viewport" content="initial-scale=1.0, user-scalable=no" /> 
		<script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=false"></script>
		<script type="text/javascript">
			var geocoder;
			var map;
			function initialize(address) {
				geocoder = new google.maps.Geocoder();
				var latlng = new google.maps.LatLng(39.50, -98.35);
				var mapOptions = {
					zoom: 2, // default: 8 
					center: latlng,
					mapTypeId: google.maps.MapTypeId.ROADMAP
				};
				map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
				
				// if address parameter is provided, translate it to latitude/longitude via geocoding and set zoom level
				if(typeof address !== 'undefined' && address !== null) {
					codeAddress(address, 14);
				}
			}
			
			function codeAddress(address, zoom) {
				geocoder.geocode( {'address': address}, function(results, status) {
					if(status == google.maps.GeocoderStatus.OK) {
						map.setCenter(results[0].geometry.location);
						
						// if zoom level (number) is provided, set zoom
						if(typeof zoom === 'number') {
							map.setZoom(zoom);
						}
						
						var marker = new google.maps.Marker({
							map: map,
							position: results[0].geometry.location
						});
					}
					else {
						// if geocoding is unsuccessful, do nothing
						// alert("Geocode was not successful for the following reason: " + status);
					}
				});
			}
		</script> 
    </head>
	
	<!-- provide location string using JSP to onload initialize() method below -->
    <body onload="initialize('${ItemResult.location}')">
		<a href="/eBay">Home</a>
		
		<form action="/eBay/item" method="GET">
            <span>Enter item ID:</span>
            <input name="id" type="text">
            <input type="submit">
        </form>
				
		
        <!--- If item does not exist -->
        <c:choose>
            <c:when test="${empty result}">
                <h1>Item not found!</h1>
                <p>The item ID could not be found</p>
            </c:when>
            <c:otherwise>
                <div id ="map_canvas" style="width: 300px; height: 250px"></div>
		        <br>

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
            </c:otherwise>
        </c:choose>

    </body>
</html>
