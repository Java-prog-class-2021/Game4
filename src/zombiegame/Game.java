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
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Game {

	//global variables
	int panW = 800;
	int panH = 600;
	JFrame window;
	GamePanel panel;

	//instance variables
	Border border = new Border();
	Player player = new Player(400-17,300-17,0,0);
	ArrayList<Zombie> zombieList = new ArrayList<>();
	ArrayList<Bullet> bulletList = new ArrayList<>();
	ArrayList<Building> buildingList = new ArrayList<>();

	boolean playerAlive = true;
	boolean roundOver = false;
	boolean hitboxOn = false; //causes "hot code replace failed" to pop up, no idea what that means


	boolean[] keys = {false,false,false,false};
	static final int UP=0, DOWN=1, LEFT=2, RIGHT=3; 

	int playerScore = 0;
	int round = 0;
	int SLEEP = 8;
	double zfh = 50;

	int GRID = (int)(border.width/200);
	int board[][] = new int [GRID][GRID];


	public static void main (String[] args) {
		new Game();
	}

	Game() {

		window = new JFrame("Wastelander");
		panel = new GamePanel();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);

		setup();
		new GfxThread().start();
		new LogicThread().start();
		new HealthThread().start();

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

		//spawnZombies();

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
			roundOver = true;

			for (int i = 0; i < 3*(round) + 6; i++) {


				Zombie z = new Zombie ();
				z.fullHealth += 10;

				//damage dealt by zombies increases per round
				for (int j = 0; j < round-1; j++) {
					if (round > 1 && roundOver == true) {
						z.damage += 2;
					}
				}

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

		//movement speed

		player.playerSpeedX = 0;
		player.playerSpeedY = 0;

		if (keys[UP])		player.playerSpeedY = -2;
		if (keys[DOWN])		player.playerSpeedY =  2;		
		if (keys[RIGHT]) 	player.playerSpeedX =  2;
		if (keys[LEFT])		player.playerSpeedX = -2;		

		if (keys[UP] && keys[RIGHT]) {
			player.playerSpeedX = 2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = -2*Math.sin(Math.toRadians(45));
		}

		if (keys[UP] && keys[LEFT]) {
			player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = -2*Math.sin(Math.toRadians(45));
		}

		if (keys[DOWN] && keys[RIGHT]) {
			player.playerSpeedX = 2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = 2*Math.sin(Math.toRadians(45));
		}

		if (keys[DOWN] && keys[LEFT]) {
			player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = 2*Math.sin(Math.toRadians(45));
		}

		if (keys[DOWN] && keys[UP]) {
			player.playerSpeedY = 0;
		}

		if (keys[LEFT] && keys[RIGHT]) {
			player.playerSpeedX = 0;
		}


		//COLLISION
		//against border
		if (player.playerPosX <= border.x && keys[LEFT]) { //left of border
			player.playerSpeedX = 0;
		}
		if (player.playerPosX + player.playerWidth >= border.x + border.width && keys[RIGHT]) { //right of border
			player.playerSpeedX = 0;
		}
		if (player.playerPosY <= border.y && keys[UP]) { //top of border
			player.playerSpeedY = 0;
		}
		if (player.playerPosY + player.playerHeight >= border.y + border.height && keys[DOWN]) { //bottom of border
			player.playerSpeedY = 0;
		}

		//against buildings
		for (Building b : buildingList) {


			//bottom of building
			if (keys[UP]) {
				if (player.playerPosX <= b.x+b.width && player.playerPosX+player.playerWidth >= b.x) {

					if (player.playerPosY <= b.y + b.height + 1 && player.playerPosY >= b.y + b.height -1) {

						player.playerSpeedY = 0;

					}

				}
			}

			//top of building
			if (keys[DOWN]) {
				if (player.playerPosX <= b.x+b.width && player.playerPosX+player.playerWidth >= b.x) {

					if (player.playerPosY + player.playerHeight <= b.y + 1 && player.playerPosY + player.playerHeight >= b.y-1) {

						player.playerSpeedY = 0;

					}

				}
			}


			//left of building
			if (keys[RIGHT]) {
				if (player.playerPosY <= b.y+b.height&& player.playerPosY+player.playerHeight >= b.y) {

					if (player.playerPosX + player.playerWidth <= b.x + 1 && player.playerPosX + player.playerWidth >= b.x-1) {

						player.playerSpeedX = 0;

					}

				}
			}

			//right of building
			if (keys[LEFT]) {
				if (player.playerPosY <= b.y+b.height&& player.playerPosY+player.playerHeight >= b.y) {

					if (player.playerPosX <= b.x + b.width +  1 && player.playerPosX>= b.x + b.width-1) {

						player.playerSpeedX = 0;

					}

				}
			}

		}



		//update the positions of all the surroundings

		for (int i = 0; i < zombieList.size(); i++) {
			Zombie z = zombieList.get(i);
			z.posX -= player.playerSpeedX;
			z.posY -= player.playerSpeedY;

		}

		for (int i = 0; i < bulletList.size(); i++) {
			Bullet b = bulletList.get(i);
			b.posX -= player.playerSpeedX;
			b.posY -= player.playerSpeedY;

		}

		for (Building b : buildingList) {
			b.x -= player.playerSpeedX;
			b.y -= player.playerSpeedY;
		}

		border.x -= player.playerSpeedX;
		border.y -= player.playerSpeedY;

	}

	void moveZombies() {


		for (int i = 0; i < zombieList.size(); i++) {
			Zombie z = zombieList.get(i);

			z.angle = Math.atan2((z.posX - player.playerPosX), (z.posY - player.playerPosY));

			//initial speed of zombies
			z.speedX = -0.5*Math.sin(z.angle);
			z.speedY = -0.5*Math.cos(z.angle);

			//after round 1, movement speed of zombies increases per round
			for (int j = 0; j < round; j++) {
				if(round > 1 && roundOver) {
					z.speedX += -0.25*Math.sin(z.angle);
					z.speedY += -0.25*Math.cos(z.angle);
				}
			}



			//pathfinding around buildings

			for (Building b : buildingList) {


				//if zombie is to the right of building (within a 25 pixel margin)

				if (z.posX >= b.x + b.width - 1 && z.posX <= b.x + b.width + 25) {

					if (z.posY >= b.y - 25 && z.posY <= b.y + b.height + 25) {

						//if player is to the left of zombie
						if (player.playerPosX < z.posX) {

							if (player.playerPosY <= (b.height/2) + b.y) {
								z.speedX = 0;
								z.speedY = -0.5;
							}

							if (player.playerPosY > (b.height/2) + b.y) {
								z.speedX = 0;
								z.speedY = 0.5;

							}

							if (player.playerPosX >= b.x + b.width - 1 && player.playerPosX <= b.x + b.width + 25) {
								z.speedX = -0.5*Math.sin(z.angle);
								z.speedY = -0.5*Math.cos(z.angle);
							}

							for (int j = 0; j < round; j++) {
								if (round > 1 && roundOver) {
									if (player.playerPosY <= (b.height/2) + b.y) {
										z.speedX = 0;
										z.speedY += -0.2;
									}
									if (player.playerPosY > (b.height/2) + b.y) {
										z.speedX = 0;
										z.speedY += 0.2;
									}
									if (player.playerPosX >= b.x + b.width - 1 && player.playerPosX <= b.x + b.width + 25) {
										z.speedX += -0.2*Math.sin(z.angle);
										z.speedY += -0.2*Math.cos(z.angle);
									}
								}
							}
						}
					}
				}

				//if zombie is to the left of building (within a 25 pixel margin)

				if (z.posX + z.width <= b.x + 1 && z.posX + z.width >= b.x - 25) {

					if (z.posY >= b.y - 25 && z.posY <= b.y + b.height + 25) {

						//if player is to the right of zombie
						if (player.playerPosX > z.posX) {


							if (player.playerPosY <= (b.height/2) + b.y) {
								z.speedX = 0;
								z.speedY = -0.5;
							}

							if (player.playerPosY > (b.height/2) + b.y) {
								z.speedX = 0;
								z.speedY = 0.5;

							}

							if (player.playerPosX + player.playerWidth <= b.x + 1 && player.playerPosX + player.playerWidth >= b.x - 25) {
								z.speedX = -0.5*Math.sin(z.angle);
								z.speedY = -0.5*Math.cos(z.angle);

							}
							for (int j = 0; j < round; j++) {
								if (round > 1 && roundOver) {
									if (player.playerPosY <= (b.height/2) + b.y) {
										z.speedX = 0;
										z.speedY += -0.2;
									}
									if (player.playerPosY > (b.height/2) + b.y) {
										z.speedX = 0;
										z.speedY += 0.2;
									}
									if (player.playerPosX + player.playerWidth <= b.x + 1 && player.playerPosX + player.playerWidth >= b.x - 25) {
										z.speedX += -0.2*Math.sin(z.angle);
										z.speedY += -0.2*Math.cos(z.angle);
									}
								}
							}
						}
					}
				}

				//if zombie is above building (within a 25 pixel margin)

				if (z.posY + z.height <= b.y +1 && z.posY + z.height >= b.y - 25) {

					if (z.posX + z.width >= b.x - 25 && z.posX <= b.x + b.width + 25) {


						//if player is below zombie
						if (player.playerPosY > z.posY) {

							if (player.playerPosX <= (b.width/2) + b.x) {
								z.speedX = -0.5;
								z.speedY = 0;
							}

							if (player.playerPosX > (b.width/2) + b.x) {
								z.speedX = 0.5;
								z.speedY = 0;

							}

							if (player.playerPosY + player.playerHeight <= b.y +1 && player.playerPosY + player.playerHeight >= b.y - 25) {
								z.speedX = -0.5*Math.sin(z.angle);
								z.speedY = -0.5*Math.cos(z.angle);

							}
							for (int j = 0; j < round; j++) {
								if (round > 1 && roundOver) {
									if (player.playerPosX <= (b.width/2) + b.x) {
										z.speedX += -0.2;
										z.speedY = 0;
									}
									if (player.playerPosX > (b.width/2) + b.x) {
										z.speedX += 0.2;
										z.speedY = 0;
									}
									if (player.playerPosY + player.playerHeight <= b.y +1 && player.playerPosY + player.playerHeight >= b.y - 25) {
										z.speedX += -0.2*Math.sin(z.angle);
										z.speedY += -0.2*Math.cos(z.angle);
									}

								}
							}

						}
					}
				}
				//if zombie is below building (within a 25 pixel margin)

				if (z.posY >= b.y + b.height -1 && z.posY <= b.y + b.height + 25) {

					if (z.posX + z.width >= b.x - 25 && z.posX <= b.x+ b.width + 25) {


						//if player is above zombie
						if (player.playerPosY < z.posY) {

							if (player.playerPosX <= (b.width/2) + b.x) {
								z.speedX = -0.5;
								z.speedY = 0;
							}

							if (player.playerPosX > (b.width/2) + b.x) {
								z.speedX = 0.5;
								z.speedY = 0;

							}

							if (player.playerPosY >= b.y + b.height -1 && player.playerPosY <= b.y + b.height + 25) {
								z.speedX = -0.5*Math.sin(z.angle);
								z.speedY = -0.5*Math.cos(z.angle);

							}
							for (int j = 0; j < round; j++) {
								if (round > 1 && roundOver) {
									if (player.playerPosX <= (b.width/2) + b.x) {
										z.speedX += -0.2;
										z.speedY = 0;
									}
									if (player.playerPosX > (b.width/2) + b.x) {
										z.speedX += 0.2;
										z.speedY = 0;
									}
									if (player.playerPosY >= b.y + b.height -1 && player.playerPosY <= b.y + b.height + 25) {
										z.speedX += -0.2*Math.sin(z.angle);
										z.speedY += -0.2*Math.cos(z.angle);
									}
								}

							}

						}
					}
				}


			}

			z.posX += z.speedX;
			z.posY += z.speedY;

		}




	}

	void shootBullets() {

		for (int i = 0; i < bulletList.size(); i++) {
			Bullet b = bulletList.get(i);

			for (int j = 0; j < zombieList.size(); j++) {
				Zombie z = zombieList.get(j);


				//if bullet goes off screen
				if (b.posX < 0 || b.posX >= panW || b.posY < 0 || b.posY >= panH) {
					bulletList.remove(i);
					return;
				}

				//if bullet hits a building
				for (int k = 0; k < buildingList.size(); k++) {
					Building build = buildingList.get(k);
					if(b.posX >= build.x && b.posX <= build.x+build.width) {

						if (b.posY >= build.y && b.posY <= build.y+build.height) {
							bulletList.remove(i);
							return;


						}
					}
				}


				//if bullet hits the border

				if (b.posX <= border.x) { //left side
					bulletList.remove(i);
					return;
				}
				if (b.posX + b.width >= border.x + border.width) { //right side
					bulletList.remove(i);
					return;
				}
				if (b.posY <= border.y) { //top side
					bulletList.remove(i);
					return;
				}
				if (b.posY + b.height >= border.y + border.height) { //bottom side
					bulletList.remove(i);
					return;
				}


				//if bullet hits a zombie

				if (b.posX >= z.posX && b.posX <= z.posX+z.width) {

					if (b.posY >= z.posY && b.posY <= z.posY+z.height) {

						z.health -= b.damage;
						bulletList.remove(i);


						//each zombie that is killed scores 10 points
						if (z.health <= 0) {
							zombieList.remove(j);
							playerScore+=10;
						}

						return;


					}




				}

			}


			b.posX += b.speedX;
			b.posY += b.speedY;

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

	//FIXME: add a comment to explain what this does. or change method name
	//It actually sees if a zombie is hitting you.
	void checkHealth() {

		for (int i = 0; i < zombieList.size(); i++) {
			Zombie z = zombieList.get(i);
			if (z.posX >= player.playerPosX-1 && z.posX <= player.playerPosX+player.playerWidth +1 && z.posY >= player.playerPosY-1 && z.posY <= player.playerPosY + player.playerHeight+1) {
				player.health -= z.damage;
				//FIXME: why is there a sleep here?
				try {
					Thread.sleep(SLEEP*200);
				} catch (InterruptedException e) {}
			}
		}
	}

	@SuppressWarnings("serial")
	class GamePanel extends JPanel {

		Image imgTextureTile;

		GamePanel() {

			imgTextureTile = loadImage("texturetile1.jpg");
			this.setBackground(Color.decode("#66c1d1"));
			this.setPreferredSize(new Dimension(panW,panH));

			this.addMouseListener(new MouseAL());
			this.addKeyListener(new WAL());
			this.addMouseMotionListener(new MotionAL());
			this.setFocusable(true);
			this.requestFocusInWindow();

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

		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON); //antialiasing

			//draw ground tiles 
			//	int treeTileWidth = imgTreeTile.getWidth(null);
			//	int treeTileHeight = imgTreeTile.getWidth(null);
			int textureTileWidth = imgTextureTile.getWidth(null);
			int textureTileHeight = imgTextureTile.getHeight(null);

			if (imgTextureTile == null) return;


			//colour tiles
			for (int i = 0; i < GRID; i++) {

				for (int j = 0; j < GRID; j++) {

					if (board[i][j]==1) {
						g2.drawImage(imgTextureTile, (int)border.x+(i*textureTileWidth),(int)border.y+(j*textureTileHeight), null);
					}
				}
			}


			//draw grid
			//			g2.setColor(Color.black);
			//			for (int i = (int)border.x; i < border.x + border.width; i+=60) {
			//				g2.drawLine(i, (int)border.y, i, (int)(border.y + border.height));
			//			}
			//			for (int i = (int)border.y; i < border.y + border.height; i+=60) {
			//				g2.drawLine((int)border.x, i, (int)(border.x + border.width), i);
			//			}


			//draw border
			g2.setColor(Color.white);
			g2.setStroke(new BasicStroke(8));
			g2.drawRect((int)border.x, (int)border.y, (int)border.width, (int)border.height);


			//draw bullets
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.decode("#444444"));
			for (int i = 0; i < bulletList.size(); i++) {
				Bullet b = bulletList.get(i);
				g2.fill(new Ellipse2D.Double(b.posX, b.posY, b.width, b.height));
			}

			//draw player
			player.draw(g2);	

			//draw zombies
			for (int i = 0; i < zombieList.size(); i++) {
				Zombie z = zombieList.get(i);
				z.draw(g2);
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
			for (int i = 0; i < zombieList.size(); i++) {
				Zombie z = zombieList.get(i);	
				g2.fill(new Rectangle2D.Double(z.posX + (z.width/2)-(12), z.posY - 15, (int)(30*(z.health/z.fullHealth)),5));
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


			if (hitboxOn) {
				//crosshair
				g2.setColor(Color.red);
				g2.drawLine(panW/2, 0, panW/2, panH);
				g2.drawLine(0, panH/2, panW, panH/2);

				//draw player hitbox
				g2.setStroke(new BasicStroke(4));
				g2.drawLine((int)player.playerPosX, (int)player.playerPosY, (int)player.playerPosX+player.playerWidth, (int)player.playerPosY);
				g2.drawLine((int)player.playerPosX, (int)player.playerPosY+player.playerHeight, (int)player.playerPosX+player.playerWidth, (int)player.playerPosY+player.playerHeight);
				g2.drawLine((int)player.playerPosX, (int)player.playerPosY, (int)player.playerPosX, (int)player.playerPosY+player.playerHeight);
				g2.drawLine((int)player.playerPosX+player.playerWidth, (int)player.playerPosY, (int)player.playerPosX+player.playerWidth, (int)player.playerPosY+player.playerHeight);

				//draw zombie hitbox
				for (Zombie z : zombieList) {
					g2.drawLine((int)z.posX, (int)z.posY, (int)z.posX+z.width, (int)z.posY);
					g2.drawLine((int)z.posX, (int)z.posY+z.height, (int)z.posX+z.width, (int)z.posY+z.height);
					g2.drawLine((int)z.posX, (int)z.posY, (int)z.posX, (int)z.posY+z.height);
					g2.drawLine((int)z.posX+z.width, (int)z.posY, (int)z.posX+z.width, (int)z.posY+z.height);
				}
			}

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

	/*****************************************************/
	/*			Event Listener classes					 */
	/*****************************************************/

	class MouseAL implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub

			int clickX = e.getX();
			int clickY = e.getY();

			createBullets(clickX, clickY);

		}

		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}

	class WAL implements KeyListener { 

		@Override
		public void keyPressed(KeyEvent e) {

			int key = e.getKeyCode();

			if (key == 'W') keys[UP] = true;
			if (key == 'A') keys[LEFT] = true;
			if (key == 'S') keys[DOWN] = true; 
			if (key == 'D') keys[RIGHT] = true; 

		}

		@Override
		public void keyReleased(KeyEvent e) {

			int key = e.getKeyCode();

			if (key == 'W') keys[UP] = false;
			if (key == 'A') keys[LEFT] = false;
			if (key == 'S') keys[DOWN] = false; 
			if (key == 'D') keys[RIGHT] = false; 
		}

		@Override
		public void keyTyped(KeyEvent e) {

			//enable or disable hitboxes by pressing b
			if (e.getKeyChar() == 'b') {

				if (hitboxOn) {
					hitboxOn = false;
				}
				else {
					hitboxOn = true;
				}

			}

		}

	}

	class MotionAL implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseMoved(MouseEvent e) {

			double x = Math.abs(e.getX() - (panW/2));
			double y = Math.abs(e.getY() - (panH/2));
			
			double angle = Math.atan((y/x));

			System.out.println(x);

			//cast rule
			if (e.getX() > panW/2) {

				if (e.getY() > panH/2) {
					//do nothing
				}

				if (e.getY() < panH/2) {
					angle = 2*Math.PI - angle;
				}


			}
			if (e.getX() < panW/2) {

				if (e.getY() > panH/2) {
					angle = Math.PI - angle;
				}

				if (e.getY() < panH/2) {
					angle = Math.PI + angle;
				}

			}

			angle += Math.PI/2;
			
			player.rotation(angle);


		}

	}

	/*****************************************************/
	/*				Thread classes						 */
	/*****************************************************/
	class LogicThread extends Thread {
		public void run() {
			while (true) {
				try { Thread.sleep(SLEEP);
				} catch (InterruptedException e) {}

				movePlayer();
				moveZombies();
				shootBullets();
				spawnZombies();
				gameStatus();

			}
		}
	};

	class GfxThread extends Thread {
		public void run() {
			while(true) {
				try { Thread.sleep(8);
				} catch (InterruptedException e) {}

				panel.repaint();
			}
		}
	};

	class HealthThread extends Thread {
		public void run() {
			while (true) {
				try { Thread.sleep(SLEEP);
				} catch (InterruptedException e) {}

				checkHealth();
			} } };

}	
