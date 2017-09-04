import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

//A player object to save some info about it
public class Player {
	
	//0 = right 1 = left
	int direction = 0;
	//position
	double x;
	double y;
	
	//coins collected
	int coins = 0;

	//some rectangles for collision detect
	Rectangle2D[] shape;
	
	double health = 0;
	double maximumHealth = 100;
	double healthRegeneration = 0.02;
	
	//can't heal if a enemy is close
	boolean cantHeal = false;
	
	int fireSpeed = 30; 
	int fireTick = fireSpeed;
	
	Weapon weapon;
	
	Player(double x, double y){
		this.x = x;
		this.y = y;
		
		//create collision rectangles
		shape = new Rectangle2D[]{
				new Rectangle2D.Double(x+5, y, Main.playerWidth - 10, 23),
				new Rectangle2D.Double(x, y+23, Main.playerWidth, Main.playerHeight - 23)
		};
		health = maximumHealth;
	}
	
	//runs every game loop
	void loop(){
		
		//bullet fire and health regeneration
		if(fireTick < fireSpeed)
			fireTick ++;
		if(health < maximumHealth - healthRegeneration)
			health += healthRegeneration;
		else
			health = maximumHealth;
		
		//update collision rectangles
		shape = new Rectangle2D[]{
				new Rectangle2D.Double(x+5, y, Main.playerWidth - 10, 23),
				new Rectangle2D.Double(x, y+23, Main.playerWidth, Main.playerHeight - 23)
		};
	}
	
	void setWeapon(int weaponType){
		weapon = new Weapon(weaponType);
	}
	
	boolean canFire(){
		return (fireTick >= fireSpeed);
	}
	
	void fire(){
		fireTick = 0;
	}
	
	void heal(double health){
		this.health += health;
		if(this.health >= maximumHealth)
			this.health = maximumHealth;
	}
	
	//player died
	void dead(List<Entity> entities) {
		health = maximumHealth;
		//all turrets reset
		for(int i = 0; i < entities.size(); i++){
			Entity currentEntity = entities.get(i);
			
			if(currentEntity.type == 0){
				((Turret)currentEntity.entityType).hasShootPlayer = false;
			}
		}
	}
	
	boolean damage(double damage){
		health -= damage;
		if(health < 0)
			return true;
		return false;
	}
	
	//get hits by a projectile type, returns the health
	boolean hit(double damage){
		health -= damage;
		if(health < 0)
			return true;
		return false;
	}
	
	
	//line and player collide
	boolean isColliding(Line2D line){
		for(int i = 0; i < shape.length; i++){
			if(line.intersects(shape[i])){
				return true;
			}
		}
		return false;
	}
	
	//rectangle and player collide
	boolean isColliding(Rectangle2D rectangle){
		for(int i = 0; i < shape.length; i++){
			if(rectangle.intersects(shape[i])){
				return true;
			}
		}
		return false;
	}
	
	void setDirection(int direction){
		this.direction = direction;
	} 
	
	double getX(){
		return x;
	}
	
	double getY(){
		return y;
	}
	
	void setX(double x){
		this.x = x;
	}
	
	void setY(double y){
		this.y = y;
	}
	
	int getXInt(){
		return (int)Math.round(x);
	}
	
	int getYInt(){
		return (int)Math.round(y);
	}
	
	double getCenterX(){
		return x + Main.playerWidth / 2;
	}
	
	double getCenterY(){
		return y + Main.playerHeight / 2;
	}
	
	int getDirection(){
		return direction;
	}

	
}
