package edu.ucla.cs.cs144;

public class ItemResult {
    private  String itemId;
    private  String name;
    private  String[] categories;
    private  String currently;
    private  String buyPrice;
    private  String firstBid;
    private  String numberOfBids;
    private  BidResult[] bids;
    private  String longitude;
    private  String latitude;
    private  String location;
    private  String country;
    private  String started;
    private  String ends;
    private  String sellerId;
    private  String sellerRating;
    private  String description;

    public ItemResult() {}
    
    public ItemResult(String itemId, String name, String[] categories, 
            String currently, String buyPrice, String firstBid, String numberOfBids,
            BidResult[] bids, String longitude, String latitude, String location, 
            String country, String started, String ends, String sellerId, String sellerRating, 
            String description) {
        this.itemId = itemId;
        this.name = name;
        this.categories = categories;
        this.currently = currently;
        this.buyPrice = buyPrice;
        this.firstBid = firstBid;
        this.numberOfBids = numberOfBids;
        this.bids = bids;
        this.longitude = longitude;
        this.latitude = latitude;
        this.location = location;
        this.country = country;
        this.started = started;
        this.ends = ends;
        this.sellerId = sellerId;
        this.sellerRating = sellerRating;
        this.description = description;
    }
    

    public String getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getCurrently() {
        return currently;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public String getFirstBid() {
        return firstBid;
    }

    public String getNumberOfBids() {
        return numberOfBids;
    }
    
    public BidResult[] getBids() {
        return bids;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLocation() {
        return location;
    }

    public String getCountry() {
        return country;
    }

    public String getStarted() {
        return started;
    }

    public String getEnds() {
        return ends;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerRating() {
        return sellerRating;
    }

    public String getDescription() {
        return description;
    }
}
