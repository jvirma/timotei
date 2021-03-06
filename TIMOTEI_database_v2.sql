PRAGMA foreign_keys = ON;

CREATE TABLE "SmartPost" (
"ID" INTEGER ROWID NOT NULL UNIQUE PRIMARY KEY,
"Name" VARCHAR(32) NOT NULL,
"Latitude" VARCHAR(12) NOT NULL,
"Longitude" VARCHAR(12) NOT NULL,
"Road" VARCHAR(32) NOT NULL,
"Postcode" VARCHAR(8) NOT NULL,
"OpeningTime" VARCHAR(64) NOT NULL,

FOREIGN KEY("Postcode") REFERENCES "PostOffice"("Postcode")
);

CREATE TABLE "PostOffice" (

"Postcode" VARCHAR(8) NOT NULL PRIMARY KEY,
"City" VARCHAR(32) NOT NULL,

FOREIGN KEY("Postcode") REFERENCES "SmartPost"("Postcode")
);

CREATE TABLE "Delivery" (
"PackageID" INTEGER NOT NULL UNIQUE,
"PostmanID" INTEGER NOT NULL UNIQUE,
"PackageUnbroken" INTEGER NOT NULL,

PRIMARY KEY("PackageID", "PostmanID"),
FOREIGN KEY("PostmanID") REFERENCES "Postman"("PostmanID"),
FOREIGN KEY("PackageID") REFERENCES "Storage"("PackageID"),

CHECK("PackageUnbroken" IN (0,1))
 
);
CREATE TABLE "Postman" (
"PostmanID" INTEGER ROWID NOT NULL UNIQUE PRIMARY KEY,
"PostmanName" VARCHAR(32) NOT NULL
);

CREATE TABLE "Deliver" (
"PackageID" INTEGER NOT NULL UNIQUE,
"PostmanID" INTEGER NOT NULL UNIQUE,
PRIMARY KEY("PackageID", "PostmanID"),
FOREIGN KEY("PostmanID") REFERENCES "Postman"("PostmanID"),
FOREIGN KEY("PackageID") REFERENCES "Storage"("PackageID")
);

CREATE TABLE "DeliveredPackage" (
"PackageID" INTEGER ROWID NOT NULL,
"Class" INTEGER NOT NULL,
"ItemID" INTEGER NOT NULL,
"StartID" INTEGER NOT NULL,
"DestinationID" INTEGER NOT NULL,
"Distance" REAL NOT NULL,
"Broken" INTEGER NOT NULL,
PRIMARY KEY("PackageID"),

FOREIGN KEY("ItemID") REFERENCES "Item"("ItemID"),
FOREIGN KEY("StartID") REFERENCES "SmartPost"("ID"),
FOREIGN KEY("DestinationID") REFERENCES "SmartPost"("ID"),
CHECK("Broken" IN (0,1))

);

CREATE TABLE "PackageCreated" (
"PackageID" INTEGER ROWID NOT NULL,
"Class" INTEGER NOT NULL,
"ItemID" INTEGER NOT NULL,
"StartID" INTEGER NOT NULL,
"DestinationID" INTEGER NOT NULL,
"Distance" REAL NOT NULL,

PRIMARY KEY("PackageID"),

FOREIGN KEY("ItemID") REFERENCES "Item"("ItemID"),
FOREIGN KEY("StartID") REFERENCES "SmartPost"("ID"),
FOREIGN KEY("DestinationID") REFERENCES "SmartPost"("ID")

);

CREATE TABLE "Log" (
"LogID" INTEGER ROWID NOT NULL PRIMARY KEY,
"Time" VARCHAR(32) NOT NULL,
"Action" VARCHAR(64) NOT NULL
);

CREATE TABLE "Statistic" (
"KilometersTotal" INTEGER PRIMARY KEY
); 

CREATE TABLE "Package" (
"Class" INTEGER NOT NULL UNIQUE PRIMARY KEY,
"Speed" INTEGER NOT NULL,
"WeightMax" REAL NOT NULL,
"SizeMax" REAL NOT NULL,
"DistanceMax" REAL NOT NULL,

CHECK("SizeMax" > 0 AND "WeightMax" > 0 AND "Class" IN (1, 2, 3))
);

CREATE TABLE "Item" (
"ItemID" INTEGER ROWID NOT NULL UNIQUE PRIMARY KEY,
"Name" VARCHAR(32) NOT NULL,
"Weight" INTEGER NOT NULL,
"ItemSize" INTEGER NOT NULL,
"Breakable" INTEGER NOT NULL,  

CHECK("ItemSize" > 0 AND "Weight" > 0 AND "Breakable" IN (0,1))
);