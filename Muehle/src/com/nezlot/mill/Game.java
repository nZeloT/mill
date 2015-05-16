package com.nezlot.mill;

import javax.swing.*;
import java.awt.*;

public class Game extends JPanel{
	private static final long serialVersionUID = 8941316724724333172L;


	private Field[][] fields;
	private boolean curPlayer;
	private int placedStones;
	private int stoneCountA;
	private int stoneCountB;
	private Field selectionOrigin;
	private boolean mill;

	private int edgeSize;
	private int dx;
	private int dy;

	public Game() {
		reset();
	}

	public void reset(){
		fields = new Field[][]{
				{new Field(0, 0),                                   new Field(3, 0),                                   new Field(6, 0)},
				{                 new Field(1, 1),                  new Field(3, 1),                  new Field(5, 1)                 },
				{                                  new Field(2, 2), new Field(3, 2), new Field(4, 2)                                  },
				{new Field(0, 3), new Field(1, 3), new Field(2, 3),                  new Field(4, 3), new Field(5, 3), new Field(6, 3)},
				{                                  new Field(2, 4), new Field(3, 4), new Field(4, 4)                                  },
				{                 new Field(1, 5),                  new Field(3, 5),                  new Field(5, 5)                 },
				{new Field(0, 6),                                   new Field(3, 6),                                   new Field(6, 6)}
		};

		curPlayer = false;
		placedStones = 0;
		selectionOrigin = null;
		mill = false;
	}

	public void mouseClick(Point click, boolean l){
		if(!l){
			clearSelections();
		}else{

			Field f = convertCoordinates(click);

			if(f != null){
				
				if(mill && f.getSelected() == 2){
					f.setStatus(0);
					if(curPlayer)
						--stoneCountB;
					else
						--stoneCountA;
					curPlayer = !curPlayer;
					mill = false;

					clearSelections();
				}else

				//1. We are in place mode so place a stone
				if(placedStones < 18 && f.getStatus() == 0){
					f.setStatus(curPlayer ? 1 : 2);
					
					++placedStones;
					if(curPlayer)
						++stoneCountA;
					else
						++stoneCountB;
					
					this.mill = checkForMill(f, curPlayer);
					System.out.println(mill);
					if(!mill)
						curPlayer = !curPlayer;
					else
						selectStonesAfterMill();
					
					System.out.println(stoneCountA + " " + stoneCountB);
				}

				//2. We are in move mode so select stone and all positions to move to
				else if(placedStones >= 18){

					if(f.getSelected() == 2){
						//a field with a move allowed selection was clicked
						//so we gotta move the stone a bit :)
						selectionOrigin.setStatus(0);
						f.setStatus(curPlayer ? 1 : 2);
						clearSelections();

						//Did we just create a mill?
						this.mill = checkForMill(f, curPlayer);
						System.out.println(mill);
						if(!mill)
							curPlayer = !curPlayer;
						else
							selectStonesAfterMill();

					}else if(f.getSelected() == 0 && f.getStatus() == (curPlayer ? 1 : 2)){
						clearSelections();

						// Ok, the field was not selected, so select all the sourroundings
						//except the current player has only three stones left
						f.setSelected(1);

						if(curPlayer && stoneCountA <= 3 || !curPlayer && stoneCountB <= 3){
							for (Field[] fie : fields) {
								for (Field fi : fie) {
									if(fi.getStatus() == 0 && fi.getSelected() == 0)
										fi.setSelected(2);
								}
							}
						}else{
							Field[] sourroundings = getNeighbors(f);

							for (Field field : sourroundings) {
								if(field != null && field.getStatus() == 0)
									field.setSelected(2);
							}
						}

						selectionOrigin = f;
					}
				}
			}
		}

		repaint();
	}

	private boolean checkForMill(Field f, boolean player){
		Point p = f.getPos();
		
		//1. horizontally
		int d = 0;
		if(p.y == 3 && p.x >= 4)
			d = 3;

		int millCount = 0;
		for(int j = d; j < d+3; j++){
			if(fields[p.y][j].getStatus() == (player ? 1 : 2))
				millCount++;
			else
				break;
		}

		if(millCount == 3)
			return true;
		
		//2. vertically
		millCount = 0;
		d = 1;
        int n, dd = 0, sd = 0;

        if(p.x == 0 || p.x == 6)
			d = 3;
		if(p.x == 1 || p.x == 5)
			d = 2;
		
		if(p.x == 3){
			dd = 1;
			sd = 2;
			
			if(p.y >= 4)
				sd = -2;
		}
		if(p.x > 3)
			dd = 2;
		
		for(int j = 0, c = 3-d-sd; j < 3; j++, c += d){
			n = 0;
			if(c == 3)
				n = p.x;
			if(c == 3 && p.x > 3)
				n -= 3;
			if(fields[c][dd+n].getStatus() == (player ? 1 : 2))
				millCount++;
			else
				break;
		}

        return millCount == 3;
    }

	private void selectStonesAfterMill(){
		int c = 0;
		for (Field[] fie : fields) {
			for (Field f : fie) {
				if(f.getStatus() == (!curPlayer ? 1 : 2) && !checkForMill(f, !curPlayer)){
					f.setSelected(2);
					++c;
				}
			}
		}
		
		if(c == 0){
			for (Field[] fie : fields) {
				for (Field f : fie) {
					if(f.getStatus() == (!curPlayer ? 1 : 2)){
						f.setSelected(2);
					}
				}
			}
		}
		
	}

	private Field[] getNeighbors(Field f){
		Point p = f.getPos();
		Field[] n = new Field[4];

		int dx = 0;
		int dy = 0;

		//1. Inner Ring
		if(p.x >= 2 && p.y >= 2
				&& p.x <= 4 && p.y <= 4 )
			dx = dy = 1;
		else{

			//2. Corners
			//a. mid ring
			if( (p.x == 1 || p.x == 5) 
					&& (p.y == 1 || p.y == 5))
				dx = dy = 2;

			//b. outer ring
			if( (p.x == 0 || p.x == 6) 
					&& (p.y == 0 || p.y == 6))
				dx = dy = 3;

			//3. Middle Cross
			//a. horizontally
			if(p.y == 3){
				if(p.x == 1 || p.x == 5){
					dx = 1;
					dy = 2;
				}else{
					dx = 1;
					dy = 3;
				}
			}

			//b. vertically
			if(p.x == 3){
				if(p.y == 1 || p.y == 5){
					dy = 1;
					dx = 2;
				}else{
					dy = 1;
					dx = 3;
				}
			}
		}

		n[0] = getField(new Point(p.x, p.y-dy));
		n[1] = getField(new Point(p.x+dx, p.y));
		n[2] = getField(new Point(p.x, p.y+dy));
		n[3] = getField(new Point(p.x-dx, p.y));

		return n;
	}

	private void clearSelections(){
		//Remove all markers
		selectionOrigin = null;
		for(Field[] fld : fields)
			for(Field f : fld)
				f.setSelected(0);
	}

	private Field getField(Point ko){
        if (ko != null && ko.x >= 0 && ko.x <= 6 && ko.y >= 0 && ko.y <= 6)
            for (Field[] fie : fields)
                for (Field f : fie)
                    if (f.getX() == ko.x && f.getY() == ko.y)
                        return f;
        return null;
	}

	private Field convertCoordinates(Point click){
		for (Field[] flds : fields) {
			for (Field f : flds) {
				Point p1 = new Point(
						dx+(int)((f.getX()+0.5d)*edgeSize/8d+edgeSize/80d), 
						dy+(int)((f.getY()+0.5d)*edgeSize/8d+edgeSize/80d)
						);

				Point p2 = new Point(
						p1.x+(int)(edgeSize/8d-2*edgeSize/80d), 
						p1.y+(int)(edgeSize/8d-2*edgeSize/80d)
						);

				if(click.x >= p1.x && click.y >= p1.y
						&& click.x <= p2.x && click.y <= p2.y){

					return f;
				}

			}
		}

		return null;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		edgeSize = Math.min(getWidth(), getHeight());
		dx		 = (getWidth() - edgeSize)/2;
		dy		 = (getHeight() - edgeSize)/2;

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());

		g.setColor(Color.GREEN);
		//Rects
		g.drawRect(dx+(int)(  edgeSize/8d), dy+(int)(  edgeSize/8d), (int)(6*edgeSize/8d), (int)(6*edgeSize/8d));
		g.drawRect(dx+(int)(2*edgeSize/8d), dy+(int)(2*edgeSize/8d), (int)(4*edgeSize/8d), (int)(4*edgeSize/8d));
		g.drawRect(dx+(int)(3*edgeSize/8d), dy+(int)(3*edgeSize/8d), (int)(2*edgeSize/8d), (int)(2*edgeSize/8d));

		//H-Lines
		g.drawLine(dx+(int)(edgeSize/8d),   dy+(int)(edgeSize/2d), dx+(int)(3*edgeSize/8d), dy+(int)(edgeSize/2d));
		g.drawLine(dx+(int)(5*edgeSize/8d), dy+(int)(edgeSize/2d), dx+(int)(7*edgeSize/8d), dy+(int)(edgeSize/2d));
		//V-Lines
		g.drawLine(dx+(int)(edgeSize/2d), dy+(int)(edgeSize/8d),   dx+(int)(edgeSize/2d), dy+(int)(3*edgeSize/8d));
		g.drawLine(dx+(int)(edgeSize/2d), dy+(int)(5*edgeSize/8d), dx+(int)(edgeSize/2d), dy+(int)(7*edgeSize/8d));

		for(Field[] fi : fields){
			for(Field f : fi){

				if(f.getStatus() > 0){
					g.setColor(f.getStatus() == 1 ? Color.RED : Color.BLUE);
					g.fillOval(	dx+(int)((f.getX()+0.5d)*edgeSize/8d+edgeSize/80d), 
							dy+(int)((f.getY()+0.5d)*edgeSize/8d+edgeSize/80d),

							(int)(edgeSize/8d-2*edgeSize/80d), 
							(int)(edgeSize/8d-2*edgeSize/80d));
				}else{
					g.setColor(Color.GREEN);
					g.drawOval(	dx+(int)((f.getX()+0.5d)*edgeSize/8d+edgeSize/80d), 
							dy+(int)((f.getY()+0.5d)*edgeSize/8d+edgeSize/80d),

							(int)(edgeSize/8d-2*edgeSize/80d), 
							(int)(edgeSize/8d-2*edgeSize/80d));
				}

				if(f.getSelected() > 0){
					if(f.getSelected() == 1)
						g.setColor(Color.ORANGE);
					else
						g.setColor(Color.MAGENTA);

					g.drawOval(	dx+(int)((f.getX()+0.5d)*edgeSize/8d+edgeSize/80d), 
							dy+(int)((f.getY()+0.5d)*edgeSize/8d+edgeSize/80d),

							(int)(edgeSize/8d-2*edgeSize/80d), 
							(int)(edgeSize/8d-2*edgeSize/80d));
				}
			}
		}
	}
}
