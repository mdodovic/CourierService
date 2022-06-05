-- courier
SELECT C.*, U.*, City.*
	FROM [dbo].[Courier] C
		INNER JOIN [dbo].[User] U ON (C.IdU = U.IdU)
		INNER JOIN [dbo].[Address] A ON (U.IdA = A.IdA)
		INNER JOIN [dbo].[City] City ON (City.IdC = A.IdC)

-- stockrooms
SELECT S.*, A.*, City.*
	FROM [dbo].[Stockroom] S
		INNER JOIN [dbo].[Address] A ON (S.IdA = A.IdA)
		INNER JOIN [dbo].[City] City ON (City.IdC = A.IdC)

select *
	from Vehicle
-- vehicle stockrooms
select VS.*, City.Name
	from VehicleStockroom VS
		INNER JOIN [dbo].[Stockroom] S ON (VS.IdS = S.IdS)
		INNER JOIN [dbo].[Address] A ON (S.IdA = A.IdA)
		INNER JOIN [dbo].[City] City ON (City.IdC = A.IdC)

select *
	from CurrentDrive

select *
	from HistoryDrive

select * 
	from Package









