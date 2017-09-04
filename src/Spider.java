import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Spider {
	Game game;
	Entity entity;
	Rectangle2D[] shape;
	
	boolean isWalking = false;

	//basic variables
	int fireTime = 0;
	double health = 0;
	double width = 52; // 74
	double height = 56; // 80
	double maximumHealth = 150;
	double healthRegeneration = 0.1;
	int fireSpeed = 150;
	
	boolean hasShootPlayer = false;
	
	int projectileDamage = 18;
	
	double velocityX = 0;
	double velocityY = 0;
	
	Point2D origionalPos;
	
	int loop = 0;
	
	Spider(Game game, Entity entity){
		this.game = game;
		this.entity = entity;
		health = maximumHealth;
		shape = new Rectangle2D[]{new Rectangle2D.Double(entity.getX(), entity.getY(), width, width)};
		origionalPos = new Point2D.Double(entity.x, entity.y);
	}
	
	//Make the pricer come alive!!!
	//let the AI do its thing
	void loop(){
		loop++;
		loop = loop % 100;
		
		if(health < maximumHealth)
			health += healthRegeneration;
		else
			health = maximumHealth;
		
		velocityX = velocityX * 0.90;
		velocityY = velocityY * 0.90;
		
		//some random movement
		if(loop % (int) Math.round(Math.random()*30 + 40) == 0){
			if(Math.random() > 0.1){
				velocityX += (Math.random()-0.5) * 20;
			}
		
			if(Math.random() > 0.1){
				velocityY += (Math.random()-0.5) * 20;
			}
		}
		
		//always be sure to go back to the origional position slowly
		if(Math.abs(origionalPos.getX()-entity.x) > 50){
			velocityX += (origionalPos.getX()-entity.x) / entity.x * 0.1;
		}
		
		if(Math.abs(origionalPos.getY()-entity.y) > 50){
			velocityY += (origionalPos.getY()-entity.y) / entity.y * 0.1;
		}
		
		//only move it it doesnt collide with anything
		entity.x += velocityX;
    	if(game.collideWithBlock(new Rectangle2D.Double(entity.x, entity.y, width, height))){
    		entity.x -= velocityX;
    	}
    	
    	entity.y += velocityY;
    	if(game.collideWithBlock(new Rectangle2D.Double(entity.x, entity.y, width, height))){
    		entity.y -= velocityY;
    	}

    	shape[0].setRect(entity.x, entity.y, shape[0].getWidth(), shape[0].getHeight());
    	
    	
    	shootAtPlayer();
    
	}

	void shootAtPlayer(){
		if(fireTime < fireSpeed)
			fireTime++;
		
		double differenceX = (game.player.getCenterX() - (entity.x + 14));
    	double differenceY = (game.player.getCenterY() - (entity.y + 14));
    	
		double directionX = (differenceX) / (abs(differenceY) + abs(differenceX));
		double directionY = (differenceY) / (abs(differenceX) + abs(differenceY));
		
		
		double x = 1 / Math.max(abs(directionX), abs(directionY));
		directionX = directionX * x;
		directionY = directionY * x;
		
		if(entity.distancePlayer >= 500){
			hasShootPlayer = false;
		}
		
			//turret is looking at player, so it can shoot now
			
			if(fireTime == fireSpeed && entity.distancePlayer < 500){
				hasShootPlayer = true;
			//	game.projectiles.add(new Projectile(entity.x + 14, entity.y + 14, velocityX, velocityY, 19, 1, projectileRotation, 1, projectileDamage));
				game.fireProjectile(entity.x + width / 2, entity.y + height / 2, game.player.getCenterX(), game.player.getCenterY(), 
			//	speed	damage	owner	type	random						
				8, 	20, 	1, 		2, 		0.01);
				
		//void fireProjectile(double sourceX, double sourceY, double destX, double destY, double speed, double damage, int owner, int type, double random){
				fireTime = (int) Math.round(Math.random() * 16 - 8);
			}
		
	}
	
	double abs(double x){
		return Math.abs(x);
	}
	
	//get hits by a projectile type, returns the health
	double hit(double damage){
		health -= damage;
		return health;
	}
	
	boolean isColliding(Rectangle2D rect){
		for(int i = 0; i < shape.length; i++){
			if(shape[i].intersects(rect)){
				return true;
			}
		}
		return false;
	}
	
	boolean isColliding(Line2D line){
		for(int i = 0; i < shape.length; i++){
			if(line.intersects(shape[i])){
				return true;
			}
		}
		return false;
	}
	
}
