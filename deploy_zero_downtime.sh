#!/usr/bin/env bash
set -euo pipefail

APP_DIR="/home/ubuntu/recnaile-app"   # adjust if different
cd "$APP_DIR"

if [ ! -f .env ]; then
  echo ".env missing; aborting" >&2
  exit 1
fi

# Update order (services with least dependencies first)
SERVICES=(
  authService
  accountService
  productService
  cartService
  wishlistService
  profile
  bulkorderservice
  address
  mailService
  oauthService
)

echo "Attempting to pull all images (best-effort)..."
docker compose pull || true

echo "Ensuring images can be built if pull missing..."
docker compose build --parallel || true

for svc in "${SERVICES[@]}"; do
  echo "---- Rolling update for: $svc ----"
  # bring up service, prefer pulled image; --no-deps prevents dependent restarts
  docker compose up -d --no-deps --build "$svc"
  # short wait for health (customize path/port if your service exposes different check)
  echo "Waiting for $svc to become healthy..."
  sleep 5

  # Optional health check via nginx path if available (uncomment & adjust if needed)
  # curl -fsS --max-time 5 "http://127.0.0.1/healthz" || echo "$svc health check failed (non-blocking)"

  echo "$svc updated."
done

echo "Rolling deploy complete."
