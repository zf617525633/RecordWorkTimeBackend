# Project Custom Agent Rules (Learnings & Memory)

These rules are documented to prevent repeating previous deployment and environment bugs in this project.

## 1. SCP Folder Nesting Bug
- **Context**: When deploying compiled frontend assets (e.g., `dist` folders) via SSH/SCP.
- **Problem**: Running `scp -r dist/ root@host:/root/h5-dist` when `/root/h5-dist` already exists will nest the `dist` directory inside (`/root/h5-dist/dist/`), preventing Nginx from serving new assets.
- **Rule**: Always delete the target directories on the remote host before executing `scp -r` (e.g., `rm -rf /root/h5-dist` followed by `scp -r dist root@host:/root/h5-dist`). When copying on the server, clean up destination paths first: `rm -rf /var/www/h5-dist && cp -r /root/h5-dist /var/www/h5-dist`.

## 2. Nginx sendfile off (HTTP/2 Protocol Error)
- **Context**: Serving static assets over HTTPS via Cloudflare/Proxies.
- **Problem**: Having `sendfile on;` in `/etc/nginx/nginx.conf` in virtualized cloud machines can lead to packet segmentation failures, causing `net::ERR_HTTP2_PROTOCOL_ERROR` on larger assets (like JS chunks).
- **Rule**: Set `sendfile off;` in `/etc/nginx/nginx.conf` to avoid protocol errors during HTTP/2 transfers.

## 3. Redis Service Dependency
- **Context**: Spring Boot backend token/SMS validation.
- **Problem**: If Redis is not running or installed on the host, login APIs will crash with `RedisConnectionFailureException` (500 Internal Server Error).
- **Rule**: Always ensure the `redis` service is active and running (`systemctl status redis`) on the target machine when launching the Spring Boot backend.
