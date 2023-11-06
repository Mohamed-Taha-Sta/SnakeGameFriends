import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class GameFramePlayer extends JFrame {


	GameFramePlayer(Vector<Player> players, Player me, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {

		this.add(new GamePanelPlayer(players, me,in ,out));
		System.out.println("were in gameFrame player and this is input" + in);
		this.setTitle("Snake");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setResizable(false);

		this.pack();

		this.setVisible(true);

		this.setLocationRelativeTo(null);

	}

}
