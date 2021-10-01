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
package de.elamx.clt.calculation.ssdialog;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.openide.util.NbBundle;

/**
 *
 * @author Andreas Hauffe
 */
public class GraphicsPanel extends JPanel {

    private static final String CLASS_PREFIX = "GraphicsPanel.";

    private static int height = 400;
    private static int width = 600;

    private static final int randDicke = 14;
    private static final int vertikalrandDicke = 14;

    private static final int pfeillaenge = 7;
    private static final int pfeilbreite = 3;

    private static Image image;

    private double[] uStr;
    private double[] lStr;
    private double[] thickness;
    private double[] angle;
    private int numOfLayers;

    public GraphicsPanel() {
        super();
        image = getEmptyImage();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g); //To change body of generated methods, choose Tools | Templates.
        if (height != getHeight() || width != getWidth()) {
            height = getHeight();
            width = getWidth();
            image = getImage(uStr, lStr, thickness, numOfLayers, angle);
        }
        g.drawImage(image, 0, 0, this);
    }

    private BufferedImage getEmptyImage() {
        // leere weiße Zeichenfläche mit dem Koordinatenkreuz erzeugen.
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        drawCoordinateSystem(g);

        return image;
    }

    private void drawCoordinateSystem(Graphics2D g) {

        BasicStroke stroke1
                = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER);

        g.setStroke(stroke1);

        g.setColor(Color.BLACK);
        g.drawLine(width / 2, 0, width / 2, height);
        g.drawLine(0, height / 2, width, height / 2);
        // Achsenbeschriftung und Pfeile antragen
        String id = GraphicsPanel.CLASS_PREFIX + "thickness.caption";
        g.drawString(NbBundle.getMessage(GraphicsPanel.class, id), width / 2 + 5, 10);
        id = GraphicsPanel.CLASS_PREFIX + "value.caption";
        g.drawString(NbBundle.getMessage(GraphicsPanel.class, id), width - 40, height / 2 - 5);
        int pfeil_0Grad_x_points[] = {width / 2, width / 2 + pfeilbreite, width / 2 - pfeilbreite};
        int pfeil_0Grad_y_points[] = {0, pfeillaenge, pfeillaenge};
        int pfeil_90Grad_x_points[] = {width, width - pfeillaenge, width - pfeillaenge};
        int pfeil_90Grad_y_points[] = {height / 2, height / 2 + pfeilbreite, height / 2 - pfeilbreite};
        g.fillPolygon(pfeil_0Grad_x_points, pfeil_0Grad_y_points, 3);
        g.fillPolygon(pfeil_90Grad_x_points, pfeil_90Grad_y_points, 3);
    }

    public void setValues(double[] uStr, double[] lStr, double[] thickness, int numOfLayers, double[] angle) {
        this.uStr = uStr;
        this.lStr = lStr;
        this.thickness = thickness;
        this.angle = angle;
        this.numOfLayers = numOfLayers;
        image = getImage(uStr, lStr, thickness, numOfLayers, angle);
        this.repaint();
    }

    private BufferedImage getImage(double[] uStr, double[] lStr, double[] thickness, int numOfLayers, double[] angle) {

        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) tempImage.getGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //wenn ok gedrückt wird, wird in der grafik immer eine schicht hinzugefügt
        // Hintergrund weiß "übermalen"
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        //ober- und unterseite des bildes wird ocker
        g.setColor(Color.getHSBColor(255, 55, 255));
        int x1points[] = {0, 0, width, width};
        int y1points[] = {0, randDicke, randDicke, 0};
        g.fillPolygon(x1points, y1points, 4);
        int x2points[] = {0, 0, width, width};
        int y2points[] = {height, height - randDicke, height - randDicke, height};
        g.fillPolygon(x2points, y2points, 4);

        //Koordinatensystem wird gezeichnet
        drawCoordinateSystem(g);

        //Skalierung in x-Richtung bezieht sich auf die höchste Spannung
        double scale;
        double scale1 = 0;
        double scale2 = 0;

        for (int ii = 0; ii < numOfLayers; ii++) {
            //es wird die höchste spannung auf der oberseite ermittelt
            if (Math.abs(uStr[ii]) > scale1) {
                scale1 = Math.abs(uStr[ii]);
            }
            //es wird die höchste spannung auf der unterseite der schicht ermittelt
            if (Math.abs(lStr[ii]) > scale2) {
                scale2 = Math.abs(lStr[ii]);
            }
        }

        //die höchste spannung wird ermittelt und in scale(Skalierungsfaktor) gepeichert               
        if (scale1 > scale2) {
            scale = scale1;
        } else {
            scale = scale2;
        }

        //Gesamtdicke der Laminatschicht wird ermittelt
        double gesdicke = 0.0;
        for (int ii = 0; ii < numOfLayers; ii++) {
            gesdicke += thickness[ii];
        }

        //die abstände vom rand sind 15 pixel        
        double y1 = randDicke + 1; //y1 ist die obere begrenzung der laminatschicht

        double temp = (height - 2 * (randDicke + 1)) / gesdicke;

        //Erzeugung der Darstellung Laminatschichten       
        for (int i = 0; i < numOfLayers; i++) {

            double scaley = temp * thickness[i];

            double y2 = scaley + y1; //y2 ist die untere begrenzung der laminatschicht

            double x1 = ((width - vertikalrandDicke) / (2 * scale)) * uStr[i] + width / 2;
            double x2 = ((width - vertikalrandDicke) / (2 * scale)) * lStr[i] + width / 2;

            if (scale == 0) {
                x1 = width / 2;
                x2 = width / 2;
            }

            //schraffierung der schichten
            double abstl = scaley / 4; //Abstand zweier Linien

            float r = (float) ((angle[i]) / (0.01 * Math.PI));
            g.setColor(Color.getHSBColor(r, 1, 1));
            //g.setColor(Color.pink);

            if (angle[i] < 0) {
                angle[i] = angle[i] + Math.PI;
            }//hier werden negative winkel als winkel über 90 grad behandelt

            //for(int j=0;j < 10*width;j++){
            if (angle[i] > 0.0 && angle[i] < Math.PI) {
                int t, s;
                t = (int) (scaley / Math.tan(angle[i]));
                int j = 0;
                double st = (abstl / Math.sin(angle[i]));
                do {
                    s = (int) (j * st + 1);
                    g.drawLine(s, (int) y1 + 1, (s + t), (int) y2);
                    j++;
                } while (s < width || (s + t) < width);
            }else if (angle[i] == 0.0){
                double ystart = y1+1;
                int j = 0;
                int yt;
                do {
                    yt = (int)(j * abstl + ystart);
                    g.drawLine(0, yt, width, yt);
                    j++;
                } while (yt < y2);
            }
            //}

            int u, v;
            if (angle[i] < Math.PI / 2.0) {
                for (int j = 0; j < 20; j++) {
                    u = (int) ((j * (abstl / Math.cos(angle[i]))) + y1);
                    v = (int) ((y2 - u) / Math.tan(angle[i]));

                    //wenn die linien benachbarte laminatschichten durchdringen soll abgebrochen werden
                    if (u > (int) y2) {
                        break;
                    }

                    g.drawLine(0, u, v, (int) y2);
                }
            }

             //Fläche unter kurve wird dargestellt
            g.setColor(Color.LIGHT_GRAY);
            int[] xpoints = {width / 2, width / 2, (int) x1, (int) x2};
            int[] ypoints = {(int) y2, (int) y1, (int) y1, (int) y2};
            g.fillPolygon(xpoints, ypoints, 4);

            //Spannungslinie wird gezeichnet
            g.setColor(Color.RED);
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            //g.drawPolygon(xpoints, ypoints, 4);

            //laminatgrenzen werden gezeichnet
            g.setColor(Color.BLACK);
            g.drawLine(0, (int) y1, width, (int) y1);

            y1 = y2;

        }

        //obere und untere Laminatgrenze werden mit der linienstärke 4 gezeichnet
        g.setColor(Color.BLACK);

        BasicStroke stroke1
                = new BasicStroke(4.0f, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_MITER);

        g.setStroke(stroke1);
        g.drawLine(0, randDicke + 1, width, randDicke + 1);
        g.drawLine(0, height - randDicke + 1, width, height - randDicke + 1);

        //Koordinatensystem wird gezeichnet
        drawCoordinateSystem(g);

        return tempImage;
    }
}
