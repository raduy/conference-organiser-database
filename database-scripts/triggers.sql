---- TRIGGERS ----------------------

-- client should not be possible to sign up to conference is there is lack of free seats
create trigger allow_sign_up_for_conference_day_when_free_seats on reservation
for insert
as
begin

	declare @conference_day_id as int;
	set @conference_day_id = (select distinct reservation.conference_day_id from reservation join 
		inserted on reservation.conference_day_id = inserted.conference_day_id); 
	declare @count_of_people as int;
	set @count_of_people = (select distinct  reservation.count_of_people from reservation join 
		inserted on reservation.reservation_id = inserted.reservation_id);
	declare @free_seats as int;
	set @free_seats = ( 
			select  dbo.get_free_seats_for_conference_day(@conference_day_id)
	);
	IF @free_seats < 0
	begin
		RAISERROR ('SORRY, BUT WE DONT HAVE SUCH NUMBER OF FREE SEATS!', 16, 1);
		ROLLBACK TRANSACTION
	end 
end
go
--insert into reservation values(5,1,2,50,'2013-12-12')

-- try add reservation
--exec add_reservation 1,1,1

--select dbo.get_free_seats_for_conference_day(1)


-- should not be possible to have two conference_day in the same day
--drop trigger block_multiple_conference_day_in_the_same_day 
create trigger block_multiple_conference_day_in_the_same_day on conference_day
for insert
as
begin
	declare @conference_id as int;
	set @conference_id = (select distinct conference_day.conference_id
	from conference_day join inserted on conference_day.conference_day_id = inserted.conference_day_id);
	PRINT @conference_id
	declare @which_day as int;
	set @which_day = (select distinct conference_day.which_day
	from conference_day join inserted on conference_day.conference_day_id = inserted.conference_day_id)
	PRINT @which_day
	IF ( select  COUNT(conference_day_id) from conference_day where conference_id = @conference_id and which_day = @which_day) > 1
	begin
		RAISERROR ('SORRY, BUT ONLY ONE CONFERENCE DAY IS ALLOWED!',16,1);
		ROLLBACK TRANSACTION
	end
	
end
go
--delete conference_day where conference_day_id = 5
-- test
--exec add_conference_day 1, 1, 1, 'Ruby' 


create trigger should_block_inserting_many_price_on_the_same_day on price
for insert
as
begin

	declare @conference_day_id as int;
	set @conference_day_id = ( select distinct price.conference_day_id
		from price join inserted on price.price_id = inserted.price_id);
	declare @days_before_conference_start as int;
	set @days_before_conference_start = (select distinct price.days_before_conference_start
	from price join inserted on price.price_id = inserted.price_id);

	IF ( select COUNT(price_id) from price where conference_day_id = @conference_day_id 
		and days_before_conference_start = @days_before_conference_start) > 1
	begin
		RAISERROR ('SORRY, BUT WE HAVE ASSIGNED PRICE FOR THIS DAY!',16,1);
		ROLLBACK TRANSACTION

	end

end
go
-- exec add_price 1,400,100

--drop trigger workshop_should_start_the_same_day_as_conference_day
create trigger workshop_should_start_the_same_day_as_conference_day on workshop
for insert
as 
begin
	declare @conference_day_id as int;
	set @conference_day_id = ( select distinct workshop.conference_day_id
		from workshop join inserted on workshop.workshop_id = inserted.workshop_id);
	PRINT @conference_day_id
	declare @start_time as datetime;
	set @start_time = ( select distinct workshop.start_time
		from workshop join inserted on workshop.workshop_id = inserted.workshop_id);
	PRINT @start_time
	declare @date_of_conference_day as datetime;
	set @date_of_conference_day = (select dbo.get_date_of_given_conference_day(@conference_day_id));
	IF (DAY(@start_time) != DAY(@date_of_conference_day) )
		OR (MONTH(@start_time) != MONTH(@date_of_conference_day) )
		OR (YEAR(@start_time) != YEAR(@date_of_conference_day) )
	begin
		RAISERROR ('SORRY, BUT WORKSHOP AND CONFERENCE DAY SHOULD HAVE THE SAME DATE!',16,1);
		ROLLBACK TRANSACTION
	end
	
end
go
--exec add_workshop 2, 40, '2014-06-13 19:12:12:000', 300.0, 'Hibernate workshop'


create trigger should_not_be_possible_to_add_payment_if_reservation_not_exist on payment
for insert
as
begin
	declare @reservation_id as int;
	set @reservation_id = ( select distinct payment.reservation_id from payment
		join inserted on payment.reservation_id = inserted.reservation_id);
	IF (@reservation_id NOT IN ( select reservation.reservation_id from reservation) )
	begin
		RAISERROR ('SORRY, BUT WE DONT HAVE THIS RESERVATION!',16,1);
		ROLLBACK TRANSACTION
	end

end
go

go

--create trigger should_not_be_possible_to_register_after_start_of_conference on reservation

create trigger client_cant_have_multiple_workshop_on_the_same_time on workshop_participant
for insert
as
begin
	declare @workshop_reservation_id as int;
	declare @participant_id as int;
	select @workshop_reservation_id = workshop_reservation_id, @participant_id = participant_id
	from inserted
	
	declare @start_time as datetime;
	declare @end_time as datetime;
	select @start_time = start_time, @end_time = end_time
	from workshop_reservation join workshop on workshop_reservation.workshop_id = workshop.workshop_id
	where workshop_reservation.workshop_reservation_id = @workshop_reservation_id

	IF  (
	select COUNT(*)
	from workshop_reservation join workshop on workshop_reservation.workshop_id = workshop.workshop_id
	join workshop_participant on 
	workshop_reservation.workshop_reservation_id = workshop_participant.workshop_reservation_id
	where workshop_participant.participant_id = @participant_id
	and ( (@start_time < start_time and @end_time >= start_time)  OR
			(@start_time >= start_time and @end_time <=  end_time) ) ) > 1
	begin

		RAISERROR ('SORRY, BUT YOU CANT HAVE MULTIPLE WOKRSHOP ON THE SAME TIME!',16,1);
		ROLLBACK TRANSACTION

	end
end
go

create trigger client_cant_register_on_workshop_if_his_reservation_is_cancelled_or_expired on workshop_reservation
for insert
as
begin
	declare @reservation_id as int;
	set @reservation_id = (select workshop_reservation.reservation_id from workshop_reservation
		join inserted on workshop_reservation.workshop_reservation_id = inserted.workshop_reservation_id)
	PRINT @reservation_id
	IF dbo.get_state_of_reservation_by_reservation_id(@reservation_id) >= 4
	begin
		PRINT 'SORRY, BUT YOUR RESERVATION IS EXPIRED!';
		RAISERROR ('SORRY, BUT YOUR RESERVATION IS EXPIRED!',16,1);
		ROLLBACK TRANSACTION
	end
end
go