DROP TRIGGER IF EXISTS [dbo].TR_TransportOffer;

GO

CREATE TRIGGER TR_TransportOffer 
	ON [dbo].[Package]
	AFTER INSERT, UPDATE
AS 
BEGIN
	
	DECLARE @IdP int;
	DECLARE @IdStartAddress int;
	DECLARE @IdEndAddress int;
	DECLARE @PackageType int;
	DECLARE @Weight decimal(10, 2);
	
	DECLARE @StartX int; 
	DECLARE @StartY int;
	DECLARE @EndX int;
	DECLARE @EndY int;
	
	DECLARE @EuclideanDistance decimal(10, 3);

	DECLARE @cursor CURSOR

	SET @cursor = CURSOR FOR 
		SELECT IdP, IdStartAddress, IdEndAddress, PackageType, Weight
	FROM inserted

	OPEN @cursor

	FETCH NEXT FROM @cursor
	INTO @IdP, @IdStartAddress, @IdEndAddress, @PackageType, @Weight

	WHILE @@FETCH_STATUS = 0
	BEGIN
	
		select @IdP, @IdStartAddress, @IdEndAddress, @PackageType, @Weight;

		SELECT @StartX = Xcoord, @StartY = Ycoord
		FROM [dbo].[Address]
		WHERE IdA = @IdStartAddress

		SELECT @EndX = Xcoord, @EndY = Ycoord
		FROM [dbo].[Address]
		WHERE IdA = @IdEndAddress

		SET @EuclideanDistance = SQRT(POWER(@StartX - @EndX, 2)+ POWER(@StartY - @EndY, 2))
		
		select @EuclideanDistance

		FETCH NEXT FROM @cursor
		INTO @IdP, @IdStartAddress, @IdEndAddress, @PackageType, @Weight
	END

	CLOSE @cursor
	DEALLOCATE @cursor


END
