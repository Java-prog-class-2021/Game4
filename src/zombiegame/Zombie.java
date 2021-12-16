package zombiegame;

public class Zombie {

	int width = 25;
	int height = 25;
	double posX;
	double posY;
	double speedX;
	double speedY;
	double angle;
	double damage = 8;
	
	Zombie(double posX, double posY, double speedX, double speedY) {
		
		this.posX = posX;
		this.posY = posY;
		this.speedX = speedX;
		this.speedY = speedY;
		
	}
	
}