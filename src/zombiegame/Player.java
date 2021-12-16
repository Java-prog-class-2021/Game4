package zombiegame;

public class Player {

	int playerWidth = 25;
	int playerHeight = 25;
	double playerPosX;
	double playerPosY;
	double playerSpeedX;
	double playerSpeedY;
	double health = 50;
	
	Player(double playerPosX, double playerPosY, double playerSpeedX, double playerSpeedY) {

		this.playerPosX = playerPosX;
		this.playerPosY = playerPosY;
		this.playerSpeedX = playerSpeedX;
		this.playerSpeedY = playerSpeedY;
	}
	
}