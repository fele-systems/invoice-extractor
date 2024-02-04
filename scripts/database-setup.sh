#! /bin/bash

if ! which psql; then
    echo 'Could not find psql client on path' 1>&2
    exit 1
fi

sudo -u postgres psql <<EOF

CREATE ROLE invoices LOGIN SUPERUSER PASSWORD 'invoice';
CREATE DATABASE invoices;
GRANT ALL PRIVILEGES ON DATABASE invoices TO invoices;

EOF