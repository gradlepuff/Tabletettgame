package org.mswsplex.pong;

public class HEntry {
	private double x, y, vx, vy;

	private long age;

	public HEntry(double x, double y, double vx, double vy) {
		this.x = x;
		this.y = y;

//		if (vx > 0) {
//			this.vx = Math.min(.1, vx);
//		} else {
//			this.vx = Math.max(-.1, vx);
//		}
//
//		if (vy > 0) {
//			this.vy = Math.min(.1, vy);
//		} else {
//			this.vy = Math.max(-.1, vy);
//		}
		this.vx = -vx;
		this.vy = -vy;

		age = System.currentTimeMillis();
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void move() {

		long existed = getTimeExisted();
		double m = (.9999 / (1 - (1.0 / (existed + .01))));

		if (m < 1.009) {
			vx *= m;
			vy *= m;

			x += vx;
			y += vy;
		}
	}

	public double getVX() {
		return this.vx;
	}

	public double getVY() {
		return this.vy;
	}

	public long getTimeExisted() {
		return System.currentTimeMillis() - age;
	}

	public long getAge() {
		return age;
	}
}

