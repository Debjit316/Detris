package main;

import javax.swing.*;
import java.awt.*;

public class Main {

	public static void main(String[] args) {
		// Create a window using JFrame
		JFrame window = new JFrame("Detris");

		ImageIcon padIcon = new ImageIcon((ClassLoader.getSystemResource("tetris.png")));
		Image icon = padIcon.getImage();
		window.setIconImage(icon);

		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		
		
		// Add GamePanel to the window
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack();
		
		// I don't know why these are down here
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		
		// Launch the game
		gp.launchGame();

	}

}
