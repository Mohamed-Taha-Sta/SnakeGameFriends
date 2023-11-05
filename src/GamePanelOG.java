import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class GamePanelOG extends JPanel implements ActionListener{

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

	Timer timer;

	Random random;

	

	GamePanelOG(){

		random = new Random();

		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));

		this.setFocusable(true);

		this.addKeyListener(new MyKeyAdapter());

		startGame();

	}

	public void startGame() {

		x = new int[GAME_UNITS];
		y = new int[GAME_UNITS];


		direction = 'R';
		applesEaten = 0;
		bodyParts = 4;

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

			Color startColor = new Color(98, 190, 155);
			Color endColor = new Color(59, 146, 116);

			// Calculate color interpolation for the gradient
			for (int i = 0; i < bodyParts; i++) {
				double ratio = (double) i / (double) (bodyParts - 1);
				int red = (int) (startColor.getRed() + ratio * (endColor.getRed() - startColor.getRed()));
				int green = (int) (startColor.getGreen() + ratio * (endColor.getGreen() - startColor.getGreen()));
				int blue = (int) (startColor.getBlue() + ratio * (endColor.getBlue() - startColor.getBlue()));
				Color gradientColor = new Color(red, green, blue);

				g.setColor(gradientColor);
				g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
			}


			g.setColor(Color.white);

			g.setFont( new Font("Roboto",Font.PLAIN, 40));

			FontMetrics metrics = getFontMetrics(g.getFont());

			g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());

		}
		else {
			gameOver(g);
		}

	}

	public void newApple(){
		do {
			appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
			appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
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

	public void move(){

		int prevX = x[0];
		int prevY = y[0];

		for(int i = bodyParts;i>0;i--) {

			x[i] = x[i-1];

			y[i] = y[i-1];

		}

		switch(direction) {

		case 'U':

			y[0] = y[0] - UNIT_SIZE;

			break;

		case 'D':

			y[0] = y[0] + UNIT_SIZE;

			break;

		case 'L':

			x[0] = x[0] - UNIT_SIZE;

			break;

		case 'R':

			x[0] = x[0] + UNIT_SIZE;

			break;

		}

		if (x[0] < 0) {
			x[0] = SCREEN_WIDTH - UNIT_SIZE;
		} else if (x[0] >= SCREEN_WIDTH) {
			x[0] = 0;
		}
		if (y[0] < 0) {
			y[0] = SCREEN_HEIGHT - UNIT_SIZE;
		} else if (y[0] >= SCREEN_HEIGHT) {
			y[0] = 0;
		}

		// Move the rest of the body parts
		for (int i = 1; i < bodyParts; i++) {
			// Swap positions with the previous body part
			int tempX = x[i];
			int tempY = y[i];
			x[i] = prevX;
			y[i] = prevY;
			prevX = tempX;
			prevY = tempY;
		}

		

	}

	public void checkApple() {

		if((x[0] == appleX) && (y[0] == appleY)) {

			bodyParts++;

			applesEaten++;

			newApple();

		}

	}

	public void checkCollisions() {

		//checks if head collides with body

		for(int i = bodyParts;i>0;i--) {

			if((x[0] == x[i])&& (y[0] == y[i])) {

				running = false;

			}

		}

		//Check if head touches obstacle

		Rectangle snakeHead = new Rectangle(x[0],y[0],UNIT_SIZE,UNIT_SIZE);

		for (Rectangle obstacle : obstacles) {
			if (obstacle.intersects(snakeHead)) {
				running = false;
			}
		}

		//check if head touches left border
//
//		if(x[0] < 0) {
//
//			running = false;
//
//		}
//
//		//check if head touches right border
//
//		if(x[0] > SCREEN_WIDTH) {
//
//			running = false;
//
//		}
//
//		//check if head touches top border
//
//		if(y[0] < 0) {
//
//			running = false;
//
//		}
//
//		//check if head touches bottom border
//
//		if(y[0] > SCREEN_HEIGHT) {
//
//			running = false;
//
//		}



		if(!running) {

			timer.stop();

		}

	}

	public void gameOver(Graphics g) {

		obstacles.clear();
		repaint();
		//Score

		g.setColor(Color.WHITE);

		g.setFont( new Font("Roboto",Font.PLAIN, 40));

		FontMetrics metrics1 = getFontMetrics(g.getFont());

		g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+applesEaten))/2, g.getFont().getSize());

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
			move();

			checkApple();

			checkCollisions();
		}
		repaint();
	}

	

	public class MyKeyAdapter extends KeyAdapter{

		@Override

		public void keyPressed(KeyEvent e) {

			switch(e.getKeyCode()) {

			case KeyEvent.VK_LEFT:

				if(direction != 'R') {

					direction = 'L';

				}

				break;

			case KeyEvent.VK_RIGHT:

				if(direction != 'L') {

					direction = 'R';

				}

				break;

			case KeyEvent.VK_UP:

				if(direction != 'D') {

					direction = 'U';

				}

				break;

			case KeyEvent.VK_DOWN:

				if(direction != 'U') {

					direction = 'D';

				}

				break;
			case KeyEvent.VK_R:

				if (!running) {
					startGame();
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
