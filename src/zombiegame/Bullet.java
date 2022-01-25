package zombiegame;

public class Bullet {

	int width = 6;
	double height = 6;
	double posX = 8;
	double posY;
	double speedX;
	double speedY;
	double damage = 20;
	
	Bullet(double posX, double posY, double speedX, double speedY) {
		
		this.posX = posX;
		this.posY = posY;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
}
