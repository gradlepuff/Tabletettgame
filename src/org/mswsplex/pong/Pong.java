package org.mswsplex.pong;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.mswsplex.pong.buttons.Button;

public class Pong extends Frame implements Runnable, KeyListener, MouseListener, WindowListener {
	ThreadLocalRandom rnd;
	Thread thread;

	private Paddle hPaddle, cPaddle;
	private Ball ball;

	// Default resolution is 1000:500
	// Personal resolution is 1360:650

	public static final int WIDTH = 1000, HEIGHT = 500;

	// General font for most text, other text (subtext, descriptions, etc) should
	// scale appropriately
	public static final Font FONT = new Font("Consolas", Font.BOLD, 24);

	// The main menu's buttons' coordinates and dimensions
	int bWidth = WIDTH / 5, bHeight = HEIGHT / 10, buttonX = (int) (WIDTH * (5.0 / 10.0) - bWidth / 2);

	// Locations of where each of the 3 buttons are
	int singleY = (int) (HEIGHT * (2.0 / 10.0));
	int multiY = (int) (HEIGHT * (4.0 / 10.0));
	int aiY = (int) (HEIGHT * (6.0 / 10.0));

	// Paddle dimensions
	int pWidth = 70, pHeight = 15;

	// Dividing by 4 places the paddle in the appropriate place (no idea why
	// honestly)
	// int hPlayerX = (int) ((WIDTH * (19.0 / 20.0)) - (pWidth / 2.0));
	int hPlayerX = (int) (((WIDTH * (19.0 / 20.0))) - (pWidth / 4));

	// List of active paddles
	private Set<Paddle> paddles;

	private BufferedImage img; // Image used to buffer rendering into one frame
	private Graphics gfx;

	private int p1Score = 0, p2Score = 0;

	private int winner, hits;
	// Winner: temporary variable to capture which player just scored
	// Hits: number of volleys in current rally

	private Status status; // Game's status

	private long lastFpsTime, lastRpsTime;

	private double fps, lastFps, rps, lastRps; // Rallies Per Second

	private boolean mousePressed = false;

	private double prevMouseX, prevMouseY;

	private List<Button> buttons;

	/**
	 * Undecorated should be set to true otherwise certain pixels will be obstructed
	 * from the person's view
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Pong game = new Pong();
		game.setSize(WIDTH, HEIGHT);
		game.setUndecorated(true);
		game.setVisible(true);
		game.setLayout(new FlowLayout());
		game.setTitle("Pong");
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Pong() {
		hits = 0;
		rnd = ThreadLocalRandom.current();
		paddles = new HashSet<Paddle>();
		buttons = new ArrayList<>();

		lastFpsTime = System.currentTimeMillis();

		ball = new Ball(this, 15, 15);

		// Random Level AI

		// hPaddle = new Paddle(Color.WHITE, 950);
		// hPaddle = new AI(Color.WHITE, 950, ball, true, 50, 950,
		// Math.round(rnd.nextDouble() * 1000.0) / 1000.0);
		// cPaddle = new AI(Color.WHITE, 50, ball, false, 50, 950,
		// Math.round(rnd.nextDouble() * 1000.0) / 1000.0);

		// Basic AI
//		hPaddle = new AI(Color.WHITE, (int) (WIDTH * (19.0 / 20.0)), ball, true, (int) (WIDTH * (1.0 / 20.0)),
//				(int) (WIDTH * (19.0 / 20.0)), 0);
//		cPaddle = new AI(Color.WHITE, (int) (WIDTH * (1.0 / 20.0)), ball, true, (int) (WIDTH * (19.0 / 20.0)),
//				(int) (WIDTH * (1.0 / 20.0)), 0);

		// Expert Level AI

//		hPaddle = new AI(this, Color.WHITE, hPlayerX, pWidth, pHeight, ball, true, (int) (WIDTH * (1.0 / 20.0)),
//				hPlayerX, 1);
//		cPaddle = new AI(this, Color.WHITE, (int) (WIDTH * (1.0 / 20.0)), pWidth, pHeight, ball, false,
//				(int) (WIDTH * (1.0 / 20.0)), hPlayerX, 1);

		// Human
//		hPaddle = new Paddle(Color.WHITE, (int) (WIDTH * (19.0 / 20.0)));
//		cPaddle = new Paddle(Color.WHITE, (int) (WIDTH * (1.0 / 20.0)));

		img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		gfx = img.getGraphics();

		gfx.setFont(FONT);

		addButtonListener(new Button("Single Player", buttonX, singleY, bWidth, bHeight) {
			@Override
			public void onClick() {
				registerPlayers(1);
				start();
				super.onClick();
			}
		});
		addButtonListener(new Button("Multi Player", buttonX, multiY, bWidth, bHeight) {
			@Override
			public void onClick() {
				registerPlayers(2);
				start();
				super.onClick();
			}
		});
		addButtonListener(new Button("Self Player", buttonX, aiY, bWidth, bHeight) {
			@Override
			public void onClick() {
				registerPlayers(0);
				start();
				super.onClick();
			}
		});

		addKeyListener(this);
		addMouseListener(this);
		addWindowListener(this);

		thread = new Thread(this);
		thread.start();

		status = Status.MENU;
	}

	/**
	 * Assigns paddle variables and inserts them into paddles
	 * 
	 * @param human
	 * @throws IllegalArgumentException Thrown if human is not between 0-2
	 *                                  (inclusive)
	 */
	public void registerPlayers(int human) throws IllegalArgumentException {
		switch (human) {
		case 0:
			hPaddle = new AI(this, Color.WHITE, hPlayerX, pWidth, pHeight, ball, true, (int) (WIDTH * (1.0 / 20.0)),
					hPlayerX, 1);

			cPaddle = new AI(this, Color.WHITE, (int) (WIDTH * (1.0 / 20.0)), pWidth, pHeight, ball, false,
					(int) (WIDTH * (1.0 / 20.0)), hPlayerX, 1);
			break;
		case 1:
			cPaddle = new AI(this, Color.WHITE, (int) (WIDTH * (1.0 / 20.0)), pWidth, pHeight, ball, false,
					(int) (WIDTH * (1.0 / 20.0)), hPlayerX, 1);
			hPaddle = new Paddle(this, Color.WHITE, hPlayerX, pWidth, pHeight);
			break;
		case 2:
			cPaddle = new Paddle(this, Color.WHITE, (int) (WIDTH * (1.0 / 20.0)), pWidth, pHeight);
			hPaddle = new Paddle(this, Color.WHITE, hPlayerX, pWidth, pHeight);
			break;
		default:
			throw new IllegalArgumentException("Number must be between 0-2");
		}

		paddles.clear();

		paddles.add(cPaddle);
		paddles.add(hPaddle);
	}

	/**
	 * Registers a new button, the button's "onPress" will trigger automatically if
	 * registered
	 * 
	 * @param button
	 */
	public void addButtonListener(Button button) {
		buttons.add(button);
	}

	@Override
	public void paint(Graphics g) {

		drawBackground(gfx);

		if (status == Status.MENU) {
			drawButtons(gfx);

			g.drawImage(img, 0, 0, this);
			return;
		}

//		if (status == Status.START) {
//			long time = System.currentTimeMillis() - startTime;
//
//			gfx.setColor(Color.cyan);
//			gfx.setFont(FONT.deriveFont((float) ((float) 5 + Math.sin((float) time / 500.0f)) * 10));
//			gfx.drawString("Press Enter To Start", WIDTH / 2 - (gfx.getFont().getSize() * 5), HEIGHT / 2);
//			gfx.setFont(FONT);
//			g.drawImage(img, 0, 0, this);
//			return;
//		}

		drawLines(gfx);

		if (status == Status.SCORE) {
			gfx.setColor(Color.GREEN);
			gfx.drawString("Player " + winner + " Scored", (int) (WIDTH / 2.25), HEIGHT / 2);
			gfx.setFont(FONT.deriveFont(18f));
			gfx.drawString("Press Enter To Continue", (int) (WIDTH / 2.5), (int) (HEIGHT / 1.8));
			gfx.setFont(FONT);
		} else {
			drawBall(gfx);
			drawPaddles(gfx);

			if (status == Status.PAUSE) {
				gfx.setColor(Color.blue);
				gfx.setFont(FONT.deriveFont(FONT.getSize() * 2f));
				gfx.drawString("Game Paused", (int) (WIDTH / 2.6), HEIGHT / 2);
				gfx.setFont(FONT);
				gfx.drawString("Press Enter To Continue", (int) (WIDTH / 2.8f), (int) (HEIGHT / 1.8));
				gfx.setFont(FONT.deriveFont(FONT.getSize() * .5f));
				gfx.drawString("(ESC) To Exit (SPACE) Menu", (int) (WIDTH / 2.2f), (int) (HEIGHT / 1.7));

			}

		}

		int sm = manageTextAndScores(gfx);
		if (sm != 0) {
			status = Status.SCORE;
			hits = 0;
			rps = 0;
			lastRps = 0;
			lastRpsTime = System.currentTimeMillis();
			resetPositions();
			winner = sm;
		}

		g.drawImage(img, 0, 0, this);
	}

	public void start() {
		status = Status.RUNNING;
		hits = 0;
		rps = 0;
		lastRps = 0;
		lastRpsTime = System.currentTimeMillis();
		p1Score = 0;
		p2Score = 0;
		resetPositions();
	}

	public int getWidth() {
		return WIDTH;
	}

	public int getHeight() {
		return HEIGHT;
	}

	public Status getStatus() {
		return status;
	}

	/**
	 * Prints text including FPS, Scores, etc.
	 * 
	 * @param g
	 * @return Number (0 if none) of player that has scored
	 */
	private int manageTextAndScores(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(FONT.deriveFont(FONT.getSize() * .75f));
		g.drawString(lastFps + " FPS", 10, (int) (HEIGHT * (2.5 / 20.0)));
		g.setFont(FONT);

		g.setColor(Color.BLUE);
		g.drawString(p1Score + "", (int) (WIDTH * (1.0 / 4.0)), (int) (HEIGHT * (2.5 / 20.0)));
		if (cPaddle instanceof AI) {
			g.drawString("(" + ((AI) cPaddle).getSkill() + ")", (int) (WIDTH * (1.0 / 4.0)),
					(int) (HEIGHT * (3.5 / 20.0)));
		}

		g.setColor(Color.RED);
		g.drawString(p2Score + "", (int) (WIDTH * (3.0 / 4.0)), (int) (HEIGHT * (2.5 / 20.0)));
		if (hPaddle instanceof AI) {
			g.drawString("(" + ((AI) hPaddle).getSkill() + ")", (int) (WIDTH * (3.0 / 4.0)),
					(int) (HEIGHT * (3.5 / 20.0)));
		}

		g.setColor(Color.GRAY);
		g.drawString(hits + " (" + lastRps + ")", (int) (WIDTH / 2.1), HEIGHT / 10);

		g.setColor(Color.WHITE);

		if (ball.getX() <= 0) {
			p2Score++;
			return 2;
		} else if (ball.getX() >= WIDTH - ball.getWidth()) {
			p1Score++;
			return 1;
		}
		return 0;
	}

	public void resetPositions() {
		ball.reset();
		cPaddle.resetPosition();
		hPaddle.resetPosition();
	}

	public void drawBackground(Graphics g) {
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
	}

	public void drawLines(Graphics g) {
		int lineWidth = 2, lineHeight = 30, lineGap = 15;

		g.setColor(Color.WHITE);

		for (int y = 0; y < HEIGHT; y += lineHeight + lineGap) {
			g.fillRect(WIDTH / 2 - (lineWidth / 2), y, lineWidth, lineHeight);
		}
	}

	public void drawPaddles(Graphics g) {
		cPaddle.draw(g);
		hPaddle.draw(g);
	}

	public void drawBall(Graphics g) {
		ball.draw(g);
	}

	public void drawButtons(Graphics g) {
		buttons.forEach((b) -> b.draw(g));
	}

	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void run() {
		while (true) {
			if (status == Status.RUNNING) {
				cPaddle.move();
				hPaddle.move();
				ball.move();

				setTitle("Pong " + p1Score + " | " + p2Score);

				if (mousePressed && getMousePosition() != null) {
					double velX = getMousePosition().getX() - prevMouseX, velY = getMousePosition().getY() - prevMouseY;
					ball.setX((int) getMousePosition().getX() - ball.getWidth() / 2);
					ball.setY((int) getMousePosition().getY() - ball.getHeight() / 2);
					ball.setXVel(ball.getXVel() + velX / 10);
					ball.setYVel(ball.getYVel() + velY / 10);
					prevMouseX = getMousePosition().getX();
					prevMouseY = getMousePosition().getY();
				}

				if (ball.checkCollision(paddles)) {
					rps++;
					hits++;
				}

			}
			repaint();
			fps++;

			long frameSampleTime = 5000, rallySampleTime = 10000;

			if (System.currentTimeMillis() - lastFpsTime > frameSampleTime) {
				lastFps = (double) (fps / (frameSampleTime / 1000.0));
				fps = 0;
				lastFpsTime = System.currentTimeMillis();
			}

			if (System.currentTimeMillis() - lastRpsTime > rallySampleTime) {
				lastRps = ((double) rps / (rallySampleTime / 1000));
				rps = 0;
				lastRpsTime = System.currentTimeMillis();
			}

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent key) {

	}

	@Override
	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_UP:
			if (!(hPaddle instanceof AI))
				hPaddle.setYVel(-5);
			break;
		case KeyEvent.VK_DOWN:
			if (!(hPaddle instanceof AI))
				hPaddle.setYVel(5);
			break;
		case KeyEvent.VK_W:
			if (!(cPaddle instanceof AI))
				cPaddle.setYVel(-5);
			break;
		case KeyEvent.VK_S:
			if (!(cPaddle instanceof AI))
				cPaddle.setYVel(5);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			switch (status) {
//			case START:
//				status = Status.MENU;
//				break;
			case SCORE:
				ball.reset();
			case PAUSE:
				status = Status.RUNNING;
				break;
			case RUNNING:
				status = Status.PAUSE;
				break;
			default:
				break;
			}
			break;
		case KeyEvent.VK_SPACE:
			if (status == Status.PAUSE)
				status = Status.MENU;
			break;
		case KeyEvent.VK_ESCAPE:
			if (status != Status.PAUSE && status != Status.MENU)
				break;
			dispose();
			System.exit(0);
			break;
		}

		if (key.getKeyCode() == KeyEvent.VK_SPACE) {
			if (status == Status.PAUSE) {
				status = Status.MENU;
			}
		}

		if (!(cPaddle instanceof AI)) {
			if ((key.getKeyCode() == KeyEvent.VK_W || key.getKeyCode() == KeyEvent.VK_S)) {
				cPaddle.setYVel(0);
			}
		}

		if (!(key.getKeyCode() == KeyEvent.VK_UP || key.getKeyCode() == KeyEvent.VK_DOWN))
			return;

		hPaddle.setYVel(0);
	}

	@Override
	public void mouseClicked(MouseEvent e) { // Let go
		int x = e.getX(), y = e.getY();

		if (e.getButton() != MouseEvent.BUTTON1)
			return;

		buttons.stream().filter((button) -> button.contains(x, y)).forEach(Button::onClick);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (status != Status.RUNNING)
			return;
		ball.setXVel(0);
		ball.setYVel(0);
		prevMouseX = e.getX();
		prevMouseY = e.getY();
		mousePressed = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mousePressed = false;
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}
}


