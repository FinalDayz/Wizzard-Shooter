import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Coin {
	
	double width = 30;
	
	Rectangle2D[] shape;
	Game game;
	Entity entity;
	
	boolean goingUp = true;
	int animateLoop = 0;
	int endLoop = 150;
	double animateSpeed = 0.1;
	
	Coin(Game game, Entity entity){
		this.game = game;
		this.entity = entity;
		shape = new Rectangle2D[]{new Rectangle2D.Double(entity.getX(), entity.getY(), width, width)};
		animateLoop = (int) Math.round(Math.random() * endLoop);
		
		entity.stopBullet = false;
		entity.playerCollide = true;
		
		//start at random position
		if(Math.random() > 0.5)
			goingUp = !goingUp;
	}
	
	void loop(Game game, Entity entity){
		if(goingUp){
			animateLoop++;
			entity.y -= animateSpeed;
			if(animateLoop >= endLoop){
				animateLoop = 0;
				goingUp = false;
			}
		}
		
		if(!goingUp){
			animateLoop++;
			entity.y += animateSpeed;
			if(animateLoop >= endLoop){
				animateLoop = 0;
				goingUp = true;
			}
		}
		
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
