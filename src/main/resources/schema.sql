-- The following is necessary to run in conjunction with data.sql IF dropping the database here
-- USE volunteerkind;

drop table if exists jhi_user_authority;
drop table if exists event_attendance;
drop table if exists jhi_user;
drop table if exists event;
drop table if exists location;
drop table if exists jhi_authority;

create table location(
    id bigint auto_increment not null,
    location_name varchar(255),
    primary key (id)
);

create table jhi_user (
dtype varchar(255) not null,
id varchar(100) not null,
login varchar(50) not null,
first_name varchar(50),
last_name varchar(50),
email varchar(191),
image_url varchar(256),
activated boolean default false not null,
lang_key varchar(10),
created_by varchar(50),
created_date varchar(255),
last_modified_by varchar(50),
last_modified_date varchar(255),
availability varchar(255),
phone_number varchar(255),
home_location_id bigint,
primary key (id),
foreign key (home_location_id) references location(id),
unique (login),
unique (email)
);

create table jhi_authority (
    name varchar(50) not null,
    primary key (name)
);

create table jhi_user_authority (
    user_id varchar(100) not null,
    authority_name varchar(50) not null,
    foreign key (user_id) references jhi_user(id),
    foreign key (authority_name) references jhi_authority(name)
);

create table event (
    id bigint auto_increment not null,
    event_date date,
    event_name varchar(255),
    projection bigint,
    start_time varchar(255),
    venue varchar(255),
    location_id bigint,
    primary key (id),
    foreign key (location_id) references location(id)
);

create table event_attendance (
    id bigint auto_increment not null,
    sign_in varchar(255),
    sign_out varchar(255),
    user_code varchar(255),
    event_id bigint,
    volunteer_id varchar(255),
    primary key (id),
    foreign key (event_id) references event(id),
    foreign key (volunteer_id) references jhi_user(id)
);
