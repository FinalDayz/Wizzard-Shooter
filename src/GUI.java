import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class GUI {
	Player player;
	Game game;
	
	double beginMiliseconds;
	
	//variables for the introduction text
	int helpIndex = 0;
	double maxWhiteTimer = 10;
	double whiteTimer;
	double stayWhite = 70;
	
	boolean isShowingHelp = true;
	
	double[] helpTextShow = new double[]{
			7, //w a s d
			5, //black blocks
			10,//CTRL
			8, //Red blocks
			17, //Green blocks
			7, //shoot
			6, //turrets
			7, //cyan blocks
			4,
			
	};
	
	int[][] removeBlocks = new int[][]{
			{},	//w a s d
			{}, //black blocks
			{26,14, 26, 22}, //CTRL
			{46,16,46,22}, //Red blocks
			{}, //Green blocks
			{}, //shoot
			{152, 1, 152, 35}, //turrets
			{}, //cyan blocks
			{} // good luck
	};
	
	String[] helpText = new String[]{
			"Press W, A, S, D to move.",
			"You can't move through \nblack blocks.",
			"Press CTRL while moving \nto move more slowly",
			"Red blocks damage you \nif you walk through them.",
			"Green blocks heal you \nwhen standing on it.",
			"Click left mouse to shoot.",
			"Turrets can also shoot bullets \nto harm you.",
			"Cyan blocks in a checker format, \nwill create a spawn point",
			"Good luck!"
			
			
	};
	
	GUI(Player player, Game game){
		this.player = player;
		beginMiliseconds = System.currentTimeMillis() / 1000.0;
		this.game = game;
		whiteTimer = maxWhiteTimer;
	}
	
	//draw the GUI on the right side of the game
	void drawGUI(Graphics2D g){
		g.setColor(Color.GRAY);
		//background
		g.fillRect(Main.width - Main.GUIWidth  , 0, Main.GUIWidth, Main.height);
		g.translate(Main.width - Main.GUIWidth, 0);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.PLAIN, 15));
		drawString(g, "FPS: "+Math.round(Canvas.fps*100)/100.0, 200, 3);
		
		showHealth(g);
		
		//show gray heping text
		if(isShowingHelp)
			showHelpText(g, System.currentTimeMillis() / 1000.0);
		
		//draw coin count
		g.drawImage(Textures.getImage("Item-Coin-Big.png"), 23, 110, 30, 30, null);
		
		g.setColor(new Color(210, 210, 100));
		g.drawString("X "+game.player.coins, 60, 135);

		//draw weapon and ammo
		drawWeapon(g);
		
		g.translate(-(Main.width - Main.GUIWidth), 0);
		
		
	}
	
	//draw weapon with the ammo and ammo count
	void drawWeapon(Graphics2D g){
		g.setFont(new Font("Arial",  Font.BOLD, 24));
		
		g.setColor(Color.BLACK);
		drawString(g, "Weapon: ", 13, 160);
		drawImage(g, Textures.getImage("Ace.png"), 13, 200, imageSizeMax(Textures.getImage("Ace.png"), 150, 120));
		
		drawString(g, "Bullet: ", 200, 160);
		int[] size = imageSizeMax(Textures.getImage("Ace-bullet.png"), 50, 100);
		g.drawImage(Textures.getImage("Ace-bullet.png"), 200, 200, size[0], size[1], null);
		
	}
	
	//show the gray help text for X amount of seconds
	void showHelpText(Graphics2D g, double time){
		double timeSinceStart = time - beginMiliseconds;

		if(helpTextShow[helpIndex] <= timeSinceStart){
			beginMiliseconds += helpTextShow[helpIndex];
			
			
			maxWhiteTimer = 30;
			whiteTimer = 30;
			stayWhite = 50;
			helpIndex++;
		}
		
		
		if(helpIndex >= helpTextShow.length){
			isShowingHelp = false;
			return;
		}
		
		if(stayWhite > 0)
			stayWhite --;
		else if(whiteTimer >= 0){
			whiteTimer --;
		}

		
		int rgbPlus = (int) Math.round(whiteTimer / maxWhiteTimer * (255 - 180));
		
		g.setColor(new Color(180 + rgbPlus, 180 + rgbPlus, 180 + rgbPlus));
		
		g.fillRoundRect(-650, 630, 380, 100, 10, 10);
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial",  Font.BOLD, 24));
		

		drawString(g, helpText[helpIndex], -640, 640);
		if(removeBlocks[helpIndex].length  == 4){
			int[] blocks = removeBlocks[helpIndex];
			game.removeBlocks(blocks[0], blocks[1], blocks[2], blocks[3]);
			removeBlocks[helpIndex] = new int[]{};
		}
		

	}
	
	void drawImage(Graphics2D g, BufferedImage image, int x, int y, double scale){
		double width = image.getWidth() * scale;
		double height = image.getHeight() * scale;
		g.drawImage(image, x, y, tint(width), tint(height), null);
	}

	void drawImage(Graphics2D g, BufferedImage image, int x, int y, int[] size){
		double width = size[0];
		double height = size[1];
		g.drawImage(image, x, y, tint(width), tint(height), null);
	}
	
	//draw image with max size
	int[] imageSizeMax(BufferedImage image, int maxWidth, int maxHeight){
		double width = image.getWidth();
		double height = image.getHeight();
		
		if(width < maxWidth && height < maxHeight){
			return new int[]{tint(width), tint(height)};
		}else{
			double widthOff = width / maxWidth;
			double heightOff = height / maxHeight;
			if(widthOff > heightOff){
				//only change width (with same ratio)
				width = width / widthOff;
				height = height / widthOff;
			}else{
				//only change height (with same ratio)
				width = width / heightOff;
				height = height / heightOff;
			}
			return new int[]{tint(width), tint(height)};
		}
	}
	

	
	void drawString(Graphics2D g, String text, int x, int y) {
	    for (String line : text.split("\n"))
	        g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}
	
	void showHealth(Graphics2D g){
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial",  Font.BOLD, 25));
		
		g.drawString("Health:", 20, 33);
		
		g.setStroke(new BasicStroke(4));
		
		g.drawRect(20, 50, 250, 40);
		
		g.setColor(Color.GREEN);
		

		g.fillRect(23, 53, (int) Math.round(player.health / player.maximumHealth * 245), 35);
	}
	
	int tint(double x){
		return (int) Math.round(x);
	}
}
