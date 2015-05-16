package com.nezlot.mill;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Mill {

	public static void main(String[] args) {
		
		JFrame window = new JFrame("Mill");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		Game g = new Game();
		g.setPreferredSize(new Dimension(500,500));
		window.add(g);
		g.addMouseListener(new MouseListener() {
			@Override public void mouseReleased(MouseEvent e) {}			
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}		
			@Override public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				g.mouseClick(e.getPoint(), e.getButton() == MouseEvent.BUTTON1);
			}
		});
		window.pack();
		window.setVisible(true);
		
	}

}
