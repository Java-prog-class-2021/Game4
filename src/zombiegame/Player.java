package zombiegame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import zombiegame.Game.GamePanel;
import java.awt.Image;
import java.io.InputStream;
public class Player{
	
	int playerWidth = 25;
	int playerHeight = 25;
	double playerPosX;
	double playerPosY;
	double playerSpeedX;
	double playerSpeedY;
	double health = 50;
	int player1W;
	int player1H;
	
	Image imgPlayer1;
	
	Player(double playerPosX, double playerPosY, double playerSpeedX, double playerSpeedY) {
		this.playerPosX = playerPosX;
		this.playerPosY = playerPosY;
		this.playerSpeedX = playerSpeedX;
		this.playerSpeedY = playerSpeedY;
		imgPlayer1 = loadImage("pisotalposeOG_forward.png");
		player1W = imgPlayer1.getWidth(null);
		player1H = imgPlayer1.getHeight(null);
		
	}
	Image loadImage(String filename) {
		
		Image image = null;
		URL imageURL = this.getClass().getResource("/" + filename);
		//	InputStream inputStr = GamePanel.class.getClassLoader().getResourceAsStream(filename);
		if (imageURL != null) {
			ImageIcon icon = new ImageIcon(imageURL);
			image = icon.getImage();
		} else {
			JOptionPane.showMessageDialog(null, "An image failed to load: " + filename , "ERROR", JOptionPane.ERROR_MESSAGE);
		}
		return image;
	}
	
	public void draw(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		if (imgPlayer1 == null) return;
		g.drawImage(imgPlayer1, (int)playerPosX-12, (int)playerPosY-45, player1W, player1H, null);
	}
}
