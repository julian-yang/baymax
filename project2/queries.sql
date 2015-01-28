SELECT COUNT(*) FROM Users;

SELECT COUNT(*) FROM Items WHERE Location="New York";

SELECT COUNT(*) 
    FROM (
        SELECT COUNT(*) AS ItemCats 
        FROM ItemCategories 
        Group By ItemID
    ) 
    AS ItemCategoryCounts WHERE ItemCats = 4;

SELECT B.ItemID
    FROM (
        SELECT Items.ItemID, Amount
        FROM Items NATURAL JOIN Bids 
        WHERE Ends > '2001-12-20 00:00:00'
    ) AS B
    WHERE Amount = 
        (SELECT MAX(Amount) 
        FROM Items NATURAL JOIN Bids 
        WHERE Ends > '2001-12-20 00:00:00');

    -- How to write this without having to join twice???



