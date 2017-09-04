
public class Weapon {
	int type;
	//I didn't really had time to make another weapon (probably should have started sooner)
	double[] fireSpeed = {	30};
	double[] damage = {	30};
	
	Weapon(int type){
		this.type = type;
	}
	
	String getDescription(){
		return getDescription(type);
	}
	
	String getDescription(int type){
		//ace
		if(type == 0){
			
		}
		return "";
	}
	
	double getDamage(int type){
		return damage[type];
	}
	
	double getDamage(){
		return damage[type];
	}
}
