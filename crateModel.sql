
DROP TABLE [VehicleStockroom]
go

DROP TABLE [Customer]
go

DROP TABLE [Admin]
go

DROP TABLE [CourierRequest]
go

DROP TABLE [HistoryDrive]
go

DROP TABLE [CurrentDrivePackage]
go

DROP TABLE [StockedPackagesForCurrentDrive]
go

DROP TABLE [CurrentDrivePlan]
go

DROP TABLE [CurrentDrive]
go

DROP TABLE [Vehicle]
go

DROP TABLE [Courier]
go

DROP TABLE [PackageStockroom]
go

DROP TABLE [Package]
go

DROP TABLE [User]
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
	[DrivingLicenceNumber] varchar(20)  NOT NULL ,
	[DeliveredPackagesNumber] integer  NULL 
	CONSTRAINT [DF_DeliveredPackagesNumber]
		 DEFAULT  0,
	[Profit]             decimal(10,2)  NULL 
	CONSTRAINT [DF_Profit]
		 DEFAULT  0.0,
	[CourierStatus]      integer  NULL 
	CONSTRAINT [DF_CourierStatus]
		 DEFAULT  0
	CONSTRAINT [CK1_CourierStatus]
		CHECK  ( (CourierStatus IN (0, 1)) )
)
go

CREATE TABLE [CourierRequest]
( 
	[IdCR]               bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[DrivingLicenceNumber] varchar(20)  NULL ,
	[IdU]                bigint  NOT NULL 
)
go

CREATE TABLE [CurrentDrive]
( 
	[IdCD]               bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdU]                bigint  NOT NULL ,
	[IdV]                bigint  NOT NULL ,
	[CurrentPlanPoint]   integer  NOT NULL 
	CONSTRAINT [DF_CurrentPlanLocation]
		 DEFAULT  0,
	[RealizedProfit]     decimal(10,3)  NOT NULL 
	CONSTRAINT [DF_RealizedProfit]
		 DEFAULT  0.0,
	[IdA]                bigint  NOT NULL ,
	[NumberOfDeliveries] integer  NULL 
	CONSTRAINT [DF_NumberOfDeliveries]
		 DEFAULT  0
)
go

CREATE TABLE [CurrentDrivePackage]
( 
	[IdCD]               bigint  NOT NULL ,
	[IdP]                bigint  NOT NULL ,
	[IdCDP]              bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[DeliveryStatus]     integer  NULL 
	CONSTRAINT [DF_DeliveryStatus]
		 DEFAULT  0
)
go

CREATE TABLE [CurrentDrivePlan]
( 
	[IdPlan]             bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdCD]               bigint  NOT NULL ,
	[OrdinalVisitNumber] integer  NOT NULL 
	CONSTRAINT [DF_OrdinalVisitNumber]
		 DEFAULT  0,
	[VisitReason]        integer  NOT NULL ,
	[IdP]                bigint  NULL 
	CONSTRAINT [DF_IdP]
		 DEFAULT  NULL,
	[IdA]                bigint  NOT NULL 
)
go

CREATE TABLE [Customer]
( 
	[IdU]                bigint  NOT NULL 
)
go

CREATE TABLE [HistoryDrive]
( 
	[IdHD]               bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdU]                bigint  NOT NULL ,
	[IdV]                bigint  NOT NULL 
)
go

CREATE TABLE [Package]
( 
	[IdP]                bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdStartAddress]     bigint  NOT NULL ,
	[IdEndAddress]       bigint  NOT NULL ,
	[PackageType]        integer  NOT NULL 
	CONSTRAINT [CK1_PackageType]
		CHECK  ( (PackageType IN (0, 1, 2, 3)) ),
	[Weight]             decimal(10,2)  NOT NULL 
	CONSTRAINT [DF_Weight]
		 DEFAULT  10.0,
	[PackageStatus]      integer  NOT NULL 
	CONSTRAINT [DF_PackageStatus]
		 DEFAULT  0
	CONSTRAINT [CK1_PackageStatus]
		CHECK  ( (PackageStatus IN (0, 1, 2, 3, 4)) ),
	[Price]              decimal(10,2)  NULL 
	CONSTRAINT [DF_Price]
		 DEFAULT  0.0,
	[CreateTime]         datetime  NULL 
	CONSTRAINT [DF_CreationTime]
		 DEFAULT  CURRENT_TIMESTAMP,
	[AcceptRejectTime]   datetime  NULL 
	CONSTRAINT [DF_AcceptTime]
		 DEFAULT  NULL,
	[IdC]                bigint  NULL ,
	[IdU]                bigint  NOT NULL 
)
go

CREATE TABLE [PackageStockroom]
( 
	[IdS]                bigint  NOT NULL ,
	[IdPS]               bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdP]                bigint  NOT NULL 
)
go

CREATE TABLE [StockedPackagesForCurrentDrive]
( 
	[IdSPCD]             bigint  IDENTITY ( 1,1 )  NOT NULL ,
	[IdPS]               bigint  NOT NULL ,
	[IdP]                bigint  NOT NULL ,
	[IdPlan]             bigint  NOT NULL 
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
	CONSTRAINT [CK1_Firstname]
		CHECK  ( (ASCII(LEFT(Firstname, 1)) BETWEEN ASCII('A') and ASCII('Z')) ),
	[Lastname]           varchar(20)  NOT NULL 
	CONSTRAINT [CK1_Lastname]
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
	CONSTRAINT [CK1_FuelType]
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

ALTER TABLE [CurrentDrive]
	ADD CONSTRAINT [XPKCurrentDrive] PRIMARY KEY  CLUSTERED ([IdCD] ASC)
go

ALTER TABLE [CurrentDrive]
	ADD CONSTRAINT [XAK1CurrentDrive] UNIQUE ([IdU]  ASC,[IdV]  ASC)
go

ALTER TABLE [CurrentDrivePackage]
	ADD CONSTRAINT [XPKCurrentDrivePackage] PRIMARY KEY  CLUSTERED ([IdCDP] ASC)
go

ALTER TABLE [CurrentDrivePlan]
	ADD CONSTRAINT [XPKCurrentDrivePlan] PRIMARY KEY  CLUSTERED ([IdPlan] ASC)
go

ALTER TABLE [Customer]
	ADD CONSTRAINT [XPKCustomer] PRIMARY KEY  CLUSTERED ([IdU] ASC)
go

ALTER TABLE [HistoryDrive]
	ADD CONSTRAINT [XPKHistoryDrive] PRIMARY KEY  CLUSTERED ([IdHD] ASC)
go

ALTER TABLE [Package]
	ADD CONSTRAINT [XPKPackage] PRIMARY KEY  CLUSTERED ([IdP] ASC)
go

ALTER TABLE [PackageStockroom]
	ADD CONSTRAINT [XPKPackagesStockroom] PRIMARY KEY  CLUSTERED ([IdPS] ASC)
go

ALTER TABLE [StockedPackagesForCurrentDrive]
	ADD CONSTRAINT [XPKStockedPackagesForCurrentDrive] PRIMARY KEY  CLUSTERED ([IdSPCD] ASC)
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


ALTER TABLE [CurrentDrive]
	ADD CONSTRAINT [FK_Courier_CurrentDrive] FOREIGN KEY ([IdU]) REFERENCES [Courier]([IdU])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [CurrentDrive]
	ADD CONSTRAINT [FK_Vehicle_CurrentDrive] FOREIGN KEY ([IdV]) REFERENCES [Vehicle]([IdV])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [CurrentDrive]
	ADD CONSTRAINT [FK_Address_CourierDrive] FOREIGN KEY ([IdA]) REFERENCES [Address]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [CurrentDrivePackage]
	ADD CONSTRAINT [FK_Package_CurrentDrivePackage] FOREIGN KEY ([IdP]) REFERENCES [Package]([IdP])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CurrentDrivePackage]
	ADD CONSTRAINT [FK_CurrentDrive_CurrentDrivePackage] FOREIGN KEY ([IdCD]) REFERENCES [CurrentDrive]([IdCD])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [CurrentDrivePlan]
	ADD CONSTRAINT [FK_Package_CurrentDrivePlan] FOREIGN KEY ([IdP]) REFERENCES [Package]([IdP])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CurrentDrivePlan]
	ADD CONSTRAINT [FK_Address_CurrentDrivePlan] FOREIGN KEY ([IdA]) REFERENCES [Address]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CurrentDrivePlan]
	ADD CONSTRAINT [FK_CurrentDrive_CurrentDrivePlan] FOREIGN KEY ([IdCD]) REFERENCES [CurrentDrive]([IdCD])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Customer]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdU]) REFERENCES [User]([IdU])
		ON DELETE CASCADE
		ON UPDATE CASCADE
go


ALTER TABLE [HistoryDrive]
	ADD CONSTRAINT [FK_Courier_HistoryDrive] FOREIGN KEY ([IdU]) REFERENCES [Courier]([IdU])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [HistoryDrive]
	ADD CONSTRAINT [FK_Vehicle_HistoryDrive] FOREIGN KEY ([IdV]) REFERENCES [Vehicle]([IdV])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Package]
	ADD CONSTRAINT [FK__Start_Address__Package] FOREIGN KEY ([IdStartAddress]) REFERENCES [Address]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [FK__End_Address__Package] FOREIGN KEY ([IdEndAddress]) REFERENCES [Address]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Package]
	ADD CONSTRAINT [FK_City_Package] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Package]
	ADD CONSTRAINT [FK_User_Package] FOREIGN KEY ([IdU]) REFERENCES [User]([IdU])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [PackageStockroom]
	ADD CONSTRAINT [FK_Stockroom_PackageStockroom] FOREIGN KEY ([IdS]) REFERENCES [Stockroom]([IdS])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [PackageStockroom]
	ADD CONSTRAINT [FK_Package_PackageStockroom] FOREIGN KEY ([IdP]) REFERENCES [Package]([IdP])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [StockedPackagesForCurrentDrive]
	ADD CONSTRAINT [FK_PackageStockroom_StockedPackagesForCurrentDrive] FOREIGN KEY ([IdPS]) REFERENCES [PackageStockroom]([IdPS])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [StockedPackagesForCurrentDrive]
	ADD CONSTRAINT [FK_Package_StockedPackagesForCurrentDrive] FOREIGN KEY ([IdP]) REFERENCES [Package]([IdP])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [StockedPackagesForCurrentDrive]
	ADD CONSTRAINT [FK_CurrentDrive_StockedPackagesForCurrentDrive] FOREIGN KEY ([IdPlan]) REFERENCES [CurrentDrivePlan]([IdPlan])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
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
