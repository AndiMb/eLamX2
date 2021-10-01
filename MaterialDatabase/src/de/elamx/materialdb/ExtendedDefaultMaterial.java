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
package de.elamx.materialdb;

import de.elamx.laminate.DefaultMaterial;

/**
 *
 * @author Andreas Hauffe
 */
public class ExtendedDefaultMaterial extends DefaultMaterial {
    
    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_UD = 0;
    public static final int TYPE_FABRIC = 1;
    public static final int TYPE_GELEGE = 2;
    
    public static final String PROP_FIBRE_TYPE = "fibreType";
    public static final String PROP_FIBRE_NAME = "fibreName";
    public static final String PROP_MATRIX_TYPE = "matrixType";
    public static final String PROP_MATRIX_NAME = "matrixName";
    public static final String PROP_PHI         = "phi";
    public static final String PROP_TYPE        = "type";

    private String fibreType;
    private String fibreName;
    private String matrixType;
    private String matrixName;
    private double phi;
    private int    type;

    public ExtendedDefaultMaterial(String uid, String name, double Epar, double Enor, double nue12, double G, double rho, boolean addToLookup) {
        super(uid, name, Epar, Enor, nue12, G, rho, addToLookup);
    }

    /**
     * Get the value of fibreType
     *
     * @return the value of fibreType
     */
    public String getFibreType() {
        return fibreType;
    }

    /**
     * Set the value of fibreType
     *
     * @param fibreType new value of fibreType
     */
    public void setFibreType(String fibreType) {
        this.fibreType = fibreType;
    }

    /**
     * Get the value of fibreName
     *
     * @return the value of fibreName
     */
    public String getFibreName() {
        return fibreName;
    }

    /**
     * Set the value of fibreName
     *
     * @param fibreName new value of fibreName
     */
    public void setFibreName(String fibreName) {
        this.fibreName = fibreName;
    }

    public String getMatrixType() {
        return matrixType;
    }

    public void setMatrixType(String matrixType) {
        this.matrixType = matrixType;
    }

    public String getMatrixName() {
        return matrixName;
    }

    public void setMatrixName(String matrixName) {
        this.matrixName = matrixName;
    }

    public double getPhi() {
        return phi;
    }

    public void setPhi(double phi) {
        this.phi = phi;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
