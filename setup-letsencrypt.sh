#!/usr/bin/env bash
set -e


APP_DIR="/home/ubuntu/app" # adjust
cd "$APP_DIR"


if [ ! -d ./certbot/www ]; then
mkdir -p certbot/www
fi


# Start nginx + other containers (nginx must be able to serve /.well-known)
docker compose up -d nginx


# Request cert (replace domain/email if not using .env)
DOMAIN=api.recnaile.com
EMAIL=software@recnaile.com


docker compose run --rm certbot certbot certonly \
--webroot -w /var/www/certbot \
-d "$DOMAIN" \
--email "$EMAIL" --agree-tos --no-eff-email


# copy recommended SSL options if missing (certbot may create them)
if [ ! -f certbot/conf/options-ssl-nginx.conf ]; then
echo "options-ssl-nginx.conf missing — downloading recommended file"
docker run --rm -v "$PWD/certbot/conf:/etc/letsencrypt" quay.io/letsencrypt/certbot:latest \
sh -c 'cp /etc/letsencrypt/options-ssl-nginx.conf /etc/letsencrypt/options-ssl-nginx.conf || true'
fi


if [ ! -f certbot/conf/ssl-dhparams.pem ]; then
echo "ssl-dhparams.pem missing — creating (may take a minute)"
docker run --rm -v "$PWD/certbot/conf:/etc/letsencrypt" quay.io/letsencrypt/certbot:latest \
sh -c 'openssl dhparam -out /etc/letsencrypt/ssl-dhparams.pem 2048'
fi


# Reload nginx
docker compose exec nginx nginx -s reload || true


echo "Let's Encrypt initial issuance done."
