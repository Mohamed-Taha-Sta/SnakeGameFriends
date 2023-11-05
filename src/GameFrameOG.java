import javax.swing.*;
public class GameFrameOG extends JFrame {


	GameFrameOG(){

		this.add(new GamePanelOG());

		this.setTitle("Snake");



		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setResizable(false);

		this.pack();

		this.setVisible(true);

		this.setLocationRelativeTo(null);

	}

}
