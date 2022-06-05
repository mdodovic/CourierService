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

-- vehicle stockroom
select VS.*, City.Name
	from VehicleStockroom VS
		INNER JOIN [dbo].[Stockroom] S ON (VS.IdS = S.IdS)
		INNER JOIN [dbo].[Address] A ON (S.IdA = A.IdA)
		INNER JOIN [dbo].[City] City ON (City.IdC = A.IdC)

-- package stockroom
select PS.*, City.Name
	from PackageStockroom PS
		INNER JOIN [dbo].[Stockroom] S ON (PS.IdS = S.IdS)
		INNER JOIN [dbo].[Address] A ON (S.IdA = A.IdA)
		INNER JOIN [dbo].[City] City ON (City.IdC = A.IdC)

select CD.*
	from CurrentDrive CD
		INNER JOIN [dbo].Vehicle V ON (CD.IdV = V.IdV)

select P.*, SCity.Name AS StartCity, ECity.Name AS EndCity
	from Package P
		INNER JOIN [dbo].[Address] SA ON (SA.IdA = P.IdStartAddress)
		INNER JOIN [dbo].[City] SCity ON (SCity.IdC = SA.IdC)
		INNER JOIN [dbo].[Address] EA ON (EA.IdA = P.IdEndAddress)
		INNER JOIN [dbo].[City] ECity ON (ECity.IdC = EA.IdC)
ORDER BY P.AcceptRejectTime ASC







