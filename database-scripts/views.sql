-------------------------------------
-- VIEWS 
-------------------------------------

-- SHOW ALL CLIENTS BEING COMPANY
create view all_company_clients as 
	select C.name, C.city, C.street, C.country, C.phone, C.client_id 
	from client as C
	where C.is_company = 1
go
--test
--select * from all_company_clients
go

-- SHOW ALL CLIENTS BEING SINGLE PERSON
create view all_single_person_clients as
	select C.name, C.city, C.street, C.country, C.phone, C.client_id 
	from client as C
	where C.is_company = 0
go
--test
--select * from all_single_person_clients
go

-- SHOW ALL CLIENTS WITH ANNOTAION ABOUT CLIENT TYPE
--drop view show_all_clients
create view all_clients as
	select *, 'company' as [client type]
	from all_company_clients
	union
	select *, 'single person' as [client type]
	from all_single_person_clients
go
--test
--select * from all_clients
go

-- SHOW CLIENTS WITH THE BIGGEST NUMBER OF PAID RESERVATION
create view the_most_active_clients as
	select top 10 C.name, C.city, C.street, C.country, C.phone, C.client_id, sum(R.count_of_people)
		+ ( --add workshops reservation
		select sum(WR.count_of_people)
		from workshop_reservation as WR
		join reservation as R2
		on R2.reservation_id = WR.reservation_id and R2.state_of_reservation_id in (
			dbo.get_state_of_reservation_id('Zapłacona'))
		where C.client_id = R2.client_id
		) as [number of paid reservations (conference days + workshops)]
	from reservation as R join client as C
	on R.client_id = C.client_id
	where R.state_of_reservation_id in ( 
		dbo.get_state_of_reservation_id('Zapłacona'))
	group by C.name, C.client_id, C.city, C.street, C.country, C.phone
	order by 2 desc
go

--test
--select * from the_most_active_clients
go

-- SHOW CLIENTS WITH THE BIGGEST AMOUNT SPENT
--drop view show_the_most_valuable_clients 
create view the_most_valuable_clients as
	select top 10 sum(P.amount_paid) as [total amout paid], C.name, C.client_id, C.city, C.street, C.country, C.phone
	from reservation as R 
	join payment as P
	on R.reservation_id = P.reservation_id
	join client as C
	on C.client_id = R.client_id
	group by C.name, C.client_id, C.city, C.street, C.country, C.phone
	order by 1 desc
go
--test
--select * from the_most_valuable_clients
go
-- SHOW STATISTICS OF FREE SEATS FOR CONFERENCE DAYS
create view free_seats_stats as 
	select C.short_description as [conference], CD.which_day, CD.short_description, CD.seats as [free seats], 
	CD.seats - dbo.get_free_seats_for_conference_day(CD.conference_day_id) as [taken seats],
	((CD.seats - dbo.get_free_seats_for_conference_day(CD.conference_day_id)) * 100) / CD.seats as [percent of fill]
	from conference_day as CD
	join conference as C
	on C.conference_id = CD.conference_id
go
--test
--select * from free_seats_stats 
go

-- SHOW STATISTICS OF FREE SEATS FOR WORKSHOPS
create view free_workshops_seats_stats as
	select W.workshop_id, W.short_description, C.short_description as [conference], CD.which_day, CD.short_description as [conference day], 
	W.seats as [free seats],
	W.seats -  dbo.get_free_seats_for_workshop(W.workshop_id) as [taken seats],
	((W.seats - dbo.get_free_seats_for_workshop(W.workshop_id)) * 100) / W.seats as [percentage fill] 
	from workshop as W
	join conference_day as CD
	on CD.conference_day_id = W.conference_day_id
	join conference as C
	on C.conference_id = CD.conference_id
go
--test
--select * from free_workshops_seats_stats
go

-- SHOW CLIENTS WHICH NOT SENT PARTICIPANT PERSONALITIES 2 WEEKS BEFORE CONFERENCE
create view clients_without_full_personalities_sent as
	select distinct C.name, C.phone --phone to call client
	from participant as P 
	join reservation as R
	on R.reservation_id = P.reservation_id
	join client as C
	on C.client_id = R.client_id
	join conference_day as CONF_DAY
	on CONF_DAY.conference_id = R.conference_day_id
	join conference as CONF
	on CONF.conference_id = CONF_DAY.conference_id
	where P.first_name is null and P.last_name is null 
	and datediff(week, dbo.wrapped_getdate(), dbo.get_date_of_given_conference_day(CONF_DAY.conference_day_id)) < 2; 
go

--test
--select * from clients_without_full_personalities_sent
go

-- SHOW COFERENCE DAYS WHICH NOT STARTED YET AND THERE ARE FREE SEATS
create view available_conference_days as
	select CD.*
	from conference_day as CD
	join conference as C
	on C.conference_id = CD.conference_id 
		and datediff(day, dbo.wrapped_getdate(), C.starting_date) > 0
		and dbo.get_free_seats_for_conference_day(CD.conference_day_id) > 0
go

--test
--select * from available_conference_days
go
-- SHOW WORKSHOPS WHICH NOT STARTED YET AND THERE ARE FREE SEATS
create view available_workshops as
	select W.*
	from available_conference_days as CD 
	join workshop as W
	on W.conference_day_id = CD.conference_day_id 
		and dbo.get_free_seats_for_workshop(W.workshop_id) > 0
go

--test
--select * from available_workshops
go

-- SHOW CLIENTS WITH PARTIAL PAYMENT AND REMAINING TIME TO PAY THE REST OF AMOUNT
create view clients_with_partial_payments
as
	select C.name, C.phone, P.amount_paid, P.amount_to_pay, R.reservation_id, 
	dbo.get_reservation_duration() - datediff(day, R.starting_date, dbo.wrapped_getdate()) as [remaining time to pay]
	from reservation as R
	join payment as P
	on R.reservation_id = P.reservation_id
		and R.state_of_reservation_id = 
			dbo.get_state_of_reservation_id('Zapłacona cześciowo')
	join client as C
	on C.client_id = R.client_id
go

--test
/*select dbo.get_state_of_reservation_id('Zapłacona cześciowo')
update reservation
set state_of_reservation_id = dbo.get_state_ofo_reservation_id('Zapłacona cześciowo')
where reservation_id = 4
select * from clients_with_partial_payments*/