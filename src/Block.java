import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Block {
	int x;
	int y;
	int blockType;
	boolean walkThrough = false;
	int blockPropety = 0;
	
	//initialize some info about the block
	Block(int x, int y, int blockType){
		this.x = x;
		this.y = y;
		this.blockType = blockType;
		walkThrough = isWalkThrough(blockType);
		blockPropety();
	}
	
	Block(int x, int y, Color blockType){
		this.x = x;
		this.y = y;
		int type = getBlockTypeFromColor(blockType);
		this.blockType = type;
		
		walkThrough = isWalkThrough(type);
		blockPropety();
	}
	
	void blockPropety(){
		if(blockType == 2)
			blockPropety = random(1, 5);
	}
	
	boolean isWalkThrough(int type){
		return (type == 1 || type == -1 || type == 2 || type == 4 || type == 7);
	}
	
	int getX(){
		return x;
	}
	
	int getY(){
		return y;
	}

	int getAbsoluteX(){
		return x * Main.blockWidth;
	}
	
	int getAbsoluteY(){
		return y * Main.blockWidth;
	}
	
	int getBlockType(){
		return blockType;
	}
	
	
	//return a block ID based on a color
	int getBlockTypeFromColor(Color blockColor){
		for(int i = 0; i < Main.blockTypes.length; i++){
			if(blockColor.equals(Main.blockTypes[i])){
				return i;
			}
		}
		return -1;
	}
	
	//only used without textures
	Color getTextureColor(){
		if(blockType == 0)
			return Color.BLACK;
		if(blockType == 1)
			return Color.RED;
		if(blockType == 2)
			return Color.GREEN;
		if(blockType == 3)
			return Color.ORANGE;
		if(blockType == 4)
			return new Color(0, 255, 255);
		return Color.WHITE;
		
	}
	
	//return the texture based on the type (Waarschijnlijk beter on een switch te gebruiken lol xD)
	BufferedImage getTextureImage(Player player){
		if(blockType == 3)
			return Textures.getImage("Turret.png");
		
		if(blockType == 1)
			return Textures.getImage("Block-Damage.png");
		
		if(blockType == 2)
			if(!player.cantHeal)
				return Textures.getImage("Block-Heal"+blockPropety+"Disabled.png");
			else
				return Textures.getImage("Block-Heal"+blockPropety+".png");
		
		if(blockType == 0)
			return Textures.getImage("Block-Sollid.png");
		
		if(blockType == 4)
			if(!player.cantHeal)
				return Textures.getImage("Block-Spawn"+(((y % 2) + (x % 2) ))+"Disabled.png");
			else
				return Textures.getImage("Block-Spawn"+(((y % 2) + (x % 2) ))+".png");
		
		if(blockType == 5)
			return Textures.getImage("Block-Despawn.png");
		
		if(blockType == 6)
			return Textures.getImage("Block-OneWay"+y % 2+".png");
		
		return null;
	}
	
	int random(int Low, int High){
		Random r = new Random();
		return r.nextInt((High+1)-Low) + Low;
	}
	
	boolean isColor(){

		return false;

	}
}
