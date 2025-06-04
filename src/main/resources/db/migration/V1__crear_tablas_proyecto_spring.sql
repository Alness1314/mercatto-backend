--perfiles
CREATE TABLE profiles (
	id uuid NOT NULL,
	"name" varchar(64) NOT NULL,
	create_at timestamp NOT NULL,
	update_at timestamp NOT NULL,
    erased BOOLEAN NOT NULL,
	CONSTRAINT profiles_name_key UNIQUE ("name"),
	CONSTRAINT profiles_pkey PRIMARY KEY (id)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(), -- requiere pgcrypto o uuid-ossp
    username VARCHAR(64) NOT NULL UNIQUE,
    "password" VARCHAR(256) NOT NULL,
    full_name VARCHAR(256) NOT NULL,
    send_expiration_alert BOOLEAN NOT NULL,
    company_id UUID,
    profile_id UUID NOT NULL,
	create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES profiles(id)
);


CREATE TABLE country (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(64) NOT NULL,
    code VARCHAR(64),
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL
);

CREATE TABLE states (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(64) NOT NULL,
    country_id UUID NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_states_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE CASCADE
);

CREATE TABLE cities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(64) NOT NULL,
    state_id UUID NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_cities_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE CASCADE
);

CREATE TABLE modules (
    id UUID PRIMARY KEY,
    "name" VARCHAR(64) NOT NULL,
    "route" VARCHAR(256) NOT NULL,
    icon_name VARCHAR(128) NOT NULL,
    parent_module_id UUID,
    CONSTRAINT fk_modules_parent FOREIGN KEY (parent_module_id) REFERENCES modules(id)
);

CREATE TABLE permissions (
    profile_id UUID NOT NULL,
    module_id UUID NOT NULL,
    can_create BOOLEAN NOT NULL,
    can_read BOOLEAN NOT NULL,
    can_update BOOLEAN NOT NULL,
    can_delete BOOLEAN NOT NULL,
    PRIMARY KEY (profile_id, module_id),
    CONSTRAINT fk_permission_profile FOREIGN KEY (profile_id) REFERENCES profiles(id),
    CONSTRAINT fk_permission_module FOREIGN KEY (module_id) REFERENCES modules(id)
);
