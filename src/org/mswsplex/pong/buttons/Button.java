package org.mswsplex.pong.buttons;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import org.mswsplex.pong.Pong;
public abstract class Button {
	private String text;

	private int x, y, width, height;

	private Color buttonColor, textColor;

	private Font font;

	public Button(String text, Font font, Color buttonColor, Color textColor, int x, int y, int width, int height) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.font = font;
		this.buttonColor = buttonColor;
		this.textColor = textColor;
	}

	public Button(String text, int x, int y, int width, int height) {
		this(text, Pong.FONT, x, y, width, height);
	}

	public Button(String text, Font font, int x, int y, int width, int height) {
		this(text, font, Color.WHITE, Color.BLACK, x, y, width, height);
	}

	public Button(String text, Font font, Color buttonColor, int x, int y, int width, int height) {
		this(text, font, buttonColor, Color.BLACK, x, y, width, height);
	}

	public void draw(Graphics g) {
		g.setColor(buttonColor);
		g.fillRect(x, y, width, height);
		g.setColor(textColor);
		g.setFont(font);
		g.drawString(text, x + width / 10, (int) (y + height / 1.5));
	}

	public boolean contains(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}

	public void onClick() {
	}
}


