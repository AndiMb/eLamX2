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
package de.elamx.clt.plate.view3d;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.renderer.queue.RenderBucketType;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.hint.TransparencyType;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.clt.CLT_Layer;
import de.elamx.clt.plate.DeformationInput;
import de.elamx.clt.plate.DeformationResult;
import de.elamx.clt.plate.Mechanical.PointLoad;
import de.elamx.clt.plate.Mechanical.SurfaceLoad_const_full;
import de.elamx.clt.plate.Mechanical.TransverseLoad;
import de.elamx.clt.plate.Stiffener.Properties.StiffenerProperties;
import static de.elamx.clt.plate.view3d.Plate.setPlateAppearance;
import de.elamx.core.GlobalProperties;
import de.elamx.laminate.StressStrainState;
import de.view3d.Arrow;
import de.view3d.ArrowData;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andreas Hauffe
 */
public class DeformationPlate extends Plate<DeformationInput> {

    public static final int DISPLACEMENT_Z = 0;
    public static final int LOCAL_STRAIN_X = 1;
    public static final int LOCAL_STRAIN_Y = 2;
    public static final int LOCAL_STRAIN_XY = 4;
    public static final int LOCAL_STRESS_X = 8;
    public static final int LOCAL_STRESS_Y = 16;
    public static final int LOCAL_STRESS_XY = 32;
    public static final int MIN_RESERVE_FACTOR = 64;
    public static final int UPPER = CLT_Layer.POSITION_UPPER;
    public static final int LOWER = CLT_Layer.POSITION_LOWER;
    public static final int MIDDLE = CLT_Layer.POSITION_MIDDLE;

    private final DeformationResult result;
    private double[][] dZ;
    private double[] maxminvec;
    private double[][][] kappa;

    private int resultType = DISPLACEMENT_Z;
    private int layerNumber = 0;
    private int position = MIDDLE;

    public DeformationPlate(DeformationInput input, DeformationResult result) {
        super(input);
        this.result = result;
        init_dZ_Kappa();
    }

    public double[] getMaxminvec() {
        return maxminvec;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public int getLayerNumber() {
        return layerNumber;
    }

    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private void init_dZ_Kappa() {
        dZ = new double[enum_x + 1][enum_y + 1];
        kappa = new double[3][enum_x + 1][enum_y + 1];
        double yTemp, xTemp, zTemp;
        double w2dx2, w2dxdy, w2dy2;

        double[] eigen;

        // calculation of plate deflection for all grid positions
        double[] bx_wx = new double[m];
        double[] by_wx = new double[n];
        double[] bx_wdx = new double[m];
        double[] by_wdx = new double[n];
        double[] bx_wdx2 = new double[m];
        double[] by_wdx2 = new double[n];

        double[][] eigenvector = result.getResultvectors();

        for (int jj = 0; jj <= enum_y; jj++) {
            yTemp = jj * elemsize_y;
            for (int nn = 0; nn < n; nn++) {
                by_wx[nn] = by.wx(nn, yTemp);
                by_wdx[nn] = by.wdx(nn, yTemp);
                by_wdx2[nn] = by.wdx2(nn, yTemp);
            }

            for (int ii = 0; ii <= enum_x; ii++) {

                xTemp = ii * elemsize_x;
                for (int mm = 0; mm < m; mm++) {
                    bx_wx[mm] = bx.wx(mm, xTemp);
                    bx_wdx[mm] = bx.wdx(mm, xTemp);
                    bx_wdx2[mm] = bx.wdx2(mm, xTemp);
                }
                zTemp = 0.0;
                w2dx2 = 0.0;
                w2dxdy = 0.0;
                w2dy2 = 0.0;

                for (int mm = 0; mm < m; mm++) {
                    eigen = eigenvector[mm];
                    for (int nn = 0; nn < n; nn++) {
                        zTemp += eigen[nn] * bx_wx[mm] * by_wx[nn];
                        w2dx2 += eigen[nn] * bx_wdx2[mm] * by_wx[nn];
                        w2dxdy += eigen[nn] * bx_wdx[mm] * by_wdx[nn];
                        w2dy2 += eigen[nn] * bx_wx[mm] * by_wdx2[nn];
                    }
                }
                dZ[ii][jj] = zTemp;
                kappa[0][ii][jj] = w2dx2;
                kappa[1][ii][jj] = w2dy2;
                kappa[2][ii][jj] = 2.0 * w2dxdy;
            }
        }
    }
    
    public double[][] getValues(int resultType){
        switch (resultType) {
            case DISPLACEMENT_Z:
                return dZ;
            case LOCAL_STRAIN_X:
            case LOCAL_STRAIN_Y:
            case LOCAL_STRAIN_XY:
            case LOCAL_STRESS_X:
            case LOCAL_STRESS_Y:
            case LOCAL_STRESS_XY:
            case MIN_RESERVE_FACTOR:
                return getStressStrain(resultType, kappa);
            default:
                return dZ;
        }
    }

    @Override
    public List<Mesh> getShapes(boolean reinit) {
        if (reinit) {
            init();
            init_dZ_Kappa();
        }

        double minVal = Double.POSITIVE_INFINITY;
        double maxVal = Double.NEGATIVE_INFINITY;
        double minZ = Double.POSITIVE_INFINITY;
        double maxZ = Double.NEGATIVE_INFINITY;

        int numPoints = enum_x * enum_y * 4;
        Vector3[] vertices = new Vector3[numPoints];
        ColorRGBA[] colors = new ColorRGBA[numPoints];

        double yTemp, xTemp, zTemp, value;
        double[][] values = getValues(resultType);

        for (int jj = 0; jj <= enum_y; jj++) {
            for (int ii = 0; ii <= enum_x; ii++) {
                zTemp = dZ[ii][jj];
                value = values[ii][jj];

                if (value > maxVal) {
                    maxVal = value;
                } else if (value < minVal) {
                    minVal = value;
                }

                if (zTemp > maxZ) {
                    maxZ = zTemp;
                } else if (zTemp < minZ) {
                    minZ = zTemp;
                }
            }
        }

        if (maxVal == Double.NEGATIVE_INFINITY) {
            maxVal = Double.POSITIVE_INFINITY;
        }

        maxminvec = new double[]{minVal, maxVal};

        maxZ /= maxsize;
        minZ /= maxsize;
        zScale = 1.0 / maxsize;
        double scale = maxVal - minVal;

        if (resultType == MIN_RESERVE_FACTOR) {
            minVal = 0;
            maxVal = 2;
            scale = maxVal - minVal;
            for (double[] value1 : values) {
                for (int jj = 0; jj < values[0].length; jj++) {
                    // Achtung: Das könnte ins Auge gehen. Wenn NaN wird hier davon ausgegangen, dass die 
                    // Spannungen zu klein waren und demnacht der ReserveFaktor groß. Das könnte aber schiefgehen.
                    value1[jj] = value1[jj] >= 1.0 || Double.isNaN(value1[jj]) ? 1 : 2;
                }
            }
        }

        // creation of Quad-Elements and adding of coordinates in kartesian plate coordinate system
        ColorRGBA farbe;
        for (int jj = 0; jj < enum_y; jj++) {
            for (int ii = 0; ii < enum_x; ii++) {
                xTemp = elemsize_x * ii;
                yTemp = elemsize_y * jj;
                xTemp = (xTemp - deltax) / maxsize;
                yTemp = (yTemp - deltay) / maxsize;
                zTemp = zScale * dZ[ii][jj];
                vertices[(ii + jj * enum_x) * 4] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - (values[ii][jj] - minVal) / scale);
                colors[(ii + jj * enum_x) * 4] = farbe;

                xTemp = elemsize_x * (ii + 1);
                xTemp = (xTemp - deltax) / maxsize;
                zTemp = zScale * dZ[ii + 1][jj];
                vertices[(ii + jj * enum_x) * 4 + 1] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - (values[ii + 1][jj] - minVal) / scale);
                colors[(ii + jj * enum_x) * 4 + 1] = farbe;

                yTemp = elemsize_y * (jj + 1);
                yTemp = (yTemp - deltay) / maxsize;
                zTemp = zScale * dZ[ii + 1][jj + 1];
                vertices[(ii + jj * enum_x) * 4 + 2] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - (values[ii + 1][jj + 1] - minVal) / scale);
                colors[(ii + jj * enum_x) * 4 + 2] = farbe;

                xTemp = elemsize_x * ii;
                xTemp = (xTemp - deltax) / maxsize;
                zTemp = zScale * dZ[ii][jj + 1];
                vertices[(ii + jj * enum_x) * 4 + 3] = new Vector3((float) xTemp, (float) yTemp, (float) (zTemp));
                farbe = getRainbowColor(1.0 - (values[ii][jj + 1] - minVal) / scale);
                colors[(ii + jj * enum_x) * 4 + 3] = farbe;
            }
        }

        ArrayList<Mesh> shapes = new ArrayList<>();

        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setColorBuffer(BufferUtils.createFloatBuffer(colors));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();
        setPlateAppearance(mesh);

        shapes.add(mesh);

        for (StiffenerProperties s : input.getStiffenerProperties()) {
            if (s.getDirection() == StiffenerProperties.X_DIRECTION) {
                Mesh shape = Stiffenerx.getShape(this, s, result.getResultvectors());
                setStiffenerAppearance(shape);
                shapes.add(shape);
            } else if (s.getDirection() == StiffenerProperties.Y_DIRECTION) {
                Mesh shape = Stiffenery.getShape(this, s, result.getResultvectors());
                setStiffenerAppearance(shape);
                shapes.add(shape);
            }
        }

        return shapes;
    }

    private double[][] getStressStrain(int type, double[][][] kappa) {
        CLT_Layer layer = result.getLaminate().getCLTLayers()[layerNumber];

        double[][] values = new double[kappa[0].length][kappa[0][0].length];

        for (int ii = 0; ii < kappa[0].length; ii++) {
            for (int jj = 0; jj < kappa[0][0].length; jj++) {

                double[] epskappa = new double[]{0.0, 0.0, 0.0, kappa[0][ii][jj], kappa[1][ii][jj], kappa[2][ii][jj]};

                StressStrainState sss = layer.getStressState(epskappa, 0.0, 0.0, position, false)[0];

                switch (type) {
                    case LOCAL_STRAIN_X:
                        values[ii][jj] = sss.getStrain()[0];
                        break;
                    case LOCAL_STRAIN_Y:
                        values[ii][jj] = sss.getStrain()[1];
                        break;
                    case LOCAL_STRAIN_XY:
                        values[ii][jj] = sss.getStrain()[2];
                        break;
                    case LOCAL_STRESS_X:
                        values[ii][jj] = sss.getStress()[0];
                        break;
                    case LOCAL_STRESS_Y:
                        values[ii][jj] = sss.getStress()[1];
                        break;
                    case LOCAL_STRESS_XY:
                        values[ii][jj] = sss.getStress()[2];
                        break;
                    case MIN_RESERVE_FACTOR:
                        values[ii][jj] = layer.getLayer().getCriterion().getReserveFactor(layer.getLayer().getMaterial(), layer.getLayer(), sss).getMinimalReserveFactor();
                        break;
                }
            }
        }
        return values;
    }

    @Override
    public List<Node> getUndeformedWithBC() {
        NumberFormat nf = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_FORCE);
        int fontSize = 20;
        Font f = new Font(Font.SERIF, Font.PLAIN, fontSize);
        ArrayList<Node> group = new ArrayList<>();

        for (TransverseLoad l : input.getLoads()) {
            if (l instanceof PointLoad) {
                PointLoad iL = (PointLoad) l;

                if (iL.getForce() == 0.0) {
                    break;
                }

                ArrowData data = new ArrowData(new float[]{(float) (iL.getX() / maxsize), (float) (iL.getY() / maxsize), 0.0f}, new float[]{0.0f, 0.0f, (float) (Math.signum(iL.getForce()))}, 1.0f, ArrowData.POSREF_TIP, 1);
                group.add(new Arrow(data));

                Node n = new Node();
                AttributedString forceCaption = new AttributedString(nf.format(iL.getForce()));
                forceCaption.addAttribute(TextAttribute.SIZE, f.getSize());
                forceCaption.addAttribute(TextAttribute.FAMILY, f.getStyle());
                n.attachChild(new com.ardor3d.RasterTextLabel(forceCaption, Color.BLACK, iL.getX() / maxsize + 0.01, iL.getY() / maxsize + 0.01, -0.08 * Math.signum(iL.getForce())));
                group.add(n);

            } else if (l instanceof SurfaceLoad_const_full) {
                SurfaceLoad_const_full iL = (SurfaceLoad_const_full) l;

                if (iL.getForce() == 0.0) {
                    break;
                }

                double xmin = -getInput().getLength() / 2.0 / maxsize;
                double xmax = getInput().getLength() / 2.0 / maxsize;
                double ymin = -getInput().getWidth() / 2.0 / maxsize;
                double ymax = getInput().getWidth() / 2.0 / maxsize;

                ArrowData data = new ArrowData(new float[]{(float) (xmin), (float) (ymin), 0.0f}, new float[]{0.0f, 0.0f, (float) (Math.signum(iL.getForce()))}, 1.0f, ArrowData.POSREF_TIP, 1);
                group.add(new Arrow(data));

                data = new ArrowData(new float[]{(float) (xmin), (float) (ymax), 0.0f}, new float[]{0.0f, 0.0f, (float) (Math.signum(iL.getForce()))}, 1.0f, ArrowData.POSREF_TIP, 1);
                group.add(new Arrow(data));

                data = new ArrowData(new float[]{(float) (xmax), (float) (ymax), 0.0f}, new float[]{0.0f, 0.0f, (float) (Math.signum(iL.getForce()))}, 1.0f, ArrowData.POSREF_TIP, 1);
                group.add(new Arrow(data));

                data = new ArrowData(new float[]{(float) (xmax), (float) (ymin), 0.0f}, new float[]{0.0f, 0.0f, (float) (Math.signum(iL.getForce()))}, 1.0f, ArrowData.POSREF_TIP, 1);
                group.add(new Arrow(data));

                Vector3[] vertices = new Vector3[4];

                float zPos = (float) (Math.signum(iL.getForce())) * Arrow.getLength();

                vertices[0] = new Vector3(xmin, ymin, -zPos);
                vertices[1] = new Vector3(xmin, ymax, -zPos);
                vertices[2] = new Vector3(xmax, ymax, -zPos);
                vertices[3] = new Vector3(xmax, ymin, -zPos);

                Vector3[] normals = new Vector3[4];
                normals[0] = new Vector3(0.0, 0.0, 1.0);
                normals[1] = new Vector3(0.0, 0.0, 1.0);
                normals[2] = new Vector3(0.0, 0.0, 1.0);
                normals[3] = new Vector3(0.0, 0.0, 1.0);

                final Mesh mesh = new Mesh();
                final MeshData meshData = mesh.getMeshData();

                meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
                meshData.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
                meshData.setIndexMode(IndexMode.Quads);
                mesh.setSolidColor(new ColorRGBA(1.0f, 0.0f, 0.0f, 0.8f));

                mesh.updateModelBound();

                // Add a material state
                final MaterialState ms = new MaterialState();
                // Pull diffuse color for front from mesh color
                ms.setColorMaterial(MaterialState.ColorMaterial.AmbientAndDiffuse);
                ms.setColorMaterialFace(MaterialState.MaterialFace.FrontAndBack);
                // Set shininess for front and back
                ms.setShininess(MaterialState.MaterialFace.FrontAndBack, 100);
                mesh.setRenderState(ms);

                BlendState blend = new BlendState();
                blend.setBlendEnabled(true);
                mesh.setRenderState(blend);

                mesh.getSceneHints().setRenderBucketType(RenderBucketType.Transparent);
                mesh.getSceneHints().setTransparencyType(TransparencyType.TwoPass);

                Node meshNode = new Node();

                meshNode.attachChild(mesh);

                group.add(meshNode);
            }
        }

        return group;
    }

}
