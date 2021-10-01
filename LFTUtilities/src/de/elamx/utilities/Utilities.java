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
package de.elamx.utilities;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author s5110955
 */
public class Utilities {
    
    /** Creates a new instance of AWTUtilities */
    public Utilities() {
    }
  
    public static void addComponent( Container cont,
                                      GridBagLayout gbl,
                                      Component c,
                                      int x, int y,
                                      int width, int height,
                                      double weightx, double weighty,
                                      int top, int left, int bottom, int right)
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx      = x;
        gbc.gridy      = y;
        gbc.gridwidth  = width;
        gbc.gridheight = height;
        gbc.weightx    = weightx;
        gbc.weighty    = weighty;
        gbc.insets     = new Insets(top, left, bottom, right);
        gbc.anchor     = GridBagConstraints.CENTER;
        gbl.setConstraints( c, gbc );
        cont.add( c );
    }

    public static void addComponent( Container cont,
                                      GridBagLayout gbl,
                                      Component c,
                                      int x, int y,
                                      int width, int height,
                                      double weightx, double weighty,
                                      int top, int left, int bottom, int right,
                                      int fill, int anchor)
    {
        GridBagConstraints gbc = new GridBagConstraints();
        //gbc.fill = GridBagConstraints.BOTH;
        gbc.fill       = fill;
        gbc.gridx      = x;
        gbc.gridy      = y;
        gbc.gridwidth  = width;
        gbc.gridheight = height;
        gbc.weightx    = weightx;
        gbc.weighty    = weighty;
        gbc.insets     = new Insets(top, left, bottom, right);
        gbc.anchor     = anchor;
        gbl.setConstraints( c, gbc );
        cont.add( c );
    }

  /*public static BufferedImage getScreenShot(
    Component component) {

    BufferedImage image = new BufferedImage(
      component.getWidth(),
      component.getHeight(),
      BufferedImage.TYPE_INT_RGB
      );
    // call the Component's paint method, using
    // the Graphics object of the image.
    component.paint( image.getGraphics() );
    return image;
  }*/



    public static void saveScreenShot(Component component, File file){
        try {
            ImageIO.write(getScreenShot(component), "png", file);
        } catch (IOException ex) {
            Logger.getLogger(Utilities.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static BufferedImage getScreenShot(Component component) {
        component.repaint();
        // determine current screen size
        Point p = component.getLocation();
        Dimension size = component.getSize();
        Rectangle screenRect = new Rectangle(p.x, p.y, size.width, size.height);

        // create screen shot
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        BufferedImage image = robot.createScreenCapture(screenRect);
        return image;
    }
    
}
