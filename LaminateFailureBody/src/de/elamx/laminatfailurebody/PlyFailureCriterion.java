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
package de.elamx.laminatfailurebody;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.clt.CLT_Laminate;
import de.elamx.clt.CLT_Layer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.LayerMaterial;
import de.elamx.laminate.StressStrainState;
import de.elamx.laminate.failure.ReserveFactor;
import de.elamx.mathtools.MatrixTools;
import java.awt.Color;
import java.util.ArrayList;


/**
 * 
 * 
 * 
 * @author Iwan Kapppes
 * @version 0.1
 */
public class PlyFailureCriterion{

    public static final int    FIRST_PLY_FAILURE = 0;
    public static final int    FINAL_FAILURE     = 1;

    private static final double ALPHA_START = -Math.PI/2.0;
    private static final double ALPHA_END   =  Math.PI/2.0;
    private static final double BETA_START  =  0.0;
    private static final double BETA_END    =  2.0*Math.PI;

    Laminat laminate = null;
    ArrayList<Color> layerColor_     = null;

    private int failureType_ = FINAL_FAILURE;
    private int alphaSteps_  = 200;
    private int betaSteps_   = 200;
    
    private static final double EPS = 1.0E-8;
    private static final double EPS_ZERO = 1.0E-14;
    
    private double scalingFaktor = 0.0;

    private final double[] axisIntersections = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    
    private boolean ABD_ok = true;
    
    public PlyFailureCriterion(Laminat laminate, ArrayList<Color> layerColor, int failureType, int resolution){
        this.laminate   = laminate;
        layerColor_     = layerColor;
        failureType_    = failureType;
        alphaSteps_     = (int)(resolution/4.0+0.76)*4;                         // Die Anzahl der Schritte muss durch vier teilbar sein, damit die Schnittpunkte mit den Achsen Teile der Punktwolke sind.
        betaSteps_      = alphaSteps_;
    }
    
    public FailureSurfaceResult getCriterion(){
        
        if (laminate.getLayers().isEmpty()){
            return null;
        }
        
        double[][][] punkte = getPointInformation();

        Vector3 normal;
        
        Vector3[] vertices = new Vector3[alphaSteps_*betaSteps_*4];
        Vector3[] normals  = new Vector3[alphaSteps_*betaSteps_*4];
        ColorRGBA[] colors  = new ColorRGBA[alphaSteps_*betaSteps_*4];
        
        double[] p1,p2,p3,p4;

        for (int i = 0 ; i < alphaSteps_ ; i++){
            for (int j = 0 ; j < betaSteps_ ; j++){
                // 1. Punkt des Quads
                p1 = punkte[i][j];
                p2 = punkte[i+1][j];
                p3 = punkte[i+1][j+1];
                p4 = punkte[i][j+1];

                vertices[4*(j+i*betaSteps_)+3] = new Vector3((float)(p1[0]),(float)(p1[1]),(float)(p1[2]));
                normal = getNormal(p1,p2,p4);
                normal.normalizeLocal();
                normals[4*(j+i*betaSteps_)+3] = normal;
                Color c = layerColor_.get((int)p1[3]);
                colors[4*(j+i*betaSteps_)+3] = new ColorRGBA(c.getRed()/255.f, c.getGreen()/255.f, c.getBlue()/255.f, c.getAlpha()/255.f);
                // 2. Punkt des Quads
                vertices[4*(j+i*betaSteps_)+2] = new Vector3((float)(p2[0]),(float)(p2[1]),(float)(p2[2]));
                normal = getNormal(p2,p3,p1);
                normal.normalizeLocal();
                normals[4*(j+i*betaSteps_)+2] = normal;
                c = layerColor_.get((int)p2[3]);
                colors[4*(j+i*betaSteps_)+2] = new ColorRGBA(c.getRed()/255.f, c.getGreen()/255.f, c.getBlue()/255.f, c.getAlpha()/255.f);
                // 3. Punkt des Quads
                vertices[4*(j+i*betaSteps_)+1] = new Vector3((float)(p3[0]),(float)(p3[1]),(float)(p3[2]));
                normal = getNormal(p3,p4,p2);
                normal.normalizeLocal();
                normals[4*(j+i*betaSteps_)+1] = normal;
                c = layerColor_.get((int)p3[3]);
                colors[4*(j+i*betaSteps_)+1] = new ColorRGBA(c.getRed()/255.f, c.getGreen()/255.f, c.getBlue()/255.f, c.getAlpha()/255.f);
                // 4. Punkt des Quads
                vertices[4*(j+i*betaSteps_)+0] = new Vector3((float)(p4[0]),(float)(p4[1]),(float)(p4[2]));
                normal = getNormal(p4,p1,p3);
                normal.normalizeLocal();
                normals[4*(j+i*betaSteps_)+0] = normal;
                c = layerColor_.get((int)p4[3]);
                colors[4*(j+i*betaSteps_)+0] = new ColorRGBA(c.getRed()/255.f, c.getGreen()/255.f, c.getBlue()/255.f, c.getAlpha()/255.f);
            }
        }
        
        final Mesh mesh = new Mesh();
        final MeshData meshData = mesh.getMeshData();

        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(vertices));
        meshData.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
        meshData.setColorBuffer(BufferUtils.createFloatBuffer(colors));
        meshData.setIndexMode(IndexMode.Quads);

        mesh.updateModelBound();

        return new FailureSurfaceResult(mesh, scalingFaktor, axisIntersections);
    }
            
    public ColorRGBA getColor() {
        float r = 0;
        float g = 0;
        float b = 1;
        if (failureType_ == PlyFailureCriterion.FIRST_PLY_FAILURE){
            g = 1;
            b = 0;
        }
        
        return new ColorRGBA(r,g,b, 1.0f);
    }

    private double[][][] getPointInformation(){
        int mm,nn;                                      // Hilfvariablen als Zähler
        int numLayers = laminate.getNumberofLayers();   // Anzahl der Lagen

        boolean[] ZFB  = new boolean[numLayers];        // Vektor, das definiert welche Lagen aktiv sind

        double alpha, beta, cos_alpha;                  // Hilfsgrößen
        double rfMax;                                   // maximaler Reservefaktor
        int    layerNum;                                // Lagennummer
        double lastLayerNum;                            // Lagennummer
        double RF;                                      // Reservefaktor
        double dAlpha  = (ALPHA_END-ALPHA_START)/alphaSteps_;   // Delta in Alpharichtung
        double dBeta   = ( BETA_END- BETA_START)/betaSteps_;    // Delta in Betarichtung

        double[] nVec       = new double[3];            // Lastvetkor
        double[] VecNMax   = new double[3];             // maximaler Kraft
        double[] eVec       = new double[6];            // Verzerrungsvektor
        double[] rfMinLayer = new double[numLayers];    // Vektor mit den minimalen Reservefaktoren der Lagen
        int[]    failTypLay = new int[numLayers];

        double[][]   ABDmatInv;                         // Inverse ABD-Matrix

        double[][][] punkte = new double [alphaSteps_+1][betaSteps_+1][4];  // Punktinformationen für die Darstellung 1-3 x,y,z Koordinate, 4 Farbe
        
        CLT_Laminate clt_lam = laminate.getLookup().lookup(CLT_Laminate.class);
        if (clt_lam == null) {
            clt_lam = new CLT_Laminate(laminate);
        }
        CLT_Layer[] origLayers = clt_lam.getCLTLayers();
        
        CLT_Layer[] layers = new CLT_Layer[origLayers.length];
        
        for (int ii = 0; ii < origLayers.length; ii++){
            Layer oldL = origLayers[ii].getLayer();
            Layer newL = new Layer("1", "noname", getAsDefaultMaterial(oldL.getMaterial()), oldL.getAngle(), oldL.getThickness(), oldL.getCriterion());
            layers[ii] = new CLT_Layer(newL);
            layers[ii].setZm(origLayers[ii].getZm());
            ZFB[ii]    = false;
        }

        // Generieren der inversen A-Matrix
        ABDmatInv = getABDMatInv(layers, ZFB);
        
        double maxVal = 0.0;

        for (int ii = 0; ii < alphaSteps_+1; ii++){
            alpha     = dAlpha*ii+ALPHA_START;
            nVec[0]   = Math.sin(alpha);
            cos_alpha = Math.cos(alpha);
            for (int jj = 0; jj < betaSteps_+1; jj++){
                beta = dBeta*jj+BETA_START;
                nVec[1] = Math.sin(beta) * cos_alpha;
                nVec[2] = Math.cos(beta) * cos_alpha;

                rfMax        =  0.0;
                lastLayerNum = -1.0;
                
                boolean firstFB = false;
                
                do{
                    // Berechnen der Dehnungen
                    for (mm = 0; mm < 6; mm++){
                        eVec[mm] = 0;
                        for (nn = 0; nn < 3; nn++) eVec[mm] += ABDmatInv[mm][nn] * nVec[nn];
                    }

                    // Suchen des minimalen Reservefaktors
                    RF = Double.MAX_VALUE;
                    layerNum = Integer.MAX_VALUE;
                    for (mm = 0; mm < numLayers; mm++) {
                        CLT_Layer layer = layers[mm];
                        StressStrainState sss_upper = layer.getStressState(eVec, 0.0, 0.0, CLT_Layer.POSITION_UPPER, false)[0];
                        ReserveFactor rf_upper = layer.getLayer().getCriterion().getReserveFactor(layer.getLayer().getMaterial(), layer.getLayer(), sss_upper);
                        StressStrainState sss_lower = layer.getStressState(eVec, 0.0, 0.0, CLT_Layer.POSITION_LOWER, false)[0];
                        ReserveFactor rf_lower = layer.getLayer().getCriterion().getReserveFactor(layer.getLayer().getMaterial(), layer.getLayer(), sss_lower);
                        if (rf_lower.getMinimalReserveFactor() < rf_upper.getMinimalReserveFactor()){
                            rfMinLayer[mm] = rf_lower.getMinimalReserveFactor();
                            failTypLay[mm] = rf_lower.getFailureType();
                        }else{
                            rfMinLayer[mm] = rf_upper.getMinimalReserveFactor();
                            failTypLay[mm] = rf_upper.getFailureType();
                        }
                        if(rfMinLayer[mm] < RF){
                            RF = rfMinLayer[mm];
                            layerNum = mm;
                        }
                    }

                    if(rfMax < RF){
                        rfMax = RF;
                        lastLayerNum = layerNum;
                        for (mm = 0; mm < 3; mm++)
                            VecNMax[mm] = RF*nVec[mm];
                    }

                    if (failureType_ == FIRST_PLY_FAILURE) break;

                    /*
                      Diese Schleife ist nur notwendig, da auch mehrere Lagen
                      auf einmal versagen könnten. Hier werden alle Lagen raus-
                      gesucht, die den kleinsten Reservefaktor haben.
                    */
                    for (int iii = 0; iii < numLayers; iii++) {
                        if (RF * (1+EPS) > rfMinLayer[iii]){
                            if (failTypLay[iii] == ReserveFactor.FIBER_FAILURE){
                                firstFB = true;
                                break;
                            }else if (failTypLay[iii] == ReserveFactor.MATRIX_FAILURE){
                                ZFB[iii] = true;
                            }
                        }
                    }
                    
                    if (firstFB){
                        break;
                    }

                    // Generieren der inversen A-Matrix
                    ABDmatInv = getABDMatInv(layers, ZFB);
                    
                } while(ABD_ok); // solange, wie die ABD-Matrix keine Fastnullelemente auf der Hauptdiagonalen hat
                
                // Wenn FinalFailure, dann alles rücksetzen, was erneut gebraucht wird
                if (failureType_ == PlyFailureCriterion.FINAL_FAILURE){
                    for (mm = 0; mm < numLayers; mm++){
                        ZFB[mm] = false;
                        layers[mm].getLayer().setMaterial(getAsDefaultMaterial(origLayers[mm].getLayer().getMaterial()));
                    }
                    ABDmatInv = getABDMatInv(layers, ZFB);
                }

                // Punkt für die Darstellung abspeichern
                for(int kk = 0; kk < 3; kk++){
                    punkte[ii][jj][kk] = VecNMax[kk];
                    if (Math.abs(VecNMax[kk]) > maxVal){
                        maxVal = Math.abs(VecNMax[kk]);
                    }
                }
                punkte[ii][jj][3] = lastLayerNum;
            }
        }
        
        scalingFaktor = maxVal;
        
        axisIntersections[0] = punkte[alphaSteps_][0][0];
        axisIntersections[1] = punkte[0][0][0];
        axisIntersections[2] = punkte[alphaSteps_/2][betaSteps_/4][1];
        axisIntersections[3] = punkte[alphaSteps_/2][3*betaSteps_/4][1];
        axisIntersections[4] = punkte[alphaSteps_/2][0][2];
        axisIntersections[5] = punkte[alphaSteps_/2][betaSteps_/2][2];
        
        return punkte;
    }

    private Vector3 getNormal(double[] p1, double[] p2, double[] p3){
        double[] xvec = new double[3];
        double[] yvec = new double[3];
        double[] zvec = new double[3];

        for (int i = 0; i < xvec.length; i++) xvec[i] = p2[i]-p1[i];
        for (int i = 0; i < yvec.length; i++) yvec[i] = p3[i]-p1[i];

        zvec[0] = xvec[1]*yvec[2] - xvec[2]*yvec[1];
        zvec[1] = xvec[2]*yvec[0] - xvec[0]*yvec[2];
        zvec[2] = xvec[0]*yvec[1] - xvec[1]*yvec[0];

        return new Vector3((float)(-zvec[0]),(float)(-zvec[1]),(float)(-zvec[2]));
    }

    private double[][] getABDMatInv(CLT_Layer[] layers, boolean[] ZFB){

        int ii, jj, m, n;
        double thick;
        double[][] Qmat;
        double[][] A = new double[3][3];
        double[][] B = new double[3][3];
        double[][] D = new double[3][3];
        double[][] ABD = new double[6][6];
        
        double temp, zm;
        CLT_Layer layer;
        for (ii = 0; ii < layers.length; ii++){
            layer = layers[ii];
            if (ZFB[ii]){
                DefaultMaterial mat = (DefaultMaterial)layer.getLayer().getMaterial();
                mat.setEnor(0.0);
                mat.setNue12(0.0);
                mat.setG(0.0);
            }
            layer.refresh();
            Qmat  = layer.getQMatGlobal();
            thick = layer.getLayer().getThickness();
            zm    = layer.getZm();
            for (m = 0; m < 3; m++){
                for (n = 0; n < 3; n++){
                    temp = Qmat[m][n] * thick;
                    A[m][n] += temp;
                    B[m][n] += temp * zm;
                    D[m][n] += temp * (thick*thick/12.0 + zm*zm);
                }
            }
        }
        
        for(ii = 0; ii < 3; ii++){
            for(jj = 0; jj <= ii; jj++){
                ABD[ii][jj] = A[ii][jj];
            }
        }

        for(ii = 0; ii < 3; ii++){
            System.arraycopy(B[ii], 0, ABD[ii+3], 0, 3);
        }

        for(ii = 0; ii < 3; ii++){
            for(jj = 0; jj <= ii; jj++){
                ABD[ii+3][jj+3] = D[ii][jj];
            }
        }
        
        for(ii = 0; ii < 6; ii++){
            for(jj = ii+1; jj < 6; jj++){
                ABD[ii][jj] = ABD[jj][ii];
            }
        }
        
        ABD_ok = true;
        for (ii = 0; ii < 6; ii++){
            if (Math.abs(ABD[ii][ii]) < EPS_ZERO ){
                ABD_ok = false;
                break;
            }
        }

        return MatrixTools.getInverse(ABD);
    }
    
    private DefaultMaterial getAsDefaultMaterial(LayerMaterial material){
        DefaultMaterial mat = new DefaultMaterial("1", "", 
                material.getEpar(),
                material.getEnor(), 
                material.getNue12(),
                material.getG(),
                0.0,
                false);
        
        mat.setRNorCom(material.getRNorCom());
        mat.setRNorTen(material.getRNorTen());
        mat.setRParCom(material.getRParCom());
        mat.setRParTen(material.getRParTen());
        mat.setRShear(material.getRShear());
        for(String key : mat.getAdditionalValueKeySet()){
            mat.putAdditionalValue(key, mat.getAdditionalValue(key));
        }
        return mat;
    }
    
    public class FailureSurfaceResult{
        
        final Mesh failureBody;
        final double scaleFactor;
        final double[] axisIntersections;

        public FailureSurfaceResult(Mesh failureBody, double scaleFactor, double[] axisIntersections) {
            this.failureBody = failureBody;
            this.scaleFactor = scaleFactor;
            this.axisIntersections = axisIntersections;
        }

        public Mesh getFailureBody() {
            return failureBody;
        }

        public double getScaleFactor() {
            return scaleFactor;
        }

        public double[] getAxisIntersections() {
            return axisIntersections;
        }
        
    }
}