import javax.swing.*;
import java.io.IOException;
import java.util.Vector;

public class GameFramePlayer extends JFrame {


	GameFramePlayer(Vector<Player> players, Player me) throws IOException, ClassNotFoundException {

		this.add(new GamePanelPlayer(players, me));

		this.setTitle("Snake");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setResizable(false);

		this.pack();

		this.setVisible(true);

		this.setLocationRelativeTo(null);

	}

}
