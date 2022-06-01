
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

ALTER TABLE [Address]
	ADD CONSTRAINT [XPKAddress] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdC] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK1City] UNIQUE ([PostalCode]  ASC)
go


ALTER TABLE [Address]
	ADD CONSTRAINT [FKCity] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go
