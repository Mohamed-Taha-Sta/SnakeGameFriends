import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class GamePanelPlayer extends JPanel implements ActionListener{

	private final Vector<Player> players;
	private final Player me;
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
			players.get(i).setBodyParts(6);
			players.get(i).setRunning(true);
		}
	}

	GamePanelPlayer(Vector<Player> players, Player me, ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
		System.out.println("were in gamePanel player and this is input" + in);

		random = new Random();

		this.players = players;

		this.me = me;

		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));

		this.setFocusable(true);

		this.addKeyListener(new MyKeyAdapter());

		startGame();

		this.out = out;
		this.in = in;

	}

	public void startGame() throws IOException, ClassNotFoundException {

		init_players();

		newApple();

		for (int i = 0; i < numObstacles; i++) {
			placeObstacle();
		}

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

//		paintObstacles(g);

	}

	public void paintObstacles(Graphics g){

		//Failed attempt at procedural generation:
//		g.setColor(Color.WHITE);
//		for (int i = 0; i < numObstacles; i++) {
//			g.fillRect(obstacleX[i], obstacleY[i], UNIT_SIZE, UNIT_SIZE);
//			int LENGTH_OF_OBSTACLE = random.nextInt(UNIT_SIZE*10)+UNIT_SIZE;
//			int DIRECTION_OF_OBSCTACLE = random.nextInt(8);
//			for (int j = 0; j < LENGTH_OF_OBSTACLE; j=j+UNIT_SIZE) {
//				if (DIRECTION_OF_OBSCTACLE == 0){
//					g.fillRect(obstacleX[i]-UNIT_SIZE, obstacleY[i]+UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
//				} else if (DIRECTION_OF_OBSCTACLE == 1) {
//					g.fillRect(obstacleX[i], obstacleY[i]+UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
//				} else if (DIRECTION_OF_OBSCTACLE == 2) {
//					g.fillRect(obstacleX[i]+UNIT_SIZE, obstacleY[i]+UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
//				} else if (DIRECTION_OF_OBSCTACLE == 3) {
//					g.fillRect(obstacleX[i]+UNIT_SIZE, obstacleY[i], UNIT_SIZE, UNIT_SIZE);
//				} else if (DIRECTION_OF_OBSCTACLE == 4) {
//					g.fillRect(obstacleX[i]+UNIT_SIZE, obstacleY[i]-UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
//				} else if (DIRECTION_OF_OBSCTACLE == 5) {
//					g.fillRect(obstacleX[i], obstacleY[i]-UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
//				} else if (DIRECTION_OF_OBSCTACLE == 6) {
//					g.fillRect(obstacleX[i]-UNIT_SIZE, obstacleY[i]-UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
//				} else {
//					g.fillRect(obstacleX[i]-UNIT_SIZE, obstacleY[i], UNIT_SIZE, UNIT_SIZE);
//				}
//			}
//		}
	}

	public void draw(Graphics g) {

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


		if(me.isRunning()) {

			g.setColor(new Color(217,84,60));
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

			Color startColor = new Color(98, 190, 155);
			Color endColor = new Color(59, 146, 116);

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

			for (int i=0; i<players.size();i++){
				g.drawString(players.get(i).getName()+" : "+players.get(i).getApplesEaten(), UNIT_SIZE , UNIT_SIZE * (i)+ 2*UNIT_SIZE);
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


	public void sendMove() throws IOException {
		out.writeObject(me.getDirection());
	}

	public void ReceiveMove() throws IOException, ClassNotFoundException {

		for (int i = 0; i<players.size();i++){
			players.get(i).setDirection((Character) in.readObject());
		}

//		Map<Socket, Character> movementServ = ((MovementServ) in.readObject()).mouvements;
//
//		for (int j = 0; j < players.size(); j++) {
//			for (var entry : movementServ.entrySet()) {
//				if (entry.getKey() == players.get(j).getPlayerSocket()) {
//					int prevX = players.get(j).x[0];
//					int prevY = players.get(j).y[0];
//
//					for (int i = players.get(j).getBodyParts(); i > 0; i--) {
//
//						players.get(j).x[i] = players.get(j).x[i - 1];
//
//						players.get(j).y[i] = players.get(j).y[i - 1];
//
//					}
//
//					switch (players.get(j).getDirection()) {
//
//						case 'U':
//
//							players.get(j).y[0] = players.get(j).y[0] - UNIT_SIZE;
//
//							break;
//
//						case 'D':
//
//							players.get(j).y[0] = players.get(j).y[0] + UNIT_SIZE;
//
//							break;
//
//						case 'L':
//
//							players.get(j).x[0] = players.get(j).x[0] - UNIT_SIZE;
//
//							break;
//
//						case 'R':
//
//							players.get(j).x[0] = players.get(j).x[0] + UNIT_SIZE;
//
//							break;
//
//					}
//
//					if (players.get(j).x[0] < 0) {
//						players.get(j).x[0] = SCREEN_WIDTH - UNIT_SIZE;
//					} else if (players.get(j).x[0] >= SCREEN_WIDTH) {
//						players.get(j).x[0] = 0;
//					}
//					if (players.get(j).y[0] < 0) {
//						players.get(j).y[0] = SCREEN_HEIGHT - UNIT_SIZE;
//					} else if (players.get(j).y[0] >= SCREEN_HEIGHT) {
//						players.get(j).y[0] = 0;
//					}
//
//					// Move the rest of the body parts
//					for (int i = 1; i < players.get(j).getBodyParts(); i++) {
//						// Swap positions with the previous body part
//						int tempX = players.get(j).x[i];
//						int tempY = players.get(j).y[i];
//						players.get(j).x[i] = prevX;
//						players.get(j).y[i] = prevY;
//						prevX = tempX;
//						prevY = tempY;
//					}
//				}
//			}
//		}
	}
	public void checkApple() throws IOException, ClassNotFoundException {

		newApple();

		for(int i = 0; i<players.size();i++){
			players.get(i).setApplesEaten((Integer) in.readObject());
			players.get(i).setBodyParts((Integer) in.readObject());
		}


//		Map<Socket, java.util.List<Integer>> playerStats = ((Stats) in.readObject()).getPlayerStats();
//		for (int i=0; i<players.size();i++){
//			for (var entry : playerStats.entrySet()){
//				if (players.get(i).getPlayerSocket() == entry.getKey()){
//					players.get(i).setApplesEaten(entry.getValue().get(0));
//					players.get(i).setBodyParts(entry.getValue().get(1));
//				}
//			}
//		}
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

		//Restart text

		g.setFont( new Font("Roboto",Font.PLAIN, 20));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press 'R' to Restart", (SCREEN_WIDTH - metrics3.stringWidth("Press 'R' to Restart"))/2, SCREEN_HEIGHT/2+2*UNIT_SIZE);

		//Escape Text

		g.setFont( new Font("Roboto",Font.PLAIN, 20));
		FontMetrics metrics4 = getFontMetrics(g.getFont());
		g.drawString("Press 'ESC' to Quit", (SCREEN_WIDTH - metrics4.stringWidth("Press 'ESC' to Quit"))/2, SCREEN_HEIGHT/2+4*UNIT_SIZE);


	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(me.isRunning()) {
			try {
				sendMove();
				ReceiveMove();
				checkApple();
			} catch (IOException | ClassNotFoundException ex) {
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
			case KeyEvent.VK_R:

				if (!me.isRunning()) {
					try {
						startGame();
					} catch (IOException | ClassNotFoundException ex) {
						throw new RuntimeException(ex);
					}
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
