#!/usr/bin/env bash
set -e


APP_DIR="/home/ubuntu/app"
cd "$APP_DIR"


# Renew using certbot container
docker compose run --rm certbot certbot renew --webroot -w /var/www/certbot


# Reload nginx to pick up new certs
docker compose exec nginx nginx -s reload || true


# Cleanup
docker image prune -f || true


echo "renew complete"
