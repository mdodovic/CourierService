DROP TRIGGER IF EXISTS [dbo].TR_CurrentDrrive;

GO

CREATE TRIGGER TR_CurrentDrrive
	ON [dbo].[CurrentDrive]
	INSTEAD OF INSERT
AS 
BEGIN
	DECLARE @HasDrive int;
	DECLARE @IdU int;
	DECLARE @IdV int;
	DECLARE @IdA int;

	DECLARE @cursor CURSOR

	SET @cursor = CURSOR FOR 
		SELECT IdU, IdV, IdA
	FROM inserted

	OPEN @cursor

	FETCH NEXT FROM @cursor
	INTO @IdU, @IdV, @IdA

	WHILE @@FETCH_STATUS = 0
	BEGIN
	
		SELECT @HasDrive = count(*)
		FROM [dbo].[CurrentDrive]
		WHERE IdU = @IdU
			OR IdV = @IdV

		IF @HasDrive = 0 BEGIN

			INSERT INTO [dbo].[CurrentDrive] (IdU, IdV, IdA)
				VALUES (@IdU, @IdV, @IdA)
		
		END 


		FETCH NEXT FROM @cursor
		INTO @IdU, @IdV, @IdA
	END

	CLOSE @cursor
	DEALLOCATE @cursor



	
END
GO

INSERT INTO [dbo].[CurrentDrive] (IdU, IdV, IdA)
	VALUES (744, 371, 2098)

--delete from CurrentDrive
select *
	from CurrentDrive

IF ((
	) = 0) BEGIN

	select 2
END
