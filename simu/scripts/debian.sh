#!/bin/bash

# Script to install Docker and Docker Compose on Linux

echo "Updating package lists..."
sudo apt update -y

echo "Installing prerequisites..."
sudo apt install -y \
    apt-transport-https \
    ca-certificates \
    curl \
    software-properties-common

echo "Checking if Docker's GPG key already exists..."
GPG_FILE="/usr/share/keyrings/docker-archive-keyring.gpg"
if [ -f "$GPG_FILE" ]; then
    echo "Docker's GPG key already exists. Skipping key addition."
else
    echo "Adding Docker's official GPG key..."
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o "$GPG_FILE"
fi

echo "Setting up Docker repository..."
if [ ! -f /etc/apt/sources.list.d/docker.list ]; then
    echo \
      "deb [arch=$(dpkg --print-architecture) signed-by=$GPG_FILE] https://download.docker.com/linux/ubuntu \
      $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
else
    echo "Docker repository already set up. Skipping."
fi

echo "Updating package lists again..."
sudo apt update -y

echo "Installing Docker..."
sudo apt install -y docker-ce docker-ce-cli containerd.io

echo "Verifying Docker installation..."
docker --version

echo "Installing Docker Compose..."
DOCKER_COMPOSE_PATH="/usr/local/bin/docker-compose"
if [ -f "$DOCKER_COMPOSE_PATH" ]; then
    echo "Docker Compose already installed. Skipping installation."
else
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o "$DOCKER_COMPOSE_PATH"
    sudo chmod +x "$DOCKER_COMPOSE_PATH"
fi

echo "Verifying Docker Compose installation..."
docker-compose --version

echo "Adding user to the Docker group..."
sudo usermod -aG docker $USER

echo "Installation complete! Please log out and log back in to apply the Docker group changes."
