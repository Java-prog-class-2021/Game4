package zombiegame;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import zombiegame.Game.GamePanel;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.io.InputStream;
public class Player{
	
	int playerWidth =34;
	int playerHeight = 34;
	double playerPosX;
	double playerPosY;
	double playerSpeedX;
	double playerSpeedY;
	double health = 50;
	
	Image imgPlayer1;
	
	Player(double playerPosX, double playerPosY, double playerSpeedX, double playerSpeedY) {
		this.playerPosX = playerPosX;
		this.playerPosY = playerPosY;
		this.playerSpeedX = playerSpeedX;
		this.playerSpeedY = playerSpeedY;
		imgPlayer1 = loadImage("pisotalposeOG_center.png");
		
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

	private AffineTransform transform = new AffineTransform();
	
	public void rotation(double rotAngle) {
        //add to movement in game

        transform = AffineTransform.getTranslateInstance(playerPosX, playerPosY);
        transform.rotate(rotAngle,imgPlayer1.getWidth(null)/2-35,imgPlayer1.getHeight(null)/2-35);

    }


    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON); //antialiasing

        if (imgPlayer1 == null) return;

        //    int player1W = imgPlayer1.getWidth(null);
        //    int player1H = imgPlayer1.getHeight(null);

        g2.setTransform(transform);
        g2.drawImage(imgPlayer1, (int)-35, (int)-35, null);
        g2.setTransform(new AffineTransform());

    }
}
