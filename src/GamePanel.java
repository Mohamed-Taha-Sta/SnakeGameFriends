import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;

public class GamePanel extends JPanel implements ActionListener{

	private final Vector<Player> players;
	private int numObstacles = 8;
	static final int SCREEN_WIDTH = 1300;
	static final int SCREEN_HEIGHT = 740;
	static final int UNIT_SIZE = 20;
	static final int GAME_UNITS = (SCREEN_WIDTH*SCREEN_HEIGHT)/(UNIT_SIZE*UNIT_SIZE);
	static final int DELAY = 70;
	int level = 1;
	int appleX;
	int appleY;
	boolean running = false;
	boolean obstaclesPainted = false;
	private java.util.List<Rectangle> obstacles = new ArrayList<>();
	private java.util.List<Player> playersToRemove = new ArrayList<>();
	private final Map<Integer,Integer> playersScore = new HashMap<>();
	private final Map<Integer,String> playersNames = new HashMap<>();
	private Timer timer;
	private final Random random;

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

	GamePanel(Vector<Player> players) throws IOException {

		random = new Random();

		this.players = players;

		for (Player player : players) {
			playersScore.put(player.getId(),0);
		}

		for (Player player : players) {
			playersNames.put(player.getId(),player.getName());
		}


		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));

		this.setFocusable(true);

		startGame();
	}

	public void startGame() throws IOException {

		init_players();

		for (int i = 0; i < numObstacles; i++) {
			placeObstacle();
		}

		newApple();

		for (int i=0; i<players.size();i++){
			players.get(i).getOut().writeObject(appleX);
			players.get(i).getOut().writeObject(appleY);
		}

		for (Player player : players) {
			player.getOut().writeObject(obstacles.size());
			for (Rectangle obstacle : obstacles) {
				player.getOut().writeObject(obstacle);
			}
		}

		running = true;

		timer = new Timer(DELAY,this);

		timer.start();

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		setBackground(new Color(46, 48, 48));
		draw(g);

		for (Rectangle obstacle : obstacles) {
			g.setColor(Color.WHITE);
			g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
		}
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

	public void newApple() throws IOException {
		do {
			appleX = random.nextInt(SCREEN_WIDTH/UNIT_SIZE) * UNIT_SIZE;
			appleY = random.nextInt(SCREEN_HEIGHT/UNIT_SIZE) * UNIT_SIZE;
		} while (appleInObstacle() || !appleOutside());

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
		for (int i=0; i<players.size();i++){
			for(int j = 0;j<players.size();j++){
				players.get(i).getOut().writeObject(players.get(j).getDirection());
			}
		}
	}


	public void receiveMove() throws IOException, ClassNotFoundException {

		//Receive Everyone's movement

		for (int i = 0; i <players.size(); i++) {
			players.get(i).setDirection((Character) players.get(i).getIn().readObject());
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


	public void checkApple() throws IOException {

		boolean appleEaten = false;
		for (int i=0;i<players.size();i++) {

				if ((players.get(i).x[0] == appleX) && (players.get(i).y[0] == appleY)) {
					newApple();
					appleEaten = true;
					players.get(i).setBodyParts(players.get(i).getBodyParts()+1);
					players.get(i).setApplesEaten(players.get(i).getApplesEaten()+1);
					playersScore.put(players.get(i).getId(), playersScore.getOrDefault(players.get(i).getId(), 0) + 1);
				}

			players.get(i).getOut().writeObject(appleX);
			players.get(i).getOut().writeObject(appleY);


			for (int j=0;j<players.size();j++) {
				players.get(i).getOut().writeObject(players.get(j).getApplesEaten());
				players.get(i).getOut().writeObject(players.get(j).getBodyParts());
			}

		}

	}

	public void checkCollisions() throws IOException {



		// Add collisions between players
		for (Player player : players) {

			//checks if head collides with body
			for (int i = player.getBodyParts(); i > 0; i--) {
				if ((player.x[0] == player.x[i]) && (player.y[0] == player.y[i])) {
					player.setRunning(false);
					playersToRemove.add(player);
				}
			}
			//Check if head touches obstacle
			Rectangle snakeHead = new Rectangle(player.x[0], player.y[0], UNIT_SIZE, UNIT_SIZE);

			for (Rectangle obstacle : obstacles) {
				if (obstacle.intersects(snakeHead)) {
					player.setRunning(false);
					playersToRemove.add(player);
				}
			}
			// Check if head collides with other players
			for (Player otherPlayer : players) {
				if (otherPlayer != player) {
					Rectangle otherPlayerHead = new Rectangle(otherPlayer.x[0], otherPlayer.y[0], UNIT_SIZE, UNIT_SIZE);
					if (snakeHead.intersects(otherPlayerHead)) {
						player.setRunning(false);
						otherPlayer.setRunning(false);
						playersToRemove.add(player);
						playersToRemove.add(otherPlayer);
					}

					// Check if head collides with the body of other players
					for (int i = 1; i < otherPlayer.getBodyParts(); i++) {
						Rectangle otherPlayerBodyPart = new Rectangle(otherPlayer.x[i], otherPlayer.y[i], UNIT_SIZE, UNIT_SIZE);
						if (snakeHead.intersects(otherPlayerBodyPart)) {
							player.setRunning(false);
							playersToRemove.add(player);
						}
					}
				}
			}
		}

		boolean stillRunning = false;
		for (Player player : players) {
			if (player.isRunning())
				stillRunning = true;
		}

		if (!stillRunning) {
			running = false;
			timer.stop();
		}
	}

	private void playersDead() throws IOException {
		for(Player player : players){
			if(playersToRemove.stream().map(Player::getId).toList().contains(player.getId())){
				player.getOut().writeObject(true);
			}else{
				player.getOut().writeObject(false);
			}
		}
		players.removeAll(playersToRemove);
		playersToRemove.clear();
	}

	private void playersAlive() throws IOException {

		for (Player player : players){
			player.getOut().writeObject(players.size());
		}

		for (Player player : players){
			for (Player player1 : players){
				player.getOut().writeObject(player1.getId());
			}
		}
	}


	public void gameOver(Graphics g) {

		obstacles.clear();
		repaint();

		//BestScore
		Map.Entry<Integer, Integer> maxEntry = playersScore.entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
		assert maxEntry != null;
		int playerId = maxEntry.getKey();
		int bestScore = maxEntry.getValue();
		String name = playersNames.get(playerId);

		g.setColor(Color.WHITE);

		g.setFont( new Font("Roboto",Font.PLAIN, 40));

		FontMetrics metrics1 = getFontMetrics(g.getFont());

		g.drawString("Best Score: "+bestScore, (SCREEN_WIDTH - metrics1.stringWidth("Best Score: "+bestScore))/2, g.getFont().getSize());
		g.drawString("by: "+name, (SCREEN_WIDTH - metrics1.stringWidth("by: "+name))/2, g.getFont().getSize()+2*UNIT_SIZE);

		//Game Over text

		g.setColor(Color.WHITE);
		g.setFont( new Font("Roboto",Font.PLAIN, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game Over", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);


		//Escape Text

		g.setFont( new Font("Roboto",Font.PLAIN, 20));
		FontMetrics metrics4 = getFontMetrics(g.getFont());
		g.drawString("Press 'ESC' to Quit", (SCREEN_WIDTH - metrics4.stringWidth("Press 'ESC' to Quit"))/2, SCREEN_HEIGHT/2+4*UNIT_SIZE);


	}

	@Override

	public void actionPerformed(ActionEvent e) {
		if(running) {
			try {
				receiveMove();
				sendMove();
				checkApple();
				checkCollisions();
				playersDead();
				playersAlive();
			} catch (IOException | ClassNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		}
		repaint();
	}

	public void placeObstacle() {
		Random rand = new Random();
		obstacles.clear(); // Clear the previous obstacles
		int maxObstacles = 40; // Adjust this value to control the number of obstacles

		for (int i = 0; i < maxObstacles; i++) {
			int x = rand.nextInt(2,SCREEN_WIDTH/UNIT_SIZE-2); // Random x position
			int y = rand.nextInt(2,SCREEN_HEIGHT/UNIT_SIZE-2); // Random y position
			int width = UNIT_SIZE * (rand.nextInt(2) + 1); // Random width (between UNIT_SIZE and 5*UNIT_SIZE)
			int height = UNIT_SIZE * (rand.nextInt(2) + 1); // Random height (between UNIT_SIZE and 5*UNIT_SIZE)

			Rectangle obstacle = new Rectangle(x*UNIT_SIZE, y*UNIT_SIZE, width, height);
			obstacles.add(obstacle);
		}
	}


}
