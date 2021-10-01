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
package de.elamx.clt.springinui.geometrycalculators;

import de.elamx.clt.springin.SpringInResult;
import de.elamx.laminate.Laminat;

/**
 *
 * @author Andreas Hauffe
 */
public class SimpleGeometryCalculator extends GeometryCalculator{
    
    private int numPointsForNinetyDegree;
    
    public SimpleGeometryCalculator(){
        this(20);
    }
    
    public SimpleGeometryCalculator(int numPointsForNinetyDegree){
        this.numPointsForNinetyDegree = numPointsForNinetyDegree;
    }

    @Override
    public double[][] getUndeformedGeometry(SpringInResult result, Laminat laminate) {
        return getGeometry(result, laminate, Math.toRadians(result.getInput().getAngle()));
    }

    @Override
    public double[][] getDeformedGeometry(SpringInResult result, Laminat laminate) {
        return getGeometry(result, laminate, Math.toRadians(result.getInput().getAngle() + result.getDeltaAngle()));
    }
    
    private double[][] getGeometry(SpringInResult result, Laminat laminate, double angle){
        
        double baseDeltaAngle = Math.PI/2.0 / numPointsForNinetyDegree;
        
        double fl = result.getInput().getFlangeLength();
        double thickness = laminate.getThickness();
        double radius = result.getInput().getRadius();
        
        int numSegments = (int)(angle/baseDeltaAngle) + 1;
        double deltaAngle = angle / numSegments;
        
        double[][] points = new double[7 + 2*numSegments][2];
        
        int index = 0;
        
        points[index][0] = 0.0; points[index][1] = 0.0;
        index++;
        points[index][0] = 0.0; points[index][1] =  fl;
        index++;
        
        double x,y;
        for (int ii = 0; ii < numSegments; ii++){
            x = (1-Math.cos(deltaAngle * (ii+1)))*(radius+thickness/2.0);
            y = Math.sin(deltaAngle * (ii+1))*(radius+thickness/2.0)+fl;
            points[index][0] = x; points[index][1] = y;
            index++;
        }
        
        double baseX = (1-Math.cos(angle))*(radius+thickness/2.0);
        double baseY = Math.sin(angle)*(radius+thickness/2.0)+fl;
        x =  fl * Math.cos(angle-Math.PI/2.0) + baseX;
        y = -fl * Math.sin(angle-Math.PI/2.0) + baseY;
        points[index][0] = x; points[index][1] = y;
        index++;
        
        baseX = (1-Math.cos(angle))*(radius-thickness/2.0)+thickness;
        baseY = Math.sin(angle)*(radius-thickness/2.0)+fl;
        x =   fl * Math.cos(angle-Math.PI/2.0) + baseX;
        y = - fl * Math.sin(angle-Math.PI/2.0) + baseY;
        points[index][0] = x; points[index][1] = y;
        index++;
        
        baseX = (1-Math.cos(angle))*(radius-thickness/2.0)+thickness;
        baseY = Math.sin(angle)*(radius-thickness/2.0)+fl;
        x = baseX;
        y = baseY;
        points[index][0] = x; points[index][1] = y;
        index++;
        
        for (int ii = numSegments-1; ii > -1; ii--){
            x = (1-Math.cos(deltaAngle * ii))*(radius-thickness/2.0)+thickness;
            y = Math.sin(deltaAngle * ii)*(radius-thickness/2.0)+fl;
            points[index][0] = x; points[index][1] = y;
            index++;
        }
        
        points[index][0] = thickness; points[index][1] = 0.0;
        index++;
        points[index][0] =       0.0; points[index][1] = 0.0;
        
        return points;
    }
    
}
