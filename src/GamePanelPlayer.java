import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class GamePanelPlayer extends JPanel implements ActionListener{

	private final Vector<Player> players;
	private Player me;
	int[] x;
	int[] y;
	private int numObstacles = 8;
	static final int SCREEN_WIDTH = 1300;

	static final int SCREEN_HEIGHT = 740;

	static final int UNIT_SIZE = 20;

	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);

	static final int DELAY = 70;

	int level = 1;
	int bodyParts = 4;
	int applesEaten;
	int appleX;
	int appleY;
	char direction = 'R';
	boolean running = false;
	boolean obstaclesPainted = false;
	private java.util.List<Rectangle> obstacles = new ArrayList<>();
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private final Map<Integer,Integer> playersScore = new HashMap<>();
	private final Map<Integer,String> playersNames = new HashMap<>();

	Timer timer;

	Random random;

	public void init_players(){
		for(int i=0;i<players.size();i++){
			for (int j=0; j<GAME_UNITS;j++){
				if (i == 0){
					players.get(i).setStartColor(new Color(98, 190, 155));
					players.get(i).setEndColor(new Color(59, 146, 116));
				} else if (i == 1){
					players.get(i).x[j] = SCREEN_WIDTH;
					players.get(i).setDirection('L');
					players.get(i).setStartColor(new Color(98, 155, 190));
					players.get(i).setEndColor(new Color(59, 103, 146));
				} else if (i == 2) {
					players.get(i).y[j] = SCREEN_HEIGHT-UNIT_SIZE;
					players.get(i).setStartColor(new Color(164, 98, 190));
					players.get(i).setEndColor(new Color(117, 59, 146));
				} else if (i == 3){
					players.get(i).y[j] = SCREEN_HEIGHT-UNIT_SIZE;
					players.get(i).x[j] = SCREEN_WIDTH;
					players.get(i).setDirection('L');
					players.get(i).setStartColor(new Color(190, 172, 98));
					players.get(i).setEndColor(new Color(146, 121, 59));
				}
			}
			players.get(i).setApplesEaten(0);
			players.get(i).setBodyParts(4);
			players.get(i).setRunning(true);
		}
	}

	GamePanelPlayer(Vector<Player> players, int my_id, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {

		this.in = in;
		this.out = out;

		random = new Random();

		this.players = players;

		for (int i=0;i<players.size();i++){
			if (players.get(i).getId() == my_id)
			{
				this.me = players.get(i);
			}
		}


		for (Player player : players) {
			playersScore.put(player.getId(),0);
		}

		for (Player player : players) {
			playersNames.put(player.getId(),player.getName());
		}

		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));

		this.setFocusable(true);

		this.addKeyListener(new MyKeyAdapter());


		startGame();


	}

	public void startGame() throws IOException, ClassNotFoundException {

		init_players();

		newApple();

		int obsNumber = (Integer) in.readObject();

		for (int i = 0; i<obsNumber; i++){
			obstacles.add((Rectangle)in.readObject());
		}

//
//		for (int i = 0; i < numObstacles; i++) {
//			placeObstacle();
//		}



		running = true;

		timer = new Timer(DELAY,this);

		timer.start();

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		setBackground(new Color(46,48,48));
		draw(g);

		for (Rectangle obstacle : obstacles) {
			g.setColor(Color.WHITE);
			g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}
	}

	public void draw(Graphics g) {

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if(me.isRunning()) {

			g.setColor(new Color(217,84,60));
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);


			// Calculate color interpolation for the gradient
			for (Player player : players) {
				for (int i = 0; i < player.getBodyParts(); i++) {
					double ratio = (double) i / (double) (player.getBodyParts() - 1);
					int red = (int) (player.getStartColor().getRed() + ratio * (player.getEndColor().getRed() - player.getStartColor().getRed()));
					int green = (int) (player.getStartColor().getGreen() + ratio * (player.getEndColor().getGreen() - player.getStartColor().getGreen()));
					int blue = (int) (player.getStartColor().getBlue() + ratio * (player.getEndColor().getBlue() - player.getStartColor().getBlue()));
					Color gradientColor = new Color(red, green, blue);

					g.setColor(gradientColor);
					g.fillRect(player.x[i], player.y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}

			Color semiTransparentColor = new Color(173, 173, 173, 115);
			g.setColor(semiTransparentColor);
			g.fillRect(0, 0, UNIT_SIZE*7, UNIT_SIZE*6);

			g.setColor(Color.white);
			g.setFont( new Font("Roboto",Font.PLAIN, 18));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("ScoreBoard: ", 0, g.getFont().getSize());

			for (Map.Entry<Integer, Integer> entry : playersScore.entrySet()) {
				int playerId = entry.getKey();
				int score = entry.getValue();
				String playerName = playersNames.get(playerId);
				g.drawString(playerName+" : "+score, UNIT_SIZE , UNIT_SIZE * (playerId-1)+ 2*UNIT_SIZE);

			}
		}
		else {
			gameOver(g);
		}

	}

	public void newApple() throws IOException, ClassNotFoundException {
		appleX = (Integer) in.readObject();
		appleY = (Integer) in.readObject();
	}


	public void sendMove() throws IOException, InterruptedException {

		out.writeObject(me.getDirection());

	}

	public void ReceiveMove() throws IOException, ClassNotFoundException {

		for (int i = 0; i<players.size();i++){
			players.get(i).setDirection((Character) in.readObject());
		}

		for (int j=0; j<players.size();j++) {
			int prevX = players.get(j).x[0];
			int prevY = players.get(j).y[0];

			for (int i = players.get(j).getBodyParts(); i > 0; i--) {

				players.get(j).x[i] = players.get(j).x[i - 1];

				players.get(j).y[i] = players.get(j).y[i - 1];

			}

			switch (players.get(j).getDirection()) {

				case 'U':

					players.get(j).y[0] = players.get(j).y[0] - UNIT_SIZE;

					break;

				case 'D':

					players.get(j).y[0] = players.get(j).y[0] + UNIT_SIZE;

					break;

				case 'L':

					players.get(j).x[0] = players.get(j).x[0] - UNIT_SIZE;

					break;

				case 'R':

					players.get(j).x[0] = players.get(j).x[0] + UNIT_SIZE;

					break;

			}

			if (players.get(j).x[0] < 0) {
				players.get(j).x[0] = SCREEN_WIDTH - UNIT_SIZE;
			} else if (players.get(j).x[0] >= SCREEN_WIDTH) {
				players.get(j).x[0] = 0;
			}
			if (players.get(j).y[0] < 0) {
				players.get(j).y[0] = SCREEN_HEIGHT - UNIT_SIZE;
			} else if (players.get(j).y[0] >= SCREEN_HEIGHT) {
				players.get(j).y[0] = 0;
			}

			// Move the rest of the body parts
			for (int i = 1; i < players.get(j).getBodyParts(); i++) {
				// Swap positions with the previous body part
				int tempX = players.get(j).x[i];
				int tempY = players.get(j).y[i];
				players.get(j).x[i] = prevX;
				players.get(j).y[i] = prevY;
				prevX = tempX;
				prevY = tempY;
			}
		}

	}
	public void checkApple() throws IOException, ClassNotFoundException {

		newApple();
		for(int i = 0; i < players.size(); i++){
			int oldApplesEaten = players.get(i).getApplesEaten();
			int oldBodyParts = players.get(i).getBodyParts();

			players.get(i).setApplesEaten((Integer) in.readObject());
			players.get(i).setBodyParts((Integer) in.readObject());

			if (players.get(i).getApplesEaten() != oldApplesEaten || players.get(i).getBodyParts() != oldBodyParts) {
				playersScore.put(players.get(i).getId(), playersScore.getOrDefault(players.get(i).getId(), 0) + 1);
			}
		}

	}




	public void gameOver(Graphics g) {

		obstacles.clear();
		repaint();

		g.setColor(Color.WHITE);

		g.setFont( new Font("Roboto",Font.PLAIN, 40));

		FontMetrics metrics1 = getFontMetrics(g.getFont());

		g.drawString("Your Score: "+me.getApplesEaten(), (SCREEN_WIDTH - metrics1.stringWidth("Your Score: "+me.getApplesEaten()))/2, g.getFont().getSize());

		//Game Over text

		g.setColor(Color.WHITE);
		g.setFont( new Font("Roboto",Font.PLAIN, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over "+me.getName(), (SCREEN_WIDTH - metrics2.stringWidth("Game Over "+me.getName()))/2, SCREEN_HEIGHT/2);

		//Better Luck text

		g.setFont( new Font("Roboto",Font.PLAIN, 20));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Better Luck Next Time!", (SCREEN_WIDTH - metrics3.stringWidth("Better Luck Next Time!"))/2, SCREEN_HEIGHT/2+2*UNIT_SIZE);

		//Escape Text

		g.setFont( new Font("Roboto",Font.PLAIN, 20));
		FontMetrics metrics4 = getFontMetrics(g.getFont());
		g.drawString("Press 'ESC' to Quit", (SCREEN_WIDTH - metrics4.stringWidth("Press 'ESC' to Quit"))/2, SCREEN_HEIGHT/2+4*UNIT_SIZE);

	}

	private void playersAlive() throws IOException, ClassNotFoundException {

		java.util.List<Integer> alive = new ArrayList<>(); // list of Ids that are alive

		int numberPlayers = (Integer)in.readObject();

		for (int i = 0; i < numberPlayers; i++) {
			alive.add((Integer) in.readObject());
		}

		players.removeIf(player -> !alive.contains(player.getId()));

	}

	private boolean playersDead() throws IOException, ClassNotFoundException {
		boolean AmIDead = (Boolean) in.readObject();
		if (AmIDead){
			me.setRunning(false);
			timer.stop();
			return true;
		}
		return false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(me.isRunning()) {
			try {
				sendMove();
				ReceiveMove();
				checkApple();
				boolean deadplayer = playersDead();
				if (!deadplayer) playersAlive();
			} catch (IOException | ClassNotFoundException | InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
		repaint();
	}




	public class MyKeyAdapter extends KeyAdapter{

		@Override

		public void keyPressed(KeyEvent e) {

			switch(e.getKeyCode()) {

			case KeyEvent.VK_LEFT:

				if(me.getDirection() != 'R') {

					me.setDirection('L');

				}

				break;

			case KeyEvent.VK_RIGHT:

				if(me.getDirection() != 'L') {

					me.setDirection('R');

				}

				break;

			case KeyEvent.VK_UP:

				if(me.getDirection() != 'D') {

					me.setDirection('U');

				}

				break;

			case KeyEvent.VK_DOWN:

				if(me.getDirection() != 'U') {

					me.setDirection('D');

				}

				break;

			case KeyEvent.VK_ESCAPE:

				System.exit(0);
				break;
			}



		}

	}

	public void placeObstacle() {
		// Add obstacles for the current level
		if (level == 1) {
			obstacles.add(new Rectangle(100, 260, 100, 20));
			obstacles.add(new Rectangle(300, 260, 100, 20));
			obstacles.add(new Rectangle(500, 260, 100, 20));
			obstacles.add(new Rectangle(200, 140, 100, 20));
			obstacles.add(new Rectangle(400, 140, 100, 20));
			obstacles.add(new Rectangle(300, 40, 100, 20));
			obstacles.add(new Rectangle(100, 340, 40, 20));
			obstacles.add(new Rectangle(500, 340, 40, 20));
			obstacles.add(new Rectangle(260, 40, 20, 20));
			obstacles.add(new Rectangle(460, 20, 20, 20));
			obstacles.add(new Rectangle(100, 620, 100, 20));
			obstacles.add(new Rectangle(300, 620, 100, 20));
			obstacles.add(new Rectangle(500, 680, 100, 20));
			obstacles.add(new Rectangle(200, 580, 100, 20));
			obstacles.add(new Rectangle(400, 560, 100, 20));
			obstacles.add(new Rectangle(300, 460, 100, 20));
			obstacles.add(new Rectangle(100, 760, 20, 20));
			obstacles.add(new Rectangle(500, 780, 20, 120));
			obstacles.add(new Rectangle(260, 480, 20, 20));
			obstacles.add(new Rectangle(460, 480, 20, 20));
			obstacles.add(new Rectangle(600, 260, 100, 20));
			obstacles.add(new Rectangle(900, 260, 100, 20));
			obstacles.add(new Rectangle(1000, 260, 100, 20));
			obstacles.add(new Rectangle(800, 140, 100, 20));
			obstacles.add(new Rectangle(800, 140, 100, 120));
			obstacles.add(new Rectangle(600, 40, 100, 20));
			obstacles.add(new Rectangle(700, 340, 40, 20));
			obstacles.add(new Rectangle(1000, 340, 40, 20));
			obstacles.add(new Rectangle(560, 40, 20, 20));
			obstacles.add(new Rectangle(760, 20, 20, 20));
			obstacles.add(new Rectangle(1000, 620, 100, 20));
			obstacles.add(new Rectangle(700, 620, 100, 20));
			obstacles.add(new Rectangle(1000, 680, 100, 20));
			obstacles.add(new Rectangle(700, 580, 100, 20));
			obstacles.add(new Rectangle(600, 560, 100, 20));
			obstacles.add(new Rectangle(1000, 460, 20, 80));
			obstacles.add(new Rectangle(800, 760, 20, 20));
			obstacles.add(new Rectangle(600, 780, 20, 20));
			obstacles.add(new Rectangle(760, 480, 20, 80));
			obstacles.add(new Rectangle(960, 480, 20, 60));


		} else if (level == 2) {
			obstacles.add(new Rectangle(100, 100, 50, 50));
			obstacles.add(new Rectangle(200, 200, 50, 50));
		} else if (level == 3) {
			obstacles.add(new Rectangle(100, 100, 50, 50));
			obstacles.add(new Rectangle(200, 200, 50, 50));
			obstacles.add(new Rectangle(300, 300, 50, 50));
		} else if (level == 4) {
			// Add more obstacles for level 4
		} else if (level == 5) {
			// Add even more obstacles for level 5
		}
	}

}
