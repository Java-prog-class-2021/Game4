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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
	Player player = new Player(panW/2-12.5,panH/2-11,0,0);
	ArrayList<Zombie> zombieList = new ArrayList<>();
	ArrayList<Bullet> bulletList = new ArrayList<>();
	ArrayList<Building> buildingList = new ArrayList<>();
	Border border = new Border();
	boolean playerAlive = true;
	boolean roundOver = false;


	boolean[] keys = {false,false,false,false};
	static final int UP=0, DOWN=1, LEFT=2, RIGHT=3; 

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
			roundOver = true;

			if (round > 1 && roundOver == true) {
				playerScore += 50;
			}
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


		//TODO: make more efficient, maybe with separate panels?

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

			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
		}

		if (keys[UP] && keys[LEFT]) {
			player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = -2*Math.sin(Math.toRadians(45));

			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
		}

		if (keys[DOWN] && keys[RIGHT]) {
			player.playerSpeedX = 2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = 2*Math.sin(Math.toRadians(45));
			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
		}

		if (keys[DOWN] && keys[LEFT]) {
			player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
			player.playerSpeedY = 2*Math.sin(Math.toRadians(45));

			//System.out.println(Math.sqrt(player.playerSpeedX*player.playerSpeedX + player.playerSpeedY*player.playerSpeedY));
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

			//initial speed of zombies
			z.speedX = -0.5*Math.sin(z.angle);
			z.speedY = -0.5*Math.cos(z.angle);

			//after round 1, movement speed of zombies increases per round
			for (int i = 0; i < round; i++) {
				if(round > 1 && roundOver) {
					z.speedX += -0.25*Math.sin(z.angle);
					z.speedY += -0.25*Math.cos(z.angle);
				}
			}

			for (Zombie m : zombieList) {

				if (zombieList.indexOf(z) != zombieList.indexOf(m)) {

					if (z.posX + z.width >= m.posX && z.posX <= m.posX + m.width) {

						if (z.posY + z.height >= m.posY && z.posY <= m.posY + m.height) {
							//TODO: make zombies not overlap each other
						}
					}


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


			for (int j = 0; j < zombieList.size(); j++) {

				//if bullet goes off screen
				if (bulletList.get(i).posX < 0 || bulletList.get(i).posX >= panW || bulletList.get(i).posY < 0 || bulletList.get(i).posY >= panH) {
					bulletList.remove(i);
					return;
				}

				//if bullet hits a building
				for (int x = 0; x < buildingList.size(); x++) {
					if(bulletList.get(i).posX >= buildingList.get(x).x && bulletList.get(i).posX <= buildingList.get(x).x+buildingList.get(x).width) {

						if (bulletList.get(i).posY >= buildingList.get(x).y && bulletList.get(i).posY <= buildingList.get(x).y+buildingList.get(x).height) {
							bulletList.remove(i);
							return;


						}
					}
				}


				//if bullet hits the border

				if (bulletList.get(i).posX <= border.x) { //left side
					bulletList.remove(i);
					return;
				}
				if (bulletList.get(i).posX + bulletList.get(i).width >= border.x + border.width) { //right side
					bulletList.remove(i);
					return;
				}
				if (bulletList.get(i).posY <= border.y) { //top side
					bulletList.remove(i);
					return;
				}
				if (bulletList.get(i).posY + bulletList.get(i).height >= border.y + border.height) { //bottom side
					bulletList.remove(i);
					return;
				}


				//if bullet hits a zombie
				
				if (bulletList.get(i).posX >= zombieList.get(j).posX && bulletList.get(i).posX <= zombieList.get(j).posX+zombieList.get(j).zombieW) {

					if (bulletList.get(i).posY >= zombieList.get(j).posY && bulletList.get(i).posY <= zombieList.get(j).posY+zombieList.get(j).zombieH) {

						zombieList.get(j).health -= bulletList.get(i).damage;
						bulletList.remove(i);


						//each zombie that is killed scores 10 points
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

	//FIXME: add a comment to explain what this does. or change method name
	//It actually sees if a zombie is hitting you.
	void checkHealth() {

		for (int i = 0; i < zombieList.size(); i++) {

			if (zombieList.get(i).posX >= player.playerPosX-1 && zombieList.get(i).posX <= player.playerPosX+player.playerWidth +1 && zombieList.get(i).posY >= player.playerPosY-1 && zombieList.get(i).posY <= player.playerPosY + player.playerHeight+1) {
				player.health -= zombieList.get(i).damage;
				//FIXME: why is there a sleep here?
				try {
					Thread.sleep(SLEEP*200);
				} catch (InterruptedException e) {}
			}
		}
	}

	class GamePanel extends JPanel {
		
		Image imgTextureTile;
		
		GamePanel() {
			
			imgTextureTile = loadImage("texturetile1.jpg");
			this.setBackground(Color.decode("#66c1d1"));
			this.setPreferredSize(new Dimension(panW,panH));

			this.addMouseListener(new MouseAL());
			this.addKeyListener(new WAL());
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

						g2.drawImage(imgTextureTile, (int)border.x+(i*(textureTileWidth-10)),(int)border.y+(j*(textureTileHeight-10)), textureTileWidth, textureTileHeight, null);


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
			//g2.setColor(new Color(0,0,0,150));
			//g2.fill(new Ellipse2D.Double(player.playerPosX+5,player.playerPosY+5,player.playerWidth,player.playerHeight)); //player shadow
			//g2.setColor(Color.white);
			//g2.fill(new Ellipse2D.Double(player.playerPosX,player.playerPosY,player.playerWidth,player.playerHeight)); //actual player
			player.draw(g);

			//draw zombies
			//g2.setColor(new Color(0,0,0,20));
			for (Zombie z : zombieList) {
				//g2.fill(new Ellipse2D.Double(z.posX+5, z.posY+5, z.width, z.height)); //zombie shadows
				
			}
			//g2.setColor(Color.decode("#38350B"));
			for (Zombie z : zombieList) {
				//g2.fill(new Ellipse2D.Double(z.posX, z.posY, z.width, z.height)); //actual zombies
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
			for (Zombie z : zombieList) {
				g2.fill(new Rectangle2D.Double(z.posX + (z.width/2)-1.5, z.posY - 12, (int)(30*(z.health/z.fullHealth)),5));
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

			g2.setColor(Color.red);
			g2.drawLine(panW/2, 0, panW/2, panH);
			g2.drawLine(0, panH/2, panW, panH/2);

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
		public void keyTyped(KeyEvent e) {}

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
