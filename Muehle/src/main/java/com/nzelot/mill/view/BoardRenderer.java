/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 nZeloT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package com.nzelot.mill.view;

import com.nzelot.mill.model.Board;
import com.nzelot.mill.utils.Point;

import javax.swing.*;
import java.awt.*;

/**
 * @author nZeloT
 */
public class BoardRenderer extends JPanel {

    private Board game;

    private int edgeSize;
    private int dx;
    private int dy;

    public BoardRenderer(Board g) {
        this.game = g;
    }

    public void handleMouseClick(Point click, boolean l) {
        if (!l) {
            game.clearSelections();
        } else {
            Point clickedField = convertCoordinates(click);
            game.fieldAction(clickedField);
        }
        repaint();
    }

    private Point convertCoordinates(Point click) {
        for (Point[] flds : Board.boardCoords) {
            for (Point f : flds) {
                Point p1 = new Point(
                        dx + (int) ((f.getX() + 0.5d) * edgeSize / 8d + edgeSize / 80d),
                        dy + (int) ((f.getY() + 0.5d) * edgeSize / 8d + edgeSize / 80d)
                );

                Point p2 = new Point(
                        p1.x + (int) (edgeSize / 8d - 2 * edgeSize / 80d),
                        p1.y + (int) (edgeSize / 8d - 2 * edgeSize / 80d)
                );

                if (click.x >= p1.x && click.y >= p1.y
                        && click.x <= p2.x && click.y <= p2.y) {

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
        dx = (getWidth() - edgeSize) / 2;
        dy = (getHeight() - edgeSize) / 2;

//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.DARK_GRAY);
        //Rects
        g.drawRect(dx + (int) (edgeSize / 8d), dy + (int) (edgeSize / 8d), (int) (6 * edgeSize / 8d), (int) (6 * edgeSize / 8d));
        g.drawRect(dx + (int) (2 * edgeSize / 8d), dy + (int) (2 * edgeSize / 8d), (int) (4 * edgeSize / 8d), (int) (4 * edgeSize / 8d));
        g.drawRect(dx + (int) (3 * edgeSize / 8d), dy + (int) (3 * edgeSize / 8d), (int) (2 * edgeSize / 8d), (int) (2 * edgeSize / 8d));

        //H-Lines
        g.drawLine(dx + (int) (edgeSize / 8d), dy + (int) (edgeSize / 2d), dx + (int) (3 * edgeSize / 8d), dy + (int) (edgeSize / 2d));
        g.drawLine(dx + (int) (5 * edgeSize / 8d), dy + (int) (edgeSize / 2d), dx + (int) (7 * edgeSize / 8d), dy + (int) (edgeSize / 2d));
        //V-Lines
        g.drawLine(dx + (int) (edgeSize / 2d), dy + (int) (edgeSize / 8d), dx + (int) (edgeSize / 2d), dy + (int) (3 * edgeSize / 8d));
        g.drawLine(dx + (int) (edgeSize / 2d), dy + (int) (5 * edgeSize / 8d), dx + (int) (edgeSize / 2d), dy + (int) (7 * edgeSize / 8d));

        Point[] states = game.getFieldStates();
        int state = 0;
        for (Point[] fi : Board.boardCoords) {
            for (Point f : fi) {
                Point st = states[state++];
                if (st.getX() > 0) {
                    g.setColor(st.getX() == 1 ? Color.BLACK : Color.LIGHT_GRAY);
                    g.fillOval(dx + (int) ((f.getX() + 0.5d) * edgeSize / 8d + edgeSize / 80d),
                            dy + (int) ((f.getY() + 0.5d) * edgeSize / 8d + edgeSize / 80d),

                            (int) (edgeSize / 8d - 2 * edgeSize / 80d),
                            (int) (edgeSize / 8d - 2 * edgeSize / 80d));
                } else {
//					g.setColor(Color.GREEN);
//					g.drawOval(	dx+(int)((f.getX()+0.5d)*edgeSize/8d+edgeSize/80d),
//							dy+(int)((f.getY()+0.5d)*edgeSize/8d+edgeSize/80d),
//
//							(int)(edgeSize/8d-2*edgeSize/80d),
//							(int)(edgeSize/8d-2*edgeSize/80d));
                }

                if (st.getY() > 0) {
                    if (st.getY() == 1)
                        g.setColor(Color.ORANGE);
                    else
                        g.setColor(Color.MAGENTA);

                    g.drawOval(dx + (int) ((f.getX() + 0.5d) * edgeSize / 8d + edgeSize / 80d),
                            dy + (int) ((f.getY() + 0.5d) * edgeSize / 8d + edgeSize / 80d),

                            (int) (edgeSize / 8d - 2 * edgeSize / 80d),
                            (int) (edgeSize / 8d - 2 * edgeSize / 80d));
                }
            }
        }
    }

}
