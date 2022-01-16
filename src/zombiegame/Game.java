/**
 *
 * Widaad
 * 
 * Hermela
 * 
 * Henry
 * 
 * Josh
 *
 *
 * Date started: December 12, 2021
 */


package zombiegame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game {

	//global variables
	int panW = 800;
	int panH = 600;
	JFrame window;
	GamePanel panel;

	//instance variables
	Player player = new Player(panW/2-10,panH/2-10,0,0);
	ArrayList<Zombie> zombieList = new ArrayList<>();
	ArrayList<Bullet> bulletList = new ArrayList<>();
	ArrayList<Building> buildingList = new ArrayList<>();
	Border border = new Border();
	boolean playerAlive = true;

	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean upPressed = false;
	boolean downPressed = false;

	int playerScore = 0;
	int round = 0;
	int SLEEP = 8;
	double zfh = 50;

	int GRID = (int)(border.width/60);
	int board[][] = new int [GRID][GRID];


	public static void main (String[] args) {
		new Game();
	}

	Game() {

		window = new JFrame("Martian Hunter");
		panel = new GamePanel();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);

		//setup();

		Thread gfxThread = new Thread() {

			public void run() {

				while(true) {
					try {
						Thread.sleep(SLEEP);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					panel.repaint();
				}
			}

		};

		Thread logicThread = new Thread() {

			public void run() {

				while (true) {
					try {
						Thread.sleep(SLEEP);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					movePlayer();
					moveZombies();
					shootBullets();
					spawnZombies();
					gameStatus();

				}
			}
		};


		Thread healthThread = new Thread() {

			public void run() {

				while (true) {

					try {
						Thread.sleep(SLEEP);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					checkHealth();

				}

			}

		};

		setup();
		gfxThread.start();
		logicThread.start();
		healthThread.start();



		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

	}

	void setup() {

		//set ground tiles

		for (int i = 0; i < GRID; i++) {

			for (int j = 0; j < GRID; j++) {

				board[i][j] = 1;

			}

		}

		spawnZombies();



		//create buildings

		//x pos, y pos, width, height, colour
		Building shack = new Building(20, 20, 200, 140, Color.decode("#452522"));
		buildingList.add(shack);
		
		Building warehouse = new Building(1000, 10, 400, 700, Color.gray);
		buildingList.add(warehouse);

	}

	void spawnZombies() {

		if (zombieList.size() == 0) {
			round++;
			for (int i = 0; i < 2*(round) + 6; i++) {

				Zombie z = new Zombie ();

				z.fullHealth += 10;

				//spawn within borders
				z.posX = (int)(Math.random()*border.width)+border.x;
				z.posY = (int)(Math.random()*border.height)+border.y;


				//make sure zombies spawn off screen
				if (z.posX + z.width >= 0 && z.posX < panW && z.posY + z.height >= 0 && z.posY < panH) { //if zombie is within screen dimensions


					//50% chance of changing the x position, 50% chance of changing the y position
					if ((int)(Math.random()*2) == 1) {

						//change x

						while (z.posX + z.width >= 0 && z.posX < panW) {

							z.posX = (int)(Math.random()*border.width)+border.x;

						}

					}
					else {

						//change y

						while (z.posY + z.height >= 0 && z.posY < panH) {

							z.posY = (int)(Math.random()*border.height)+border.y;

						}

					}


				}

				zombieList.add(z);


			}


		}


	}

	void movePlayer() {


		//TODO: make more efficient, maybe with separate panels?

		//movement speed

		player.playerSpeedX = 0;
		player.playerSpeedY = 0;

		if (upPressed) {
			player.playerSpeedY = -2;
		}

		if (downPressed) {
			player.playerSpeedY = 2;
		}

		if (rightPressed) {
			player.playerSpeedX = 2;
		}

		if (leftPressed) {
			player.playerSpeedX = -2;
		}

		if (upPressed && rightPressed) {
			player.playerSpeedX = 2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = -2*Math.sin(Math.toRadians(45));

			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
		}

		if (upPressed && leftPressed) {
			player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = -2*Math.sin(Math.toRadians(45));

			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
		}

		if (downPressed && rightPressed) {
			player.playerSpeedX = 2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = 2*Math.sin(Math.toRadians(45));
			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
		}

		if (downPressed && leftPressed) {
			player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = 2*Math.sin(Math.toRadians(45));

			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
		}

		if (downPressed && upPressed) {
			player.playerSpeedY = 0;
		}

		if (leftPressed && rightPressed) {
			player.playerSpeedX = 0;
		}



		//COLLISION


		//against border
		if (player.playerPosX <= border.x && leftPressed) { //left of border
			player.playerSpeedX = 0;
		}
		if (player.playerPosX + player.playerWidth >= border.x + border.width && rightPressed) { //right of border
			player.playerSpeedX = 0;
		}
		if (player.playerPosY <= border.y && upPressed) { //top of border
			player.playerSpeedY = 0;
		}
		if (player.playerPosY + player.playerHeight >= border.y + border.height && downPressed) { //bottom of border
			player.playerSpeedY = 0;
		}

		//against buildings
		for (Building b : buildingList) {


			//bottom of building
			if (upPressed) {
				if (player.playerPosX <= b.x+b.width && player.playerPosX+player.playerWidth >= b.x) {

					if (player.playerPosY <= b.y + b.height + 1 && player.playerPosY >= b.y + b.height -1) {

						player.playerSpeedY = 0;

					}

				}
			}

			//top of building
			if (downPressed) {
				if (player.playerPosX <= b.x+b.width && player.playerPosX+player.playerWidth >= b.x) {

					if (player.playerPosY + player.playerHeight <= b.y + 1 && player.playerPosY + player.playerHeight >= b.y-1) {

						player.playerSpeedY = 0;

					}

				}
			}


			//left of building
			if (rightPressed) {
				if (player.playerPosY <= b.y+b.height&& player.playerPosY+player.playerHeight >= b.y) {

					if (player.playerPosX + player.playerWidth <= b.x + 1 && player.playerPosX + player.playerWidth >= b.x-1) {

						player.playerSpeedX = 0;

					}

				}
			}

			//right of building
			if (leftPressed) {
				if (player.playerPosY <= b.y+b.height&& player.playerPosY+player.playerHeight >= b.y) {

					if (player.playerPosX <= b.x + b.width +  1 && player.playerPosX>= b.x + b.width-1) {

						player.playerSpeedX = 0;

					}

				}
			}

		}



		//update the positions of all the surroundings

		for (Zombie z : zombieList) {

			z.posX -= player.playerSpeedX;
			z.posY -= player.playerSpeedY;

		}

		for (Bullet b : bulletList) {

			b.posX -= player.playerSpeedX;
			b.posY -= player.playerSpeedY;

		}

		for (Building b : buildingList) {

			b.x -= player.playerSpeedX;
			b.y -= player.playerSpeedY;

		}


		border.x -= player.playerSpeedX;
		border.y -= player.playerSpeedY;


		//		System.out.println("X: " + player.playerSpeedX);
		//		System.out.println("Y: " + player.playerSpeedY);
		//		System.out.println(" ");

		//		System.out.println(player.playerSpeedX);
		//		System.out.println(player.playerSpeedY);
		//		System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY * player.playerSpeedY));
	}

	void moveZombies() {


		for (Zombie z : zombieList) {

			z.angle = Math.atan2((z.posX - player.playerPosX), (z.posY - player.playerPosY));

			for (int i = 0; i < zombieList.size(); i++) {
				if(zombieList.indexOf(z) != i && z.posX > zombieList.get(i).posX - 30 && z.posX < zombieList.get(i).posX + 30) {

					z.speedX = 0;

				}
				else {
					z.speedX = -0.5*Math.sin(z.angle);
				}
			}


			for (int i = 0; i < zombieList.size(); i++) {
				if(zombieList.indexOf(z) != i && z.posY > zombieList.get(i).posY - 30 && z.posY < zombieList.get(i).posY + 30) {

					z.speedY = 0;

				}
				else {
					z.speedY = -0.5*Math.cos(z.angle);
				}
			}


			z.posX += z.speedX;
			z.posY += z.speedY;

		}


	}

	void shootBullets() {

		for (int i = 0; i < bulletList.size(); i++) {


			for (int j = 0; j < zombieList.size(); j++) {

				//if bullet goes off screen
				if (bulletList.get(i).posX < 0 || bulletList.get(i).posX >= panW || bulletList.get(i).posY < 0 || bulletList.get(i).posY >= panH) {
					bulletList.remove(i);
					return;
				}


				//if bullet hits a zombie
				if (bulletList.get(i).posX >= zombieList.get(j).posX && bulletList.get(i).posX <= zombieList.get(j).posX+zombieList.get(j).width) {

					if (bulletList.get(i).posY >= zombieList.get(j).posY && bulletList.get(i).posY <= zombieList.get(j).posY+zombieList.get(j).height) {

						zombieList.get(j).health -= bulletList.get(i).damage;
						bulletList.remove(i);


						if (zombieList.get(j).health <= 0) {
							zombieList.remove(j);
							playerScore+=10;
						}

						return;


					}


				}

			}


			bulletList.get(i).posX += bulletList.get(i).speedX;
			bulletList.get(i).posY += bulletList.get(i).speedY;

		}

	}

	void gameStatus() {

		if (player.health <= 0) {
			window.setTitle("Game over");
			playerAlive = false;
			player.health = 0; //so that player health doesn't display as negative

		}


	}

	void createBullets(int x, int y) {

		double deltaX = Math.abs((player.playerPosX + (player.playerWidth/2)-4)-x);
		double deltaY = Math.abs((player.playerPosY + (player.playerHeight/2)-4)-y);
		double angle = Math.atan2(deltaY, deltaX);
		Bullet b = new Bullet(player.playerPosX + (player.playerWidth/2)-4,player.playerPosY + (player.playerHeight/2)-4,0,0);


		if (x > player.playerPosX) b.speedX = (double)(5*Math.cos(angle));
		if (x < player.playerPosX) b.speedX = (double)(-5*Math.cos(angle));
		if (y > player.playerPosY) b.speedY = (double)(5*Math.sin(angle));
		if (y < player.playerPosY) b.speedY = (double)(-5*Math.sin(angle));

		bulletList.add(b);	

	}

	void checkHealth() {

		for (int i = 0; i < zombieList.size(); i++) {

			if (zombieList.get(i).posX >= player.playerPosX-1 && zombieList.get(i).posX <= player.playerPosX+player.playerWidth +1 && zombieList.get(i).posY >= player.playerPosY-1 && zombieList.get(i).posY <= player.playerPosY + player.playerHeight+1) {
				player.health -= zombieList.get(i).damage;
				try {
					Thread.sleep(SLEEP*200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}





			}

		}



	}

	class GamePanel extends JPanel {

		GamePanel() {

			this.setBackground(Color.decode("#66c1d1"));
			this.setPreferredSize(new Dimension(panW,panH));

			this.addMouseListener(new MouseAL());
			this.addKeyListener(new WAL());
			this.setFocusable(true);
			this.requestFocusInWindow();

		}

		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON); //antialiasing

			//colour tiles

			for (int i = 0; i < GRID; i++) {

				for (int j = 0; j < GRID; j++) {

					if (board[i][j]==1) {

						g2.setColor(Color.decode("#809B63"));
						g2.fillRect((int)border.x+(i*60), (int)border.y+(j*60), 60, 60);

					}

				}

			}


			//draw grid

			g2.setColor(Color.black);
			for (int i = (int)border.x; i < border.x + border.width; i+=60) {

				g2.drawLine(i, (int)border.y, i, (int)(border.y + border.height));

			}
			for (int i = (int)border.y; i < border.y + border.height; i+=60) {

				g2.drawLine((int)border.x, i, (int)(border.x + border.width), i);

			}

			//draw border
			g2.setColor(Color.white);
			g2.setStroke(new BasicStroke(8));
			g2.drawRect((int)border.x, (int)border.y, (int)border.width, (int)border.height);


			
			
			

			g2.setStroke(new BasicStroke(1));
			//draw bullets
			g2.setColor(new Color(0,0,0,20));
			for (Bullet b : bulletList) {
				g2.fill(new Ellipse2D.Double(b.posX+2, b.posY+2, b.width, b.height));
			}
			g2.setColor(Color.decode("#444444"));
			for (Bullet b : bulletList) {
				g2.fill(new Ellipse2D.Double(b.posX, b.posY, b.width, b.height));
			}

			//draw player
			g2.setColor(new Color(0,0,0,20));
			g2.fill(new Ellipse2D.Double(player.playerPosX+5,player.playerPosY+5,player.playerWidth,player.playerHeight)); //player shadow
			g2.setColor(Color.white);
			g2.fill(new Ellipse2D.Double(player.playerPosX,player.playerPosY,player.playerWidth,player.playerHeight)); //actual player

			//draw zombies
			g2.setColor(new Color(0,0,0,20));
			for (Zombie z : zombieList) {
				g2.fill(new Ellipse2D.Double(z.posX+5, z.posY+5, z.width, z.height)); //zombie shadows
			}
			g2.setColor(Color.decode("#38350B"));
			for (Zombie z : zombieList) {
				g2.fill(new Ellipse2D.Double(z.posX, z.posY, z.width, z.height)); //actual zombies
			}
			
			//draw buildings

			for (Building b : buildingList) {

				g2.setColor(new Color(0,0,0,40));

				g2.fill(new Rectangle2D.Double(b.x, b.y, b.width+30, b.height+30));

			}
			
			for (Building b : buildingList) {

				g2.setColor(b.colour);

				g2.fill(new Rectangle2D.Double(b.x, b.y, b.width, b.height));

			}

			//draw zombie health bars
			g2.setColor(Color.white);
			for (Zombie z : zombieList) {
				g2.fill(new Rectangle2D.Double(z.posX + (z.width/2)-15, z.posY - 12, (int)(30*(z.health/z.fullHealth)),5));
			}

			//score and round displays
			g2.setFont(new Font("Helvetica", Font.BOLD, 24));
			g2.setColor(Color.white);
			g2.drawString("Score: " + playerScore, panW-180, panH-30);
			g2.drawString("Round: " + round, 30, panH-30);

			//health display
			g2.setColor(Color.red);
			g2.drawString("❤", 20,30); //this will probably get replaced by a sprite
			g2.fillRect(55, 15, (int)(100*(player.health/50)), 12);
			g2.setColor(Color.white);
			g2.setFont(new Font("Helvetica", Font.BOLD, 20));
			g2.drawString(""+(int)player.health, 162, 29);

			if (!playerAlive) {
				g2.setColor(new Color(200,0,0,100));
				g2.fillRect(0,0,panW,panH);
				g2.setFont(new Font("Helvetica", Font.BOLD, 40));
				g2.setColor(Color.white);
				g2.drawString("GAME OVER", 280, panH/2 - 20);
				SLEEP = 32;
			}


		}

	}


	class MouseAL implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

			int clickX = e.getX();
			int clickY = e.getY();

			createBullets(clickX, clickY);

		}

		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}

	}



	class WAL implements KeyListener { 



		@Override
		public void keyPressed(KeyEvent e) {

			int key = e.getKeyCode();

			if (key == 87) {
				upPressed = true;
			}
			if (key == 65) {
				leftPressed = true;
			}
			if (key == 83) {
				downPressed = true;
			}
			if (key == 68) {
				rightPressed = true;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {

			int key = e.getKeyCode();

			if (key == 87) {
				upPressed = false;
			}
			if (key == 65) {
				leftPressed = false;
			}
			if (key == 83) {
				downPressed = false;
			}
			if (key == 68) {
				rightPressed = false;
			}


		}
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}

	}


}
