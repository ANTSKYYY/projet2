package moa.streams.generators.MasTackGen;

import moa.core.ObjectRepository;
import moa.tasks.TaskMonitor;
import moa.options.AbstractOptionHandler;
import com.github.javacliparser.FileOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;
import org.apache.commons.io.IOUtils;
import moa.streams.MasTackStream;
import moa.core.Example;

import com.yahoo.labs.samoa.instances.InstancesHeader;
import com.yahoo.labs.samoa.instances.Instances;
import com.yahoo.labs.samoa.instances.Instance;
import com.yahoo.labs.samoa.instances.DenseInstance;
import com.yahoo.labs.samoa.instances.Attribute;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * MasTackGen - A custom MOA generator that generates network attack streams.
 */
public class MasTackGen extends AbstractOptionHandler implements
        MasTackStream {

    private static final long serialVersionUID = 1L;
    private InstancesHeader datasetStructure;

    // Option for specifying the simulation directory
    public FileOption simulationDirectoryOption = new FileOption(
            "simulationDirectory",
            'd',
            "Path to the simulation directory.",
            "./simu",
            "",
            false
    );


    // Option for specifying attack type
    public StringOption attackTypeOption = new StringOption(
            "attack_type", 
            'a', 
            "Type of attack (SSH, DDoS UDP, MITM)", 
            "SSH"
    );

    // Option for specifying attack duration
    public IntOption durationOption = new IntOption(
            "duration", 
            't', 
            "Duration of the attack in seconds", 
            60
    );

    public MasTackGen() {
        super();
        datasetStructure = defineDatasetStructure();
    }

    private InstancesHeader defineDatasetStructure() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("feature1"));
        attributes.add(new Attribute("feature2"));
        attributes.add(new Attribute("class", Arrays.asList("attack", "normal")));
        InstancesHeader header = new InstancesHeader(new Instances("MasTackGenDataset", attributes, 0));
        header.setClassIndex(header.numAttributes() - 1);
        return header;
    }

    @Override
    public Example<Instance> nextInstance() {
        // unimplemented, return random values for compilation purposes
        double[] values = new double[datasetStructure.numAttributes()];
        values[0] = Math.random();
        values[1] = Math.random();
        values[2] = Math.random() > 0.5 ? 0 : 1;

        DenseInstance instance = new DenseInstance(1.0, values);
        instance.setDataset(datasetStructure);

        return new moa.core.InstanceExample(instance);
    }


    @Override
    public InstancesHeader getHeader() {
        return datasetStructure;
    }

    @Override
    public void prepareForUseImpl(TaskMonitor monitor, ObjectRepository repository) {
        monitor.setCurrentActivity("Starting Docker simulation...", -1.0);

        String simulationDir = this.simulationDirectoryOption.getValueAsCLIString();
        String attackType = this.attackTypeOption.getValue();
        int duration = this.durationOption.getValue();

        try {
            System.out.println("Starting Docker simulation in directory: " + simulationDir);
            System.out.println("Attack Type: " + attackType);
            System.out.println("Duration: " + duration + " seconds");

            startDockerSimulation(simulationDir, attackType, duration);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to start the Docker simulation.", e);
        }

        monitor.setCurrentActivity("Docker simulation started successfully.", 1.0);
    }

    /**
     * Starts the Docker simulation by invoking the docker-compose command.
     * Also triggers the attack with the specified attack type and duration.
     *
     * @param simulationDir path to the simulation directory.
     * @param attackType    the type of attack (e.g., SSH, DDoS UDP, MITM).
     * @param duration      the duration of the attack in seconds.
     * @throws IOException          If the process cannot be started.
     * @throws InterruptedException If the process is interrupted.
     */
    private void startDockerSimulation(String simulationDir, String attackType, int duration) throws IOException, InterruptedException {
        File simuDir = new File(simulationDir);
        if (!simuDir.exists() || !simuDir.isDirectory()) {
            throw new IllegalArgumentException("Simulation directory does not exist: " + simulationDir);
        }

        // Start Docker simulation (docker-compose up)
        String[] command = {
                "docker-compose",
                "-f",
                simulationDir + "/docker-compose.yml",
                "up",
                "--build",
                "-d"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(simuDir);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        String output = IOUtils.toString(process.getInputStream(), "UTF-8");
        System.out.println(output);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Docker simulation failed with exit code: " + exitCode);
        }

        // Now trigger the attack based on attack type and duration
        startAttack(attackType, duration);
    }

    /**
     * Triggers the attack based on the selected attack type and duration.
     *
     * @param attackType the type of attack (e.g., SSH, DDoS UDP, MITM).
     * @param duration   the duration of the attack in seconds.
     */
    private void startAttack(String attackType, int duration) {
        System.out.println("Starting attack: " + attackType + " for " + duration + " seconds...");

        String attackCommand = "docker exec -it attacker /attack_handler.sh " + attackType + " " + duration;
        try {
            Process process = new ProcessBuilder(attackCommand.split(" ")).start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to execute attack: " + e.getMessage());
        }
    }

    /**
     * Stops the Docker simulation by invoking the docker-compose down command.
     *
     * @param simulationDir path to the simulation directory.
     * @throws IOException          If the process cannot be started.
     * @throws InterruptedException If the process is interrupted.
     */
    private void stopDockerSimulation(String simulationDir) throws IOException, InterruptedException {
        File simuDir = new File(simulationDir);
        if (!simuDir.exists() || !simuDir.isDirectory()) {
            throw new IllegalArgumentException("Simulation directory does not exist: " + simulationDir);
        }

        // command to stop the simulation (docker-compose down)
        String[] command = {
                "docker-compose",
                "-f",
                simulationDir + "/docker-compose.yml",
                "down"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(simuDir);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        String output = IOUtils.toString(process.getInputStream(), "UTF-8");
        System.out.println(output);

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Failed to stop Docker simulation with exit code: " + exitCode);
        }
    }

    @Override
    public void restart() {
        String simulationDir = this.simulationDirectoryOption.getValueAsCLIString();

        try {
            System.out.println("Stopping Docker simulation...");
            stopDockerSimulation(simulationDir);

            System.out.println("Restarting Docker simulation...");
            startDockerSimulation(simulationDir, this.attackTypeOption.getValue(), this.durationOption.getValue());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to restart the Docker simulation.", e);
        }
    }

    @Override
    public void getDescription(StringBuilder sb, int indent) {
        sb.append("MasTackGen: A generator for simulating Docker-based attacks and testing MOA algorithms.");
    }
    
    @Override
    public boolean isRestartable() {
        return true;
    }

    @Override
    public long estimatedRemainingInstances() {
        return -1; // return -1 for now
    }

    @Override
    public boolean hasMoreInstances() {
        return true;
    }

    @Override
    public String getPurposeString() {
        return "MasTackGen: A generator for simulating Docker-based attacks and testing MOA algorithms.";
    }
}
