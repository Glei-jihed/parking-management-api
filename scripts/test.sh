#!/usr/bin/env bash
set -e

echo "Running backend tests..."
cd backend
./mvnw test

echo "Running frontend tests..."
cd ../frontend
npm test -- --watch=false