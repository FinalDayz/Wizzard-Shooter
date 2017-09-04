import java.awt.Graphics2D;

public class Projectile {
	
	//basic info about projectile
	double x;
	double y;
	double velocityX;
	double velocityY;
	double speed;
	int type;
	double rotation;
	
	int lifeTicks = 0;
	//time when it DIESSSS
	int totalLifeTicks = 330;
	
	double damage;
	
	//0 = player, 1 = hostile entity
	int owner;
	
	//basic sh*t right here :)
	Projectile(double startX, double startY, double velocityX, double velocityY,  double speed, int type, double rotation, int owner, double damage){
		this.x = startX;
		this.y = startY;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.speed = speed;
		this.type = type;
		this.rotation = rotation;
		this.owner = owner;
		this.damage = damage;
	}
	
	//move the projectile a bit
	void frame(){
		x += velocityX * speed;
		y += velocityY * speed;
		
		lifeTicks++;
	}
	
	boolean isDead(){
		return lifeTicks >= totalLifeTicks;
	}
	
	int getLifeTicks(){
		return this.lifeTicks;
	}
	
	double getDamage(){
		return damage;
	}
	
	void draw(Graphics2D g){
		g.rotate(rotation, x, y);
		if(type == 1){
			g.drawImage(Textures.getImage("Ace-bullet.png"), (int) Math.round(x), (int) Math.round(y), 14, 4, null);
		}else if(type == 2){
			g.drawImage(Textures.getImage("SlimeBall-bullet.png"), (int) Math.round(x), (int) Math.round(y), 60, 44, null);
		}
		g.rotate(-rotation, x, y);
	}
}
