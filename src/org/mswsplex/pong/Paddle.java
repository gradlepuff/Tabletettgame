package org.mswsplex.pong;
import java.awt.Color;
import java.awt.Graphics;

public class Paddle {
	private int height, width, x, y;
	private float xVel, yVel, maxYVel, maxXVel;

	private Color color;

	private Pong game;

	public Paddle(Pong game, Color color, int x, int height, int width) {
		this.height = height;
		this.width = width;
		xVel = 0;
		yVel = 0;
		maxXVel = 0;
		maxYVel = 10;

		this.game = game;

		y = game.getHeight() / 2 - (height * 1 / 4);
		this.x = x;

		this.color = color;
	}

	public void resetPosition() {
		xVel = 0;
		yVel = 0;
		y = game.getHeight() / 2 - (height * 1 / 4);
	}

	public void draw(Graphics g) {
		g.setColor(color);

		g.fillRect(x, y, width, height);
	}

	public void move() {
		y += yVel;
		if (y < 0) {
			y = 0;
		} else if (y > game.getHeight() - height) {
			y = game.getHeight() - height;
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = Math.min(Math.max(0, y), game.getHeight() - height);
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float getXVel() {
		return xVel;
	}

	public float getYVel() {
		return yVel;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setXVel(float xVel) {
		this.xVel = Math.min(Math.max(xVel, -maxXVel), maxXVel);
	}

	public void setYVel(float yVel) {
		this.yVel = Math.min(Math.max(yVel, -maxYVel), maxYVel);
	}

	public void addXVel(float xVel) {
		setXVel(this.xVel + xVel);
	}

	public void addYVel(float yVel) {
		setYVel(this.yVel + yVel);
	}
}


