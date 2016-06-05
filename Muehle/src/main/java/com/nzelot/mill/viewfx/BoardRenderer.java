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

package com.nzelot.mill.viewfx;

import com.nzelot.mill.model.Board;
import com.nzelot.mill.utils.Point;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * @author nZeloT
 */
public class BoardRenderer extends Pane {

    private Canvas canvas = new Canvas();
    private Board game;

    private int edgeSize;
    private int dx;
    private int dy;

    public BoardRenderer(Board game) {
        this.game = game;
        getChildren().add(canvas);

        setOnMouseClicked(e -> handleMouseClick(new Point((int) e.getX(), (int) e.getY()), e.getButton() == MouseButton.PRIMARY));
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

    private void repaint() {
        final int w = (int) canvas.getWidth();
        final int h = (int) canvas.getHeight();

        GraphicsContext g = canvas.getGraphicsContext2D();
        g.clearRect(0, 0, w, h);

        edgeSize = Math.min(w, h);
        dx = (w - edgeSize) / 2;
        dy = (h - edgeSize) / 2;

//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, getWidth(), getHeight());

        g.setStroke(new Color(0.25d, 0.25d, 0.25d, 1));
        g.setLineWidth(8);
        //Rects
        g.strokeRect(dx + edgeSize / 8d, dy + edgeSize / 8d, 6 * edgeSize / 8d, 6 * edgeSize / 8d);
        g.strokeRect(dx + 2 * edgeSize / 8d, dy + 2 * edgeSize / 8d, 4 * edgeSize / 8d, 4 * edgeSize / 8d);
        g.strokeRect(dx + 3 * edgeSize / 8d, dy + 3 * edgeSize / 8d, 2 * edgeSize / 8d, 2 * edgeSize / 8d);

        //H-Lines
        g.strokeLine(dx + edgeSize / 8d, dy + edgeSize / 2d, dx + 3 * edgeSize / 8d, dy + edgeSize / 2d);
        g.strokeLine(dx + 5 * edgeSize / 8d, dy + edgeSize / 2d, dx + 7 * edgeSize / 8d, dy + edgeSize / 2d);
        //V-Lines
        g.strokeLine(dx + edgeSize / 2d, dy + edgeSize / 8d, dx + edgeSize / 2d, dy + 3 * edgeSize / 8d);
        g.strokeLine(dx + edgeSize / 2d, dy + 5 * edgeSize / 8d, dx + edgeSize / 2d, dy + 7 * edgeSize / 8d);

        Point[] states = game.getFieldStates();
        int state = 0;
        for (Point[] fi : Board.boardCoords) {
            for (Point f : fi) {
                Point st = states[state++];
                if (st.getX() > 0) {
                    g.setFill(st.getX() == 1 ? Color.BLACK : Color.LIGHTGRAY);
                    g.fillOval(dx + (int) ((f.getX() + 0.5d) * edgeSize / 8d + edgeSize / 80d),
                            dy + (int) ((f.getY() + 0.5d) * edgeSize / 8d + edgeSize / 80d),

                            (int) (edgeSize / 8d - 2 * edgeSize / 80d),
                            (int) (edgeSize / 8d - 2 * edgeSize / 80d));
                }

                g.setLineWidth(3);
                if (st.getY() > 0) {
                    if (st.getY() == 1)
                        g.setStroke(Color.ORANGE);
                    else
                        g.setStroke(Color.MAGENTA);


                    g.strokeOval(dx + (f.getX() + 0.5d) * edgeSize / 8d + edgeSize / 80d,
                            dy + (f.getY() + 0.5d) * edgeSize / 8d + edgeSize / 80d,

                            edgeSize / 8d - 2 * edgeSize / 80d,
                            edgeSize / 8d - 2 * edgeSize / 80d);
                }

                g.setLineWidth(1);
                g.setStroke(Color.LIMEGREEN);
                Point p1 = new Point(
                        dx + (int) ((f.getX() + 0.5d) * edgeSize / 8d + edgeSize / 80d),
                        dy + (int) ((f.getY() + 0.5d) * edgeSize / 8d + edgeSize / 80d)
                );

                Point p2 = new Point(
                        p1.x + (int) (edgeSize / 8d - 2 * edgeSize / 80d),
                        p1.y + (int) (edgeSize / 8d - 2 * edgeSize / 80d)
                );

                g.strokeRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
            }
        }

    }

    @Override
    protected void layoutChildren() {
        final int top = (int) snappedTopInset();
        final int right = (int) snappedRightInset();
        final int bottom = (int) snappedBottomInset();
        final int left = (int) snappedLeftInset();
        final int w = (int) getWidth() - left - right;
        final int h = (int) getHeight() - top - bottom;
        canvas.setLayoutX(left);
        canvas.setLayoutY(top);
        if (w != canvas.getWidth() || h != canvas.getHeight()) {
            canvas.setWidth(w);
            canvas.setHeight(h);
            repaint();
        }
    }
}
