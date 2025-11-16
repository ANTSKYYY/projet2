package moa.streams.generators.MasTackGen;

import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.IOException;

public class MasTackGenUtils {
    /**
     * Starts an attack using the attack_handler.sh script in the Docker "attacker" container.
     *
     * @param simulationDir The path to the simulation directory where the Docker setup exists.
     * @param attackType    The type of attack to start (e.g., "ddos_udp", "ssh").
     * @param duration      The duration of the attack in seconds.
     * @throws IOException          If the process cannot be started.
     * @throws InterruptedException If the process is interrupted.
     */
    public static void startAttack(String simulationDir, String attackType, int duration)
            throws IOException, InterruptedException {

        File simuDir = new File(simulationDir);
        if (!simuDir.exists() || !simuDir.isDirectory()) {
            throw new IllegalArgumentException("Simulation directory does not exist: " + simulationDir);
        }

        // Build the command to execute attack_handler.sh inside the attacker container
        String[] command = {
                "docker",
                "exec",
                "-it",
                "attacker",
                "/attack_handler.sh",
                attackType,
                String.valueOf(duration)
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // Combine stdout and stderr
        Process process = processBuilder.start();

        String output = IOUtils.toString(process.getInputStream(), "UTF-8");
        System.out.println(output);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Attack execution failed with exit code: " + exitCode);
        }

        System.out.println("Attack started successfully: Type=" + attackType + ", Duration=" + duration + " seconds.");
    }
}
