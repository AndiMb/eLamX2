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
package de.elamx.clt.plate.AdditionalStiffeners;

import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plate.Stiffener.Stiffener;
import de.elamx.clt.plateui.stiffenerui.StiffenerDefinitionService;
import de.elamx.core.propertyeditor.DensityPropertyEditorSupport;
import de.elamx.core.propertyeditor.ThicknessPropertyEditorSupport;
import de.elamx.core.propertyeditor.YoungsModulusPropertyEditorSupport;
import java.awt.Image;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Andreas Hauffe
 */
@ServiceProvider(service=StiffenerDefinitionService.class)
public class T_StiffenerProperties extends StiffenerDefinitionService {

    private double w1, w2;
    private double t1, t2;
    private double E;
    private double G;
    private double Rho;

    public static final String PROP_W1 = "w1";
    public static final String PROP_T1 = "t1";
    public static final String PROP_W2 = "w2";
    public static final String PROP_T2 = "t2";
    public static final String PROP_E = "E";
    public static final String PROP_G = "G";
    public static final String PROP_Rho = "Rho";
    
    private static Property[] props;
    
    public T_StiffenerProperties(){
        this("T-Stringer", Stiffener.X_DIRECTION, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public T_StiffenerProperties(String name, int direction, double position, 
            double w1, double t1, double w2, double t2, 
            double E, double G, double Rho) {
        super(name, direction, position);
        this.w1 = w1;
        this.t1 = t1;
        this.w2 = w2;
        this.t2 = t2;
        this.E = E;
        this.G = G;
        this.Rho = Rho;
        if (props == null) initProperties();
    }
    
    private void initProperties(){ 
        props = new Property[7];        
        props[0] = new Property(PROP_W1, double.class, 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.w1"), 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.w1.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
        props[1] = new Property(PROP_T1, double.class, 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.t1"), 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.t1.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
        props[2] = new Property(PROP_W2, double.class, 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.w2"), 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.w2.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
        props[3] = new Property(PROP_T2, double.class, 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.t2"), 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.t2.shortDescription"), 
                ThicknessPropertyEditorSupport.class);
        props[4] = new Property(PROP_E, double.class, 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.e"), 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.e.shortDescription"), 
                YoungsModulusPropertyEditorSupport.class);
        props[5] = new Property(PROP_G, double.class, 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.g"), 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.g.shortDescription"), 
                YoungsModulusPropertyEditorSupport.class);
        props[6] = new Property(PROP_Rho, double.class, 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.rho"), 
                NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.rho.shortDescription"), 
                DensityPropertyEditorSupport.class);
    }

    /**
     * Get the value of t2
     *
     * @return the value of t2
     */
    public double getT2() {
        return t2;
    }

    /**
     * Set the value of t2
     *
     * @param t2 new value of t2
     */
    public void setT2(double t2) {
        double oldT2 = this.t2;
        this.t2 = t2;
        propertyChangeSupport.firePropertyChange(PROP_T2, oldT2, t2);
    }

    
    /**
     * Get the value of w2
     *
     * @return the value of w2
     */
    public double getW2() {
        return w2;
    }

    /**
     * Set the value of w2
     *
     * @param w2 new value of w2
     */
    public void setW2(double w2) {
        double oldW2 = this.w2;
        this.w2 = w2;
        propertyChangeSupport.firePropertyChange(PROP_W2, oldW2, w2);
    }


    /**
     * Get the value of G
     *
     * @return the value of G
     */
    @Override
    public double getG() {
        return G;
    }

    /**
     * Set the value of G
     *
     * @param G new value of G
     */
    public void setG(double G) {
        double oldG = this.G;
        this.G = G;
        propertyChangeSupport.firePropertyChange(PROP_G, oldG, G);
    }

    /**
     * Get the value of E
     *
     * @return the value of E
     */
    @Override
    public double getE() {
        return E;
    }

    /**
     * Set the value of E
     *
     * @param E new value of E
     */
    public void setE(double E) {
        double oldE = this.E;
        this.E = E;
        propertyChangeSupport.firePropertyChange(PROP_E, oldE, E);
    }

    /**
     * Get the value of E
     *
     * @return the value of E
     */
    @Override
    public double getRho() {
        return Rho;
    }

    /**
     * Set the value of E
     *
     * @param E new value of E
     */
    public void setRho(double Rho) {
        double oldRho = Rho;
        this.Rho = Rho;
        propertyChangeSupport.firePropertyChange(PROP_Rho, oldRho, Rho);
    }

    /**
     * Get the value of t1
     *
     * @return the value of t1
     */
    public double getT1() {
        return t1;
    }

    /**
     * Set the value of t1
     *
     * @param t1 new value of t1
     */
    public void setT1(double t1) {
        double oldT1 = this.t1;
        this.t1 = t1;
        propertyChangeSupport.firePropertyChange(PROP_T1, oldT1, t1);
    }

    /**
     * Get the value of w1
     *
     * @return the value of w1
     */
    public double getW1() {
        return w1;
    }

    /**
     * Set the value of w1
     *
     * @param w1 new value of w1
     */
    public void setW1(double w1) {
        double oldW1 = this.w1;
        this.w1 = w1;
        propertyChangeSupport.firePropertyChange(PROP_W1, oldW1, w1);
    }

    @Override
    public double getI() {
        double zm1 = w2+t1/2.0;
        double zm2 = w2/2.0;
        return w1*t1*t1*t1/12.0+zm1*zm1*w1*t1+t2*w2*w2*w2/12.0+zm2*zm2*w2*t2;
    }

    @Override
    public double getJ() {
        // ACHTUNG: Das ist eine Näherung für w1 >> t1
        return (w1*t1*t1*t1+(w2+t1/2.0)*t2*t2*t2)/3.0;
    }

    @Override
    public double getZ() {
        return ((w2+t1/2.0)*w1*t1+w2/2.0*w2*t2)/getA();
    }

    @Override
    public double getA() {
        return w1*t1+w2*t2;
    }

    @Override
    public StiffenerProperties getCopy() {
        return new T_StiffenerProperties(getName(), getDirection(), getPosition(), w1, t1, w2, t2, E, G, Rho);
    }

    @Override
    public Property[] getPropertyDefinitions() {
        return props;
    }

    @Override
    public ImageIcon getGeometryParameterImage() {
        return new ImageIcon(getClass().getResource("/de/elamx/clt/plate/AdditionalStiffeners/resources/T-Stringer.gif")); // NOI18N
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(T_StiffenerProperties.class, "T_StiffenerProperties.displayname");
    }

    @Override
    public ImageIcon getImage() {
        return new ImageIcon(getClass().getResource("/de/elamx/clt/plate/AdditionalStiffeners/resources/TVersteifung.png")); // NOI18N
    }

    @Override
    public Image getNodeIcon() {
        return ImageUtilities.loadImage("/de/elamx/clt/plate/AdditionalStiffeners/resources/TVersteifung16.png"); // NOI18N
    }
}
