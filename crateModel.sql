
DROP TABLE [VehicleStockroom]
go

DROP TABLE [PackageStockroom]
go

DROP TABLE [Stockroom]
go

DROP TABLE [Address]
go

DROP TABLE [City]
go

CREATE TABLE [Address]
( 
	[IdA]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[Street]             varchar(50)  NOT NULL ,
	[Number]             integer  NOT NULL ,
	[Xcoord]             integer  NOT NULL ,
	[Ycoord]             integer  NOT NULL ,
	[IdC]                bigint  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[IdC]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[Name]               varchar(50)  NOT NULL ,
	[PostalCode]         varchar(20)  NOT NULL 
)
go

CREATE TABLE [PackageStockroom]
( 
	[IdS]                bigint  NOT NULL 
)
go

CREATE TABLE [Stockroom]
( 
	[IdS]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdA]                bigint  NOT NULL 
)
go

CREATE TABLE [VehicleStockroom]
( 
	[IdS]                bigint  NOT NULL 
)
go

ALTER TABLE [Address]
	ADD CONSTRAINT [XPKAddress] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdC] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK1City] UNIQUE ([PostalCode]  ASC)
go

ALTER TABLE [PackageStockroom]
	ADD CONSTRAINT [XPKPackagesStockroom] PRIMARY KEY  CLUSTERED ([IdS] ASC)
go

ALTER TABLE [Stockroom]
	ADD CONSTRAINT [XPKStockroom] PRIMARY KEY  CLUSTERED ([IdS] ASC)
go

ALTER TABLE [VehicleStockroom]
	ADD CONSTRAINT [XPKVehicleStockroom] PRIMARY KEY  CLUSTERED ([IdS] ASC)
go


ALTER TABLE [Address]
	ADD CONSTRAINT [FK_City_Address] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [PackageStockroom]
	ADD CONSTRAINT [FK_Stockroom_PackageStockroom] FOREIGN KEY ([IdS]) REFERENCES [Stockroom]([IdS])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Stockroom]
	ADD CONSTRAINT [FK_Address_Stockroom] FOREIGN KEY ([IdA]) REFERENCES [Address]([IdA])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [VehicleStockroom]
	ADD CONSTRAINT [FK_Stockroom_VehicleStockroom] FOREIGN KEY ([IdS]) REFERENCES [Stockroom]([IdS])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go
