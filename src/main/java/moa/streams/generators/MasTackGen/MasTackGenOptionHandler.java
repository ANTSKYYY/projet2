package moa.streams.generators.MasTackGen;

import com.github.javacliparser.IntOption;
import com.github.javacliparser.StringOption;

/**
 * This class is used to parse command-line arguments for MasTackGen.
 */
public class MasTackGenOptionHandler {

    public StringOption attackTypeOption = new StringOption("attack_type", 'a', "Type of attack (SSH, DDoS UDP, MITM)", "SSH");
    public IntOption durationOption = new IntOption("duration", 'd', "Duration of the attack in seconds", 60);

    public String getAttackType() {
        return attackTypeOption.getValue();
    }

    public int getDuration() {
        return durationOption.getValue();
    }

    public void setOptions(String[] options) {
        if (options.length > 0) {
            attackTypeOption.setValueViaCLIString(options[0]);
        }
        if (options.length > 1) {
            durationOption.setValueViaCLIString(options[1]);
        }
    }

    public String[] getOptions() {
        return new String[] {
            attackTypeOption.getValueAsCLIString(),
            durationOption.getValueAsCLIString()
        };
    }
}
