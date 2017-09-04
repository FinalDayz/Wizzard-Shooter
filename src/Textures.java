import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class Textures {
	static File[] texturesFiles;
	static BufferedImage[] textures;
	
	static List<BufferedImage> textures2 = new ArrayList<BufferedImage>();
	static List<String> fileNames2 = new ArrayList<String>();

	static String[] fileNames;
	
	static boolean inJar = false;
	
	//load all textures in arrays
	static void loadTextures() throws IOException{
		texturesFiles = new File("Images/").listFiles();
		try{
			textures = new BufferedImage[texturesFiles.length];
			fileNames = new String[texturesFiles.length];
		}catch(Exception e){
			inJar = true;
			texturesFiles = new File("/").listFiles();
			textures = new BufferedImage[texturesFiles.length];
			fileNames = new String[texturesFiles.length];
		
		}
		
		for(int i = 0; i < texturesFiles.length; i++){
			if(texturesFiles[i].getName().indexOf("png") >= 0 || texturesFiles[i].getName().indexOf("jpg") >= 0 || texturesFiles[i].getName().indexOf("gif") >= 0){
				textures[i] = ImageIO.read(texturesFiles[i]);
				fileNames[i] = texturesFiles[i].getName();
				
			}else{
				fileNames[i] = "";
			}
		}
		texturesFiles = null;
	}
	
	//get a image from the loaded images
	static BufferedImage getImage(String imageName){
		if(inJar){
			//System.out.println("Read "+imageName);
			BufferedImage image;
			try {
				
				for(int i = 0; i < fileNames2.size(); i++){
					if(fileNames2.get(i).toLowerCase().equals(imageName.toLowerCase()))
						return textures2.get(i);
				}
				for(int i = 0; i < fileNames2.size(); i++){
					if(fileNames2.get(i).toLowerCase().indexOf(imageName.toLowerCase()) == 0)
						return textures2.get(i);
				}
				
				image = ImageIO.read(Textures.class.getResourceAsStream(imageName));
				fileNames2.add(imageName);
				textures2.add(image);
				return image;
				
			} catch (IOException e) {
				System.out.println("Read "+imageName);
				e.printStackTrace();
			}
			
		}
		for(int i = 0; i < fileNames.length; i++){
			if(fileNames[i].toLowerCase().equals(imageName.toLowerCase()))
				return textures[i];
		}
		for(int i = 0; i < fileNames.length; i++){
			if(fileNames[i].toLowerCase().indexOf(imageName.toLowerCase()) == 0)
				return textures[i];
		}
		
		return null;
	}
	

	
}
