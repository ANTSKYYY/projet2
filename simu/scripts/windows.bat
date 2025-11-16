@echo off
:: Batch script to install Docker Desktop on Windows with a check for WSL

echo Checking for WSL installation...
wsl --status >nul 2>&1
if %errorlevel% neq 0 (
    echo WSL is not installed or enabled on your system.
    echo Please install and enable WSL 2 before proceeding.
    echo For instructions, visit: https://docs.microsoft.com/en-us/windows/wsl/install
    pause
    exit /b
)

echo WSL is installed. Checking for WSL version...
for /f "tokens=2 delims=: " %%i in ('wsl --status ^| findstr "Default Version"') do set "WSL_VERSION=%%i"

if not "%WSL_VERSION%"=="2" (
    echo WSL is installed but not set to version 2.
    echo Please upgrade to WSL 2 before proceeding.
    echo For instructions, visit: https://docs.microsoft.com/en-us/windows/wsl/install
    pause
    exit /b
)

echo Downloading Docker Desktop installer...
curl -LO https://desktop.docker.com/win/stable/Docker%20Desktop%20Installer.exe

if %errorlevel% neq 0 (
    echo Failed to download the Docker Desktop installer.
    echo Please check your internet connection and try again.
    pause
    exit /b
)

echo Running the Docker Desktop installer...
start /wait "" "Docker Desktop Installer.exe" install

if %errorlevel% neq 0 (
    echo Docker Desktop installation failed.
    pause
    exit /b
)

echo Docker Desktop installed successfully!
echo Configuring Docker to use WSL 2...
wsl --set-default-version 2

echo Installation complete! Please restart your computer if prompted.
pause
