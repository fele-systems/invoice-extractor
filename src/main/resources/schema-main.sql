
CREATE SCHEMA IF NOT EXISTS invoices;

SET client_encoding = 'UTF8';

-- ///////////////////////////////////////////////////////
-- App User Table

CREATE TABLE IF NOT EXISTS invoices.app_user (
    id          BIGSERIAL PRIMARY KEY,
    admin       boolean NOT NULL,
    email       character varying(255),
    enabled     boolean NOT NULL,
    first_name  character varying(255),
    last_name   character varying(255),
    password    character varying(255)
);

ALTER TABLE invoices.app_user OWNER TO invoices;

-- ///////////////////////////////////////////////////////
-- Invoice

CREATE TABLE IF NOT EXISTS invoices.invoice (
    id          BIGSERIAL PRIMARY KEY,
    due_date    date,
    appuser_id  bigint,

    CONSTRAINT fk_app_user
        FOREIGN KEY (appuser_id)
        REFERENCES invoices.app_user(id)
);

ALTER TABLE invoices.invoice OWNER TO invoices;

-- ///////////////////////////////////////////////////////
-- Expenses

CREATE TABLE IF NOT EXISTS invoices.expense (
    id                  BIGSERIAL PRIMARY KEY,
    amount              numeric(38,2),
    date                date,
    description         character varying(255),
    installment_no      integer NOT NULL,
    total_installments  integer NOT NULL,
    local_id            INT NOT NULL,
    invoice_id          BIGINT,

    CONSTRAINT fk_invoice
        FOREIGN KEY (invoice_id)
        REFERENCES invoices.invoice(id)
);

ALTER TABLE invoices.expense
    OWNER TO invoices;