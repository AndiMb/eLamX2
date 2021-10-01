/*
 *  This program developed in Java is based on the netbeans platform and is used
 *  to design and to analyse composite structures by means of analytical and 
 *  numerical methods.
 * 
 *  Further information can be found here:
 *  http://www.elamx.de
 *    
 *  Copyright (C) 2021 Technische Universität Dresden - Andreas Hauffe
 * 
 *  This file is part of eLamX².
 *
 *  eLamX² is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  eLamX² is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with eLamX².  If not, see <http://www.gnu.org/licenses/>.
 */
package de.elamx.clt.calculation.calc;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author Andreas Hauffe
 */
public class LetterPanel extends JPanel {

    private String letter_          = null;
    private Color  letterColor_     = null;
    private Color  backgroundColor_ = null;
    
    public LetterPanel(String letter, Color letterColor, Color backgroundColor){
        letter_ = letter;
        letterColor_ = letterColor;
        backgroundColor_ = backgroundColor;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(backgroundColor_);
        g.fillRect(0, 0, this.getSize().width, this.getSize().height);

        g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 70));
        g.setColor(letterColor_);
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(letter_, g);
        g.drawString(letter_, this.getSize().width/2-(int)bounds.getWidth()/2, getBounds().height/2+(int)bounds.getHeight()/2-15);
    }
}
