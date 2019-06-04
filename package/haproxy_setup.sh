#!/bin/bash

add-apt-repository ppa:certbot/certbot
apt-get update
apt-get install certbot

DOMAIN='ballovertime.com'
certbot certonly --standalone --preferred-challenges http --http-01-port 80 -d $DOMAIN -d www.$DOMAIN

mkdir -p /etc/haproxy/certs
cat /etc/letsencrypt/live/$DOMAIN/fullchain.pem /etc/letsencrypt/live/$DOMAIN/privkey.pem > /etc/haproxy/certs/$DOMAIN.pem
