------------------------------
--	FUNCTIONS
------------------------------

create function get_conference_day_id_of_day(@conference_id int, @which_day int)
	returns int
as begin
	declare @conference_day_id as int;
	set @conference_day_id = ( select conference_day.conference_day_id  
		from conference join conference_day on conference.conference_id = conference_day.conference_id
		where conference.conference_id = @conference_id and which_day = @which_day);

	return @conference_day_id;
end
go

create function get_date_of_given_conference_day(@conference_day_id int)
	returns datetime
as begin
	declare @conference_id as int;
	set @conference_id = ( select conference_id
		from conference_day where conference_day_id = @conference_day_id);
	
	declare @which_day as int;
	set @which_day = ( select which_day
		from conference_day 
		where conference_day_id = @conference_day_id);
	
	declare @conference_start as datetime;
	set @conference_start = ( select starting_date
		from conference 
		where conference.conference_id = @conference_id)

	return dateadd(day, @which_day - 1, @conference_start); 
end
go

create function get_state_of_reservation_id(@readable_description varchar(25))
	returns int
as
begin
	declare @status as int;
	set @status = (
		select S.state_of_reservation_id
		from state_of_reservation as S
		where S.readable_description = @readable_description
	)
	return @status;
end
go

create function get_reservation_duration()
	returns int
as
begin
	declare @reservation_duration as int;
	set @reservation_duration = 7;
	return (@reservation_duration);
end
go

create function get_free_seats_for_conference_day(@conference_day_id int)
	returns int
as
begin
	declare @total_available_seats as int;
	set @total_available_seats = ( select seats from conference_day where conference_day_id = @conference_day_id);
	declare @taken_seats as int;
	set @taken_seats = (
		select SUM(count_of_people)
		from reservation
		where state_of_reservation_id <= 3 and conference_day_id = @conference_day_id
	)
	IF @taken_seats is null
		set @taken_seats = 0 

	return @total_available_seats - @taken_seats;
end
go

create function get_free_seats_for_workshop(@workshop_id int)
	returns int
as
begin
	declare @total_available_seats as int;
	set @total_available_seats = ( 
		select seats 
		from workshop 
		where workshop_id = @workshop_id
	)
	declare @taken_seats as int;
	set @taken_seats = (
		select SUM(workshop_reservation.count_of_people)
		from workshop_reservation 
		join reservation on workshop_reservation.reservation_id = reservation.reservation_id 
		where reservation.state_of_reservation_id <= 3 and workshop_reservation.workshop_id = @workshop_id
	)

	IF @taken_seats is NULL
	begin
		set @taken_seats = 0;
	end
	return @total_available_seats - @taken_seats
end
go


-- CALCULATE RESERVATION COST --
create function calculate_cost_of_reservation(@reservation_id int)
	returns int
as
begin
	declare @cost as int;
	set @cost = (
	select amount_to_pay
	from payment 
	where reservation_id = @reservation_id);
	return @cost
end
go

create function calculate_paid_amount(@reservation_id int)
	returns int
as
begin
	declare @cost as int;
	set @cost = (
	select amount_paid
	from payment 
	where reservation_id = @reservation_id);
	return @cost
end
go


create function calculate_current_amount_to_pay(@reservation_id int)
	returns int
as
begin	
	return  dbo.calculate_cost_of_reservation(@reservation_id) 
	- dbo.calculate_paid_amount(@reservation_id)

end
go

create function calculate_total_amount_spent_by_client(@client_id int)
	returns int
as
begin
	declare @total_sum_per_client as int;
	set @total_sum_per_client = (
	select SUM(dbo.calculate_paid_amount(reservation_id))
	from reservation where client_id = @client_id);
	return @total_sum_per_client
end
go

create function generate_identificator(@participant_id int)
	returns varchar(30)
as
begin
	declare @identificator as varchar(30);
	set @identificator = (
		select first_name + ' ' + last_name
		from participant
		where participant_id = @participant_id
	)
	declare @company_name as varchar(30);
	set @company_name = (
	select client.name
	from participant join reservation 
	on participant.reservation_id = reservation.reservation_id
	join client 
	on reservation.client_id = client.client_id
	where participant.participant_id = @participant_id and client.is_company = 1)
	
	if @company_name is not null
	begin
		set @identificator = @identificator + ', company : ' + @company_name;
	end
	return @identificator;
end
go

create function get_state_of_reservation_by_reservation_id(@reservation_id int)
	returns int
as
begin
	return ( select state_of_reservation.state_of_reservation_id from state_of_reservation 
		join reservation on reservation.state_of_reservation_id = state_of_reservation.state_of_reservation_id
		where reservation_id = @reservation_id)
end
go


------------------------------------------------
-- PARAMATRIZED VIEWS (FUNCTION RETURNING VIEW)
------------------------------------------------

-- SHOW PARTICIPANTS LIST (RETURNS PEOPLE ONLY FROM FULLY PAID RESERVATIONS)
create function show_conference_day_member(@conference_day int)
	returns table
as
	return (
		select P.*
		from participant as P
		join reservation as R
		on R.reservation_id = P.reservation_id 
		and R.conference_day_id = @conference_day 
		and R.state_of_reservation_id = dbo.get_state_of_reservation_id('Zapłacona')
	)
go

--test
--select * from dbo.show_conference_day_member(1)
go

-- SHOW WORKSHOP PARTICIPANTS LIST (RETURNS PEOPLE ONLY FROM FULLY PAID RESERVATIONS)
create function show_workshop_member(@workshop_id int)
	returns table
as
	return (
		select P.*
		from workshop_reservation as WR
		join reservation as R
		on R.reservation_id = WR.reservation_id 
			and WR.workshop_id = @workshop_id
			and R.state_of_reservation_id = dbo.get_state_of_reservation_id('Zapłacona')
		join workshop_participant as WP
		on WP.workshop_reservation_id = WR.workshop_reservation_id
		join participant as P
		on P.participant_id = WP.participant_id
	)
go
