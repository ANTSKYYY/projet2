### **README.md**

# Network Attack Simulator

This project is a Docker-based simulation environment designed to test network attacks between two machines: an **attacker** (Kali Linux), a **victim** (Kali Linux) and a **machine** (Alpine Linux). The environment is isolated using Docker networking and includes tools for attacking and testing.

---

### 1. **Build and Launch the Environment**

#### Build and Start the Containers:
```bash
docker-compose up --build -d
```

#### Verify the Running Containers:
```bash
docker ps
```

You should see three containers: **attacker**, **victim** and **machine**.

---

## **Usage**

### 2. **Access the Containers**

#### Attacker:
```bash
docker exec -it attacker bash
```

#### Victim:
```bash
docker exec -it victim bash
```

#### Machine
```bash
docker exec -it machine sh
```

---

### 3. **Test Network Connectivity**

From the **attacker**, verify connectivity to the other machines:

```bash
ping victim
ping machine
```

If successful, you can proceed with attacks.

---

### 4. **Simulate Network Attacks**

#### Example 1: Scan All Open Ports on the Victim:
```bash
nmap -p- victim
```

#### Example 2: Run a Brute Force Attack on SSH:

from attacker:
```bash
./attack_handler.sh ssh <duration>
```

---

### 5. **Monitor Network Traffic**

using tshark we can sniff the traffic of the docker virtual network:

```bash
tshark -i <docker virtual network>
```

---

### 6. **View Logs**

#### Attacker Logs:
```bash
docker logs attacker
```

#### Victim Logs:
```bash
docker logs victim
```

#### Machine Logs:
```bash
docker logs machine
```

---

## **Shutting Down the Environment**

To stop and remove the containers and network:
```bash
docker-compose down
```

---

## **Additional Notes**

1. **Default Network**:
   The containers are connected to the `attack_network`, an isolated Docker bridge network.

2. **Victim Configuration**:
   - Services like SSH are started at the launch of the machine.

3. **Extending the Environment**:
   - Modify the `/Docker/<machine>/Dockerfile` to add more packages.
   - Keep in mind adding package will require the iso to be built back from scratch which can take some times.

4. **Customizing IPs**:
   You can define custom IPs for the attacker and victim containers by modifying the `attack_network` configuration. by default there is an alias for victim, attacker and machine.

---
