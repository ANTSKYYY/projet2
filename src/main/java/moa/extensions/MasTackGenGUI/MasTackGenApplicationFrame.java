package extensions.MasTackGenUI;

import javax.swing.*;
import java.awt.*;

/**
 * MasTackGenApplicationFrame - Main application frame for interacting with MasTackGen stream generator.
 */
public class MasTackGenApplicationFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    public MasTackGenApplicationFrame() {
        super("MasTackGen Attack Generator");

        setLayout(new BorderLayout());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MasTackGenStreamPanel MasTackGenStreamPanel = new MasTackGenStreamPanel();
        add(MasTackGenStreamPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MasTackGenApplicationFrame::new);
    }
}
