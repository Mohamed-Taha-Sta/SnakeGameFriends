import javax.swing.*;
import java.io.IOException;
import java.util.Vector;

public class GameFrame extends JFrame {


	GameFrame(Vector<Player> players) throws IOException {

		this.add(new GamePanel(players));

		this.setTitle("Snake Server");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setResizable(false);

		this.pack();

		this.setVisible(true);

		this.setLocationRelativeTo(null);

	}

}
