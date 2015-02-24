package edu.ucla.cs.cs144;

public class BidResult {
    private String bidderId;
    private String bidderRating;
    private String bidderLocation;
    private String bidderCountry;
    private String bidAmount;
    private String bidTime;

    public BidResult() {}

    public BidResult(String bidderId, String bidderRating, String bidderLocation,
            String bidderCountry, String bidAmount, String bidTime) {
        this.bidderId = bidderId;
        this.bidderRating = bidderRating;
        this.bidderLocation = bidderLocation;
        this.bidderCountry = bidderCountry;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }

    public String getBidderId() {
        return bidderId;
    }

    public String getBidderRating() {
        return bidderRating;
    }

    public String getBidderLocation() {
        return bidderLocation;
    }

    public String getBidderCountry() {
        return bidderCountry;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public String getBidTime() {
        return bidTime;
    }
}
