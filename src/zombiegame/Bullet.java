package zombiegame;

public class Bullet {

	int width = 8; //was 7
	double height = 8;
	double posX = 8;
	double posY;
	double speedX;
	double speedY;
	
	Bullet(double posX, double posY, double speedX, double speedY) {
		
		this.posX = posX;
		this.posY = posY;
		this.speedX = speedX;
		this.speedY = speedY;
	}
	
}
