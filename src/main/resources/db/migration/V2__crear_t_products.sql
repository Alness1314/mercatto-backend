-- Tabla: categories
CREATE TABLE categories (
    id UUID PRIMARY KEY,
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
    id UUID PRIMARY KEY,
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
    id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    code VARCHAR(256) NOT NULL,
    price NUMERIC(18,4) NOT NULL,
    stock BIGINT NOT NULL,
    category_id UUID NOT NULL,
    unit_id UUID NOT NULL,
    active BOOLEAN NOT NULL,
    tax NUMERIC(18,4) NOT NULL,
    company_id UUID,
    create_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    update_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    erased BOOLEAN NOT NULL,

    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_product_unit FOREIGN KEY (unit_id) REFERENCES unit_measurement(id),
    CONSTRAINT fk_product_company FOREIGN KEY (company_id) REFERENCES company(id)
);