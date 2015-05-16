package com.nezlot.mill;

import java.awt.Point;

public class Field {
	private int x;
	private int y;
	private int status;
	private int selected;
	
	public Field(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setSelected(int selected) {
		this.selected = selected;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Point getPos(){
		return new Point(x, y);
	}
	
	public int getSelected() {
		return selected;
	}

	@Override
	public String toString() {
		return "Field [x=" + x + ", y=" + y + ", status=" + status
				+ ", selected=" + selected + "]";
	}
	
}
