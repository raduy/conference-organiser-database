------------------------------
--	PROCEDURES
------------------------------

-- CREATE NEW CLIENT --
create procedure add_client
	@name nvarchar(45),
	@is_company bit,
	@phone varchar(25),
	@country varchar(25),
	@street varchar(25),
	@city varchar(25)

as
begin
	set nocount on;
	insert into client
	values(@name, @is_company, @phone, @country, @street, @city)
end
go


-- ADD STATE OF RESERVATION --
create procedure add_state_of_reservation
	@readable_description varchar(25)
as
begin
	set nocount on;
	insert into state_of_reservation
	values(@readable_description)
end
go

--test
exec add_state_of_reservation 'Zapłacona'
exec add_state_of_reservation 'Zapłacona cześciowo'
exec add_state_of_reservation 'Trwająca nadal'
exec add_state_of_reservation 'Anulowana'
exec add_state_of_reservation 'Wygasła'
go

--  ADD CONFERENCE --
create procedure add_conference
	@starting_date date,
	@count_of_days int,
	@short_description varchar(30)

as
begin

	if(@starting_date < dbo.wrapped_getdate())
	begin
		raiserror('Starting date of conference cannot be past!', 16, 1);
		return;
	end

	insert into conference
	values(@starting_date, @count_of_days, @short_description)
end
go


-- ADD CONFERENCE DAY --
create procedure add_conference_day
	@conference_id int,
	@seats int,
	@which_day tinyint,
	@short_description varchar(30)

as
begin
	if (select count_of_days from conference where conference_id = @conference_id) < @which_day
	begin
		raiserror('Conference does not have so much days', 16, 1);
		return
	end

	insert into conference_day
	values(@conference_id, @seats, @which_day, @short_description)
end
go

-- ADD WORKSHOP --
create procedure add_workshop
	@conference_day_id int,
	@seats int,
	@start_time datetime,
	@end_time datetime,
	@price_per_person smallmoney,
	@short_description varchar(30)

as
begin
	set nocount on;

	if (select conference_day_id from conference_day where conference_day_id = @conference_day_id) is null
	begin
		raiserror('Could not possible to add workshop. Given conference day does not exist', 16, 1);
		return;
	end

	if ((year(@start_time) != year(@end_time)) 
		OR (month(@start_time) != month(@end_time)) 
		OR (day(@start_time) != day(@end_time)))
	begin
		raiserror('Workshop can take place only during conference day', 16, 1);
		return;
	end
	if(@start_time >= @end_time)
	begin
		raiserror('Workshop cannot start later than it begins', 16, 1);
		return;
	end

	insert into workshop
	values(@conference_day_id, @seats, @start_time, @end_time, @price_per_person, @short_description)
end
go

-- ADD PRICE --
create procedure add_price
	@conference_day_id int,
	@price smallmoney,
	@days_before_conference_start int

as
begin
	if @days_before_conference_start <= 0 
	begin
		raiserror('Count of days before conference start must be positive', 16, 1);
		return; 
	end

	insert into price
	values(@conference_day_id, @price, @days_before_conference_start);
end
go

create procedure add_reservation --TODO DEBUGGING
	@conference_day_id int,
	@client_id int,
	@count_of_people int
as
begin
	set nocount on;
	IF dbo.get_date_of_given_conference_day(@conference_day_id) <= dbo.wrapped_getdate()
	begin
		raiserror('It is too late to make reservation', 16, 1);
		return;
	end
	declare @starting_status as int;
	set @starting_status = (
		select dbo.get_state_of_reservation_id('Trwająca nadal')
	)

	--create a payment
	declare @days_to_start_conference as int;
	set @days_to_start_conference = (
		select top 1 datediff(day, dbo.wrapped_getdate(), C.starting_date)
		from conference as C join conference_day as CD
		on C.conference_id = CD.conference_id
		where CD.conference_day_id = @conference_day_id
	)
	declare @date_diff as int;
	set @date_diff = (
		select min(P.days_before_conference_start - @days_to_start_conference)
		from price as P
		where P.days_before_conference_start - @days_to_start_conference >= 0
	)

	declare @current_price as smallmoney;
	set @current_price = (
		select top 1 P.price
		from price as P 
		where P.days_before_conference_start = @date_diff + @days_to_start_conference
	)

	declare @created_reservation_id int;
	begin try
		begin tran
		
		--create reservation
		insert into reservation
		values(@conference_day_id, @client_id, @starting_status, @count_of_people, dbo.wrapped_getdate())
		--reservation created

		set @created_reservation_id = @@IDENTITY

		insert into payment
		values(@created_reservation_id, @current_price, 0)
		--payment created
		
		--reserve seats
		while @count_of_people > 0
		begin
			insert into participant
			values(@created_reservation_id, null, null, null)
			set @count_of_people = @count_of_people - 1;
		end
		--seats reserved

		commit tran
	end try
	begin catch
		print error_message()
		rollback tran
	end catch
end
go



-- LOG PAYMENT FROM CLIENT --
create procedure log_income_from_client
	@reservation_id int,
	@income money
as
begin
	if (select R.reservation_id from reservation as R where R.reservation_id = @reservation_id) is null
	begin
		raiserror('Could not log payment - there is no such reservation', 16, 1);
		return;
	end

	if (select R.state_of_reservation_id from reservation as R where R.reservation_id = @reservation_id) 
	not in (dbo.get_state_of_reservation_id('Zapłacona'),
			dbo.get_state_of_reservation_id('Zapłacona cześciowo'),
			dbo.get_state_of_reservation_id('Trwająca nadal')
	)
	 
	begin
		raiserror('Could not log payment - reservatoin canceled or expired', 16, 1);
		return;
	end
	
	update payment
	set amount_paid = amount_paid + @income
	where reservation_id = @reservation_id

	declare @amount_to_pay as money;
	set @amount_to_pay = (
		select P.amount_to_pay
		from payment as P
		where P.reservation_id = @reservation_id
	)

	declare @amount_paid as money;
	set @amount_paid = (
		select P.amount_paid
		from payment as P
		where P.reservation_id = @reservation_id
	)

	declare @paid_status as int;
	set @paid_status = (
		select S.state_of_reservation_id
		from state_of_reservation as S
		where S.readable_description = 'Zapłacona'
	)

	if (@amount_paid >= @amount_to_pay)
	begin
		update reservation 
		set state_of_reservation_id = @paid_status
		where reservation_id = @reservation_id
	end
end
go


-- RETURN MONEY TO CLIENT --
create procedure return_money
	@reservation_id int,
	@amount_of_returned_money int
as
begin
	set nocount on;

	if (select R.reservation_id from reservation as R where R.reservation_id = @reservation_id) is null
	begin
		raiserror('Could not return money to client - there is no such reservation', 16, 1);
		return;
	end

	begin try
		set nocount on;
		update payment
		set amount_paid = amount_paid - @amount_of_returned_money
		where reservation_id = @reservation_id
	end try
	begin catch
		declare @error as varchar(128)
		set @error = (select ERROR_MESSAGE())
		raiserror('Could not possible to return money', 16, 1, @error);
	end catch
end
go

create procedure cancel_reservation
	@reservation_id int
as
begin
	set nocount on;
	if (select reservation_id from reservation where reservation_id = @reservation_id) is null
	begin
		raiserror('Could not be possible to cancel reservation, there is no such', 16, 1);
		return;
	end

	if ( (select starting_date from reservation where reservation_id = @reservation_id) <= dbo.wrapped_getdate() )
	begin
		raiserror('IT IS TOO LATE!', 16, 1);
		return;
	end
	
	declare @cancel_status_id as int;
	set @cancel_status_id = (
		select dbo.get_state_of_reservation_id('Anulowana')
	)
	update reservation
	set state_of_reservation_id = @cancel_status_id
	where reservation_id = @reservation_id
end
go


create procedure add_workshop_reservation
	@workshop_id int,
	@reservation_id int,
	@count_of_people int

as
begin
	declare @given_reservation_conference_day_id as int
	set @given_reservation_conference_day_id = (
		select CD.conference_day_id
		from conference_day as CD join reservation as R
		on CD.conference_day_id = R.conference_day_id
		where R.reservation_id = @reservation_id
	)
	IF @given_reservation_conference_day_id is null
	begin
		raiserror('Could not be possible to add workshop reservation', 16, 1);
		return;
	end

	declare @given_workshop_conference_day_id as int
	set @given_workshop_conference_day_id = (
		select CD.conference_day_id
		from conference_day as CD join workshop as W
		on CD.conference_day_id = W.conference_day_id
		where W.workshop_id = @workshop_id
	)

	if @given_reservation_conference_day_id != @given_workshop_conference_day_id
	begin
		raiserror('It is now allowed to participate in workshop without participating in conference day', 16, 1);
		return;
	end

	if @reservation_id not in ( select reservation.reservation_id from reservation)
	begin
		raiserror('WE DONT HAVE SUCH RESERVATION', 16, 1)
		return
	end

	declare @cost_of_workshop_reservation as int;
	set @cost_of_workshop_reservation = @count_of_people * ( 
		select price_per_person 
		from workshop 
		where workshop_id = @workshop_id 
	);
	
	begin try
		begin tran
		insert into workshop_reservation
		values(@workshop_id, @reservation_id, @count_of_people)
		
		update payment 
		set amount_to_pay = amount_to_pay + @cost_of_workshop_reservation
		where reservation_id = @reservation_id;
		commit tran
	end try
	begin catch
		declare @error as varchar(128);
		set @error = (select ERROR_MESSAGE())
		raiserror('Could not be possible to add workshop reservation', 16, 1, @error);
		rollback tran;
	end catch
end
go

create procedure add_workshop_participant
	@workshop_reservation_id int,
	@participant_id int
as
begin	
	IF EXISTS ( select * from workshop_participant
		where participant_id = @participant_id and @workshop_reservation_id = workshop_reservation_id)
	begin
		raiserror('It is not allowed to add to the same workshop participants', 16, 1);
		return;
	end
	IF NOT EXISTS ( select * from workshop_reservation
		where workshop_reservation_id = @workshop_reservation_id)
	begin
		raiserror('No such reservation', 16, 1);
		return;
	end
	declare @reservation_id_of_given_workshop_reservation as int
	set @reservation_id_of_given_workshop_reservation = (
		select reservation_id 
		from workshop_reservation 
		where workshop_reservation_id = @workshop_reservation_id
	)

	declare @reservation_id_of_given_participant as int
	set @reservation_id_of_given_participant = (
		select reservation_id 
		from participant 
		where participant_id = @participant_id
	)

	if @reservation_id_of_given_participant != @reservation_id_of_given_workshop_reservation
	begin
		raiserror('It is not allowed to create workshop participant from foreign reservation', 16, 1);
		return;
	end

	insert into workshop_participant
	values(@workshop_reservation_id, @participant_id)

end
go

create procedure mark_expired_reservations
	--no params 

as
begin
	declare @resevation_duration as int
	set @resevation_duration = (
		select dbo.get_reservation_duration()
	)

	declare @expired_status_id as int
	set @expired_status_id = (
		select dbo.get_state_of_reservation_id('Wygasła')
	)

	update reservation
	set state_of_reservation_id = @expired_status_id
	where datediff(day, starting_date, dbo.wrapped_getdate()) > @resevation_duration
end
go

create procedure apply_discount
	@reservation_id int,
	@percentage_discount int,
	@number_of_people_to_apply_discount int

as
begin
	if (select reservation_id from reservation where reservation_id = @reservation_id) is null
	begin
		raiserror('Could not be possible to apply discount. There is no such reservation', 14, 1);
		return;
	end

	declare @number_of_people_in_reservation as int
	set @number_of_people_in_reservation = (
		select count_of_people from reservation where reservation_id = @reservation_id
	)


	declare @old_price_per_person as money; 
	set @old_price_per_person = (
		select P.amount_to_pay / @number_of_people_in_reservation 
		from payment as P
		where P.reservation_id = @reservation_id
	)

	update payment
	set amount_to_pay = amount_to_pay - 
		@old_price_per_person * (@percentage_discount / 100.0) * @number_of_people_to_apply_discount 
	where reservation_id = @reservation_id
end
go

create procedure fill_reservation_with_personalities
	@reservation_id int,
	@first_name varchar(30), 
	@last_name varchar(30),
	@student_identificator int
as
begin
	
	if (select reservation_id from reservation where reservation_id = @reservation_id) is null
	begin
		raiserror('Could not fill reservation with personalities. No such reservation', 14, 1);
		return;
	end
	
	declare @first_free_slot as int
	set @first_free_slot = (
		select TOP 1 participant_id 
		from participant 
		where first_name is null and last_name is null and reservation_id = @reservation_id
	)

	if @first_free_slot is null
	begin
		raiserror('Could not fill reservation with personalities.', 14, 1);
		return;
	end

	update participant set first_name = @first_name, last_name = @last_name, 
	student_identyficator = @student_identificator
	where reservation_id = @reservation_id and participant_id  = @first_free_slot
end
go

-- CHANGE NUMBER OF SEATS AT CONFERENCE DAY --
create procedure change_number_of_seats_at_conference_day 
	@conference_day_id int,
	@new_number_of_seats int
as
begin
	set nocount on;
	begin try
		set nocount on;
		update conference_day
		set seats = @new_number_of_seats
		where conference_day_id = @conference_day_id
	end try
	begin catch
		declare @error as varchar(128)
		set @error = (select ERROR_MESSAGE())
		raiserror('Could not change number of seats at conference day', 16, 1, @error);
	end catch
end
go

-- CHANGE NUMBER OF SEATS AT WORKSHOP --
create procedure change_number_of_seats_at_workshop 
	@workshop int,
	@new_number_of_seats int
as
begin
	set nocount on;
	begin try
		set nocount on;
		update workshop
		set seats = @new_number_of_seats
		where workshop_id = @workshop
	end try
	begin catch
		declare @error as varchar(128)
		set @error = (select ERROR_MESSAGE())
		raiserror('Could not change number of seats at workshop', 16, 1, @error);
	end catch
end
go

create table system_date(
	false_system_date date not null,
	true_date bit not null default 0
)
go

create function wrapped_getdate()
	returns date
as
begin
	--handle illegal state of table system_date
	if (select count(*) from system_date) != 1
	begin
		return getdate();
	end

	declare @date as date;
	if (select true_date from system_date) = 1
	begin 
		set @date = getdate()
	end
	else
	begin
		set @date = (select false_system_date from system_date)
	end

	return @date
end
go

create procedure set_false_system_date 
	@bool as bit,
	@false_date as date
as
begin
	begin try	
		begin transaction

		truncate table system_date

		insert into system_date
		values(@false_date, @bool)

		commit transaction
	end try
	begin catch
		rollback transaction
		raiserror('Could not possible to set false system time', 16, 1);
		return;
	end catch
end
go

create procedure turn_off_false_system_time
	--no params

as
begin
	truncate table system_time
end
go