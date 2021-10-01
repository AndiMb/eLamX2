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
package de.elamx.clt.plateui.stiffenerui;

import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import de.elamx.clt.plate.Stiffener.Stiffener;
import de.elamx.core.propertyeditor.DensityPropertyEditorSupport;
import de.elamx.core.propertyeditor.DoublePropertyEditorSupport;
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
public class DefaultStiffenerProperties extends StiffenerDefinitionService {
    
    private double E;
    public static final String PROP_E = "E";
    private double I;
    public static final String PROP_I = "I";
    private double G;
    public static final String PROP_G = "G";
    private double J;
    public static final String PROP_J = "J";
    private double z;
    public static final String PROP_Z = "z";
    private double A;
    public static final String PROP_A = "A";
    private double Rho;
    public static final String PROP_Rho = "Rho";
    
    private static Property[] props;

    public DefaultStiffenerProperties(){
        this("Default", Stiffener.X_DIRECTION, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
    
    public DefaultStiffenerProperties(String name, int direction, double position, double E, double I, double G, double J, double z, double A, double Rho) {
        super(name, direction, position);
        this.E = E;
        this.I = I;
        this.G = G;
        this.J = J;
        this.z = z;
        this.A = A;
        this.Rho = Rho;
        if (props == null) initProperties();
    }
    
    private void initProperties(){
        props = new Property[6];
        
        props[0] = new Property(PROP_E, double.class, 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.e"), 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.e.shortDescription"), 
                YoungsModulusPropertyEditorSupport.class);
        props[1] = new Property(PROP_I, double.class, 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.i"), 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.i.shortDescription"), 
                DoublePropertyEditorSupport.class);
        props[2] = new Property(PROP_G, double.class, 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.g"), 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.g.shortDescription"), 
                YoungsModulusPropertyEditorSupport.class);
        props[3] = new Property(PROP_J, double.class, 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.j"), 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.j.shortDescription"), 
                DoublePropertyEditorSupport.class);
        props[4] = new Property(PROP_Rho, double.class, 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.rho"), 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.rho.shortDescription"), 
                DensityPropertyEditorSupport.class);
        props[5] = new Property(PROP_A, double.class, 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.a"), 
                NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.a.shortDescription"), 
                DoublePropertyEditorSupport.class);
    }

    /**
     * Get the value of A
     *
     * @return the value of A
     */
    @Override
    public double getA() {
        return A;
    }

    /**
     * Set the value of A
     *
     * @param A new value of A
     */
    public void setA(double A) {
        double oldA = this.A;
        this.A = A;
        propertyChangeSupport.firePropertyChange(PROP_A, oldA, A);
    }

    /**
     * Get the value of z
     *
     * @return the value of z
     */
    @Override
    public double getZ() {
        return z;
    }

    /**
     * Set the value of z
     *
     * @param z new value of z
     */
    public void setZ(double z) {
        double oldZ = this.z;
        this.z = z;
        propertyChangeSupport.firePropertyChange(PROP_Z, oldZ, z);
    }

    /**
     * Get the value of J
     *
     * @return the value of J
     */
    @Override
    public double getJ() {
        return J;
    }

    /**
     * Set the value of J
     *
     * @param J new value of J
     */
    public void setJ(double J) {
        double oldJ = this.J;
        this.J = J;
        propertyChangeSupport.firePropertyChange(PROP_J, oldJ, J);
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
     * Get the value of I
     *
     * @return the value of I
     */
    @Override
    public double getI() {
        return I;
    }

    /**
     * Set the value of I
     *
     * @param I new value of I
     */
    public void setI(double I) {
        double oldI = this.I;
        this.I = I;
        propertyChangeSupport.firePropertyChange(PROP_I, oldI, I);
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
     * Get the value of Rho
     *
     * @return the value of Rho
     */
    @Override
    public double getRho() {
        return Rho;
    }

    /**
     * Set the value of Rho
     *
     * @param Rho new value of Rho
     */
    public void setRho(double Rho) {
        double oldRho = Rho;
        this.Rho = Rho;
        propertyChangeSupport.firePropertyChange(PROP_Rho, oldRho, Rho);
    }

    @Override
    public StiffenerProperties getCopy() {
        return new DefaultStiffenerProperties(getName(), getDirection(), getPosition(), E,  I,  G,  J,  z,  A, Rho);
    }

    @Override
    public Property[] getPropertyDefinitions() {
        return props;
    }

    @Override
    public ImageIcon getGeometryParameterImage() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(DefaultStiffenerProperties.class, "DefaultStiffenerProperties.displayname");
    }

    @Override
    public ImageIcon getImage() {
        return new ImageIcon(getClass().getResource("/de/elamx/clt/plateui/resources/FreieEingabeVersteifung.png")); // NOI18N
    }

    @Override
    public Image getNodeIcon() {
        return ImageUtilities.loadImage("/de/elamx/clt/plateui/resources/FreieEingabeVersteifung16.png"); // NOI18N
    }
}
