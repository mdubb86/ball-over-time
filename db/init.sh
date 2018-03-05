#!/bin/bash

# Ensure that the SSH user has linux postgres superuser permissions first
#    - sudo su - postgres
#    - createuser <linuxuser> -s
#    - edit pg_hba to allow linux user to connect
#    - edit conf file to allow remote connections
#    - edit pg_hba to allow remote connections


cd /meridian/ball-over-time/db
if [ $1 = "prod" ]; then
    scp init.sql mmdb:/tmp/
    ssh mmdb psql -d postgres -f /tmp/init.sql
else
    psql -d postgres -f init.sql
fi
