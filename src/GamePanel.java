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
import java.net.Socket;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener{

	private final Vector<Player> players;
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
	Map<Player, ObjectInputStream> playersIn = new HashMap<>();
	Map<Player, ObjectOutputStream> playersOut = new HashMap<>();

	Timer timer;

	Random random;

	public void init_players(){
		for(int i=0;i<players.size();i++){
			for (int j=0; j<GAME_UNITS;j++){
				if (i == 0){
					players.get(i).setStartColor(new Color(98, 190, 155));
					players.get(i).setEndColor(new Color(59, 146, 116));
					System.out.println("player 1 init");
				} else if (i == 1){
					players.get(i).x[j] = SCREEN_WIDTH;
					players.get(i).setDirection('L');
					players.get(i).setStartColor(new Color(98, 155, 190));
					players.get(i).setEndColor(new Color(59, 103, 146));
					System.out.println("player 2 init");
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

	GamePanel(Vector<Player> players) throws IOException {

		random = new Random();

		this.players = players;

		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));

		this.setFocusable(true);

		startGame();

//		for(Player player : players){
//			playersIn.put(player,new ObjectInputStream(player.getHandlerPlayerSocket().getInputStream()));
//			playersOut.put(player,new ObjectOutputStream(player.getHandlerPlayerSocket().getOutputStream()));
//		}

	}

	public void startGame() throws IOException {

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


		if(running) {

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

			for (int i=0; i<players.size();i++){
				g.drawString(players.get(i).getName()+" : "+players.get(i).getApplesEaten(), UNIT_SIZE , UNIT_SIZE * (i)+ 2*UNIT_SIZE);
			}

		}
		else {
			gameOver(g);
		}

	}

	public void newApple() throws IOException {
		do {
			appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
			appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
		} while (appleInObstacle() || !appleOutside());
		//Send signal of coords of new apple

//		for (Player player:players){
//			player.getOut().writeObject(appleX);
//			player.getOut().writeObject(appleY);
//		}
//
		for (int i=0; i<players.size();i++){
			players.get(i).getOut().writeObject(appleX);
			players.get(i).getOut().writeObject(appleY);
			System.out.println(i);
		}


//		for (var entry : playersOut.entrySet()){
//			entry.getValue().writeObject(appleX);
//			entry.getValue().writeObject(appleY);
//		}

	}

	public boolean appleInObstacle() {
		Rectangle appleRect = new Rectangle(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

		for (Rectangle obstacle : obstacles) {
			if (appleRect.intersects(obstacle)) {
				return true;
			}
		}
		return false;
	}

	public boolean appleOutside() {
		return appleX >= 0 && appleX < SCREEN_WIDTH && appleY >= 0 && appleY < SCREEN_HEIGHT;
	}


	public void sendMove() throws IOException {

		//Send Everyone's movement

//		for (Player player : players){
//			for (Player player1 : players){
//				player.getOut().writeObject(player1.getDirection());
//				System.out.println(player+" sending to "+player1);
//			}
//		}

		for (int i=0; i<players.size();i++){
			for(int j = 0;j<players.size();j++){
				players.get(i).getOut().writeObject(players.get(j).getDirection());
				System.out.println(players.get(i)+" sending to "+players.get(j));


			}
		}


//		Map<Socket,Character> socketDirectionMap = new HashMap<>();
//		for (Player player : players){
//			socketDirectionMap.put(player.getHandlerPlayerSocket(),player.getDirection());
//		}

//		MovementServ movementServ = new MovementServ(socketDirectionMap);

//		for (var entry : playersOut.entrySet()){
//			entry.getValue().writeObject(movementServ);
//		}

	}


	public void receiveMove() throws IOException, ClassNotFoundException {

		//Receive Everyone's movement

		for (int i = 0; i <players.size(); i++) {
			System.out.println(players.get(i).getName());
			players.get(i).setDirection((Character) players.get(i).getIn().readObject());
		}

//		for (var entry : playersIn.entrySet()){
//			entry.getKey().setDirection((Character) entry.getValue().readObject());
//		}

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

	public void checkApple() throws IOException {
		boolean appleEaten = false;
		for (int i=0;i<players.size();i++) {
			if ((players.get(i).x[0] == appleX) && (players.get(i).y[0] == appleY)) {
				newApple();
				appleEaten = true;
				players.get(i).setBodyParts(players.get(i).getBodyParts()+1);
				players.get(i).setApplesEaten(players.get(i).getApplesEaten()+1);
			}
		}

		System.out.println("ended up");
		for (int i=0; i<players.size();i++){
			if (!appleEaten) {
				for (int j = 0; j < players.size(); j++) {
					players.get(j).getOut().writeObject(appleX);
					players.get(j).getOut().writeObject(appleY);
				}
			}
			players.get(i).getOut().writeObject(players.get(i).getApplesEaten());
			players.get(i).getOut().writeObject(players.get(i).getBodyParts());
			System.out.println("ended end");
		}

//		Map<Socket, List<Integer>> playerStats = new HashMap<>();
//
//		for(Player player : players){
//			playerStats.put(player.getHandlerPlayerSocket(),Arrays.asList(player.getApplesEaten(),player.getBodyParts()));
//		}
//		for(var entry : playersOut.entrySet()){
//			entry.getValue().writeObject(new Stats(playerStats));
//		}

	}

	public void checkCollisions() {

		// Add collisions between players

		for (int j = 0; j < players.size(); j++) {

			//checks if head collides with body

			for (int i = players.get(j).getBodyParts(); i > 0; i--) {

				if ((players.get(j).x[0] == players.get(j).x[i]) && (players.get(j).y[0] == players.get(j).y[i])) {
					players.get(j).setRunning(false);
				}

			}

			//Check if head touches obstacle

			Rectangle snakeHead = new Rectangle(players.get(j).x[0], players.get(j).y[0], UNIT_SIZE, UNIT_SIZE);

			for (Rectangle obstacle : obstacles) {
				if (obstacle.intersects(snakeHead)) {
					players.get(j).setRunning(false);
				}
			}

		}
		boolean stillRunning = false;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isRunning())
				stillRunning = true;
		}

		if (!stillRunning) {
			running = false;
			timer.stop();
		}

		// Delete player
		// Send collision update
		// Send Player Update

	}

	public void gameOver(Graphics g) {

		obstacles.clear();
		repaint();
		//BestScore
		int bestScore = players.get(0).getApplesEaten();
		String name = players.get(0).getName();
		for (int i = 0; i<players.size();i++){
			if (players.get(i).getApplesEaten() > bestScore)
			{
				bestScore = players.get(i).getApplesEaten();
				name = players.get(i).getName();
			}
		}

		g.setColor(Color.WHITE);

		g.setFont( new Font("Roboto",Font.PLAIN, 40));

		FontMetrics metrics1 = getFontMetrics(g.getFont());

		g.drawString("Best Score: "+bestScore, (SCREEN_WIDTH - metrics1.stringWidth("Best Score: "+bestScore))/2, g.getFont().getSize());
		g.drawString("by: "+name, (SCREEN_WIDTH - metrics1.stringWidth("by: "+name))/2, UNIT_SIZE);

		//Game Over text

		g.setColor(Color.WHITE);
		g.setFont( new Font("Roboto",Font.PLAIN, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);

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
		if(running) {
			try {

				System.out.println("before anything action related ");
				receiveMove();
				System.out.println("after receive move");
				sendMove();
				checkApple();
				checkCollisions();

			} catch (IOException | ClassNotFoundException ex) {
				throw new RuntimeException(ex);
			}


		}
		repaint();
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
