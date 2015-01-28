CREATE TABLE Users
                (   
                    UserID VARCHAR(200), 
                    BuyerRating INTEGER, 
                    SellerRating Integer,
                    Location VARCHAR(200), 
                    Country VARCHAR(200),
                    PRIMARY KEY (UserID)
                );

CREATE TABLE Items 
                (   
                    ItemID INTEGER, 
                    Name VARCHAR(200), 
                    BuyPrice DECIMAL(8,2),
                    FirstBid DECIMAL(8,2), 
                    Started TIMESTAMP, 
                    Ends TIMESTAMP, 
                    Latitude VARCHAR(200), 
                    Longitude VARCHAR(200), 
                    Location VARCHAR(200), 
                    Country VARCHAR(200), 
                    Description VARCHAR(4000), 
                    SellerID VARCHAR(200),
                    PRIMARY KEY (ItemID),
                    FOREIGN KEY (SellerID) REFERENCES Users(UserID)
                );

CREATE TABLE ItemCategories
                (
                    ItemID Integer,
                    Category VARCHAR(200),
                    PRIMARY KEY (ItemID, Category),
                    FOREIGN KEY (ITEMID) REFERENCES Items(ItemID)
                );

CREATE TABLE Bids
                (
                    BidderID VARCHAR(200),
                    ItemID Integer,
                    Time TIMESTAMP,
                    Amount DECIMAL(8,2),
                    PRIMARY KEY (BidderID, ItemID, Time),
                    FOREIGN KEY (BidderID) REFERENCES Users(UserID),
                    FOREIGN KEY (ItemID) REFERENCES Items(ItemID)
                );
