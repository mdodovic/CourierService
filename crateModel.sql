
DROP TABLE [VehicleStockroom]
go

DROP TABLE [Vehicle]
go

DROP TABLE [PackageStockroom]
go

DROP TABLE [Stockroom]
go

DROP TABLE [Customer]
go

DROP TABLE [Admin]
go

DROP TABLE [Courier]
go

DROP TABLE [CourierRequest]
go

DROP TABLE [User]
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

CREATE TABLE [Admin]
( 
	[IdU]                bigint  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[IdC]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[Name]               varchar(50)  NOT NULL ,
	[PostalCode]         varchar(20)  NOT NULL 
)
go

CREATE TABLE [Courier]
( 
	[IdU]                bigint  NOT NULL ,
	[DrivingLicenceNumber] varchar(20)  NOT NULL 
)
go

CREATE TABLE [CourierRequest]
( 
	[IdCR]               bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[DrivingLicenceNumber] varchar(20)  NULL ,
	[IdU]                bigint  NOT NULL 
)
go

CREATE TABLE [Customer]
( 
	[IdU]                bigint  NOT NULL 
)
go

CREATE TABLE [PackageStockroom]
( 
	[IdS]                bigint  NOT NULL ,
	[IdPS]               bigint  IDENTITY ( 1,1 )  NOT NULL 
)
go

CREATE TABLE [Stockroom]
( 
	[IdS]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdA]                bigint  NOT NULL 
)
go

CREATE TABLE [User]
( 
	[IdU]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[Firstname]          varchar(20)  NOT NULL 
	CONSTRAINT [Firstname]
		CHECK  ( (ASCII(LEFT(Firstname, 1)) BETWEEN ASCII('A') and ASCII('Z')) ),
	[Lastname]           varchar(20)  NOT NULL 
	CONSTRAINT [Lastname]
		CHECK  ( (ASCII(LEFT(Lastname, 1)) BETWEEN ASCII('A') and ASCII('Z')) ),
	[Username]           varchar(20)  NOT NULL ,
	[Password]           varchar(20)  NOT NULL ,
	[IdA]                bigint  NOT NULL 
)
go

CREATE TABLE [Vehicle]
( 
	[IdV]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[LicencePlateNumber] varchar(10)  NOT NULL ,
	[FuelType]           integer  NOT NULL 
	CONSTRAINT [FuelType]
		CHECK  ( (FuelType IN (0, 1, 2)) ),
	[FuelConsumption]    decimal(10,2)  NOT NULL ,
	[Capacity]           decimal(10,2)  NOT NULL 
)
go

CREATE TABLE [VehicleStockroom]
( 
	[IdVS]               bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdS]                bigint  NOT NULL ,
	[IdV]                bigint  NOT NULL 
)
go

ALTER TABLE [Address]
	ADD CONSTRAINT [XPKAddress] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [Admin]
	ADD CONSTRAINT [XPKAdmin] PRIMARY KEY  CLUSTERED ([IdU] ASC)
go

ALTER TABLE [Admin]
	ADD CONSTRAINT [XAK1Admin] UNIQUE ([IdU]  ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdC] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK1City] UNIQUE ([PostalCode]  ASC)
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [XPKCourier] PRIMARY KEY  CLUSTERED ([IdU] ASC)
go

ALTER TABLE [Courier]
	ADD CONSTRAINT [XAK1Courier] UNIQUE ([DrivingLicenceNumber]  ASC)
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [XPKCourierRequest] PRIMARY KEY  CLUSTERED ([IdCR] ASC)
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [XAK1CourierRequest] UNIQUE ([IdU]  ASC)
go

ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [XAK2CourierRequest] UNIQUE ([DrivingLicenceNumber]  ASC)
go

ALTER TABLE [Customer]
	ADD CONSTRAINT [XPKCustomer] PRIMARY KEY  CLUSTERED ([IdU] ASC)
go

ALTER TABLE [PackageStockroom]
	ADD CONSTRAINT [XPKPackagesStockroom] PRIMARY KEY  CLUSTERED ([IdPS] ASC)
go

ALTER TABLE [Stockroom]
	ADD CONSTRAINT [XPKStockroom] PRIMARY KEY  CLUSTERED ([IdS] ASC)
go

ALTER TABLE [User]
	ADD CONSTRAINT [XPKUser] PRIMARY KEY  CLUSTERED ([IdU] ASC)
go

ALTER TABLE [User]
	ADD CONSTRAINT [XAK1User] UNIQUE ([Username]  ASC)
go

ALTER TABLE [Vehicle]
	ADD CONSTRAINT [XPKVehicle] PRIMARY KEY  CLUSTERED ([IdV] ASC)
go

ALTER TABLE [Vehicle]
	ADD CONSTRAINT [XAK1Vehicle] UNIQUE ([LicencePlateNumber]  ASC)
go

ALTER TABLE [VehicleStockroom]
	ADD CONSTRAINT [XPKVehicleStockroom] PRIMARY KEY  CLUSTERED ([IdVS] ASC)
go


ALTER TABLE [Address]
	ADD CONSTRAINT [FK_City_Address] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Admin]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdU]) REFERENCES [User]([IdU])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [Courier]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdU]) REFERENCES [User]([IdU])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [CourierRequest]
	ADD CONSTRAINT [FK_User_CourierRequest] FOREIGN KEY ([IdU]) REFERENCES [User]([IdU])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Customer]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdU]) REFERENCES [User]([IdU])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [PackageStockroom]
	ADD CONSTRAINT [FK_Stockroom_PackageStockroom] FOREIGN KEY ([IdS]) REFERENCES [Stockroom]([IdS])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Stockroom]
	ADD CONSTRAINT [FK_Address_Stockroom] FOREIGN KEY ([IdA]) REFERENCES [Address]([IdA])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [User]
	ADD CONSTRAINT [FK_Address_User] FOREIGN KEY ([IdA]) REFERENCES [Address]([IdA])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [VehicleStockroom]
	ADD CONSTRAINT [FK_Stockroom_VehicleStockroom] FOREIGN KEY ([IdS]) REFERENCES [Stockroom]([IdS])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [VehicleStockroom]
	ADD CONSTRAINT [FK_Vehicle_VehicleStockroom] FOREIGN KEY ([IdV]) REFERENCES [Vehicle]([IdV])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go
