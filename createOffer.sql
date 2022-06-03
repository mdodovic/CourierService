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
	DECLARE @BasePrice int;
	DECLARE @PricePerKg int;

	DECLARE @cursor CURSOR

	SET @cursor = CURSOR FOR 
		SELECT IdP, IdStartAddress, IdEndAddress, PackageType, Weight
	FROM inserted

	OPEN @cursor

	FETCH NEXT FROM @cursor
	INTO @IdP, @IdStartAddress, @IdEndAddress, @PackageType, @Weight

	WHILE @@FETCH_STATUS = 0
	BEGIN
	
		SELECT @StartX = Xcoord, @StartY = Ycoord
		FROM [dbo].[Address]
		WHERE IdA = @IdStartAddress

		SELECT @EndX = Xcoord, @EndY = Ycoord
		FROM [dbo].[Address]
		WHERE IdA = @IdEndAddress

		SET @EuclideanDistance = SQRT(POWER(@StartX - @EndX, 2)+ POWER(@StartY - @EndY, 2))
		
		IF @PackageType = 0 BEGIN
			SET @BasePrice = 115;
			SET @PricePerKg = 0;
		END 
		IF @PackageType = 1 BEGIN
			SET @BasePrice = 175;
			SET @PricePerKg = 100;
		END 
		IF @PackageType = 2 BEGIN
			SET @BasePrice = 250;
			SET @PricePerKg = 100;
		END 
		IF @PackageType = 3 BEGIN
			SET @BasePrice = 350;
			SET @PricePerKg = 500;
		END 
		
		UPDATE [dbo].[Package]
			SET Price = (@BasePrice + @Weight * @PricePerKg) * @EuclideanDistance
			WHERE IdP = @IdP

		FETCH NEXT FROM @cursor
		INTO @IdP, @IdStartAddress, @IdEndAddress, @PackageType, @Weight
	END

	CLOSE @cursor
	DEALLOCATE @cursor


END
