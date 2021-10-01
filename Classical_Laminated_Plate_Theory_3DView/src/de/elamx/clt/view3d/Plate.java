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
package de.elamx.clt.view3d;


import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.WireframeState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.clt.Loads;
import de.elamx.clt.Strains;
import de.view3d.Arrow;
import de.view3d.ArrowData;
import de.view3d.View3DProperties;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Andreas Hauffe
 */
public class Plate{

    private double zScale = 1.0;
    private double elemsize_x = 1.0;
    private double elemsize_y = 1.0;
    private double maxsize = 1.0;
    private double deltax = 1.0;
    private double deltay = 1.0;
    private int enum_x = 1;
    private int enum_y = 1;
    private double width;
    private double length;
    private final Strains strains;
    private final Loads loads;
    
    public Plate(Strains strains, Loads loads) {
        this.strains = strains;
        this.loads = loads;
        init();
    }

    private void init() {
        width = 1.0;
        length = 1.0;

        // Netzerzeugung

        int numOfElements = (int)(View3DProperties.getDefault().getNetQuality()*20);
        int minNumOfElements = 10;
        if (length >= width) {
            maxsize = length;
            enum_x = numOfElements;
            elemsize_x = length / enum_x;

            enum_y = (int) (width / elemsize_x);
            if (enum_y < minNumOfElements) {
                enum_y = minNumOfElements;
            }
            elemsize_y = width / enum_y;
        } else {
            maxsize = width;
            enum_y = numOfElements;
            elemsize_y = width / enum_y;

            enum_x = (int) (length / elemsize_y);
            if (enum_x < minNumOfElements) {
                enum_x = minNumOfElements;
            }
            elemsize_x = length / enum_x;
        }

        deltax = length / 2.0;
        deltay = width / 2.0;
    }

    public double getZScale() {
        return zScale;
    }

    public double getElementSizeX() {
        return elemsize_x;
    }

    public double getElementSizeY() {
        return elemsize_y;
    }

    public double getDeltaX() {
        return deltax;
    }

    public double getDeltaY() {
        return deltay;
    }

    public int getElementNumberX() {
        return enum_x;
    }

    public int getElementNumberY() {
        return enum_y;
    }

    public double getMaximumSize() {
        return maxsize;
    }

    public List<Mesh> getShapes(boolean reinit, double displScale){
        if (reinit) {
            init();
        }
        
        double[][] X = new double[enum_x + 1][enum_y + 1];
        double[][] Y = new double[enum_x + 1][enum_y + 1];
        
        double[][] dX = new double[enum_x + 1][enum_y + 1];
        double[][] dY = new double[enum_x + 1][enum_y + 1];
        double[][] dZ = new double[enum_x + 1][enum_y + 1];
        double[][] dV = new double[enum_x + 1][enum_y + 1];
        
        double eps_x = strains.getEpsilon_x();
        double eps_y = strains.getEpsilon_y();
        double gamma_xy = strains.getGamma_xy();
        double kappa_x = strains.getKappa_x();
        double kappa_y = strains.getKappa_y();
        double kappy_xy = strains.getKappa_xy();
        
        double vMax = -Double.MAX_VALUE;
        
        for (int jj = 0; jj < enum_y+1; jj++) {
            for (int ii = 0; ii < enum_x+1; ii++) {
                X[ii][jj] = elemsize_x * ii - deltax;
                Y[ii][jj] = elemsize_y * jj - deltay;
                
                dX[ii][jj] = (X[ii][jj] * eps_x + 0.5 * gamma_xy * Y[ii][jj])*displScale;
                dY[ii][jj] = (Y[ii][jj] * eps_y + 0.5 * gamma_xy * X[ii][jj])*displScale;
                dZ[ii][jj] = (-kappa_x*X[ii][jj]*X[ii][jj]/2.0 - kappa_y*Y[ii][jj]*Y[ii][jj]/2.0 - kappy_xy*X[ii][jj]*Y[ii][jj]/2.0)*displScale;
                
                dV[ii][jj] = Math.sqrt(dX[ii][jj]*dX[ii][jj] + dY[ii][jj]*dY[ii][jj] + dZ[ii][jj]*dZ[ii][jj]);
                if (dV[ii][jj] > vMax){
                    vMax = dV[ii][jj];
                }
            }
        }

        int numPoints = enum_x * enum_y * 4;
        Vector3[] vertices = new Vector3[numPoints];
        ColorRGBA[] colors = new ColorRGBA[numPoints];
        double yTemp, xTemp, zTemp;

        // creation of Quad-Elements and adding of coordinates in kartesian plate coordinate system
        int index = 0;
        for (int jj = 0; jj < enum_y; jj++) {
            for (int ii = 0; ii < enum_x; ii++) {
                xTemp = (X[ii][jj]+dX[ii][jj])/maxsize;
                yTemp = (Y[ii][jj]+dY[ii][jj])/maxsize;
                zTemp = dZ[ii][jj]/maxsize;
                vertices[index] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                colors[index] = getRainbowColor(1.0 - Math.abs(dV[ii][jj] / vMax));
                index++;

                xTemp = (X[ii+1][jj]+dX[ii+1][jj])/maxsize;
                yTemp = (Y[ii+1][jj]+dY[ii+1][jj])/maxsize;
                zTemp = dZ[ii+1][jj]/maxsize;
                vertices[index] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                colors[index] = getRainbowColor(1.0 - Math.abs(dV[ii+1][jj] / vMax));
                index++;

                xTemp = (X[ii+1][jj+1]+dX[ii+1][jj+1])/maxsize;
                yTemp = (Y[ii+1][jj+1]+dY[ii+1][jj+1])/maxsize;
                zTemp = dZ[ii+1][jj+1]/maxsize;
                vertices[index] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                colors[index] = getRainbowColor(1.0 - Math.abs(dV[ii+1][jj+1] / vMax));
                index++;

                xTemp = (X[ii][jj+1]+dX[ii][jj+1])/maxsize;
                yTemp = (Y[ii][jj+1]+dY[ii][jj+1])/maxsize;
                zTemp = dZ[ii][jj+1]/maxsize;
                vertices[index] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                colors[index] = getRainbowColor(1.0 - Math.abs(dV[ii][jj+1] / vMax));
                index++;
            }
        }

        ArrayList<Mesh> shapes = new ArrayList<>();
        
        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setColorBuffer(BufferUtils.createFloatBuffer(colors));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();
        
                // Add a material state
        final MaterialState ms = new MaterialState();
        // Pull diffuse color for front from mesh color
        ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
        ms.setColorMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        mesh.setRenderState(ms);

        shapes.add(mesh);
        
        return shapes;
    }
    
    public List<Node> getUndeformedWithBC(){
        
        ArrayList<Node> group = new ArrayList<>();
        
        double nx = loads.getN_x();
        double ny = loads.getN_y();
        double nxy= loads.getN_xy();
        double mx = loads.getM_x();
        double my = loads.getM_y();
        double mxy= loads.getM_xy();
        
        if (nx != 0.0){
            ArrowData data = new ArrowData(
                    new float[]{(float)(-length/maxsize/2.0), 0.0f, 0.0f}, 
                    new float[]{(float)(-Math.signum(nx)), 0.0f, 0.0f}, 
                    1.0f,
                    nx > 0.0 ? ArrowData.POSREF_TAIL : ArrowData.POSREF_TIP,
                    1);
            group.add(new Arrow(data));
            
            data = new ArrowData(
                    new float[]{(float)(length/maxsize/2.0), 0.0f, 0.0f}, 
                    new float[]{(float)(Math.signum(nx)), 0.0f, 0.0f}, 
                    1.0f,
                    nx > 0.0 ? ArrowData.POSREF_TAIL : ArrowData.POSREF_TIP,
                    1);
            group.add(new Arrow(data));
        }
        
        if (ny != 0.0){
            ArrowData data = new ArrowData(
                    new float[]{0.0f, (float)(-width/maxsize/2.0), 0.0f}, 
                    new float[]{0.0f, (float)(-Math.signum(ny)), 0.0f}, 
                    1.0f,
                    ny > 0.0 ? ArrowData.POSREF_TAIL : ArrowData.POSREF_TIP,
                    1);
            group.add(new Arrow(data));
            
            data = new ArrowData(
                    new float[]{0.0f, (float)( width/maxsize/2.0), 0.0f}, 
                    new float[]{0.0f, (float)(Math.signum(ny)), 0.0f}, 
                    1.0f,
                    ny > 0.0 ? ArrowData.POSREF_TAIL : ArrowData.POSREF_TIP,
                    1);
            group.add(new Arrow(data));
        }
        
        if (nxy != 0.0){
            ArrowData data = new ArrowData(new float[]{(float)(-length/maxsize/2.0), 0.0f, 0.0f}, new float[]{0.0f, (float)(-Math.signum(nxy)), 0.0f}, 1.0f, ArrowData.POSREF_TIP, 1);
            group.add(new Arrow(data));
            
            data = new ArrowData(new float[]{(float)(length/maxsize/2.0), 0.0f, 0.0f}, new float[]{0.0f, (float)(Math.signum(nxy)), 0.0f}, 1.0f, ArrowData.POSREF_TIP, 1);
            group.add(new Arrow(data));
            
            data = new ArrowData(new float[]{0.0f, (float)(-width/maxsize/2.0), 0.0f}, new float[]{(float)(-Math.signum(nxy)),  0.0f, 0.0f}, 1.0f, ArrowData.POSREF_TIP, 1);
            group.add(new Arrow(data));
            
            data = new ArrowData(new float[]{0.0f, (float)( width/maxsize/2.0), 0.0f}, new float[]{(float)(Math.signum(nxy)), 0.0f, 0.0f}, 1.0f, ArrowData.POSREF_TIP, 1);
            group.add(new Arrow(data));
        }
        
        if (mx != 0.0){
            ArrowData data = new ArrowData(new float[]{(float)(-length/maxsize/2.0), 0.0f, 0.0f}, new float[]{0.0f, (float)(-Math.signum(mx)), 0.0f}, 1.0f, ArrowData.POSREF_TAIL, 2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
            
            data = new ArrowData(new float[]{(float)(length/maxsize/2.0), 0.0f, 0.0f}, new float[]{0.0f, (float)(Math.signum(mx)), 0.0f}, 1.0f, ArrowData.POSREF_TAIL, 2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
        }
        
        if (my != 0.0){
            ArrowData data = new ArrowData(new float[]{0.0f, (float)(-width/maxsize/2.0), 0.0f}, new float[]{(float)(-Math.signum(my)),  0.0f, 0.0f}, 1.0f, ArrowData.POSREF_TAIL, 2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
            
            data = new ArrowData(new float[]{0.0f, (float)( width/maxsize/2.0), 0.0f}, new float[]{(float)(Math.signum(my)), 0.0f, 0.0f}, 1.0f, ArrowData.POSREF_TAIL, 2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
        }
        
        if (mxy != 0.0){
            ArrowData data = new ArrowData(
                    new float[]{(float)(-length/maxsize/2.0), 0.0f, 0.0f}, 
                    new float[]{(float)(Math.signum(mxy)), 0.0f, 0.0f}, 
                    1.0f,
                    mxy > 0.0 ? ArrowData.POSREF_TAIL : ArrowData.POSREF_TIP,
                    2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
            
            data = new ArrowData(
                    new float[]{(float)(length/maxsize/2.0), 0.0f, 0.0f}, 
                    new float[]{(float)(-Math.signum(mxy)), 0.0f, 0.0f}, 
                    1.0f,
                    mxy > 0.0 ? ArrowData.POSREF_TAIL : ArrowData.POSREF_TIP,
                    2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
            
            data = new ArrowData(
                    new float[]{0.0f, (float)(-width/maxsize/2.0), 0.0f}, 
                    new float[]{0.0f, (float)(-Math.signum(mxy)), 0.0f}, 
                    1.0f,
                    mxy > 0.0 ? ArrowData.POSREF_TIP : ArrowData.POSREF_TAIL,
                    2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
            
            data = new ArrowData(
                    new float[]{0.0f, (float)( width/maxsize/2.0), 0.0f}, 
                    new float[]{0.0f, (float)(Math.signum(mxy)), 0.0f}, 
                    1.0f,
                    mxy > 0.0 ? ArrowData.POSREF_TIP : ArrowData.POSREF_TAIL,
                    2);
            group.add(new Arrow(data, new ColorRGBA(0.0f, 0.0f, 1.0f, 1.0f)));
        }
        
        Vector3[] vertices = new Vector3[4];
        
        vertices[0] = new Vector3((float)(-length/maxsize/2.0), (float)(-width/maxsize/2.0), (float) 0.0);
        vertices[1] = new Vector3((float)(length/maxsize/2.0), (float)(-width/maxsize/2.0), (float) 0.0);
        vertices[2] = new Vector3((float)(length/maxsize/2.0), (float)(width/maxsize/2.0), (float) 0.0);
        vertices[3] = new Vector3((float)(-length/maxsize/2.0), (float) (width/maxsize/2.0), (float) 0.0);
        
        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();
        
        mesh.setDefaultColor(ColorRGBA.BLUE);
        
        setUndeformedPlateAppearance(mesh);
        
        Node meshNode = new Node();
        
        meshNode.attachChild(mesh);
        
        group.add(meshNode);
        
        return group;
    }

    public ColorRGBA getRainbowColor(double value) {
        int r = 0;
        int g = 255;
        int b = 0;

        if (value < 0.25) {
            r = 255;
        } else if (value <= 0.5 && value >= 0.25) {
            r = (int) (255.0 - 255.0 * (value - 0.25) / 0.25);
        }

        if (value <= 0.25) {
            g = (int) (255.0 * value / 0.25);
        } else if (value >= 0.75) {
            g = (int) (255.0 - 255.0 * (value - 0.75) / 0.25);
        }

        if (value > 0.75) {
            b = 255;
        } else if (value >= 0.5 && value <= 0.75) {
            b = (int) (255.0 * (value - 0.5) / 0.25);
        }

        return new ColorRGBA(r/255.0f, g/255.0f, b/255.0f, 1.0f);
    }

    private static void setUndeformedPlateAppearance(Mesh mesh) {
        
        // Add a material state
        final MaterialState ms = new MaterialState();
        // Pull diffuse color for front from mesh color
        ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
        ms.setColorMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        mesh.setRenderState(ms);
        
        final WireframeState ws = new WireframeState();
        ws.setLineWidth(1.0f);
        ws.setAntialiased(true);
        mesh.setRenderState(ws);
    }
}
