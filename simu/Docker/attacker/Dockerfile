FROM kalilinux/kali-rolling

RUN apt update && apt install -y iputils-ping nmap metasploit-framework hydra hping3 && apt clean

CMD ["tail", "-f", "/dev/null"]

