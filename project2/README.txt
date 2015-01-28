Part B:
1) Relations
    Items
        *ItemID
        -Name  
        -Buy_Price
        -First_Bid
        -Started
        -Ends
        -Latitude?
        -Longitude?
        -Location
        -Country
        -Description
        -Seller [UserID]
        
    ItemCategory
        *ItemID
        *Category

    Users
        *UserID
        -BuyerRating
        -SellerRating
        -Location
        -Country
        
    Bids
        *BidderID [UserID]
        *ItemID [ItemID]
        *Time
        -Amount

Symbols:
    "*" denotes primary key.  
    "[ ]" denotes foreign key.

2) Nontrivial functional dependencies
    Items:
        ItemID ->   Name, Buy_Price, First_Bid, Latitude, Longitude, Started,
                    Ends, Seller, Description,  
    Bids:
        BidderID, ItemID, Time -> Amount
        BidderID, Time -> ItemID, Amount
        
    ItemCategory:
        No non-trivial functional dependencies.  The only functional dependency 
        is: ItemID, Category -> ItemID, Category 

    Users: 
        UserID -> Rating, Location, Country
    
**Note: "Location" on bidders ONLY has country && location
            "Location" on items includes country, location, latitude &&
                longitude

3) Bids breaks BCNF form, since a bidder can only bid on one auction at
    any given point in time.  However it is much more useful to just keep
    ItemID in the same relation as opposed to splitting the table into two
    smaller relations.

4) All of the realtions are in Fourth Normal Form.  TODO: Check if this is
    correct...    
