#! /bin/bash

if [[ "$1" == "install" ]]; then
    apt update && apt install postgresql -y
    service postgresql start
fi

if [[ "$1" == "recreate" ]]; then
    sudo -u postgres psql <<-EOF
    DROP DATABASE invoices;
    CREATE DATABASE invoices;
EOF
    exit 0
fi

if ! which psql; then
    echo 'Could not find psql client on path' 1>&2
    exit 1
fi

sudo -u postgres psql <<EOF

CREATE ROLE invoices LOGIN SUPERUSER PASSWORD 'invoice';
CREATE DATABASE invoices;
GRANT ALL PRIVILEGES ON DATABASE invoices TO invoices;

EOF