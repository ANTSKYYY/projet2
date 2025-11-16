FROM kalilinux/kali-rolling

RUN apt-get update && apt-get install -y \
    openssh-server \
    iputils-ping \
    iptables \
    netcat-openbsd \
    apache2 \
    ftp \
    && apt-get clean

RUN mkdir -p /var/run/sshd
RUN echo 'root:toor' | chpasswd

EXPOSE 22
EXPOSE 80
EXPOSE 21
EXPOSE 12345

COPY startup.sh /usr/local/bin/startup.sh
RUN chmod +x /usr/local/bin/startup.sh

ENTRYPOINT ["/usr/local/bin/startup.sh"]
