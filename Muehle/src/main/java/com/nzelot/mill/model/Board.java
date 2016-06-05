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

package com.nzelot.mill.model;

import com.nzelot.mill.utils.Point;

public class Board {

    public static final Point[][] boardCoords = new Point[][]{
            {new Point(0, 0), new Point(3, 0), new Point(6, 0)},
            {new Point(1, 1), new Point(3, 1), new Point(5, 1)},
            {new Point(2, 2), new Point(3, 2), new Point(4, 2)},
            {new Point(0, 3), new Point(1, 3), new Point(2, 3), new Point(4, 3), new Point(5, 3), new Point(6, 3)},
            {new Point(2, 4), new Point(3, 4), new Point(4, 4)},
            {new Point(1, 5), new Point(3, 5), new Point(5, 5)},
            {new Point(0, 6), new Point(3, 6), new Point(6, 6)}
    };
    private final Field[][] fields;
    private boolean curPlayer;
    private int placedStones;
    private int stoneCountA;
    private int stoneCountB;
    private Field selectionOrigin;
    private boolean mill;

    public Board() {

        fields = new Field[][]{
                {new Field(0, 0), new Field(3, 0), new Field(6, 0)},
                {new Field(1, 1), new Field(3, 1), new Field(5, 1)},
                {new Field(2, 2), new Field(3, 2), new Field(4, 2)},
                {new Field(0, 3), new Field(1, 3), new Field(2, 3), new Field(4, 3), new Field(5, 3), new Field(6, 3)},
                {new Field(2, 4), new Field(3, 4), new Field(4, 4)},
                {new Field(1, 5), new Field(3, 5), new Field(5, 5)},
                {new Field(0, 6), new Field(3, 6), new Field(6, 6)}
        };

        curPlayer = false;
        placedStones = 0;
        selectionOrigin = null;
        mill = false;
    }

    public void fieldAction(Point clickedField) {
        Field field = getField(clickedField);
        if (field != null) {

            if (mill && field.getSelected() == 2) {
                field.setStatus(0);
                if (curPlayer)
                    --stoneCountB;
                else
                    --stoneCountA;
                curPlayer = !curPlayer;
                mill = false;

                clearSelections();
            } else

                //1. We are in place mode so place a stone
                if (placedStones < 18 && field.getStatus() == 0) {
                    field.setStatus(curPlayer ? 1 : 2);

                    ++placedStones;
                    if (curPlayer)
                        ++stoneCountA;
                    else
                        ++stoneCountB;

                    mill = checkForMill(field, curPlayer);
                    if (!mill)
                        curPlayer = !curPlayer;
                    else
                        selectStonesAfterMill();

                }

                //2. We are in move mode so select stone and all positions to move to
                else if (placedStones >= 18) {

                    if (field.getSelected() == 2) {
                        //a field with a move allowed selection was clicked
                        //so we gotta move the stone a bit :)
                        selectionOrigin.setStatus(0);
                        field.setStatus(curPlayer ? 1 : 2);
                        clearSelections();

                        //Did we just create a mill?
                        this.mill = checkForMill(field, curPlayer);
                        if (!mill)
                            curPlayer = !curPlayer;
                        else
                            selectStonesAfterMill();

                    } else if (field.getSelected() == 0 && field.getStatus() == (curPlayer ? 1 : 2)) {
                        clearSelections();

                        // Ok, the field was not selected, so select all the surroundings
                        //except the current player has only three stones left
                        field.setSelected(1);

                        if (curPlayer && stoneCountA <= 3 || !curPlayer && stoneCountB <= 3) {
                            for (Field[] fie : fields) {
                                for (Field fi : fie) {
                                    if (fi.getStatus() == 0 && fi.getSelected() == 0)
                                        fi.setSelected(2);
                                }
                            }
                        } else {
                            Field[] surroundings = getNeighbors(field);

                            for (Field f : surroundings) {
                                if (f != null && f.getStatus() == 0)
                                    f.setSelected(2);
                            }
                        }

                        selectionOrigin = field;
                    }
                }
        }
    }

    private Field getField(Point ko) {
        if (ko != null && ko.x >= 0 && ko.x <= 6 && ko.y >= 0 && ko.y <= 6)
            for (Field[] fie : fields)
                for (Field f : fie)
                    if (f.getX() == ko.x && f.getY() == ko.y)
                        return f;
        return null;
    }

    public void clearSelections() {
        //Remove all markers
        selectionOrigin = null;
        for (Field[] fld : fields)
            for (Field f : fld)
                f.setSelected(0);
    }

    private boolean checkForMill(Field f, boolean player) {
        Point p = f.getPos();

        //1. horizontally
        int d = 0;
        if (p.y == 3 && p.x >= 4)
            d = 3;

        int millCount = 0;
        for (int j = d; j < d + 3; j++) {
            if (fields[p.y][j].getStatus() == (player ? 1 : 2))
                millCount++;
            else
                break;
        }

        if (millCount == 3)
            return true;

        //2. vertically
        millCount = 0;
        d = 1;
        int n, dd = 0, sd = 0;

        if (p.x == 0 || p.x == 6)
            d = 3;
        if (p.x == 1 || p.x == 5)
            d = 2;

        if (p.x == 3) {
            dd = 1;
            sd = 2;

            if (p.y >= 4)
                sd = -2;
        }
        if (p.x > 3)
            dd = 2;

        for (int j = 0, c = 3 - d - sd; j < 3; j++, c += d) {
            n = 0;
            if (c == 3)
                n = p.x;
            if (c == 3 && p.x > 3)
                n -= 3;
            if (fields[c][dd + n].getStatus() == (player ? 1 : 2))
                millCount++;
            else
                break;
        }

        return millCount == 3;
    }

    private void selectStonesAfterMill() {
        int c = 0;
        for (Field[] fie : fields) {
            for (Field f : fie) {
                if (f.getStatus() == (!curPlayer ? 1 : 2) && !checkForMill(f, !curPlayer)) {
                    f.setSelected(2);
                    ++c;
                }
            }
        }

        if (c == 0) {
            for (Field[] fie : fields) {
                for (Field f : fie) {
                    if (f.getStatus() == (!curPlayer ? 1 : 2)) {
                        f.setSelected(2);
                    }
                }
            }
        }

    }

    private Field[] getNeighbors(Field f) {
        Point p = f.getPos();
        Field[] n = new Field[4];

        int dx = 0;
        int dy = 0;

        //1. Inner Ring
        if (p.x >= 2 && p.y >= 2
                && p.x <= 4 && p.y <= 4)
            dx = dy = 1;
        else {

            //2. Corners
            //a. mid ring
            if ((p.x == 1 || p.x == 5)
                    && (p.y == 1 || p.y == 5))
                dx = dy = 2;

            //b. outer ring
            if ((p.x == 0 || p.x == 6)
                    && (p.y == 0 || p.y == 6))
                dx = dy = 3;

            //3. Middle Cross
            //a. horizontally
            if (p.y == 3) {
                if (p.x == 1 || p.x == 5) {
                    dx = 1;
                    dy = 2;
                } else {
                    dx = 1;
                    dy = 3;
                }
            }

            //b. vertically
            if (p.x == 3) {
                if (p.y == 1 || p.y == 5) {
                    dy = 1;
                    dx = 2;
                } else {
                    dy = 1;
                    dx = 3;
                }
            }
        }

        n[0] = getField(new Point(p.x, p.y - dy));
        n[1] = getField(new Point(p.x + dx, p.y));
        n[2] = getField(new Point(p.x, p.y + dy));
        n[3] = getField(new Point(p.x - dx, p.y));

        return n;
    }

    public Point[] getFieldStates() {
        Point[] states = new Point[24];
        int cnt = 0;
        for (Field[] flds : fields) {
            for (Field f : flds) {
                states[cnt++] = new Point(f.getStatus(), f.getSelected());
            }
        }

        return states;
    }

}
