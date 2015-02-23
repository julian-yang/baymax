CREATE TABLE Locations
				(
					ItemID INTEGER,
					Position POINT NOT NULL,
					PRIMARY KEY (ItemID, Position),
					FOREIGN KEY (ItemID) REFERENCES Items(ItemID)
				) ENGINE = MyISAM COLLATE latin1_general_cs;

INSERT INTO Locations SELECT ItemID, Point(Latitude, Longitude) FROM Items WHERE Latitude<>'null' AND Longitude<>'null';

CREATE SPATIAL INDEX sp_index ON Locations (Position);