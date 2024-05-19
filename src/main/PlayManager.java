package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import mino.Block;
import mino.Mino_Bar;
import mino.Mino_L1;
import mino.Mino_L2;
import mino.Mino_Square;
import mino.Mino_T;
import mino.Mino_Z1;
import mino.Mino_Z2;
import mino.Tetromino;

import javax.swing.*;

public class PlayManager {
	
	// Main play area
	final int WIDTH = 360;
	final int HEIGHT = 600;
	public static int left_x;
	public static int right_x;
	public static int top_y;
	public static int bottom_y;
	
	// Tetromino
	Tetromino currentMino;
	final int MINO_START_X;
	final int MINO_START_Y;
	Tetromino nextMino;
	final int NEXTMINO_X;
	final int NEXTMINO_Y;
	public static ArrayList<Block> staticBlocks = new ArrayList<>();
	
	// Others
	public static int dropInterval = 60;
	boolean gameOver;
	
	// Effect
	boolean effectCounterOn;
	int effectCounter;
	ArrayList<Integer> effectY = new ArrayList<>();
	
	// Score
	int level = 1;
	int lines;
	int score;
	
	public PlayManager() {
		
		// Main play area frame
		left_x = (GamePanel.WIDTH/2) - (WIDTH/2);
		right_x = left_x + WIDTH;
		top_y = 50;
		bottom_y = top_y + HEIGHT;
		
		MINO_START_X = left_x + (WIDTH/2) - Block.SIZE;
		MINO_START_Y = top_y + Block.SIZE;
		
		NEXTMINO_X = right_x + 178;
		NEXTMINO_Y = top_y + 500;
		
		// Set starting mino
		currentMino = pickMino();
		currentMino.setXY(MINO_START_X, MINO_START_Y);
		nextMino = pickMino();
		nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
	}
	
	private Tetromino pickMino() {
		Tetromino mino = null;
		int i = new Random().nextInt(7);
		
		switch(i) {
		case 0: mino = new Mino_L1(); break;
		case 1: mino = new Mino_L2(); break;
		case 2: mino = new Mino_Square(); break;
		case 3: mino = new Mino_Bar(); break;
		case 4: mino = new Mino_T(); break;
		case 5: mino = new Mino_Z1(); break;
		case 6: mino = new Mino_Z2(); break;
		}
		return mino;
	}
	
	public void update() {
		
		// check if currentMino is active
		if(currentMino.active == false) {
			// if the mino is not active, put it into the staticBlocks
			staticBlocks.add(currentMino.b[0]);
			staticBlocks.add(currentMino.b[1]);
			staticBlocks.add(currentMino.b[2]);
			staticBlocks.add(currentMino.b[3]);
			
			// check if the game is over
			if(currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
				// here the current mino immediately collided with a block, & cannot move further
				// so its x & y coordinates are the same as the upcoming Mino
				gameOver = true;
				GamePanel.music.stop();
				GamePanel.se.Play(2, false);
			}
			
			currentMino.deactivating = false;
			
			// replace the currentMino with nextMino
			currentMino = nextMino;
			currentMino.setXY(MINO_START_X, MINO_START_Y);
			nextMino = pickMino();
			nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
			
			// when a mino becomes inactive, check if line(s) can be deleted
			checkDelete();
		}
		else {
		currentMino.update();
		}
		
	}
	
	private void checkDelete() {
		
		int x = left_x;
		int y = top_y;
		int blockCount = 0;
		int lineCount = 0;
		
		while(x < right_x && y < bottom_y) {
			
			for(int i = 0; i < staticBlocks.size(); i++) {
				if(staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
					// increase the block count
					blockCount++;
				}
			}
			
			x += Block.SIZE;
			
			if(x == right_x) {
				
				// if blockcount hits 12, we can delete the line
				if(blockCount == 12) {
					
					effectCounterOn = true;
					effectY.add(y);
					
					for(int i = staticBlocks.size()-1; i > -1; i--) {
						// remove lines
						if(staticBlocks.get(i).y == y) {
							staticBlocks.remove(i);
							
						}
					}
					
					lineCount++;
					lines++;
					// Drop speed increases
					if(lines % 10 == 0 && dropInterval > 1) {
						level++;
						if(dropInterval > 10) {
							dropInterval -= 10;
						}
						else {
							dropInterval -= 1;
						}
					}
					
					//  a line has been deleted, so we slide down all the other blocks
					for(int i = 0; i < staticBlocks.size(); i++) {
						if(staticBlocks.get(i).y < y) {
							staticBlocks.get(i).y += Block.SIZE;
						}
						
					}
					
				}
				
				blockCount = 0;
				x = left_x;
				y += Block.SIZE;
			}
			
		}
		
		// Add Score
		if(lineCount > 0) {
			GamePanel.se.Play(1, false);
			int singleLineScore = 10 * level;
			score += singleLineScore * lineCount;
		}
	}
	
	public void draw(Graphics2D g2) {
		
		// Draw main play area frame
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(4f));
		g2.drawRect(left_x - 4, top_y - 4, WIDTH + 8, HEIGHT + 8);
		
		// Draw next tetromino frame
		int x = right_x + 100;
		int y = bottom_y - 200;
		g2.drawRect(x, y, 200, 200);
		g2.setFont(new Font("Helvetica", Font.BOLD, 30));
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.drawString("NEXT", x + 60, y + 60);
		
		// Draw score frame
		g2.drawRect(x, top_y, 250, 300);
		x += 40;
		y = top_y + 90;
		g2.drawString("LEVEL: " + level, x, y); 
		y += 70;
		g2.drawString("LINES: " + lines, x, y);
		y += 70;
		g2.drawString("SCORE: " + score, x, y);
		
		// Draw currentMino
		if(currentMino != null) {
			currentMino.draw(g2);
			
		}
		
		// Draw nextMino
		nextMino.draw(g2);
		
		// Draw Static Blocks
		for(int i = 0; i < staticBlocks.size(); i++) {
			staticBlocks.get(i).draw(g2);
		}
		
		// Draw effect
		if(effectCounterOn) {
			effectCounter++;

			// Highlighting the completed row, before deleting
			g2.setColor(Color.white);
			for(int i = 0; i < effectY.size(); i++) {
				g2.fillRect(left_x, effectY.get(i), WIDTH, Block.SIZE);
			}
			
			if(effectCounter == 10) {
				effectCounterOn = false;
				effectCounter = 0;
				effectY.clear();
			}
		}
		
		// Draw Pause or Game Over
		g2.setColor(Color.white);
		g2.setFont(g2.getFont().deriveFont(50f));
		if(gameOver) {
			x = left_x + 25;
			y = top_y + 320;
			
			g2.drawString("GAME OVER", x, y);
		}
		else if(KeyHandler.pausePressed) {
			x = left_x + 100;
			y = top_y + 320;
			g2.drawString("PAUSED", x, y);
		}
		
		// Draw game title
		x = 35;
		y = top_y + 150;
		g2.setColor(Color.white);
		g2.setFont(new Font("SAN SERIF", Font.BOLD, 80));
		g2.drawString("DETRIS", x+50, y);

		ImageIcon dispIcon = new ImageIcon((ClassLoader.getSystemResource("display.png")));
		Image icon = dispIcon.getImage();
		//g2.drawImage(icon,x+50 ,y+50);
		g2.drawImage(icon, x+50, y+100, 250, 250, null);


		
		
	}

}
