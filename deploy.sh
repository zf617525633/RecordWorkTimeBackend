#!/bin/bash
set -e

echo "Starting MariaDB..."
systemctl enable mariadb
systemctl start mariadb

echo "Setting up Database..."
# Set root password if it's empty, ignore error if already set
mysqladmin -u root password 'Zf617525633!@#' || true
mysql -u root -p'Zf617525633!@#' -e "CREATE DATABASE IF NOT EXISTS fission_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

echo "Importing DB Dump..."
mysql -u root -p'Zf617525633!@#' fission_system < /root/fission_system_dump.sql

echo "Setting up Nginx..."
mkdir -p /var/www
rm -rf /var/www/h5-dist /var/www/admin-dist
cp -r /root/h5-dist /var/www/h5-dist
cp -r /root/admin-dist /var/www/admin-dist

cat << 'EOF' > /etc/nginx/nginx.conf
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log;
pid /run/nginx.pid;

include /usr/share/nginx/modules/*.conf;

events {
    worker_connections 1024;
}

http {
    log_format  main  '$remote_addr - $remote_user [$time_local] "$request" '
                      '$status $body_bytes_sent "$http_referer" '
                      '"$http_user_agent" "$http_x_forwarded_for"';

    access_log  /var/log/nginx/access.log  main;
    sendfile            off;
    tcp_nopush          on;
    tcp_nodelay         on;
    keepalive_timeout   65;
    types_hash_max_size 4096;

    include             /etc/nginx/mime.types;
    default_type        application/octet-stream;

    server {
        listen       80;
        server_name  _;
        root         /var/www/h5-dist;
        location / { try_files $uri $uri/ /index.html; }
        location /api/ {
            proxy_pass http://127.0.0.1:8080;
            proxy_set_header Host $host;
        }
    }
    server {
        listen       8081;
        server_name  _;
        root         /var/www/admin-dist;
        location / { try_files $uri $uri/ /index.html; }
        location /api/ {
            proxy_pass http://127.0.0.1:8080;
            proxy_set_header Host $host;
        }
    }
}
EOF

systemctl enable nginx
systemctl restart nginx

echo "Starting Backend..."
# Kill existing Java process if any
pkill -f backend-0.0.1-SNAPSHOT.jar || true
sleep 2
cd /root
nohup java -jar backend-0.0.1-SNAPSHOT.jar --spring.config.additional-location=file:/root/application-secrets.yml > backend.log 2>&1 &

echo "Deployment completed successfully!"
