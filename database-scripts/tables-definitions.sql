create database Conferences
go
use Conferences

create table conference(
	conference_id int identity(1, 1) primary key not null,
	starting_date date not null,
	count_of_days int not null check(count_of_days >= 1) default 1,
	short_description varchar(30)
)

create table conference_day(
	conference_day_id int identity(1, 1) primary key not null,
	conference_id int foreign key references conference(conference_id) not null,
	seats int not null check(seats >= 1),
	which_day tinyint not null check(which_day >= 1) default 1,
	short_description varchar(30)

	constraint no_doubled_days_in_one_conference unique clustered (
	conference_id,
	which_day
	)
)

create table workshop(
	workshop_id int identity(1, 1) primary key not null,
	conference_day_id int foreign key references conference_day(conference_day_id) not null,
	seats int not null check(seats >= 1),
	start_time datetime not null,
	end_time datetime not null,
	price_per_person smallmoney not null check(price_per_person >= 0),
	short_description varchar(30)  
)

create table client(
	client_id int identity(1, 1) primary key not null,
	name nvarchar(45) not null,
	is_company bit not null default 0,
	phone varchar(25),
	country varchar(25) not null,
	street varchar(25) not null,
	city varchar(25) not null
)

create table state_of_reservation(
	state_of_reservation_id int identity(1,1) primary key not null,
	readable_description varchar(25) unique not null
)

create table reservation(
	reservation_id int identity(1, 1) primary key not null,
	conference_day_id int foreign key references conference_day(conference_day_id) not null,
	client_id int foreign key references client(client_id) not null,
	state_of_reservation_id int foreign key references state_of_reservation(state_of_reservation_id) not null,
	count_of_people int not null check(count_of_people >= 1),
	starting_date date not null
)

create table participant(
	participant_id int identity(1, 1) primary key not null,
	reservation_id int foreign key references reservation(reservation_id) not null,
	student_identyficator int null,
	first_name nvarchar(25) null,
	last_name nvarchar(25) null
)

create table workshop_reservation(
	workshop_reservation_id int identity(1, 1) primary key not null,
	workshop_id int foreign key references workshop(workshop_id) not null,
	reservation_id int foreign key references reservation(reservation_id) not null,
	count_of_people int check(count_of_people >= 1) default 1
)

create table workshop_participant(
	workshop_reservation_id int foreign key references workshop_reservation(workshop_reservation_id) not null,
	participant_id int foreign key references participant(participant_id) not null,
	primary key(workshop_reservation_id, participant_id)
)

create table price(
	price_id int identity(1, 1) primary key not null,
	conference_day_id int foreign key references conference_day(conference_day_id) not null,
	price smallmoney not null check(price > 0),
	days_before_conference_start int not null check(days_before_conference_start >= 1)
)

create table payment(
	reservation_id int foreign key references reservation(reservation_id) not null,
	amount_to_pay money not null check(amount_to_pay >= 0),
	amount_paid money not null check(amount_paid >= 0) default 0,
	primary key(reservation_id)
)
go