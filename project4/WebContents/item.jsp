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
				if(typeof address !== 'undefined') {
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
    <body onload="initialize('Los Angeles')">
		<a href="/eBay">Home</a>
		
		<form action="/eBay/item" method="GET">
            <span>Enter item ID:</span>
            <input name="id" type="text">
            <input type="submit">
        </form>
				
		<div id ="map_canvas" style="width: 300px; height: 250px"></div>
		<br>
		
		${result}

    </body>
	
</html>