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

CREATE TABLE country (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(64) NOT NULL,
    code VARCHAR(64),
    erased BOOLEAN NOT NULL
);

CREATE TABLE states (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(64) NOT NULL,
    country_id UUID NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_states_country FOREIGN KEY (country_id) REFERENCES country(id) ON DELETE CASCADE
);

CREATE TABLE cities (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(64) NOT NULL,
    state_id UUID NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_cities_state FOREIGN KEY (state_id) REFERENCES states(id) ON DELETE CASCADE
);

CREATE TABLE address (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    nickname VARCHAR(32),
    street VARCHAR(64) NOT NULL,
    number VARCHAR(15) NOT NULL,
    suburb VARCHAR(32) NOT NULL,
    zip_code VARCHAR(64) NOT NULL,
    reference VARCHAR(128),
    country_id UUID NOT NULL,
    state_id UUID NOT NULL,
    city_id UUID NOT NULL,
    CONSTRAINT fk_address_country FOREIGN KEY (country_id) REFERENCES country(id),
    CONSTRAINT fk_address_state FOREIGN KEY (state_id) REFERENCES states(id),
    CONSTRAINT fk_address_city FOREIGN KEY (city_id) REFERENCES cities(id)
);

CREATE TABLE files (
    id UUID PRIMARY KEY,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    name VARCHAR(256) NOT NULL,
    extension VARCHAR(64) NOT NULL,
    mime_type VARCHAR(128) NOT NULL
);

CREATE TABLE modules (
    id UUID PRIMARY KEY,
    "name" VARCHAR(64) NOT NULL,
    "route" VARCHAR(256) NOT NULL,
    icon_name VARCHAR(128) NOT NULL,
    parent_module_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
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

CREATE TABLE taxpayer (
    id UUID PRIMARY KEY,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    corporate_reason_or_natural_person VARCHAR(512) NOT NULL,
    rfc VARCHAR(256) NOT NULL,
    type_person VARCHAR(64) NOT NULL,
    address_id UUID NOT NULL,
    data_key VARCHAR(64) NOT NULL,
    CONSTRAINT fk_taxpayer_address FOREIGN KEY (address_id) REFERENCES address(id)
);


CREATE TABLE legal_representative (
    id UUID PRIMARY KEY,
    erased BOOLEAN NOT NULL,
    full_name VARCHAR(512) NOT NULL,
    rfc VARCHAR(256) NOT NULL,
    data_key VARCHAR(64) NOT NULL,
    taxpayer_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_legal_taxpayer FOREIGN KEY (taxpayer_id) REFERENCES taxpayer(id)
);

CREATE TABLE company (
    id UUID PRIMARY KEY,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(256) NOT NULL,
    email VARCHAR(32) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address_id UUID NOT NULL,
    image_id UUID UNIQUE,
    taxpayer_id UUID NOT NULL,
    CONSTRAINT fk_company_address FOREIGN KEY (address_id) REFERENCES address(id),
    CONSTRAINT fk_company_image FOREIGN KEY (image_id) REFERENCES files(id),
    CONSTRAINT fk_company_taxpayer FOREIGN KEY (taxpayer_id) REFERENCES taxpayer(id)
);


CREATE TABLE users (
    id UUID PRIMARY KEY,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    full_name VARCHAR(256) NOT NULL,
    send_expiration_alert BOOLEAN NOT NULL,
    profile_id UUID NOT NULL,
    company_id UUID NULL,
    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES profiles(id),
    CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES company(id)
);