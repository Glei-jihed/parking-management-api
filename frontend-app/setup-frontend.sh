#!/bin/bash

echo "🚀 Creating Angular frontend structure..."

BASE=frontend-app

# ROOT
mkdir -p $BASE/src/app

# CORE
mkdir -p $BASE/src/app/core/{guards,interceptors,models,services}

# SHARED
mkdir -p $BASE/src/app/shared/components/navbar

# FEATURES
mkdir -p $BASE/src/app/features/login
mkdir -p $BASE/src/app/features/employee-dashboard
mkdir -p $BASE/src/app/features/admin
mkdir -p $BASE/src/app/features/manager-dashboard

# FILES ROOT
touch $BASE/Dockerfile
touch $BASE/nginx.conf
touch $BASE/package.json

# SRC ROOT
touch $BASE/src/index.html
touch $BASE/src/main.ts
touch $BASE/src/styles.scss

# APP CORE FILES
touch $BASE/src/app/app.config.ts
touch $BASE/src/app/app.routes.ts
touch $BASE/src/app/app.ts
touch $BASE/src/app/app.html
touch $BASE/src/app/app.scss

# MODELS
touch $BASE/src/app/core/models/api.models.ts

# SERVICES
touch $BASE/src/app/core/services/api.service.ts
touch $BASE/src/app/core/services/auth.service.ts
touch $BASE/src/app/core/services/token.service.ts

# GUARDS
touch $BASE/src/app/core/guards/auth.guard.ts
touch $BASE/src/app/core/guards/role.guard.ts

# INTERCEPTOR
touch $BASE/src/app/core/interceptors/auth.interceptor.ts

# NAVBAR
touch $BASE/src/app/shared/components/navbar/navbar.component.ts
touch $BASE/src/app/shared/components/navbar/navbar.component.html
touch $BASE/src/app/shared/components/navbar/navbar.component.scss

# LOGIN
touch $BASE/src/app/features/login/login.component.ts
touch $BASE/src/app/features/login/login.component.html
touch $BASE/src/app/features/login/login.component.scss

# EMPLOYEE
touch $BASE/src/app/features/employee-dashboard/employee-dashboard.component.ts
touch $BASE/src/app/features/employee-dashboard/employee-dashboard.component.html
touch $BASE/src/app/features/employee-dashboard/employee-dashboard.component.scss

# ADMIN
touch $BASE/src/app/features/admin/admin.component.ts
touch $BASE/src/app/features/admin/admin.component.html
touch $BASE/src/app/features/admin/admin.component.scss

# MANAGER
touch $BASE/src/app/features/manager-dashboard/manager-dashboard.component.ts
touch $BASE/src/app/features/manager-dashboard/manager-dashboard.component.html
touch $BASE/src/app/features/manager-dashboard/manager-dashboard.component.scss

echo "✅ Frontend structure created successfully!"
