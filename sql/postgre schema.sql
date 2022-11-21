-- DROP SCHEMA resort;
CREATE SCHEMA resort AUTHORIZATION pg_database_owner;
-- Permissions
GRANT ALL ON SCHEMA resort TO pg_database_owner;

-- resort.beds definition

-- Drop table

-- DROP TABLE resort.beds;

CREATE TABLE resort.beds (
	id SERIAL,
	beds varchar(100) UNIQUE NOT NULL,
	beds_adult smallint NOT NULL,
	beds_child smallint,
	CONSTRAINT beds_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.beds OWNER TO postgres;
GRANT ALL ON TABLE resort.beds TO postgres;


-- resort.features definition

-- Drop table

-- DROP TABLE resort.features;

CREATE TABLE resort.features (
	id SERIAL,
	feature varchar(45) NOT NULL,
	description varchar(1000) NOT NULL,
	lastchanged timestamp with time zone NOT NULL DEFAULT NOW(),
	deleted boolean NOT NULL DEFAULT false,
	CONSTRAINT features_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.features OWNER TO postgres;
GRANT ALL ON TABLE resort.features TO postgres;


-- resort.floor definition

-- Drop table

-- DROP TABLE resort.floor;

CREATE TABLE resort.floor (
	id SERIAL,
	floor varchar(45) NOT NULL,
	CONSTRAINT floor_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.floor OWNER TO postgres;
GRANT ALL ON TABLE resort.floor TO postgres;


-- resort.gender definition

-- Drop table

-- DROP TABLE resort.gender;

CREATE TABLE resort.gender (
	id SERIAL,
	gender varchar(45) NOT NULL,
	CONSTRAINT gender_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.gender OWNER TO postgres;
GRANT ALL ON TABLE resort.gender TO postgres;


-- resort.paymentstatus definition

-- Drop table

-- DROP TABLE resort.paymentstatus;

CREATE TABLE resort.paymentstatus (
	id SERIAL,
	paymentstatus varchar(100) UNIQUE NOT NULL,
	enabled boolean NOT NULL DEFAULT true,
	CONSTRAINT paymentstatus_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.paymentstatus OWNER TO postgres;
GRANT ALL ON TABLE resort.paymentstatus TO postgres;


-- resort.paymenttype definition

-- Drop table

-- DROP TABLE resort.paymenttype;

CREATE TABLE resort.paymenttype (
	id SERIAL,
	paymenttype varchar(100) UNIQUE NOT NULL,
	enabled boolean NOT NULL DEFAULT true,
	CONSTRAINT paymenttype_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.paymenttype OWNER TO postgres;
GRANT ALL ON TABLE resort.paymenttype TO postgres;


-- resort.reservestatus definition

-- Drop table

-- DROP TABLE resort.reservestatus;

CREATE TABLE resort.reservestatus (
	id SERIAL,
	status varchar(100) UNIQUE NOT NULL,
	CONSTRAINT reservestatus_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.paymenttype OWNER TO postgres;
GRANT ALL ON TABLE resort.paymenttype TO postgres;


-- resort.photo definition

-- Drop table

-- DROP TABLE resort.photo;

CREATE TABLE resort.photo (
	id BIGSERIAL,
	hash varchar(255) NOT NULL,
	filename varchar(255) NOT NULL,
	filetype varchar(255) NOT NULL,
	created timestamp with time zone NOT NULL DEFAULT NOW(),
	CONSTRAINT photo_pkey PRIMARY KEY (id),
	CONSTRAINT photo_un UNIQUE (hash)
);

-- Permissions

ALTER TABLE resort.photo OWNER TO postgres;
GRANT ALL ON TABLE resort.photo TO postgres;


-- resort.resortclass definition

-- Drop table

-- DROP TABLE resort.resortclass;

CREATE TABLE resort.resortclass (
	id SERIAL,
	resortclass varchar(45) UNIQUE NOT NULL,
	description varchar(100) NOT NULL,
	CONSTRAINT resortclass_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.resortclass OWNER TO postgres;
GRANT ALL ON TABLE resort.resortclass TO postgres;


-- resort.resorttype definition

-- Drop table

-- DROP TABLE resort.resorttype;

CREATE TABLE resort.resorttype (
	id SERIAL,
	"name" varchar(100) NOT NULL,
	description varchar(1000) NOT NULL,
	CONSTRAINT resorttype_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.resorttype OWNER TO postgres;
GRANT ALL ON TABLE resort.resorttype TO postgres;


-- resort."role" definition

-- Drop table

-- DROP TABLE resort."role";

CREATE TABLE resort."role_resortASU" (
	id SERIAL,
	"name" varchar(45) NOT NULL,
	enabled boolean NOT NULL DEFAULT true,
	CONSTRAINT role_name_key UNIQUE (name),
	CONSTRAINT role_pk PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort."role_resortASU" OWNER TO postgres;
GRANT ALL ON TABLE resort."role_resortASU" TO postgres;


-- resort.services definition

-- Drop table

-- DROP TABLE resort.services;

CREATE TABLE resort.services (
	id SERIAL,
	service varchar(45) UNIQUE NOT NULL,
	description varchar(1000) NOT NULL,
	currentprice numeric(10, 4) NOT NULL,
	minimumprice numeric(10, 4) NOT NULL,
	onetimepayment boolean NOT NULL,
	lastchanged timestamp with time zone NOT NULL DEFAULT NOW(),
	deleted boolean NOT NULL DEFAULT true,
	CONSTRAINT services_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.services OWNER TO postgres;
GRANT ALL ON TABLE resort.services TO postgres;


-- resort.suitclass definition

-- Drop table

-- DROP TABLE resort.suitclass;

CREATE TABLE resort.suitclass (
	id SERIAL,
	suitclass varchar(100) UNIQUE NOT NULL,
	description varchar(1000) NOT NULL,
	lastchanged timestamp with time zone NOT NULL DEFAULT NOW(),
	CONSTRAINT suitclass_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.suitclass OWNER TO postgres;
GRANT ALL ON TABLE resort.suitclass TO postgres;


-- resort.suitstatus definition

-- Drop table

-- DROP TABLE resort.suitstatus;

CREATE TABLE resort.suitstatus (
	id SERIAL,
	status varchar(100) UNIQUE NOT NULL,
	CONSTRAINT suitstatus_pkey PRIMARY KEY (id)
);

-- Permissions

ALTER TABLE resort.suitstatus OWNER TO postgres;
GRANT ALL ON TABLE resort.suitstatus TO postgres;


-- resort."user" definition

-- Drop table

-- DROP TABLE resort."user";

CREATE TABLE resort."user_resortASU" (
	id BIGSERIAL,
	username varchar(100) NOT NULL,
	"password" varchar(100) NOT NULL,
	enabled boolean NOT NULL DEFAULT true,
	CONSTRAINT user_pk PRIMARY KEY (id),
	CONSTRAINT user_username_key UNIQUE (username)
);

-- Permissions

ALTER TABLE resort."user_resortASU" OWNER TO postgres;
GRANT ALL ON TABLE resort."user_resortASU" TO postgres;


-- resort.customer definition

-- Drop table

-- DROP TABLE resort.customer;
-- информация о клиентах (ФИО, адрес, e-mail, телефон, др.);
CREATE TABLE resort.customer (
	id BIGSERIAL,
	firstname varchar(100) NOT NULL,
	surname varchar(100) NOT NULL,
	middlename varchar(100),
	phone varchar(20),
	email varchar(100),
	birthdate date NOT NULL,
	gender_id integer NOT NULL,
	address varchar(200),
	lastchanged timestamp with time zone NOT NULL,
	CONSTRAINT customer_pkey PRIMARY KEY (id),
	CONSTRAINT fk_customer_gender1 FOREIGN KEY (gender_id) REFERENCES resort.gender(id)
);

-- Permissions

ALTER TABLE resort.customer OWNER TO postgres;
GRANT ALL ON TABLE resort.customer TO postgres;



-- resort.reserve definition

-- Drop table

-- DROP TABLE resort.reserve;

CREATE TABLE resort.reserve (
	id BIGSERIAL,
	checkin date NOT NULL,
	checkout date NOT NULL,
	paymentstatus integer NOT NULL,
	paymenttype integer NOT NULL,
	status integer NOT NULL,
	created timestamp with time zone NOT NULL DEFAULT NOW(),
	lastchanged timestamp with time zone NOT NULL,
	CONSTRAINT reserve_pkey PRIMARY KEY (id),
	CONSTRAINT fk_reserve_paymentstatus1 FOREIGN KEY (paymentstatus) REFERENCES resort.paymentstatus(id),
	CONSTRAINT fk_reserve_paymenttype1 FOREIGN KEY (paymenttype) REFERENCES resort.paymenttype(id),	
	CONSTRAINT fk_reserve_reservestatus FOREIGN KEY (status) REFERENCES resort.reservestatus(id),
	CONSTRAINT finish_after_start CHECK(checkout >= checkin))
);

-- Permissions

ALTER TABLE resort.reserve OWNER TO postgres;
GRANT ALL ON TABLE resort.reserve TO postgres;


-- resort.resort definition

-- Drop table

-- DROP TABLE resort.resort;

CREATE TABLE resort.resort (
	id SERIAL,
	name varchar(100) NOT NULL,
	description varchar(1000) NOT NULL,
	resorttype integer NOT NULL,
	resortclass integer NOT NULL,
	lastchanged timestamp with time zone NOT NULL DEFAULT NOW(),
	photo bigint,
	deleted boolean NOT NULL DEFAULT false,
	CONSTRAINT resort_pkey PRIMARY KEY (id),
	CONSTRAINT fk_resort_photo1 FOREIGN KEY (photo) REFERENCES resort.photo(id),
	CONSTRAINT fk_resort_resortclass1 FOREIGN KEY (resortclass) REFERENCES resort.resortclass(id),
	CONSTRAINT fk_resort_resorttype1 FOREIGN KEY (resorttype) REFERENCES resort.resorttype(id)
);

-- Permissions

ALTER TABLE resort.resort OWNER TO postgres;
GRANT ALL ON TABLE resort.resort TO postgres;


-- resort.suittype definition

-- Drop table

-- DROP TABLE resort.suittype;

CREATE TABLE resort.suittype (
	id SERIAL,
	suitclass integer,
	lastchanged timestamp with time zone NOT NULL DEFAULT NOW(),
	beds integer NOT NULL,
	area float8 NOT NULL,
	currentprice numeric(10, 4) NOT NULL,
	minimumprice numeric(10, 4) NOT NULL,
	mainphoto bigint NULL,
	CONSTRAINT suittype_pkey PRIMARY KEY (id),
	CONSTRAINT fk_suit_suitclass1 FOREIGN KEY (suitclass_id) REFERENCES resort.suitclass(id),
	CONSTRAINT fk_suittype_beds1 FOREIGN KEY (beds) REFERENCES resort.beds(id),
	CONSTRAINT suittype_fk FOREIGN KEY (mainphoto) REFERENCES resort.photo(id)
);

-- Permissions

ALTER TABLE resort.suittype OWNER TO postgres;
GRANT ALL ON TABLE resort.suittype TO postgres;


-- resort.userrole definition

-- Drop table

-- DROP TABLE resort.userrole;

CREATE TABLE resort.userrole (
	"user_resortASU" bigint NOT NULL,
	"role_resortASU" integer NOT NULL,
	CONSTRAINT userrole_pkeys PRIMARY KEY ("user_resortASU", "role_resortASU"),
	CONSTRAINT userrole_fk_role FOREIGN KEY ("role_resortASU") REFERENCES resort."role_resortASU"(id),
	CONSTRAINT userrole_fk_user FOREIGN KEY ("user_resortASU") REFERENCES resort."user_resortASU"(id)
);

-- Permissions

ALTER TABLE resort.userrole OWNER TO postgres;
GRANT ALL ON TABLE resort.userrole TO postgres;


-- resort.building definition

-- Drop table

-- DROP TABLE resort.building;

CREATE TABLE resort.building (
	id SERIAL,
	resort integer NOT NULL,
	name varchar(100) NOT NULL,
	description varchar(1000) NOT NULL,
	lastchanged timestamp with time zone NOT NULL DEFAULT NOW(),
	deleted boolean DEFAULT false,
	CONSTRAINT building_pkey PRIMARY KEY (id),
	CONSTRAINT fk_building_resort1 FOREIGN KEY (resort) REFERENCES resort.resort(id)
);

-- Permissions

ALTER TABLE resort.building OWNER TO postgres;
GRANT ALL ON TABLE resort.building TO postgres;


-- resort.customerreserve definition

-- Drop table

-- DROP TABLE resort.customerreserve;

CREATE TABLE resort.customerreserve (
	customer_id bigint NOT NULL,
	reserve_id bigint NOT NULL,
	CONSTRAINT customerreserve_pkeys PRIMARY KEY (customer_id, reserve_id),
	CONSTRAINT fk_customerreserve_customer1 FOREIGN KEY (customer_id) REFERENCES resort.customer(id),
	CONSTRAINT fk_customerreserve_reserve1 FOREIGN KEY (reserve_id) REFERENCES resort.reserve(id)
);

-- Permissions

ALTER TABLE resort.customerreserve OWNER TO postgres;
GRANT ALL ON TABLE resort.customerreserve TO postgres;


-- resort.suit definition

-- Drop table

-- DROP TABLE resort.suit;

CREATE TABLE resort.suit (
	id BIGSERIAL,
	"number" varchar(10) NOT NULL,
	suitstatus integer NOT NULL,
	suittype integer NOT NULL,
	buildingfloor integer NOT NULL,
	"comment" varchar(1000) NULL,
	deleted boolean DEFAULT false,
	CONSTRAINT suit_pkey PRIMARY KEY (id),
	CONSTRAINT fk_suit_suitstatus1 FOREIGN KEY (suitstatus) REFERENCES resort.suitstatus(id),
	CONSTRAINT fk_suit_suittype1 FOREIGN KEY (suittype) REFERENCES resort.suittype(id),
	CONSTRAINT fk_suit_buildingfloor FOREIGN KEY (buildingfloor) REFERENCES resort.buildingfloor(id)
);

-- Permissions

ALTER TABLE resort.suit OWNER TO postgres;
GRANT ALL ON TABLE resort.suit TO postgres;


-- resort.suitfeatures definition

-- Drop table

-- DROP TABLE resort.suitfeatures;

CREATE TABLE resort.suitfeatures (
	features_id integer NOT NULL,
	suittype_id integer NOT NULL,
	CONSTRAINT suitfeatures_pkeys PRIMARY KEY (suittype_id, features_id),
	CONSTRAINT fk_suitfeatures_features1 FOREIGN KEY (features_id) REFERENCES resort.features(id),
	CONSTRAINT fk_suitfeatures_suittype1 FOREIGN KEY (suittype_id) REFERENCES resort.suittype(id)
);

-- Permissions

ALTER TABLE resort.suitfeatures OWNER TO postgres;
GRANT ALL ON TABLE resort.suitfeatures TO postgres;


-- resort.suitservices definition

-- Drop table

-- DROP TABLE resort.suitservices;

CREATE TABLE resort.suitservices (
	services_id integer NOT NULL,
	suittype_id integer NOT NULL,
	CONSTRAINT suitservices_pkeys PRIMARY KEY (services_id, suittype_id),
	CONSTRAINT fk_suitservices_services1 FOREIGN KEY (services_id) REFERENCES resort.services(id),
	CONSTRAINT fk_suitservices_suittype1 FOREIGN KEY (suittype_id) REFERENCES resort.suittype(id)
);

-- Permissions

ALTER TABLE resort.suitservices OWNER TO postgres;
GRANT ALL ON TABLE resort.suitservices TO postgres;


-- resort.buildingfloor definition

-- Drop table

-- DROP TABLE resort.buildingfloor;

CREATE TABLE resort.buildingfloor (
	id SERIAL,
	building integer NOT NULL,
	floor integer NOT NULL,
	CONSTRAINT buildingfloor_pkey PRIMARY KEY (id),
	CONSTRAINT buildingfloor_un UNIQUE (building, floor),
	CONSTRAINT fk_buildingfloor_building FOREIGN KEY (building) REFERENCES resort.building(id),
	CONSTRAINT fk_buildingfloor_floor FOREIGN KEY (floor) REFERENCES resort.floor(id)
);

-- Permissions

ALTER TABLE resort.buildingfloorsuit OWNER TO postgres;
GRANT ALL ON TABLE resort.buildingfloorsuit TO postgres;

-- resort.reserveservices definition

-- Drop table

-- DROP TABLE resort.reserveservices;

CREATE TABLE resort.reserveservices (
	id BIGSERIAL,
	reservesuit bigint NOT NULL,
	service integer NOT NULL,
	price numeric(10, 4) NOT NULL,
	CONSTRAINT reserveservices_pkey PRIMARY KEY (id),
	CONSTRAINT reserveservices_un UNIQUE (reservesuit, service),
	CONSTRAINT reserveservices_fk FOREIGN KEY (reservesuit) REFERENCES resort.reservesuit(id),
	CONSTRAINT reserveservices_fk_1 FOREIGN KEY (service) REFERENCES resort.services(id),
	CONSTRAINT reserveservices_fk_2 FOREIGN KEY (suit) REFERENCES resort.suit(id)
);
CREATE INDEX reserveservices_reserve_idx ON resort.reserveservices USING btree (reserve, service);

-- Permissions

ALTER TABLE resort.reserveservices OWNER TO postgres;
GRANT ALL ON TABLE resort.reserveservices TO postgres;


-- resort.reservesuit definition

-- Drop table

-- DROP TABLE resort.reservesuit;

CREATE TABLE resort.reservesuit (
	id BIGSERIAL,
	reserve_id bigint NOT NULL,
	suit_id bigint NOT NULL,
	price numeric(10, 4) NOT NULL,
	CONSTRAINT reservesuit_pkey PRIMARY KEY (id),
	CONSTRAINT reservesuit_un UNIQUE (reserve_id, suit_id),
	CONSTRAINT fk_reservesuit_reserve1 FOREIGN KEY (reserve_id) REFERENCES resort.reserve(id),
	CONSTRAINT fk_reservesuit_suit1 FOREIGN KEY (suit_id) REFERENCES resort.suit(id)
);

-- Permissions

ALTER TABLE resort.reservesuit OWNER TO postgres;
GRANT ALL ON TABLE resort.reservesuit TO postgres;