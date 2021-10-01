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
package de.elamx.core;

import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import java.util.ArrayList;

/**
 *
 *
 * @author Andreas Hauffe
 */
public class LaminatStringGenerator {

    public static String getLaminatAsHTMLString(Laminat laminate) {
        ArrayList<Layer> layers = laminate.getLayers();

        String lamString = " ";

        if (layers == null || layers.isEmpty()) {
            return lamString;
        }

        int numOfLayers = layers.size();

        StringBuilder lamStrBuf = new StringBuilder();
        lamStrBuf.append("[");

        boolean sym = false;

        if (numOfLayers > 1) {
            sym = laminate.isSymmetric();
            int halfNumOfLayers = numOfLayers;
            if (!sym) {
                halfNumOfLayers = numOfLayers / 2;
                sym = true;
                for (int ii = 0; ii < halfNumOfLayers; ii++) {
                    if (layers.get(ii).getAngle() != layers.get(numOfLayers - ii - 1).getAngle()) {
                        sym = false;
                        break;
                    }
                }
            }

            int numShowLayers = numOfLayers;
            boolean withMiddLayer = sym && laminate.isWithMiddleLayer();
            if (withMiddLayer){
                numShowLayers--;
            }else if (sym && !laminate.isSymmetric()) {
                numShowLayers = halfNumOfLayers;
                if (numOfLayers % 2 > 0) {
                    withMiddLayer = true;
                }
            }

            double angle;
            double lastAngle = Double.NaN;
            int counter = 1;
            for (int ii = 0; ii < numShowLayers; ii++) {
                angle = layers.get(ii).getAngle();
                if (angle == lastAngle) {
                    counter++;
                } else {
                    if (counter > 1) {
                        lamStrBuf.append("<sub>").append(counter).append("</sub>");
                        counter = 1;
                    }
                    lamStrBuf.append(ii > 0 ? "/" : "").append(getAngleAsString(angle));
                }
                lastAngle = angle;
            }
            if (counter > 1) {
                lamStrBuf.append("<sub>").append(counter).append("</sub>");
            }
            if (withMiddLayer) {
                lamStrBuf.append("/<U>").append(getAngleAsString(layers.get(numShowLayers).getAngle())).append("</U>");
            }
        } else {
            lamStrBuf.append(getAngleAsString(layers.get(0).getAngle()));
        }

        lamStrBuf.append("]");

        if (sym) {
            lamStrBuf.append("<sub>S</sub>");
        }

        return lamStrBuf.toString();
    }

    private static String getAngleAsString(double angle) {
        String retString = "";
        if (angle - (int) angle == 0.0) {
            retString += (int) angle;
        } else {
            retString += GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_ANGLE).format(angle);
        }
        return retString;
    }
}
