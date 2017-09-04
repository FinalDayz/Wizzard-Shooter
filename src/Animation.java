import java.awt.Frame;
import java.awt.image.BufferedImage;

public class Animation {
	
	BufferedImage animationFrames;
	BufferedImage[] frames;
	
	int frameCount, frameWidth, miliSecondPerFrame;
	int skipPixels = 1;
	
	//frame information
	int currentFrame = 0;
	long lastFrameShow = 0;
	
	boolean repeat = true;
	boolean hasStopped = false;
	
	Animation(BufferedImage animationFrames, int miliSecondPerFrame, int frames, int frameWidth, int skipPixels, boolean repeat){
		this.miliSecondPerFrame = miliSecondPerFrame;
		this.frameCount = frames;
		this.frameWidth = frameWidth;
		this.skipPixels = skipPixels + 1;
		this.animationFrames = animationFrames;
		this.repeat = repeat;
		
		
		createFrames();
	}
	
	Animation(BufferedImage animationFrames, int miliSecondPerFrame, int frames, int frameWidth, boolean repeat){
		this.miliSecondPerFrame = miliSecondPerFrame;
		this.frameCount = frames;
		this.frameWidth = frameWidth;
		this.animationFrames = animationFrames;
		this.repeat = repeat;
		
		createFrames();
	}
	
	//get the frame and change to the next frame if it is time to
	BufferedImage getFrame(){

		if(lastFrameShow == 0){
			lastFrameShow = System.currentTimeMillis();
		}
		if(lastFrameShow + miliSecondPerFrame < System.currentTimeMillis()){
			currentFrame++;
			lastFrameShow = System.currentTimeMillis();
		}
		if(repeat)
			currentFrame = currentFrame % frameCount;
		if(currentFrame == frameCount){
			hasStopped = true;
			return null;
		}
		if(currentFrame+1 > frames.length)
			return null;
		return frames[currentFrame];
	}
	
	boolean hasStopped(){
		return hasStopped;
	}
	
	void skipFrame(){
		currentFrame++;

		currentFrame = currentFrame % frameCount;
		lastFrameShow = System.currentTimeMillis();
	}
	
	void resetFrames(){
		lastFrameShow = System.currentTimeMillis();
		currentFrame = 0;
	}
	
	void createFrames(){
		frames = new BufferedImage[frameCount];
		for(int i = 0; i < frameCount; i++){
			frames[i] = animationFrames.getSubimage(i * frameWidth + skipPixels * i, 0, frameWidth, animationFrames.getHeight());
		}
	}
	
	
}
