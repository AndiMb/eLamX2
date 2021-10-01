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
package de.elamx.clt.cutout;

import de.elamx.mathtools.Complex;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;
import javax.swing.ImageIcon;

/**
 *
 * @author raedel
 */
public abstract class CutoutGeometry {
    
    public static final String PROP_HOLETYPE = "PROP_HOLETYPE";
    public static final String PROP_NAME     = "Name";
    public static final String PROP_A        = "A";
    public static final String PROP_B        = "B";
    
    private   String     name;                                                 // shape name
    protected double     a, b;                                                 // length and width
    protected double[]   constants;                                            // shape constants m_k
    private   double[][] geometry = null;                                      // actual shape
    private   double     max;
    
    public CutoutGeometry(String name, double a){
        this(name, a, a);
    }
    
    public CutoutGeometry(String name, double a, double b){
        this.name  = name;
        this.a     = a;
        this.b     = b;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        propertyChangeSupport.firePropertyChange(PROP_NAME, oldName, name);
    }

    /**
     * @param a the diameter of the circle, first semi axis of an ellipse or length of a rectangle to set
     */
    public void setA(double a) {
        if (a < 0.0){a = Math.abs(a);}
        double olda = this.a;
        this.a = a;
        propertyChangeSupport.firePropertyChange(PROP_A, olda, a);
    }

    /**
     * @return the diameter of the circle, first semi axis of an ellipse or length of a rectangle
     */
    public double getA() {return a;}
    
    /**
     * @param b the second semi axis of an ellipse or width of a rectangle to set
     */
    public void setB(double b) {
        if (b < 0.0){b = Math.abs(b);}
        double oldb = this.b;
        this.b = b;
        propertyChangeSupport.firePropertyChange(PROP_B, oldb, b);
    }

    /**
     * @return the second semi axis of an ellipse or width of a rectangle
     */
    public double getB() {return b;}
    
    public abstract Property[] getPropertyDefinitions();
    
    public abstract double getMaximumAspectRatio();
    
    public abstract void calcConstants();
    
    public double[] getConstants(){return constants;}
    
    /**
     * Gibt den Winkel in der z-Ebene für einen Winkel aus der zeta-Ebene im Gradmaß wieder.
     * @param theta Winkel aus zeta-Ebene
     * @return Winkel in der z-Ebene im Gradmaß
     */
    public double getAlpha(double theta){
        
        double angle = theta;
        
        while(angle < 0.0)  {angle = angle + 360;}
        while(angle > 360.0){angle = angle - 360;}
        
        double t = Math.toRadians(angle);
        
        double tmp1 = Math.sin(t);
        double tmp2 = Math.cos(t);
        
        for(int k = 1; k < constants.length; k++){
            tmp1 -= constants[k]*Math.sin(k*t);
            tmp2 += constants[k]*Math.cos(k*t);
        }
        
        double alpha = Math.toDegrees(Math.atan(tmp1/tmp2));
        
        if      (angle <=  90){return         alpha;}
        else if (angle <= 270){return 180.0 + alpha;}
        else                  {return 360.0 + alpha;}
    }
    
    /**
     * Gibt die x-Koordinate und die y-Koordinate der Lochkontur in der reellen 
     * Ebene für einen gegebenen Lochwinkel zurück.
     * 
     * @param angles Winkel für die Koordinaten der Lochkontur ausgegeben werden sollen
     * @return x-Koordinate
     */
    public abstract double[][] getSimplifiedGeometry(double[] angles);
    
    public abstract String getDisplayName();
    
    public abstract ImageIcon getImage();
    
    public abstract Image getNodeIcon();
    
    public abstract CutoutGeometry getCopy();
    
    /**
     * Gibt die skalierte reale Geometrie der Ausschnittgeometrie im ersten Quadranten zurück.
     * 
     * @return Array mit den Koordinaten der Ausschnittgeometrie
     */
    public double[][] getScaledComplexGeometry(){
        if (geometry == null){calcScaledComplexGeometry();}
        return geometry;
    }
    
    /**
     * Berechnet die Koordinaten der Geometrie im ersten Quadranten.
     * 
     * Wird aus den Koeffizienten der Geometrie berechnet.
     * 
     * z=x+i*y=R*(zeta+sum_k(m_k/zeta^k)) (1) Ukadgaonker
     */
    public void calcScaledComplexGeometry(){
        int numanglestoninty = (Cutout.getNumWerte()-1)/4;                      // Number of angles for 1st quadrant
        
        geometry      = new double[numanglestoninty][2];
        Complex[] zeta = new Complex[numanglestoninty];
        Complex[] sums = new Complex[numanglestoninty];
        Complex val;
        //double max=0.0, maxy=0.0;
        max = 0.0;
        double maxy=0.0;
        
        for (int ii = 0; ii < numanglestoninty; ii++){                          // loop over 1st quadrant angle 0...90°
            
            // angle
            double angle = Math.toRadians(ii*90.0/(numanglestoninty-1));        // angle
            
            // zeta
            zeta[ii] = new Complex(Math.cos(angle), Math.sin(angle));           // zeta=cos(angle)+i*sin(angle)
            
            // summand
            sums[ii] = zeta[ii];
            for (int jj = 0; jj < constants.length; jj++){
                if(constants[jj] != 0.0){
                    val = new Complex(constants[jj]);                          // m_k
                    val = val.divide(Complex.pow(zeta[ii], jj));                // m_k/zeta^k
                    sums[ii] = sums[ii].add(val);                               // sum
                }
            }
            
            // max
            if (sums[ii].getRe() > max) {max  = sums[ii].getRe();}
            if (sums[ii].getIm() > maxy){maxy = sums[ii].getIm();}
        }
        
        max = Math.max(max, maxy);
        
        for (int ii = 0; ii < numanglestoninty; ii++){                          // z = x+i*y
            geometry[ii][0] = sums[ii].getRe()/max;                            // x-Koordinate
            geometry[ii][1] = sums[ii].getIm()/max;                            // y-Koordinate
        }
    }
    
    public double[] getScaledComplexCoordinates(double angle){
        
        if (geometry == null){calcScaledComplexGeometry();}
        
        // zeta
        Complex zeta = new Complex(Math.cos(angle), Math.sin(angle));           // zeta=cos(angle)+i*sin(angle)

        // summand
        Complex sums = zeta, val;
        for (int jj = 0; jj < constants.length; jj++){
            if(constants[jj] != 0.0){
                val = new Complex(constants[jj]);                              // m_k
                val = val.divide(Complex.pow(zeta, jj));                        // m_k/zeta^k
                sums = sums.add(val);                                           // sum
            }
        }
        
        return new double[]{sums.getRe()/max,sums.getIm()/max};
    }
    
    protected transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public class Property{
        
        private final String name;
        private final Class<? extends Object> cl;
        private final String displayName;
        private final Class<? extends PropertyEditor> editorClass;
        private final String shortDescription;

        public Property(String name, Class<? extends Object> cl, String displayName, String shortDescription, Class<? extends PropertyEditor> editorClass) {
            this.name = name;
            this.cl = cl;
            this.displayName = displayName;
            this.editorClass = editorClass;
            this.shortDescription = shortDescription;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Object> getCl() {
            return cl;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Class<? extends PropertyEditor> getEditorClass() {
            return editorClass;
        }

        public String getShortDescription() {
            return shortDescription;
        }
    }
    
}
