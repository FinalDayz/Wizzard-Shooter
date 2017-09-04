import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Turret {
	Player player;
	Rectangle2D[] shape;
	Game game;
	Entity entity;
	
	//some basic variables
	double health = 0;
	int fireTime = 0;
	double directionX, directionY;
	boolean playerAlign = false;
	
	double width = 30;
	double maximumHealth = 100;
	double rotationSpeed = 1;
	double healthRegeneration = 0.06;
	int fireSpeed = 150;
	
	boolean hasShootPlayer = false;
	
	int projectileDamage = 13;
	
	Turret(Game game, Entity entity){
		this.game = game;
		this.entity = entity;
		shape = new Rectangle2D[]{new Rectangle2D.Double(entity.getX(), entity.getY(), width, width)};
		rotationSpeed = rotationSpeed / 360.0 * (Math.PI * 2);
		 health = maximumHealth;
	}
	

	//rotate and fire
	void loop(Game game, Entity entity){
		//make the health go up slowly
		if(entity.distancePlayer >= 450)
			if(health < maximumHealth)
				health += healthRegeneration * 10;
			else
				health = maximumHealth;
		
		if(health < maximumHealth)
			health += healthRegeneration;
		else
			health = maximumHealth;
		
		if(fireTime < fireSpeed)
			fireTime++;
		
		//<START UGLY MATH>
		
		//rotate entity to look to the player
		Player player = game.player;
		double playerX = player.getCenterX();
		double playerY = player.getCenterY();

		//rotate to player
		double differenceX = (playerX - (entity.x + 14));
    	double differenceY = (playerY - (entity.y + 14));
    	
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
    	
    	
		double projectileRotation = Math.PI *  targetDegrees / 2 - Math.PI/2;
		
		if(projectileRotation - entity.rotation > Math.PI){
			entity.rotation += Math.PI * 2;
		}
		
		if(projectileRotation - entity.rotation < -Math.PI){
			entity.rotation -= Math.PI * 2;
		}

		
		double rotationPlus = 0;
		
		directionX = (differenceX) / (abs(differenceY) + abs(differenceX));
		directionY = (differenceY) / (abs(differenceX) + abs(differenceY));
		
		double velocityX = directionX + (Math.random()-0.5) / 15;
		double velocityY = directionY + (Math.random()-0.5) / 15;
		
		double x = 1 / Math.max(abs(directionX), abs(directionY));
		directionX = directionX * x;
		directionY = directionY * x;

		rotationPlus = rotationSpeed;
		if(projectileRotation < entity.rotation)
			rotationPlus = -rotationSpeed;
		
		if(entity.distancePlayer >= 500){
			hasShootPlayer = false;
		}
		//</END UGLY MATH>
		
		if(playerAlign){
			if(abs(rotationPlus- (projectileRotation - entity.rotation)) <= rotationSpeed){
				rotationPlus = projectileRotation - entity.rotation;
			}
			
			//turret is looking at player, so it can shoot now
			if(fireTime == fireSpeed && entity.distancePlayer < 500){
				hasShootPlayer = true;
				game.projectiles.add(new Projectile(entity.x + 14, entity.y + 14, velocityX, velocityY, 19, 1, projectileRotation, 1, projectileDamage));
				
				fireTime = (int) Math.round(Math.random() * 16 - 8);
			}
		}
		entity.rotation += rotationPlus;
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
