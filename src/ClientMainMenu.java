import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ClientMainMenu extends JFrame {
    private JPanel mainPanel;
    private JPanel gamePanel;
    private JTextField nameField;
    private JTextField ipField;
    private JTextField portField;
    private JButton playButton;
    private JButton quitButton;
    private JButton backButton;
    private final Object lock = new Object();

    static final int SCREEN_WIDTH = 1300;
    static final int SCREEN_HEIGHT = 740;

    public ClientMainMenu() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        setResizable(false); // Make the window size non-adjustable
        getContentPane().setBackground(new Color(46, 48, 48)); // Set the background color of the window
        setLayout(new CardLayout());

        this.setTitle("Snake Game");

        ImageIcon img = new ImageIcon("assets/SnakeLogo.png");

        this.setIconImage(img.getImage());

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false); // Make the panel transparent
        mainPanel.add(Box.createVerticalGlue());

        JLabel gameLabel = new JLabel("Snake", SwingConstants.CENTER);
        gameLabel.setFont(new Font("Roboto", Font.BOLD, 48)); // Set the font to Roboto
        gameLabel.setForeground(Color.WHITE); // Set the font color to white
        gameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label horizontally
        mainPanel.add(gameLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false); // Make the panel transparent
        playButton = createButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to game panel
                ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "GamePanel");
            }
        });
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createVerticalGlue());

        quitButton = createButton("Quit");
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle quit button click
                System.exit(0);
            }
        });
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        buttonPanel.add(quitButton);
        buttonPanel.add(Box.createVerticalGlue());
        mainPanel.add(buttonPanel);

        mainPanel.add(Box.createVerticalGlue());

        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setOpaque(false); // Make the panel transparent

        gamePanel.add(Box.createVerticalGlue());

        gamePanel.add(createEntry("Name:", nameField = new JTextField(10)));
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some vertical space between the entries
        gamePanel.add(createEntry("IP Address:", ipField = new JTextField(10)));
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some vertical space between the entries
        gamePanel.add(createEntry("Port:", portField = new JTextField(10)));
        gamePanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add some vertical space between the entries

        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.Y_AXIS));
        buttonPanel2.setOpaque(false); // Make the panel transparent

        playButton = createButton("Play");
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle play button click
                String name = nameField.getText();
                String ipAddress = ipField.getText();
                int port = 4444;
                try {
                    port = Integer.parseInt(portField.getText());
                }catch (Exception ex){
                }
                // Use these values to connect to the server
                synchronized (lock) {
                    lock.notify();
                }
            }
        });
        playButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        buttonPanel2.add(Box.createVerticalGlue());
        buttonPanel2.add(playButton);
        buttonPanel2.add(Box.createVerticalGlue());

        backButton = createButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch back to main panel
                ((CardLayout)getContentPane().getLayout()).show(getContentPane(), "MainPanel");
            }
        });
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button horizontally
        buttonPanel2.add(backButton);
        buttonPanel2.add(Box.createVerticalGlue());

        gamePanel.add(buttonPanel2);

        gamePanel.add(Box.createVerticalGlue());

        getContentPane().add(mainPanel, "MainPanel");
        getContentPane().add(gamePanel, "GamePanel");
    }

    private JPanel createEntry(String labelText, JTextField textField) {
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));
        entryPanel.setOpaque(false); // Make the panel transparent

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE); // Set the font color to white
        label.setPreferredSize(new Dimension(100, 30)); // Set the preferred size of the label

        textField.setMaximumSize(new Dimension(100, 30)); // Set the size of the text field
        textField.setBackground(new Color(169, 169, 169)); // Set the background color of the text field to a shade of gray
        textField.setBorder(new RoundedBorder(1, 1, new Color(169, 169, 169))); // Set the border to a rounded border with a shade of gray

        entryPanel.add(Box.createHorizontalGlue());
        entryPanel.add(label);
        entryPanel.add(Box.createRigidArea(new Dimension(10, 0))); // Add some space between the label and the text field
        entryPanel.add(textField);
        entryPanel.add(Box.createHorizontalGlue());

        return entryPanel;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 24)); // Set the font to Roboto and make it larger
        button.setPreferredSize(new Dimension(400, 100));
        button.setMaximumSize(new Dimension(400, 100));
        button.setBackground(new Color(98, 190, 155));
        button.setBorder(new RoundedBorder(10, 5, new Color(59, 146, 116))); // 10 is the radius of the corner rounding, 5 is the border thickness
        button.setFocusPainted(false);
        return button;
    }

    // Custom border class to create rounded corners
    class RoundedBorder implements Border {
        private int radius;
        private int thickness;
        private Color color;

        RoundedBorder(int radius, int thickness, Color color) {
            this.radius = radius;
            this.thickness = thickness;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    public String[] getConnectionDetails() {
        String name = nameField.getText();
        String ipAddress = ipField.getText();
        String port = portField.getText();

        return new String[]{name, ipAddress, port};
    }

    public Object getLock() {
        return lock;
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ClientMainMenu().setVisible(true);
            }
        });
    }
}
