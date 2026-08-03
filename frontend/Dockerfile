# ==========================================
# Stage 1: Build the React application
# ==========================================
FROM node:20-alpine AS builder

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json
COPY package*.json ./

# Install dependencies (clean install)
RUN npm ci

# Copy the rest of the source code
COPY . .

# Build the application for production
RUN npm run build

# ==========================================
# Stage 2: Serve the app using NGINX
# ==========================================
FROM nginx:alpine

# Remove default NGINX website
RUN rm -rf /usr/share/nginx/html/*

# Copy the built assets from the builder stage
COPY --from=builder /app/dist /usr/share/nginx/html

# Copy the custom NGINX configuration
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Start NGINX
CMD ["nginx", "-g", "daemon off;"]
