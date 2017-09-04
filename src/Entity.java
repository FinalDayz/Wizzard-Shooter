import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Entity {
	double x;
	double y;
	double rotation = 0;
	int type = -1;
	boolean stopBullet = true;
	boolean playerCollide = false;
	Object entityType;
	Game game;
	double distancePlayer = 0;
	
	Animation entityAnimation;
	
	Entity(Game game, double x, double y, int type){
		this.x = x;
		this.y = y;
		this.type = type;
		this.game = game;
		//define the entity Object
		defineEntityType();
	}
	
	//call the loop on the entity object (probably could have done better)
	void loop(Game game){
		if(type == 0){
			((Turret) entityType).loop(game, this);
		}else if(type == 1){
			((Coin) entityType).loop(game, this);
		}else if(type == 2){
			if(entityAnimation.hasStopped()){
				game.entities.remove(this);
			}
		}else if(type == 3){
			((Spider) entityType).loop();
		}
		
	}
	
	//get hits by a projectile type, returns the health
	double hit(double damage){
		if(type == 0){
			return ((Turret) entityType).hit(damage);
		}else if(type == 3){
			return ((Spider) entityType).hit(damage);
		}
		
		return 0;
	}
	
	double getX(){
		return x;
	}
	
	double getY(){
		return y;
	}
	
	//for every type there is a class
	void defineEntityType(){
		if(type == 0){
			entityType = new Turret(game, this);
		}else if(type == 1){
			entityType = new Coin(game, this);
		}else if(type == 2){
			entityAnimation = new Animation(Textures.getImage("Item-Coin-Animation.png"), 15, 8, 59, 1, false);
		}else if(type == 3){
			entityType = new Spider(game, this);
			entityAnimation = new Animation(Textures.getImage("Enemy-Spider-Animation.png"), 40, 5, 74, 1, true);
		}
	}
	
	boolean isColliding(Rectangle2D rect){
		if(type == 0){
			return ((Turret) entityType).isColliding(rect);
		}else if(type == 1){
			return ((Coin) entityType).isColliding(rect);
		}else if(type == 3){
			return ((Spider) entityType).isColliding(rect);
		}
		return false;
	}
	
	boolean isColliding(Line2D line){
		if(type == 0){
			return ((Turret) entityType).isColliding(line);
		}else if(type == 1){
			return ((Coin) entityType).isColliding(line);
		}else if(type == 3){
			return ((Spider) entityType).isColliding(line);
		}
		return false;
	}
	
	//rotate and draw the entity 
	void draw(Graphics2D g){
		BufferedImage texture = getTexture();
		g.rotate(rotation, x+14, y+14);
		if(type != 3)
			g.drawImage(texture, tint(x), tint(y), null);
		g.rotate(-rotation, x+14, y+14);
		
		double percent = 0;
		double width = 0;
		if(type == 0){
			percent = ((Turret) entityType).health / ((Turret) entityType).maximumHealth;
			width = ((Turret) entityType).width;			
		}else if(type == 3){
			
			g.drawImage(texture, tint(x), tint(y), tint(texture.getWidth() * 0.7), tint(texture.getHeight() * 0.7), null);
			
			percent = ((Spider) entityType).health / ((Spider) entityType).maximumHealth;
			width = ((Spider) entityType).width;
		}
		
		if(type == 3 || type == 0){
			g.setColor(Color.GRAY);
			g.setStroke(new BasicStroke(1));
			
			g.drawRect(tint(x)-1, tint(y + width + 2), tint(width) + 1, 7);
			g.setColor(Color.RED);
			g.fillRect(tint(x), tint(y + width + 2) + 1, tint(width * percent), 6);
		}
		
		
	}
	
	BufferedImage getTexture(){
		if(type == 0)
			return Textures.getImage("Turret.png");
		else if(type == 1)
			return Textures.getImage("Item-Coin.png");
		else if(type == 2)
			return entityAnimation.getFrame();
		else if(type == 3)
			return entityAnimation.getFrame();

		return null;
		
	}
	
	
	int tint(double num){return (int)Math.round(num);}
}
