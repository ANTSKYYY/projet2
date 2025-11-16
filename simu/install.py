import os
import platform
import sys
try:
    import distro
except ImportError:
    distro = None


def is_debian_based_platform():
    if distro:
        dist_name = distro.id().lower()
        return "debian" in dist_name or "ubuntu" in dist_name
    else:
        # in case distro lib is not installed.
        try:
            with open("/etc/os-release", "r") as f:
                os_release = f.read().lower()
                return "debian" in os_release or "ubuntu" in os_release
        except FileNotFoundError:
            return False


if platform.system() == "Linux":
    if is_debian_based_platform():
        os.system('./scripts/debian.sh')
    else:
        print("This installer doesn't support your Linux distribution at the moment. You can try to install it yourself by installing Docker, Docker Compose, and their dependencies.")
elif platform.system() == "Windows":
    os.system('./scripts/windows.bat')
else:
    print("This installer doesn't support your OS for the moment. You can try to install it yourself by installing Docker, Docker Compose, and their dependencies.")