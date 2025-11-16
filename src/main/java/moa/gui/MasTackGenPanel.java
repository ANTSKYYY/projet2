package moa.gui;

import moa.gui.PreviewPanel.TypePanel;
import moa.streams.generators.MasTackGen.MasTackGen;
import com.github.javacliparser.FileOption;
import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MasTackGenPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private JComboBox<String> attackTypeComboBox;
    private JTextField durationTextField;
    private JButton startButton;

    private MasTackGen MasTackGen;

    public MasTackGenPanel() {
        setLayout(new BorderLayout());

        MasTackGen = new MasTackGen();

        attackTypeComboBox = new JComboBox<>(new String[] { "SSH", "DDoS UDP", "MITM" });
        durationTextField = new JTextField("60", 5);
        startButton = new JButton("Start Attack");

        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FlowLayout());

        optionsPanel.add(new JLabel("Attack Type:"));
        optionsPanel.add(attackTypeComboBox);
        optionsPanel.add(new JLabel("Duration (seconds):"));
        optionsPanel.add(durationTextField);
        optionsPanel.add(startButton);

        add(optionsPanel, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
    }

    /**
     * Starts the simulation with the selected attack type and duration.
     */
    private void startSimulation() {
        try {
            String attackType = (String) attackTypeComboBox.getSelectedItem();
            int duration = Integer.parseInt(durationTextField.getText());

            MasTackGen.attackTypeOption.setValue(attackType);
            MasTackGen.durationOption.setValue(duration);

            MasTackGen.prepareForUseImpl(null, null);

            JOptionPane.showMessageDialog(this, "Simulation started with attack: " + attackType + " for " + duration + " seconds.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error starting the simulation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
