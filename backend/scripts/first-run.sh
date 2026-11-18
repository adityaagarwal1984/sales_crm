#!/usr/bin/env bash
set -euo pipefail

if ! command -v psql >/dev/null 2>&1; then
  echo "PostgreSQL command-line tools were not found. Install and start Postgres.app, then add its bin folder to PATH; see backend/README.md."
  exit 1
fi
if ! command -v mvn >/dev/null 2>&1; then
  echo "Maven was not found. Install Maven and try again."
  exit 1
fi

read -r -p "PostgreSQL host [localhost]: " db_host
db_host=${db_host:-localhost}
read -r -p "PostgreSQL port [5432]: " db_port
db_port=${db_port:-5432}
read -r -p "Database name [prosperr_crm]: " db_name
db_name=${db_name:-prosperr_crm}
read -r -p "Database user [prosperr]: " db_user
db_user=${db_user:-prosperr}
read -r -s -p "Database password: " db_password
printf '\n'

if ! PGPASSWORD="$db_password" psql -h "$db_host" -p "$db_port" -U "$db_user" -d "$db_name" -v ON_ERROR_STOP=1 -c 'SELECT 1' >/dev/null; then
  echo "Could not connect to that database. Check that PostgreSQL is running and the database/user/password exist."
  exit 1
fi

read -r -p "First administrator name: " admin_name
read -r -p "First administrator work email: " admin_email
read -r -s -p "Temporary administrator password (12+ characters): " admin_password
printf '\n'
read -r -s -p "Confirm temporary password: " admin_password_confirm
printf '\n'
if [[ "$admin_password" != "$admin_password_confirm" ]]; then
  echo "Passwords did not match. No changes were made."
  exit 1
fi
admin_password_bytes=$(printf '%s' "$admin_password" | wc -c | tr -d ' ')
if (( ${#admin_password} < 12 || admin_password_bytes > 72 )); then
  echo "Use at least 12 characters and no more than 72 UTF-8 bytes."
  exit 1
fi

export DB_URL="jdbc:postgresql://${db_host}:${db_port}/${db_name}"
export DB_USERNAME="$db_user"
export DB_PASSWORD="$db_password"
export APP_BOOTSTRAP_ADMIN_NAME="$admin_name"
export APP_BOOTSTRAP_ADMIN_EMAIL="$admin_email"
export APP_BOOTSTRAP_ADMIN_PASSWORD="$admin_password"
export APP_JWT_SECRET_BASE64="$(openssl rand -base64 32)"

repo_path=${MAVEN_REPO_LOCAL:-$HOME/.local/m2}
echo "Starting the API. On its first start, Liquibase creates the tables and the first administrator account."
echo "When Spring Boot reports that it started, open another terminal to start the frontend."
cd "$(dirname "$0")/.."
mvn -Dmaven.repo.local="$repo_path" spring-boot:run
