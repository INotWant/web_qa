#!/usr/bin/env bash
docker run -d --name qa -p 2202:22 -p 8080:8080 -p 80:80 qa:0.6
docker exec qa /etc/init.d/ssh start