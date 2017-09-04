import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Game {
	
	//list of game blocks
	List<Block> gameBlocks = new ArrayList<Block>();
	
	//list of blocks that are close to the player 
	List<Block> renderingBlocks = new ArrayList<Block>();
	
	//list of projectiles
	List<Projectile> projectiles = new ArrayList<Projectile>();
	
	//list of entities (including coins)
	List<Entity> entities = new ArrayList<Entity>();
	Player player;
	
	Listeners listeners;
	Rendering renderer;
	
	
	//walk speed
	double verticalPlayerSpeed = 2;
	double horizontalPlayerSpeed = 2;
	
	//green and blue blocks
	boolean isInWalkThroughBlock = false;
	
	//time player can't move
	int standStill = 100;
	
	//because there are multiple threads (game loop and game draw), wait for eachother to finish
	boolean isInUpdate = false;
	boolean isInFrame = false;
	long updates = 0;
	
	//int cameraFollowSpace = 0;
	
	int mouseX = 0;
	int mouseY = 0;
	
	//waypoint for the player
	int lastSaveX = 40;
	int lastSaveY = 50;
	
	double targetDegrees = 0;
	
	Game(){
		
		//create game blocks from an image
		BufferedImage gameImage = Textures.getImage("Map.png");
		int width = gameImage.getWidth();
		int height = gameImage.getHeight();
		
		//for each pixel make a block
		for(int y = 0; y < height; y++)
			for(int x = 0; x < width; x++){
				Color pixelColor = new Color(gameImage.getRGB(x, y));
				//if the 'block' is an entity
				int entityType = getEntityType(pixelColor);
				if(entityType != -1){
					entities.add(new Entity(this, x * Main.blockWidth, y * Main.blockWidth, entityType));
				}else if(!pixelColor.equals(Color.WHITE)){
					gameBlocks.add(new Block(x, y, pixelColor));
				}
			}
		
		
		
		
		//create player
		player = new Player(lastSaveX, lastSaveY);
		player.setWeapon(0);
		
		//only render blocks that are close to the player
		updateRenderedBlocks();
		
		renderer = new Rendering(this);
	}
	
	//wait for the frame to render before continuing
	void waitForRender(){
		//if the system is rendering a frame, wait until its over, then continue
		while(isInFrame){
			try {
				Thread.sleep(0,1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//precise calling of the game loop
	void callLoop(final double timesPerSecond){
		final double delayTime = 1000 / timesPerSecond;

		new Thread(new Runnable(){
			public void run() {
				double prefFrame = 0;
				
		   		while(true){

		   			double beginTime = getMilis();
		   			
		   			if(prefFrame != 0){
		   		    	while(getMilis() - prefFrame < delayTime - 1){
		   		    		try {
		   						Thread.sleep(1);
		   					} catch (InterruptedException e) {
		   						e.printStackTrace();
		   					}
		   		    	}
		   	    	}
		   	    	prefFrame = getMilis();
		   			
		     		gameLoop();
		     		
		     		//just to make sure the game is running at exactly X updates per second
		     		while(getMilis() - beginTime < delayTime - 1){
		     			try {
		     				long delay = (long) Math.round((delayTime - 1) - (getMilis() - beginTime));
		     				if(delay > 0)
		     					Thread.sleep(delay);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		     		}
		     		
		   		}
			}
		}).start();
		
    	
	}
	
	//loop that runs 100 times a second
	void gameLoop(){
		updates++;
		
		waitForRender();
    	
    	isInUpdate = true;
		
		//save the variables in local ones to make sure that they won't change while the loop is running
		double playerX = player.getX();
		double playerY = player.getY();
		double centerX = player.getCenterX();
		double centerY = player.getCenterY();
		double camX = renderer.cameraX;
		double camY = renderer.cameraY;
		
		double playerSpeedX = horizontalPlayerSpeed;
		double playerSpeedY = verticalPlayerSpeed;
		
		//key ctrl
		if(listeners.keyPressed[17]){
			playerSpeedX = 1;
			playerSpeedY = 1;
		}

		
		if(standStill > 0)
			standStill--;

		
    	// D is pressed
    	if(listeners.keyPressed[68] && standStill <= 0){
    		playerX += playerSpeedX;
    		player.direction = 0;
    		if(collideWithBlock(new Rectangle2D.Double(playerX, playerY, Main.playerWidth, Main.playerHeight), 6)){
    			playerX -= playerSpeedX;
    		}

    	}
    	
    	// A is pressed
    	if(listeners.keyPressed[65] && standStill <= 0){
    		playerX -= playerSpeedX;
    		if(collideWithBlock(new Rectangle2D.Double(playerX, playerY, Main.playerWidth, Main.playerHeight))){
    			playerX += playerSpeedX;
    		}
    		player.direction = 1;
    		
    	}
    	// W is pressed
    	if(listeners.keyPressed[87] && standStill <= 0){
    		playerY -= playerSpeedY;
    		if(collideWithBlock(new Rectangle2D.Double(playerX, playerY, Main.playerWidth, Main.playerHeight), 6)){
    			playerY += playerSpeedY;
    		}
    		
    	}
    	// S is pressed
    	if(listeners.keyPressed[83] && standStill <= 0){
    		
    		playerY += playerSpeedY;
    		if(collideWithBlock(new Rectangle2D.Double(playerX, playerY, Main.playerWidth, Main.playerHeight), 6)){
    			playerY -= playerSpeedY;
    		}
    	}
    	
    	//camera position
		camX = (centerX - Main.gameWidth / 2);
		camY = (centerY - Main.gameWidth / 2);
		
    	if(camX < 0)
    		camX = 0;
    	if(camY < 0)
    		camY = 0;
    	

    	
    	mouseX = listeners.mouseX;
    	mouseY = listeners.mouseY;
    	
    	//move all projectiles
    	for(int i = 0; i < projectiles.size(); i++){
    		Projectile current = projectiles.get(i);
    		current.frame();
			if(current.isDead()){
				projectiles.remove(i);
			}
			double x = current.x;
			double y = current.y;
			
			double distance = Math.max(abs(x - player.getX()), abs(y - player.getY()));
			if(distance >= 2000){
				projectiles.remove(i);
			}
		}
    	
    	//call the loop on all entities
    	for(int i = 0; i < entities.size(); i++){
    		Entity currentEntity = entities.get(i);
    		double x = currentEntity.getX();
			double y = currentEntity.getY();
			
			double distance = Math.max(abs(x - player.x), abs(y - player.y));
			currentEntity.distancePlayer = distance;
			if(distance < 2000){
				currentEntity.loop(this);
			}
    	}
    	player.loop();
    	
    	//projectile Collision detect with an entity or player
    	Point2D playerPos = projectileColission(new Point2D.Double(playerX, playerY));

    	playerX = playerPos.getX();
    	playerY = playerPos.getY();
    	
    	
    	//player collides with blocks with effects (heal, spawnpoint etc.)
    	boolean healed = false;
    	boolean harmed = false;
    	for(int i = 0; i < renderingBlocks.size(); i++){
    		
    		Block currentBlock = renderingBlocks.get(i);
    		
    		if(currentBlock.walkThrough){
    			double blockX = currentBlock.getAbsoluteX();
    			double blockY = currentBlock.getAbsoluteY();
    			
    			if(Math.max(abs(blockX - player.getCenterX()), abs(blockY - player.getCenterY())) < 150){
    				if(player.isColliding(new Rectangle2D.Double(blockX, blockY, Main.blockWidth, Main.blockWidth))){
    					int blockType = currentBlock.getBlockType();
    					//red block
    					if(blockType == 1 && !harmed){
    						harmed = true;
    						if(player.damage(0.4)){
    							player.dead(entities);
    							playerX = lastSaveX;
    							playerY = lastSaveY;
    							standStill = 80;
    						}
    					}
    					
    					//green block, heal
    					if(blockType == 2 && !healed){
    						
    						healed = true;
    						if(player.cantHeal)
    							player.heal(1);
    					}
    					
    					if(blockType == 4 && !collideWithAnyBlock(new Rectangle2D.Double(playerX, playerY, Main.playerWidth, Main.playerHeight), 0, 1, 2, 3, 4, 5, 6)){
    						if(player.cantHeal){
	    						lastSaveX = tint(playerX);
	    						lastSaveY = tint(playerY);
    						}
    					}
    					
    				}
    			}
    		}
    	}
    	
    	//destroy projectile if it hits a block
    	projectileColissionBlock();
    	
    	//colission detect with entity and player
    	playerEntityCollission();
    	
    	//if user pressed the mouse, spawn projectile
    	if(listeners.hasPressed() < 100 && player.canFire()){
    		
    		//some ugly calculations to calculate projectile direction :(
        	double differenceX = (mouseX - (playerX - camX));
        	double differenceY = (mouseY - (playerY - camY));
        	
        	targetDegrees = Math.min(abs(differenceX), abs(differenceY)) / Math.max(Math.abs(differenceX), abs(differenceY));
        	if(!(abs(differenceY) > abs(differenceX)))
        		targetDegrees = (1-targetDegrees) + 1;
        	targetDegrees /= 2;
        	if(differenceY > 0){
        		targetDegrees = (1-targetDegrees) + 1;
        	}
        	
        	if(differenceX < 0){
        		targetDegrees = (1-targetDegrees) + 3;
        	}
    		
    		int  projectileType = 1;
    		
    		differenceX = (mouseX - (centerX - camX));
        	differenceY = (mouseY - (centerY - camY));

    		
    		fireProjectile((centerX ), (centerY ), mouseX + camX, mouseY + camY, 28, player.weapon.getDamage(), 0, projectileType, 0.05);
    		player.fire();
    	}
    	
    	
    	
    	//targetDegrees += 0.1;
    	player.setX(playerX);
    	player.setY(playerY);
    	renderer.cameraX = camX;
    	renderer.cameraY = camY;
    	
    	isInUpdate = false;
    	
    	if(updates % 50 == 0){
    		waitForRender();
    		updateRenderedBlocks();
    	}
    	
	}
	
	//again some ugly calculations to fire the projectile
	void fireProjectile(double sourceX, double sourceY, double destX, double destY, double speed, double damage, int owner, int type, double random){
		double differenceX = (destX - sourceX);
    	double differenceY = (destY - sourceY);
    	
    	double targetDegrees = Math.min(abs(differenceX), abs(differenceY)) / Math.max(Math.abs(differenceX), abs(differenceY));
    	if(!(abs(differenceY) > abs(differenceX)))
    		targetDegrees = (1-targetDegrees) + 1;
    	targetDegrees /= 2;
    	if(differenceY > 0){
    		targetDegrees = (1-targetDegrees) + 1;
    	}
    	
    	if(differenceX < 0){
    		targetDegrees = (1-targetDegrees) + 3;
    	}
		
		int  projectileType = type;
		
		double velocityX = 1;
		double velocityY = 1;
		
		double projectileRotation = Math.PI *  targetDegrees / 2 - Math.PI/2;

		velocityX = (differenceX) / (abs(differenceY) + abs(differenceX)) + (Math.random()-0.5) * random;
		velocityY = (differenceY) / (abs(differenceX) + abs(differenceY)) + (Math.random()-0.5) * random;

		projectiles.add(new Projectile(sourceX, sourceY, velocityX, velocityY, speed, projectileType, projectileRotation, owner, damage));
	}
	
	//loop through the projectiles and every block to test if there is a colission
	void projectileColissionBlock(){
		if(projectiles.size() > 0)
        	for(int i = 0; i < renderingBlocks.size(); i++){
        		
        		Block currentBlock = renderingBlocks.get(i);
        		
        		if(!currentBlock.walkThrough){
    	    		Rectangle blockRect = new Rectangle(currentBlock.getAbsoluteX(), currentBlock.getAbsoluteY(), Main.blockWidth, Main.blockWidth);
    	    		
    	    		for(int j = 0; j < projectiles.size(); j++){
    	    			Projectile p = projectiles.get(j);
    	    			
    	    			//create two lines that represent the bullet, so collision can be detected easely
    	    			
    	    			double x = currentBlock.getAbsoluteX();
    	    			double y = currentBlock.getAbsoluteY();
    	    			
    	    			double distance = Math.max(abs(x - p.x), abs(y - p.y));
    	    			if(distance < 30){

    	    				Line2D[] projectileLines = lineFromProjectile(p);
    		    			
    		    			if(projectileLines[0].intersects(blockRect) || projectileLines[1].intersects(blockRect)){
    		    				projectiles.remove(j);
    		    			}
    	    			}
    	    			
    	
    	    		}
        		}
        	}
	}
	
	
	//if projectile hits an entity, return player position (if the player died)
	private Point2D projectileColission(Point2D playerPos) {
		
		if(projectiles.size() > 0)
    		for(int j = 0; j < projectiles.size(); j++){
    			Projectile p = projectiles.get(j);
    			//if the owner is the player
    			if(p.owner == 0){
    				for(int i = 0; i < entities.size(); i++){
    		    		Entity currentEntity = entities.get(i);
    		    		double x = currentEntity.getX();
    	    			double y = currentEntity.getY();
    	    			
    	    			double distance = Math.max(abs(x - p.x), abs(y - p.y));
    	    			if(distance < 100 && currentEntity.stopBullet){
    	    				//create two lines that represent the bullet, so collision can be detected easely
    	
    	    				Line2D[] projectileLines = lineFromProjectile(p);
    	    				
    		    			//when projectile collides with entity, damage it and maybe kill it
    		    			if(currentEntity.isColliding(projectileLines[0]) || currentEntity.isColliding(projectileLines[1])){
    		    				projectiles.remove(p);
    		    				if(currentEntity.hit(p.getDamage()) <= 0){
    		    					int entityType = currentEntity.type;
    		    					
    		    					entities.remove(i);
    		    					
    		    					//spawn coin when entity dies
    		    					
    		    					if(entityType == 0)
    		    						spawnCoins(tint(Math.random() * 3), x, y, 25);
    		    					else if(entityType == 3)
    		    						spawnCoins(tint(Math.random()* 3 + 2), x, y, 50);
    		    				}
    		    				
    		    			}
    	    			}
    				}
    				//the owner is an entity
	    		}else if(p.owner == 1){
	    			double x = player.getX();
	    			double y = player.getY();
	    			
	    			double distance = Math.max(abs(x - p.x), abs(y - p.y));
	    			if(distance < 100){
	    				//create two lines that represent the bullet, so collision can be detected easely
	    				
		    			Line2D[] projectileLines = lineFromProjectile(p);
	    				
	    				
		    			//when projectile collides with player, damage it and maybe kill it
		    			if(player.isColliding(projectileLines[0]) || player.isColliding(projectileLines[1])){
		    				projectiles.remove(p);
		    				if(player.hit(p.damage)){
		    					player.dead(entities);
		    					playerPos.setLocation(lastSaveX, lastSaveY);
    							standStill = 80;
		    				}
		    				
		    			}
	    			}
	    		}
    			
    			
    		}
		return playerPos;
		
	}
	
	//spawn coin entity
	void spawnCoins(int number, double x, double y, double radius){
		double diameter = radius * 2;
		for(int k = 0; k < number; k ++)
			entities.add(new Entity(this, x+ Math.random() * diameter - radius, y+ Math.random() * diameter - radius, 1));
	}

	//loop through every entity and check if it collides with the player
	private void playerEntityCollission() {
		for(int i = 0; i < entities.size(); i++){
    		Entity currentEntity = entities.get(i);
    		double x = currentEntity.getX();
			double y = currentEntity.getY();
			
			double distance = Math.max(abs(x - player.x), abs(y - player.y));
			if(distance < 100 && currentEntity.playerCollide){
				for(Rectangle2D playerRect : player.shape){
					if(currentEntity.isColliding(playerRect)){
						int type = currentEntity.type;
						if(type == 1){
							//player picked up coin
							player.coins++;
							//add destroy animation
							entities.add(new Entity(this, currentEntity.getX()- currentEntity.getTexture().getWidth(), currentEntity.getY() -  currentEntity.getTexture().getHeight(), 2));
							
							entities.remove(currentEntity);
							
							
						}
					}	
				}
				
			}
		}
		
	}

	//remove blocks in a certain space (not the best way tho....)
	void removeBlocks(int x1, int y1, int x2, int y2){
		
		Block[] blocksToRemove = new Block[gameBlocks.size() + 1];
		int j = 0;
		
		for(int i = 0; i < gameBlocks.size(); i++){
			Block currentBlock = gameBlocks.get(i);
			double x = currentBlock.getX();
			double y = currentBlock.getY();
			if(x >= x1 && x <= x2 &&
			   y >= y1 && y <= y2){
				blocksToRemove[j] = currentBlock;
				
				j++;
			}
		}
		for(int i = 0; i < blocksToRemove.length; i++){
			if(blocksToRemove[i] == null)
				break;
			gameBlocks.remove(blocksToRemove[i]);
		}
		
	}
	
	//create line around a projectile to detect a colission 
	Line2D[] lineFromProjectile(Projectile p){
		if(p.type == 1){
			double[] point0 = rotate(p.x + 20, p.y, p.rotation + Math.PI , p.x, p.y);
			double[] point1 = rotate(point0[0] + 30, point0[1], p.rotation, point0[0], point0[1]);
			double[] point2 = rotate(point0[0] + 3, point0[1], p.rotation + Math.PI / 2, point0[0], point0[1]);
			double[] point3 = rotate(point2[0] + 30, point2[1], p.rotation, point2[0], point2[1]);
			
			Line2D pLine1 = new Line2D.Double(point0[0], point0[1], point1[0], point1[1]);
			Line2D pLine2 = new Line2D.Double(point2[0], point2[1], point3[0], point3[1]);
			
			return new Line2D[] {pLine1, pLine2};
		}else if (p.type == 2){
			Line2D[] lines = new Line2D[]{//						_
					new Line2D.Double(p.x, p.y, p.x + 60, p.y), //
					new Line2D.Double(p.x + 60, p.y, p.x + 60, p.y + 44),//  |
					new Line2D.Double(p.x, p.y, p.x, p.y + 44),// |
					new Line2D.Double(p.x , p.y + 44, p.x + 60, p.y + 44)// _
			};
			return lines;
		}
		return null;
	}
	
	//return damage from projectile type
	public static double projectileDamage(int projectileType){
		//red block damage
		if(projectileType == 2){
			return 0.3;
		}
		//red block damage
		if(projectileType == 3){
			return -0.2;
		}
		//bullet
		if(projectileType == 1){
			return 13;
		}
		
		return 1;
	}
	
	//update array of blocks that are getting rendered
	void updateRenderedBlocks(){

		
		renderingBlocks = new ArrayList<Block>();
		for(int i = 0; i < gameBlocks.size(); i++){
			Block currentBlock = gameBlocks.get(i);
			double x = currentBlock.getAbsoluteX();
			double y = currentBlock.getAbsoluteY();
			
			double distance = Math.max(abs(x - player.getX()), abs(y - player.getY()));
			if(distance < 2200){
				renderingBlocks.add(currentBlock);
			}
		}

	}
	
	int getEntityType(Color color){
		for(int i = 0; i < Main.entityTypes.length; i++){
			if(color.equals(Main.entityTypes[i])){
				return i;
			}
		}
		return -1;
	}

	void paintGame(Graphics2D g){
		renderer.RenderGame(g, player, renderingBlocks, projectiles, entities);
	}
	
	static boolean collideRect(Rectangle2D rect1, Rectangle2D rect2){
		return rect1.intersects(rect2);
	}
	
	static boolean collideRect(Line2D rect1, Rectangle2D rect2){
		return rect1.intersects(rect2);
	}
	
	
	double abs(double num){
		return Math.abs(num);
	}
	
	
	//Collision detection
	boolean collideWithBlock(Rectangle2D playerRect, int... ignoreTypes){
		return collideBlock(playerRect, false, ignoreTypes);
	}
	
	boolean collideWithAnyBlock(Rectangle2D playerRect, int... ignoreTypes){
		return collideBlock(playerRect, true, ignoreTypes);
	}
	
	//player collides with block with some parameters :
	//ignoreWalkThrough : ignore blocks that the player cna walk through(green heal, blue sapwnpoint), 
	//ignoreTypes : ignore block types in this array
	boolean collideBlock(Rectangle2D playerRect, boolean ignoreWalkThrough, int... ignoreTypes){
		boolean checkIgnore = ignoreTypes.length > 0;
		
		for(int i = 0; i < renderingBlocks.size(); i++){
    		boolean ignore = false;
    		Block thisBlock = renderingBlocks.get(i);
    		
    		//if there are blocks to ignore
    		if(checkIgnore){
    			for(int j = 0; j < ignoreTypes.length; j++){
    				if(ignoreTypes[j] == thisBlock.getBlockType()){
    					ignore = true;
    				}
    			}
    		}

    		//if the block isn't being ignored
    		if(!(thisBlock.walkThrough && !ignoreWalkThrough) && !ignore){

    			double x = thisBlock.getAbsoluteX();
    			double y = thisBlock.getAbsoluteY();
    			
    			double distance = Math.max(abs(x - playerRect.getX()), abs(y - playerRect.getY()));
    			if(distance < 2000)
    				if(collideRect(playerRect, new Rectangle2D.Double(thisBlock.getAbsoluteX(), thisBlock.getAbsoluteY(), Main.blockWidth, Main.blockWidth))){
    					return true;
    				}
	    	}
    	}
		return false;
	}
	
	//Collision detection with line and block
	boolean collideWithBlock(Line2D line){
		for(int i = 0; i < renderingBlocks.size(); i++){
			Block thisBlock;
    		try{
	    		thisBlock = renderingBlocks.get(i);
    		}catch(IndexOutOfBoundsException e){
    			return collideWithBlock(line);
    		}
    		if(!thisBlock.walkThrough){
    			double x = thisBlock.getAbsoluteX();
    			double y = thisBlock.getAbsoluteY();
    			
    			double distance = Math.max( Math.max(abs(x - line.getX1()), abs(y - line.getY1())),
    										Math.max(abs(x - line.getX2()), abs(y - line.getY2())));
    			if(distance < 501)
	    		if(collideRect(line, new Rectangle2D.Double(thisBlock.getAbsoluteX(), thisBlock.getAbsoluteY(), Main.blockWidth, Main.blockWidth))){
	    			return true;
	    		}
	    	}
    	}
		return false;
	}
	
	
	
	//rotate point around a other point 
	public static double[] rotate(double x1, double y1,double deg1, double rotatePointX, double rotatePointY){
		double tempx = 0;
		double tempy = 0;
		
		tempx = rotatePointX + (x1-rotatePointX)*Math.cos(deg1) - (y1-rotatePointY)*Math.sin(deg1);
		tempy = rotatePointY + (x1-rotatePointX)*Math.sin(deg1) + (y1-rotatePointY)*Math.cos(deg1);

		return new double[]{tempx,tempy};
	}

	
	  double getMilis(){
	    	return (double) (System.nanoTime() / 1000000.0);
	    }
	  
	  int tint(double num){return (int)Math.round(num);}
}
