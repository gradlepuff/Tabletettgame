package org.mswsplex.pong;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
public class AI extends Paddle{
	private Ball ball;

	private List<Double> prevY;

	private int minX, maxX;

	private boolean onRight, lastRight;

	private double skill; 

	private Pong game;

	public AI(Pong game, Color color, int x, int height, int width, Ball ball, boolean onRight, int minX, int maxX,
			double skill) {
		super(game, color, x, height, width);
		this.ball = ball;
		this.minX = minX;
		this.maxX = maxX;
		this.onRight = onRight;
		this.skill = skill;
		this.lastRight = false;
		this.game = game;

		prevY = new ArrayList<>();
	}

	@Override
	public void move() {
		double estY = ball.getY(), tmpX = ball.getX(), tmpY = ball.getY(),
				tmpVX = ball.getXVel() * (3 + ((1 - skill) * 40.0)),
				tmpVY = ball.getYVel() * (3 + ((1 - skill) * 40.0));

		if (skill == 0) {
			if (Math.abs(this.getY() + this.getHeight() / 2 - (ball.getY() + ball.getHeight() / 2)) > 10) {
				if (this.getY() + this.getHeight() / 2 < ball.getY() + ball.getHeight() / 2) {
					this.setYVel(3);
				} else {
					this.setYVel(-3);
				}
			} else {
				this.setYVel(0);
			}

			super.move();
			return;
		}

		boolean est = false;

		ThreadLocalRandom rnd = ThreadLocalRandom.current();

		int amo = 0;

		// Clear Right Paddle History
		if (ball.getXVel() > 0 && !lastRight && onRight) {
			prevY.clear();
		}

		// Clear Left Paddle History
		if (ball.getXVel() < 0 && lastRight && !onRight) {
			prevY.clear();
		}

		int max = (int) (500 + (skill * 100.0));

		if (Math.abs(tmpVX) >= 0.00001)
			while (!est && amo < max) {
				if (onRight) {
					if (tmpX >= this.getX() - ball.getWidth()) {
						estY = tmpY;
						est = true;
					}
				} else {
					if (tmpX <= this.getX() + this.getWidth()) {
						estY = tmpY;
						est = true;
					}
				}

				if (tmpX >= maxX || tmpX <= minX) {
					tmpVX = -tmpVX;
				}

				if (tmpY <= 0) {
					tmpY = 0;
					tmpVY = -tmpVY;
				}

				if (tmpY > game.getHeight() - ball.getHeight()) {
					tmpY = game.getHeight() - ball.getHeight();
					tmpVY = -tmpVY;
				}

				tmpX += tmpVX + rnd.nextDouble((-(1 - skill)) * 5, (1.01 - skill) * 5);
				tmpY += tmpVY + rnd.nextDouble((-(1 - skill)) * 5, (1.01 - skill) * 5);
				amo++;
			}

		estY += ball.getHeight() / 2;
		estY -= this.getHeight() / 2;

		prevY.add(estY);

		int avgSize = (int) (50 - ((1 - skill) * 45));

		if (prevY.size() > avgSize)
			for (int i = 0; i < avgSize && i < prevY.size(); i++) {
				prevY.remove(i);
			}

		double avgY = 0;

		for (double d : prevY)
			avgY += d;

		avgY /= prevY.size();

		double dist = Math.abs(getY() - avgY);

		if (dist > 4.5 + (1 - skill) * 50.0) {
			if (getY() > avgY) {
				setYVel((float) (-3 - (skill * 1.5)));
			} else {
				setYVel((float) (3 + (skill * 1.5)));
			}
		} else {
			setYVel(0);
		}

		lastRight = ball.getXVel() > 0;
		super.move();
	}

	public double getSkill() {
		return this.skill;
	}

}


