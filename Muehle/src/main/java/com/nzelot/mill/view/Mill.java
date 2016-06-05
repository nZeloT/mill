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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Mill {

    public static void main(String[] args) {

        JFrame window = new JFrame("Mill");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final Board game = new Board();
        final BoardRenderer g = new BoardRenderer(game);
        g.setPreferredSize(new Dimension(500, 500));
        window.add(g);
        g.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                java.awt.Point p = e.getPoint();
                g.handleMouseClick(new com.nzelot.mill.utils.Point(p.x, p.y), e.getButton() == MouseEvent.BUTTON1);
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
        window.pack();
        window.setVisible(true);

    }

}
