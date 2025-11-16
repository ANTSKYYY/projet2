# run a ddos for 60sec
docker exec -it attacker attack_handler.sh ddos_udp 60

# run a ssh brutforce for 60sec
docker exec -it attacker attack_handler.sh ssh 60

# run a man in the middle attack for 60 sec
docker exec -it attacker attack_handler.sh mitm 60
