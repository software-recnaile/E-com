#!/usr/bin/env bash
set -euo pipefail

APP_DIR="/home/ubuntu/recnaile-app"   # adjust if different
cd "$APP_DIR"

if [ ! -f .env ]; then
  echo ".env missing in $APP_DIR - aborting" >&2
  exit 1
fi

echo "Pulling images (if available) from Docker Hub..."
docker compose pull || true

echo "Starting services (compose will build locally if image not found)..."
docker compose up -d --remove-orphans

echo "Pruning unused images..."
docker image prune -f || true

echo "Deploy finished."
