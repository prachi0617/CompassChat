# ---- Stage 1: build the static bundle ----
FROM node:20-alpine AS build
WORKDIR /build

# Install deps from the lockfile (cached unless package files change)
COPY package*.json ./
RUN npm ci

# Build the production bundle into /build/dist
COPY . .
RUN npm run build

# ---- Stage 2: serve with nginx (also reverse-proxies /api and /ws) ----
FROM nginx:1.27-alpine
COPY --from=build /build/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 8080
CMD ["nginx", "-g", "daemon off;"]
