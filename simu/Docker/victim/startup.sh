#!/bin/bash

# Start the SSH service
service ssh start

# Start the Netcat listener on port 12345
nc -u -l -p 12345 &

# Keep the container running
exec tail -f /dev/null
