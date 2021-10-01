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
package de.elamx.clt;

/**
 *
 * @author Andreas Hauffe
 */
public class Strains {

    private double epsilon_x;
    private double epsilon_y;
    private double gamma_xy;
    private double kappa_x;
    private double kappa_y;
    private double kappa_xy;

    /**
     * Get the value of kappa_xy
     *
     * @return the value of kappa_xy
     */
    public double getKappa_xy() {
        return kappa_xy;
    }

    /**
     * Set the value of kappa_xy
     *
     * @param kappa_xy new value of kappa_xy
     */
    public void setKappa_xy(double kappa_xy) {
        this.kappa_xy = kappa_xy;
    }

    /**
     * Get the value of kappa_y
     *
     * @return the value of kappa_y
     */
    public double getKappa_y() {
        return kappa_y;
    }

    /**
     * Set the value of kappa_y
     *
     * @param kappa_y new value of kappa_y
     */
    public void setKappa_y(double kappa_y) {
        this.kappa_y = kappa_y;
    }

    /**
     * Get the value of kappa_x
     *
     * @return the value of kappa_x
     */
    public double getKappa_x() {
        return kappa_x;
    }

    /**
     * Set the value of kappa_x
     *
     * @param kappa_x new value of kappa_x
     */
    public void setKappa_x(double kappa_x) {
        this.kappa_x = kappa_x;
    }

    /**
     * Get the value of gamma_xy
     *
     * @return the value of gamma_xy
     */
    public double getGamma_xy() {
        return gamma_xy;
    }

    /**
     * Set the value of gamma_xy
     *
     * @param gamma_xy new value of gamma_xy
     */
    public void setGamma_xy(double gamma_xy) {
        this.gamma_xy = gamma_xy;
    }

    /**
     * Get the value of epsilon_y
     *
     * @return the value of epsilon_y
     */
    public double getEpsilon_y() {
        return epsilon_y;
    }

    /**
     * Set the value of epsilon_y
     *
     * @param epsilon_y new value of epsilon_y
     */
    public void setEpsilon_y(double epsilon_y) {
        this.epsilon_y = epsilon_y;
    }

    /**
     * Get the value of epsilon_x
     *
     * @return the value of epsilon_x
     */
    public double getEpsilon_x() {
        return epsilon_x;
    }

    /**
     * Set the value of epsilon_x
     *
     * @param epsilon_x new value of epsilon_x
     */
    public void setEpsilon_x(double epsilon_x) {
        this.epsilon_x = epsilon_x;
    }
    
    public double[] getEpsilonKappaAsVector(){
        return new double[]{epsilon_x, epsilon_y, gamma_xy, kappa_x, kappa_y, kappa_xy};
    }
}
