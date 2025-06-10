
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

CREATE TABLE modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "name" VARCHAR(64) NOT NULL,
    "route" VARCHAR(256) NOT NULL,
    icon_name VARCHAR(128) NOT NULL,
    parent_module_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_modules_parent FOREIGN KEY (parent_module_id) REFERENCES modules(id)
);

CREATE TABLE taxpayer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
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
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    erased BOOLEAN NOT NULL,
    full_name VARCHAR(512) NOT NULL,
    rfc VARCHAR(256) NOT NULL,
    data_key VARCHAR(64) NOT NULL,
    taxpayer_id UUID NOT NULL UNIQUE,
    CONSTRAINT fk_legal_taxpayer FOREIGN KEY (taxpayer_id) REFERENCES taxpayer(id)
);

-- Paso 1: crear las tablas SIN las FK circulares

CREATE TABLE files (
    id UUID PRIMARY KEY NOT NULL,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    "name" VARCHAR(256) NOT NULL,
    extension VARCHAR(64) NOT NULL,
    mime_type VARCHAR(128) NOT NULL,
    company_id UUID NULL
    -- FK se agregará después
);

CREATE TABLE company (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    name VARCHAR(128) NOT NULL,
    description VARCHAR(256) NOT NULL,
    email VARCHAR(32) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address_id UUID NOT NULL,
    image_id UUID NULL,
    taxpayer_id UUID NOT NULL,
    CONSTRAINT fk_company_address FOREIGN KEY (address_id) REFERENCES address(id),
    CONSTRAINT fk_company_image FOREIGN KEY (image_id) REFERENCES files(id), -- se mueve
    CONSTRAINT fk_company_taxpayer FOREIGN KEY (taxpayer_id) REFERENCES taxpayer(id)
);

--perfiles
CREATE TABLE profiles (
	id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
	"name" varchar(64) NOT NULL,
	create_at timestamp NOT NULL,
	update_at timestamp NOT NULL,
    erased BOOLEAN NOT NULL,
    company_id UUID NULL,
	CONSTRAINT profiles_name_key UNIQUE ("name"),
    CONSTRAINT fk_profiles_company FOREIGN KEY (company_id) REFERENCES company(id)
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    full_name VARCHAR(256) NOT NULL,
    image_id UUID NULL,
    send_expiration_alert BOOLEAN NOT NULL,
    profile_id UUID NOT NULL,
    company_id UUID NULL,
    CONSTRAINT fk_users_profile FOREIGN KEY (profile_id) REFERENCES profiles(id),
    CONSTRAINT fk_user_company FOREIGN KEY (company_id) REFERENCES company(id),
    CONSTRAINT fk_users_image FOREIGN KEY (image_id) REFERENCES files(id)
);

CREATE TABLE permissions (
    profile_id UUID NOT NULL,
    module_id UUID NOT NULL,
    can_create BOOLEAN NOT NULL,
    can_read BOOLEAN NOT NULL,
    can_update BOOLEAN NOT NULL,
    can_delete BOOLEAN NOT NULL,
    company_id UUID NULL,
    PRIMARY KEY (profile_id, module_id),
    CONSTRAINT fk_permission_profile FOREIGN KEY (profile_id) REFERENCES profiles(id),
    CONSTRAINT fk_permission_module FOREIGN KEY (module_id) REFERENCES modules(id),
    CONSTRAINT fk_permission_company FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Tabla: categories
CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(64) NOT NULL,
    description VARCHAR(255) NOT NULL,
    company_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,

    CONSTRAINT fk_category_company FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Tabla: unit_measurement
CREATE TABLE unit_measurement (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(64) NOT NULL,
    abbreviation VARCHAR(64) NOT NULL,
    description VARCHAR(255) NOT NULL,
    company_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,

    CONSTRAINT fk_unit_company FOREIGN KEY (company_id) REFERENCES company(id)
);

-- Tabla: products
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(64) NOT NULL,
    code VARCHAR(256) NOT NULL,
    price NUMERIC(18,4) NOT NULL,
    stock BIGINT NOT NULL,
    category_id UUID NOT NULL,
    unit_id UUID NOT NULL,
    active BOOLEAN NOT NULL,
    tax NUMERIC(18,4) NOT NULL,
    image_id UUID NULL,
    company_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,

    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_product_unit FOREIGN KEY (unit_id) REFERENCES unit_measurement(id),
    CONSTRAINT fk_product_company FOREIGN KEY (company_id) REFERENCES company(id),
    CONSTRAINT fk_product_image FOREIGN KEY (image_id) REFERENCES files(id)
);

CREATE TABLE settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    "key" CHARACTER VARYING(128) NOT NULL,
    "value" CHARACTER VARYING NOT NULL,
    data_type CHARACTER VARYING(128) NOT NULL,
    "grouping" CHARACTER VARYING(64) NOT NULL,
    company_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_settings_company FOREIGN KEY (company_id) REFERENCES company(id)
);

CREATE TABLE sales (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    amount NUMERIC(18,4) NOT NULL,
    payment_method VARCHAR(64) NOT NULL,
    sync BOOLEAN NOT NULL,
    user_id UUID,
    company_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,
    CONSTRAINT fk_sales_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_sales_company FOREIGN KEY (company_id) REFERENCES company(id)
);

CREATE TABLE sales_details (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sales_id UUID,
    product_id UUID,
    stock BIGINT NOT NULL,
    unit_price NUMERIC(18,4) NOT NULL,
    subtotal NUMERIC(18,4) NOT NULL,
    tax NUMERIC(18,4) NOT NULL,
    company_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,

    CONSTRAINT fk_sales FOREIGN KEY (sales_id) REFERENCES sales(id),
    CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);