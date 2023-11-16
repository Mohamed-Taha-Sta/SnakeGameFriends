import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.stream.Collectors;

public class GameFramePlayer extends JFrame {


	GameFramePlayer(Vector<Player> players, int my_id, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {

		this.add(new GamePanelPlayer(players, my_id,in ,out));
		System.out.println("were in gameFrame player and this is input" + in);
//		this.setTitle("Snake Player: "+players.stream().filter(player -> player.getId() == my_id).findFirst().map(Player::getName).orElse(""));
		this.setTitle("Snake Player: "+players.stream().filter(player -> player.getId() == my_id).findFirst().map(Player::toString).get());

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setResizable(false);

		this.pack();

		this.setVisible(true);

		this.setLocationRelativeTo(null);
	}

}
