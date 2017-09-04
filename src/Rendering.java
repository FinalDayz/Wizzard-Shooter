import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.awt.image.RescaleOp;
import java.util.Arrays;
import java.util.List;

public class Rendering {
	Game game;
	
	double cameraX = 0;
	double cameraY = 0;
	long frameCount = 0;
	GUI gui;

	double camX, camY;
	
	//the redness on the side of the screen
	BufferedImage hurt = Textures.getImage("Hurt.png");
	
	Rendering(Game game){
		this.game = game;
		gui = new GUI(game.player, game);
	}
	
	void waitForUpdate(){
		while(game.isInUpdate){
			try {
				Thread.sleep(0,1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//render the WHOLEEE game
	void RenderGame(Graphics2D g, Player player, List<Block> gameBlocks, List<Projectile> projectiles, List<Entity> entities){
		
		//some anti-analising (not sure if it works tho)
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		//if the system is doing an update, wait until its over, then continue
		waitForUpdate();

		game.isInFrame = true;
		
		int playerX = player.getXInt();
		int playerY = player.getYInt();
		
		camX = cameraX;
		camY = cameraY;
		
		game.isInFrame = false;
		

		g.clearRect(-10, -10, Main.width + 10, Main.height + 10);
		
		
		//draw background and align to the camera position
		drawImage(g, Textures.getImage("Background.png"), tint(-camX/10), 0, 1.6);
		
		AffineTransform cameraPositionNegative = AffineTransform.getTranslateInstance(-camX, -camY);
		
		g.transform(cameraPositionNegative);
		
		
		//draw the blocks
		int blockWidth = Main.blockWidth;

		for(int i = 0; i < gameBlocks.size(); i++){
			
			Block currentBlock = gameBlocks.get(i);
			if(currentBlock.getAbsoluteX() > camX-10 && currentBlock.getAbsoluteX() < camX+Main.width-400 &&
				currentBlock.getAbsoluteY() > camY-10 && currentBlock.getAbsoluteY() < camY+Main.height+10)
				//draw a simple color or a image(the texture)
			if(currentBlock.isColor()){
				g.setColor(currentBlock.getTextureColor());

				g.fillRect(currentBlock.getAbsoluteX(), currentBlock.getAbsoluteY(), blockWidth, blockWidth);
			}else{
				g.drawImage(currentBlock.getTextureImage(player), currentBlock.getAbsoluteX(), currentBlock.getAbsoluteY(), null);
			}
		}
		waitForUpdate();
		game.isInFrame = true;

		//the line between the player and the turret
		playerTurretLine(g, entities, player);
		
		

		game.isInFrame = false;
		
		AffineTransform cameraPosition = AffineTransform.getTranslateInstance(camX, camY);

		g.transform(cameraPosition);
		
		//draw Player
		int playerDrawX =  tint(playerX - camX);
		int playerDrawY =  tint(playerY - camY);
		if(player.direction == 0){
			g.drawImage(Textures.getImage("PlayerRight.png"), playerDrawX, playerDrawY, Main.playerWidth, Main.playerHeight, null);
		}else{
			g.drawImage(Textures.getImage("PlayerLeft.png"), playerDrawX, playerDrawY, Main.playerWidth, Main.playerHeight, null);
		}
		
		//draw fire ready state
		double percentLoaded =  player.fireTick * 1.0 / player.fireSpeed;
		g.setColor(Color.BLACK);
		g.fillRect(playerDrawX-2, Main.playerHeight + 5 + playerDrawY - 2, 39, 8);

		g.setColor(new Color(tint((1 - percentLoaded) * 200), tint(percentLoaded * 150), 0));
		if(percentLoaded == 1)
			g.setColor(Color.GREEN);
		g.fillRect(playerDrawX, Main.playerHeight + 5 + playerDrawY, tint(percentLoaded * 35), 4);
		
		//draw the gun 
		
		drawGun(g, playerX, playerY, camX, camY);
		g.transform(cameraPositionNegative);
		
		waitForUpdate();
		
		drawProjectiles(g, projectiles);
		g.transform(cameraPosition);
		
		//draw the GUI on top of everything
		
		gui.drawGUI((Graphics2D) g);
		
		//draw the custom cursor(I was realy happy with this)
		drawCustomMouse(g);

		//draw red on the side of the screen when the player it hurt
		if(player.health < 100){
			
			
			Composite original = g.getComposite();
			//Set to semi translucent
			Composite translucent = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1 - (float)player.health/110);
			g.setComposite(translucent);
			
			g.drawImage(hurt, null, 0, 0);
			
			g.setComposite(original);
		}
			
		g.clearRect(Main.width, 0, 1920, 1920);
		g.clearRect(0, Main.height, 1920, 1920);
		
		frameCount++;
		
		//be sure that the game is always a curtain height and width
		if(frameCount % 20 == 0){
			Dimension frame = Main.canvas.frame.getSize();
			if(frame.getWidth() != Main.width + 18 || frame.getHeight() !=  Main.height + 47)
					Main.canvas.frame.setSize(Main.width + 18, Main.height + 47); 
		}
	}
	
	//draw all the projectiles
	void drawProjectiles(Graphics2D g, final List<Projectile> projectiles){
		game.isInFrame = true;
		
		for(int i = 0; i < projectiles.size(); i++){
			
			Projectile p = projectiles.get(i);
			if(p.x > camX-10 && p.x < camX+Main.width-400 &&
					p.y > camY-10 && p.y < camY+Main.height+10)
			p.draw(g);
		}
		game.isInFrame = false;
		
	}
	
	//rotate point
	public static double[] rotate(double x1, double y1,double deg1, double rotatePointX, double rotatePointY){
		double tempx = 0;
		double tempy = 0;
		
		tempx = rotatePointX + (x1-rotatePointX)*Math.cos(deg1) - (y1-rotatePointY)*Math.sin(deg1);
		tempy = rotatePointY + (x1-rotatePointX)*Math.sin(deg1) + (y1-rotatePointY)*Math.cos(deg1);

		return new double[]{tempx,tempy};
	}
	
	//some difficult and annoying math to determine the line between the player and turret 
	//(while also making sure the line wasn's hitting any blocks)
	void playerTurretLine(Graphics2D g, List<Entity> entities, Player player){
		boolean hitsPlayer = false;

		
		for(int i = 0; i < entities.size(); i++){
			Entity currentEntity = entities.get(i);
			if(currentEntity.getX() > camX-100 && currentEntity.getX() < camX+Main.width-300 &&
					currentEntity.getY() > camY-100 && currentEntity.getY() < camY+Main.height+100)
			currentEntity.draw(g);
			
			if(currentEntity.type == 0){
				Turret entityTurret = (Turret)currentEntity.entityType;
				
				if(entityTurret.hasShootPlayer)
					hitsPlayer = true;
				if(currentEntity.distancePlayer <= 500 && entityTurret.fireTime + 50 >= entityTurret.fireSpeed){
					
					int x = tint(currentEntity.getX() + 14);
					int y = tint(currentEntity.getY() + 14);
					double[] xy = rotate(x+1, y, currentEntity.rotation, x, y);

					double x2 =	xy[0] - x;//entityTurret.directionX;
					double y2 = xy[1] - y;//entityTurret.directionY;
					
	
					double number = 250;
					double length = 500;
					Line2D bulletLine = null;
					boolean playerCollide = true;
					boolean collidesWithBlock = false;
					
					// a nice way to make a precice prediction of how far the line can go without hitting something
					for(int j = 0; j < 10; j++){
						bulletLine = new Line2D.Double(x, y, x + tint(x2 * length), y + tint(y2 * length));
						
						boolean plc = player.isColliding(bulletLine);
						if(plc){
							playerCollide = true;
						}
						boolean blockCollide = game.collideWithBlock(bulletLine);
						if(plc || blockCollide){
							collidesWithBlock = blockCollide;
							if( j < 8 && !plc)
								playerCollide = false;
							length -= number;
						
						}else{
							length += number;
						}
						number = number / 2;
					}
					
					entityTurret.playerAlign = playerCollide ||  player.isColliding(bulletLine);
			
					
					if(!collidesWithBlock){
						g.setStroke(new BasicStroke((int) Math.round((entityTurret.fireSpeed - entityTurret.fireTime) / 45.0 * 7)+ 1));
						g.drawLine(x, y, tint(bulletLine.getX2()), tint(bulletLine.getY2()));
					}
				}
			}
		}
	
		player.cantHeal = !hitsPlayer;
		

		
	}
	
	void drawGun(Graphics2D g, double playerX, double playerY, double camX, double camY){
		if(game.targetDegrees < 2){
			double degrees = Math.PI *  game.targetDegrees / 2 - Math.PI/2;
			g.rotate(degrees,tint(playerX - camX) + 22, tint(playerY - camY) + 35);
			drawImage(g, Textures.getImage("Ace.png"), tint(playerX - camX) + 19, tint(playerY - camY) + 24, 0.15);
			g.rotate(-degrees,tint(playerX - camX) + 22, tint(playerY - camY) + 35);
		}else{
			double degrees = Math.PI *  (game.targetDegrees - 0.05) / 2 - Math.PI/2;
			g.rotate(degrees + Math.PI,tint(playerX - camX) + 22, tint(playerY - camY) + 35);
			drawImageFlipped(g, Textures.getImage("Ace.png"), tint(playerX - camX) + 19, tint(playerY - camY) + 24, 0.15);
			g.rotate(-(degrees + Math.PI),tint(playerX - camX + 22),  tint(playerY - camY) + 35);
		}
		
	}
	
	void drawCustomMouse(Graphics2D g){
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		g.drawOval(game.mouseX-12, game.mouseY-12, 24, 24);
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.RED);
		g.drawOval(game.mouseX-12, game.mouseY-12, 24, 24);
		
		g.setStroke(new BasicStroke(1));
		g.setColor(Color.BLACK);
		g.drawLine(game.mouseX-12, game.mouseY, game.mouseX - 3, game.mouseY);
		g.drawLine(game.mouseX+12, game.mouseY, game.mouseX + 3, game.mouseY);
		g.drawLine(game.mouseX, game.mouseY-12, game.mouseX, game.mouseY-3);
		g.drawLine(game.mouseX, game.mouseY+12, game.mouseX, game.mouseY+3);	
	}
	
	void drawImage(Graphics2D g, BufferedImage image, int x, int y, double scale){
		double width = image.getWidth() * scale;
		double height = image.getHeight() * scale;
		g.drawImage(image, x, y, tint(width), tint(height), null);
	}
	
	void drawImageFlipped(Graphics2D g, BufferedImage image, int x, int y, double scale){
		double width = image.getWidth() * scale;
		double height = image.getHeight() * scale;
		g.drawImage(image, x, y, -tint(width), tint(height), null);
	}
	
	int tint(double num){
		return (int)Math.round(num);
	}
	
}
