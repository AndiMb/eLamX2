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
package de.elamx.failureview3d;

import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.IndexMode;
import com.ardor3d.scenegraph.Mesh;
import com.ardor3d.scenegraph.MeshData;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.geom.BufferUtils;
import de.elamx.core.GlobalProperties;
import de.elamx.core.RawDataExportService;
import de.elamx.core.SnapshotService;
import de.view3d.View3D;
import de.view3d.View3D.AdditionalGeometryButton;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.AttributedString;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JToolBar;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;
import org.openide.windows.TopComponentGroup;
import org.openide.windows.WindowManager;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//de.elamx.failureview3d//FailureView3D//EN",
        autostore = false)
@TopComponent.Description(preferredID = "FailureView3DTopComponent",
        iconBase = "de/elamx/failureview3d/resources/puck.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "de.elamx.failureview3d.FailureView3DTopComponent")
@ActionReference(path = "Menu/Window", position = 332)
@TopComponent.OpenActionRegistration(displayName = "#CTL_FailureView3DAction",
        preferredID = "FailureView3DTopComponent")
public final class FailureView3DTopComponent extends TopComponent {

    private AttributedString captionX = new AttributedString("\u03C3\u2225");
    private AttributedString captionY = new AttributedString("\u03C3\u22A5");
    private AttributedString captionZ = new AttributedString("\u03C4 ");
    private boolean[] show;
    private JButton importVTKButton;
    private Spatial add3DObject = null;
    private AdditionalGeometryButton enableVTK;
    private String vtkFilename = "";
    private double vtkScale = 1.0;

    public FailureView3DTopComponent() {
        captionX.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);
        captionY.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB, 1, 2);
        initComponents();
        setName(NbBundle.getMessage(FailureView3DTopComponent.class, "CTL_FailureView3DTopComponent"));
        setToolTipText(NbBundle.getMessage(FailureView3DTopComponent.class, "HINT_FailureView3DTopComponent"));
        this.associateLookup(Lookups.fixed(new Snapshot(), new VTKExport()));
        if (view3D != null) {
            view3D.addAdditionalButtonBar(getToolBar(view3D));
        }
    }

    private JToolBar getToolBar(View3D view3D) {
        JToolBar bar = new JToolBar();

        importVTKButton = new JButton();
        importVTKButton.setMargin(new Insets(0, 0, 0, 0));
        importVTKButton.setIcon(ImageUtilities.loadImageIcon("de/elamx/failureview3d/resources/fileopen24.png", false));
        importVTKButton.setToolTipText(NbBundle.getMessage(FailureView3DTopComponent.class, "importVTKButton.tip"));
        importVTKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                VTKImportPanel form = new VTKImportPanel(vtkFilename, vtkScale);
                String msg = NbBundle.getMessage(FailureView3DTopComponent.class, "openVTKDialog.caption");
                DialogDescriptor dd = new DialogDescriptor(form, msg);
                Object result = DialogDisplayer.getDefault().notify(dd);
                if (result != NotifyDescriptor.OK_OPTION) {
                    return;
                }
                
                vtkFilename = form.getFilename();
                vtkScale = form.getScale();
                
                BufferedReader br = null;

                try {
                    br = new BufferedReader(new FileReader(new File(vtkFilename)));
                } catch (FileNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }

                if (br != null) {
                    try {
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        br.readLine();
                        String temp = br.readLine();
                        int nP = Integer.parseInt(temp.split(" ")[1]);
                        float[] points = new float[nP * 3];
                        float[] normals = new float[nP * 3];

                        int quads = nP / 4;
                        int index = 0;
                        int indNor = 0;

                        for (int q = 0; q < quads; q++) {
                            for (int ii = 0; ii < 4; ii++) {
                                temp = br.readLine();
                                String[] tempArr = temp.split(" ");
                                points[index++] = (float)(Float.parseFloat(tempArr[0])*vtkScale);
                                points[index++] = (float)(Float.parseFloat(tempArr[1])*vtkScale);
                                points[index++] = (float)(Float.parseFloat(tempArr[2])*vtkScale);
                            }
                            
                            float[] normal = getNormal(
                                    new double[]{points[index-12], points[index-11], points[index-10]}, // p1
                                    new double[]{points[index- 9], points[index- 8], points[index- 7]}, // p2
                                    new double[]{points[index- 3], points[index- 2], points[index- 1]}  // p4
                            );
                            
                            normals[indNor++] = normal[0];
                            normals[indNor++] = normal[1];
                            normals[indNor++] = normal[2];
                            
                            normal = getNormal(
                                    new double[]{points[index- 9], points[index- 8], points[index- 7]}, // p2
                                    new double[]{points[index- 6], points[index- 5], points[index- 4]}, // p3
                                    new double[]{points[index-12], points[index-11], points[index-10]}  // p1
                            );
                            
                            normals[indNor++] = normal[0];
                            normals[indNor++] = normal[1];
                            normals[indNor++] = normal[2];
                            
                            normal = getNormal(
                                    new double[]{points[index- 6], points[index- 5], points[index- 4]}, // p3
                                    new double[]{points[index- 3], points[index- 2], points[index- 1]}, // p4
                                    new double[]{points[index- 9], points[index- 8], points[index- 7]}  // p2
                            );
                            
                            normals[indNor++] = normal[0];
                            normals[indNor++] = normal[1];
                            normals[indNor++] = normal[2];
                            
                            normal = getNormal(
                                    new double[]{points[index- 3], points[index- 2], points[index- 1]}, // p4
                                    new double[]{points[index-12], points[index-11], points[index-10]}, // p1
                                    new double[]{points[index- 6], points[index- 5], points[index- 4]}  // p3
                            );
                            
                            normals[indNor++] = normal[0];
                            normals[indNor++] = normal[1];
                            normals[indNor++] = normal[2];

                        }

                        Mesh mesh = new Mesh();
                        MeshData meshData = mesh.getMeshData();

                        meshData.setVertexBuffer(BufferUtils.createFloatBuffer(points));
                        meshData.setNormalBuffer(BufferUtils.createFloatBuffer(normals));
                        meshData.setIndexMode(IndexMode.Quads);

                        mesh.updateModelBound();

                        add3DObject = mesh;
                        enableVTK.setSelected(true);
                        enableVTK.setGeo(add3DObject);

                        br.close();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

        });

        bar.add(importVTKButton);

        enableVTK = new AdditionalGeometryButton(view3D);
        enableVTK.setMargin(new Insets(0, 0, 0, 0));
        enableVTK.setIcon(ImageUtilities.loadImageIcon("de/elamx/failureview3d/resources/laminatversagenskoerper24.png", false));
        enableVTK.setToolTipText(NbBundle.getMessage(FailureView3DTopComponent.class, "showVTKButton.tip"));

        bar.add(enableVTK);

        return bar;
    }

    private float[] getNormal(double[] p1, double[] p2, double[] p3) {
        double[] xvec = new double[3];
        double[] yvec = new double[3];
        double[] zvec = new double[3];

        for (int i = 0; i < xvec.length; i++) {
            xvec[i] = p2[i] - p1[i];
        }
        for (int i = 0; i < yvec.length; i++) {
            yvec[i] = p3[i] - p1[i];
        }

        zvec[0] = xvec[1] * yvec[2] - xvec[2] * yvec[1];
        zvec[1] = xvec[2] * yvec[0] - xvec[0] * yvec[2];
        zvec[2] = xvec[0] * yvec[1] - xvec[1] * yvec[0];
        
        double abs = Math.sqrt(zvec[0]*zvec[0] + zvec[1]*zvec[1] + zvec[2]*zvec[2]);

        return new float[]{(float) (zvec[0]/abs), (float) (zvec[1]/abs), (float) (zvec[2]/abs)};
    }

    public boolean[] getShow() {
        return show;
    }

    public void setShow(boolean[] show) {
        this.show = show;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        view3D = new View3D(captionX, captionY, captionZ, true);

        setLayout(new java.awt.BorderLayout());
        add(view3D, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private de.view3d.View3D view3D;
    // End of variables declaration//GEN-END:variables

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    protected void componentShowing() {
        TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("FailureView3DGroup");
        if (group != null) {
            group.open();
        }
        super.componentShowing();
    }

    @Override
    protected void componentHidden() {
        TopComponentGroup group = WindowManager.getDefault().findTopComponentGroup("FailureView3DGroup");
        if (group != null) {
            group.close();
        }
        super.componentHidden();
    }

    @Override
    protected void componentDeactivated() {
        super.componentDeactivated(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        view3D.setActive(false);
    }

    @Override
    protected void componentActivated() {
        super.componentActivated(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
        view3D.setActive(true);
    }

    private List<Mesh> shapes;

    protected void setMesh(List<Mesh> shapes, double scale) {
        this.shapes = shapes;
        view3D.setShape3D(shapes, scale);
    }

    protected void addPoints(Collection<Vector3> points) {
        view3D.addPoints(points);
    }

    protected void setPoints(Collection<Vector3> points) {
        view3D.setPoints(points);
    }

    private class VTKExport implements RawDataExportService {

        @Override
        public void export(FileWriter fw) {
            try {
                view3D.exportQuadArrays(fw);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public String getFileExtension() {
            return "vtk";
        }
    }

    private class RawDataExport implements RawDataExportService {

        @Override
        public void export(FileWriter fw) {
            if (shapes == null || shapes.isEmpty()) {
                return;
            }
            try {
                String ls = System.getProperty("line.separator");
                DecimalFormat df_stress = GlobalProperties.getDefault().getFormat(GlobalProperties.FORMAT_STRESS);
                float[] coords = new float[3];
                for (Mesh s : shapes) {
                    MeshData data = s.getMeshData();
                    FloatBuffer buffer = data.getVertexBuffer();
                    fw.write("sigma_parallel" + "\t"
                            + "sigma_perpendicular" + "\t"
                            + "tau" + ls);
                    buffer.rewind();
                    for (int ii = 0; ii < data.getVertexCount(); ii++) {
                        buffer.get(coords, 0, 3);
                        fw.write(df_stress.format(coords[0]) + "\t"
                                + df_stress.format(coords[1]) + "\t"
                                + df_stress.format(coords[2]) + ls);
                    }
                    fw.write("----------------------------" + ls);
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public String getFileExtension() {
            return "txt";
        }
    }

    private class Snapshot implements SnapshotService {

        @Override
        public void saveSnapshot(File file) {
            view3D.saveScreenshot(file, "png");
        }
    }
}
