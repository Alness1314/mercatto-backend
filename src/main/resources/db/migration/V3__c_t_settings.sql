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