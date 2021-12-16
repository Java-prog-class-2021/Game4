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
import java.awt.event.MouseMotionListener;
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
	Rectangle border = new Rectangle(-1000+(panW/2),-1000+(panH/2),2000,2000);
	boolean playerAlive = true;
	boolean leftMove = false;
	boolean rightMove = false;
	boolean upMove = false;
	boolean downMove = false;
	int mx;
	int my;
	int playerScore = 0;
	int round = 0;
	int SLEEP = 8;


	public static void main (String[] args) {

		new Game();

	}

	Game() {

		window = new JFrame("Martian Hunter");
		panel = new GamePanel();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.add(panel);

		spawnZombies();

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


		gfxThread.start();
		logicThread.start();
		healthThread.start();

		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);

	}

	void spawnZombies() {

		if (zombieList.size() == 0) {
			round++;
			for (int i = 0; i < 2*(round) + 6; i++) {

				Zombie z = new Zombie ((int)(Math.random()*panW),(int)(Math.random()*panH),0,0);

				while (z.posX >= player.playerPosX - 250 && z.posX <= player.playerPosX + 200) {
					z.posX = (int)(Math.random()*panW);
				}
				while (z.posY >= player.playerPosY - 250 && z.posY <= player.playerPosY + 200) {
					z.posY = (int)(Math.random()*panH);
				}

				zombieList.add(z);


			}
		}


	}

	void movePlayer() {
		//		player.playerPosX += player.playerSpeedX;
		//		player.playerPosY += player.playerSpeedY;

		//TODO: make more efficient, maybe with separate panels?

		for (Zombie z : zombieList) {

			z.posX -= player.playerSpeedX;
			z.posY -= player.playerSpeedY;

		}

		for (Bullet b : bulletList) {

			b.posX -= player.playerSpeedX;
			b.posY -= player.playerSpeedY;

		}
		
		if (player.playerPosX <= border.x) {
			player.playerSpeedX = 0;
		}
		if (player.playerPosX + player.playerWidth >= border.x + border.width) {
			player.playerSpeedX = 0;

		}
		if (player.playerPosY <= border.y) {
			player.playerSpeedY = 0;
		}
		if (player.playerPosY + player.playerHeight >= border.y + border.height) {
			player.playerSpeedY = 0;
		}
		
		border.x -= player.playerSpeedX;
		border.y -= player.playerSpeedY;
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
			
			if (z.posX >= player.playerPosX-1 && z.posX <= player.playerPosX+player.playerWidth +1 && z.posY >= player.playerPosY-1 && z.posY <= player.playerPosY + player.playerHeight+1) {
				z.speedX = 0;
				z.speedY = 0;
			}


			z.posX += z.speedX;
			z.posY += z.speedY;

		}


	}

	void shootBullets() {

		int j = 0;
		for (int k = 0; k < bulletList.size(); k++) {

			bulletList.get(k).posX += bulletList.get(k).speedX;
			bulletList.get(k).posY += bulletList.get(k).speedY;

			for (int i = 0; i < zombieList.size(); i++) {

				if (bulletList.get(k).posX >= zombieList.get(i).posX && bulletList.get(k).posX <= zombieList.get(i).posX + 20) {

					if (bulletList.get(k).posY >= zombieList.get(i).posY && bulletList.get(k).posY <= zombieList.get(i).posY + 20) {

						zombieList.remove(i);
						playerScore+=10;
						i--;
						//bulletList.remove(0);

					}

				}

			}

			j++;
		}


	}

	void gameStatus() {

		for (Zombie z : zombieList) {

			if (player.health <= 0) {
				window.setTitle("Game over");
				playerAlive = false;
			}

		}


	}

	void createBullets(int x, int y) {
		
		double deltaX = Math.abs((player.playerPosX + (player.playerWidth/2)-4)-mx);
		double deltaY = Math.abs((player.playerPosY + (player.playerHeight/2)-4)-my);
		double angle = Math.atan2(deltaY, deltaX);
		Bullet b = new Bullet(player.playerPosX + (player.playerWidth/2)-4,player.playerPosY + (player.playerHeight/2)-4,0,0);


		if (x > player.playerPosX) b.speedX = (double)(5*Math.cos(angle));
		if (x < player.playerPosX) b.speedX = (double)(-5*Math.cos(angle));
		if (y > player.playerPosY) b.speedY = (double)(5*Math.sin(angle));
		if (y < player.playerPosY) b.speedY = (double)(-5*Math.sin(angle));

		bulletList.add(b);	
		
	}
	
	void checkHealth() {
		
		for (Zombie z : zombieList) {
			
			if (z.posX >= player.playerPosX-1 && z.posX <= player.playerPosX+player.playerWidth +1 && z.posY >= player.playerPosY-1 && z.posY <= player.playerPosY + player.playerHeight+1) {
				try {
					Thread.sleep(SLEEP*200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				player.health -= z.damage;
				
				//so that health doesn't display as negative
				if (player.health < 0) {
					player.health = 0;
				}
				
				
			}
			
		}
		

		
	}
	
	class GamePanel extends JPanel {

		GamePanel() {

			this.setBackground(Color.decode("#809B63"));
			this.setPreferredSize(new Dimension(panW,panH));

			this.addMouseListener(new MouseAL());
			this.addMouseMotionListener(new MouseMotionAL());
			this.addKeyListener(new WAL());
			this.setFocusable(true);
			this.requestFocusInWindow();

		}

		public void paintComponent(Graphics g) {

			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON); //antialiasing

			//draw border
			g2.setColor(Color.white);
			g2.setStroke(new BasicStroke(8));
			g2.drawRect(border.x, border.y, border.width, border.height);
			
			
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
				upMove = true;
				player.playerSpeedY = -2;
			}
			if (key == 65) {
				leftMove = true;
				player.playerSpeedX = -2;
			}
			if (key == 83) {
				downMove = true;
				player.playerSpeedY = 2;
			}
			if (key == 68) {
				rightMove = true;
				player.playerSpeedX = 2;
			}
			if (rightMove && downMove && key!=65 && key!=87) {
				player.playerSpeedX = 2*Math.cos(Math.toRadians(45));
				player.playerSpeedY = 2*Math.sin(Math.toRadians(45));
			}
			if (leftMove && downMove && key!=87 && key!=68) {
				player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
				player.playerSpeedY = 2*Math.sin(Math.toRadians(45));
			}
			if (leftMove && upMove && key!=68 && key!=83) {
				player.playerSpeedX = -2*Math.cos(Math.toRadians(45));
				player.playerSpeedY = -2*Math.sin(Math.toRadians(45));
			}
			if (rightMove && upMove && key!=83 && key!=65) {
				player.playerSpeedX = 2*Math.cos(Math.toRadians(45));
				player.playerSpeedY = -2*Math.sin(Math.toRadians(45));
			}


		}

		@Override
		public void keyReleased(KeyEvent e) {

            int key = e.getKeyCode();

            if (key == 87) {
                upMove = false;
                player.playerSpeedY = 2;
            }
            if (key == 65) {
                leftMove = false;
                player.playerSpeedX = 2;
            }
            if (key == 83) {
                downMove = false;
                player.playerSpeedY = -2;
            }
            if (key == 68) {
                rightMove = false;
                player.playerSpeedX = -2;
            }

            if (!upMove && !downMove) {
                player.playerSpeedY = 0;
            }
            if (!leftMove && !rightMove) {
                player.playerSpeedX = 0;
            }

        }
		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}





	}

	class MouseMotionAL implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
		}

		@Override
		public void mouseMoved(MouseEvent e) {

			mx = e.getX();
			my = e.getY();

		}

	}

}