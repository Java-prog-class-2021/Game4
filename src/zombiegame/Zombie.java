package zombiegame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.Image;

public class Zombie {

	int width = 25;
	int height = 25;
	double posX;
	double posY;
	double speedX;
	double speedY;
	double angle;
	double damage = 8;
	double fullHealth = 50;
	double health = fullHealth;
	int zombieW;
	int zombieH;
	
	Image imgZombie;
	
	Zombie() {
		imgZombie = loadImage("zombie1.png");
		zombieW = imgZombie.getWidth(null);
		zombieH = imgZombie.getWidth(null);
	}
	Image loadImage(String filename) {
		Image image = null;
		URL imageURL = this.getClass().getResource("/" + filename);
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
		if (imgZombie == null) return;
		g.drawImage(imgZombie, (int)posX, (int)posY, zombieW, zombieH, null);
		
	}
}
