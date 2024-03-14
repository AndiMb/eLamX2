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
package de.elamx.elamx1import;

import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.Laminat;
import de.elamx.laminate.Layer;
import de.elamx.laminate.eLamXLookup;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.Puck;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

@ActionID(
        category = "File",
        id = "de.elamx.elamx1import.ImporteLamX1Action"
)
@ActionRegistration(
        displayName = "#CTL_ImporteLamX1Action"
)
@ActionReference(path = "Menu/File", position = 100)
public final class ImporteLamX1Action implements ActionListener {
    
    HashMap<String, Criterion> criterionMap = new HashMap<>();

    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = new File(System.getProperty("user.home"));
        File file = new FileChooserBuilder("import-dir").setTitle(NbBundle.getMessage(ImporteLamX1Action.class, "ImporteLamX1Action.Title")).setDefaultWorkingDirectory(basePath).setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".lam") || name.endsWith(".LAM") || (f.isDirectory() && !f.isHidden());
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(ImporteLamX1Action.class, "ImporteLamX1Action.Description");
            }
        }).setSelectionApprover(new FileChooserBuilder.SelectionApprover() {

            @Override
            public boolean approve(File[] selection) {
                if (selection.length > 1) {
                    return false;
                }
                return selection[0].getName().endsWith(".lam") | selection[0].getName().endsWith(".LAM");
            }
        }).setApproveText(NbBundle.getMessage(ImporteLamX1Action.class, "ImporteLamX1Action.ApproveText")).setFileHiding(true).showOpenDialog();

        if (file != null && file.exists()) {
            try {
                FileObject fo = FileUtil.toFileObject(file);
                Properties prop = new Properties();
                
                prop.load(fo.getInputStream());
                
                Laminat lam = new Laminat(UUID.randomUUID().toString(), NbBundle.getMessage(ImporteLamX1Action.class, "ImporteLamX1Action.NewLaminate", file.getName()));
                
                criterionMap.clear();
                Lookup critLookup = Lookups.forPath("elamx/failurecriteria");
                for (Criterion c: critLookup.lookupAll(Criterion.class)){
                    criterionMap.put(c.getClass().getName(), c);
                }
                
                int layerInd = 0;
                while (true){
                    
                    Layer layer = readLayer(prop, layerInd);
                    if (layer == null) break;

                    lam.addLayer(layer);

                    layerInd++;
                }

                int symmetry = Integer.parseInt(prop.getProperty("Symmetry"));
                
                switch(symmetry){
                    case 0:
                        lam.setSymmetric(false);
                        lam.setWithMiddleLayer(false);
                        break;
                    case 1:
                        lam.setSymmetric(true);
                        lam.setWithMiddleLayer(true);
                        break;
                    case 2:
                        lam.setSymmetric(true);
                        lam.setWithMiddleLayer(false);
                        break;
                    default:
                        lam.setSymmetric(false);
                        lam.setWithMiddleLayer(false);
                        break;
                }

                lam.setInvertZ(false);
                
                eLamXLookup.getDefault().add(lam);
                
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    private Layer readLayer(Properties prop, int layerInd){
        
        String keyPrefix = "Layer" + layerInd + ".";
        
        String name =prop.getProperty(keyPrefix+"name");
        if (name == null) return null;
        double Enor =Double.valueOf(prop.getProperty(keyPrefix+"Enor"));
        double Epar =Double.valueOf(prop.getProperty(keyPrefix+"Epar"));
        double nu12 =Double.valueOf(prop.getProperty(keyPrefix+"nu12"));
        double G =Double.valueOf(prop.getProperty(keyPrefix+"G"));
        double rho =Double.valueOf(prop.getProperty(keyPrefix+"rho"));
        double angle =Double.valueOf(prop.getProperty(keyPrefix+"angle"));
        double thick =Double.valueOf(prop.getProperty(keyPrefix+"thick"));
        double RParTen =Double.valueOf(prop.getProperty(keyPrefix+"RParTen"));
        double RParCom =Double.valueOf(prop.getProperty(keyPrefix+"RParCom"));
        double RNorTen =Double.valueOf(prop.getProperty(keyPrefix+"RNorTen"));
        double RNorCom =Double.valueOf(prop.getProperty(keyPrefix+"RNorCom"));
        double RShear =Double.valueOf(prop.getProperty(keyPrefix+"RShear"));
        double Pspd =Double.valueOf(prop.getProperty(keyPrefix+"Pspd"));
        double Pspz =Double.valueOf(prop.getProperty(keyPrefix+"Pspz"));
        double a0 =Double.valueOf(prop.getProperty(keyPrefix+"a0"));
        double lambdamin =Double.valueOf(prop.getProperty(keyPrefix+"lambdamin"));
        double F12s =Double.valueOf(prop.getProperty(keyPrefix+"F12s"));
        double m      = 3.1;
        double muesp  = 0.15;
        if (prop.containsKey(keyPrefix+"muesp")){
            m =Double.valueOf(prop.getProperty(keyPrefix+"m"));
            muesp =Double.valueOf(prop.getProperty(keyPrefix+"muesp"));
        }

        double apar =0.0;
        double anor =0.0;
        double bpar =0.0;
        double bnor =0.0;
        if (prop.containsKey(keyPrefix+"apar")){
            apar =Double.valueOf(prop.getProperty(keyPrefix+"apar"));
            anor =Double.valueOf(prop.getProperty(keyPrefix+"anor"));
            bpar =Double.valueOf(prop.getProperty(keyPrefix+"bpar"));
            bnor =Double.valueOf(prop.getProperty(keyPrefix+"bnor"));
        }
        
        String materialName = NbBundle.getMessage(ImporteLamX1Action.class, "ImporteLamX1Action.LayerMaterialName", layerInd+1);
        
        DefaultMaterial material = new DefaultMaterial(UUID.randomUUID().toString(), materialName, Epar, Enor, nu12, G, rho, true);
        material.setAlphaTPar(apar);
        material.setAlphaTNor(anor);
        material.setBetaPar(bpar);
        material.setBetaNor(bnor);
        material.setRParTen(RParTen);
        material.setRParCom(RParCom);
        material.setRNorTen(RNorTen);
        material.setRNorCom(RNorCom);
        material.setRShear(RShear);
        
        material.putAdditionalValue("de.elamx.laminate.failure.Puck.a0", a0);
        material.putAdditionalValue("de.elamx.laminate.failure.Puck.pspz", Pspz);
        material.putAdditionalValue("de.elamx.laminate.failure.Puck.lambda_min", lambdamin);
        material.putAdditionalValue("de.elamx.laminate.failure.Puck.pspd", Pspd);
        material.putAdditionalValue("de.elamx.laminate.addFailureCriteria.TsaiWu.f12star", F12s);
        material.putAdditionalValue("de.elamx.laminate.addFailureCriteria.FMC.m", m);
        material.putAdditionalValue("de.elamx.laminate.addFailureCriteria.FMC.muesp", muesp);
        
        
        String failureCriterion = prop.getProperty(keyPrefix+"failureCriterion");
        failureCriterion = failureCriterion.substring(failureCriterion.lastIndexOf('.')+1);
        System.out.println(failureCriterion);
        
        if (failureCriterion.equals("Puck")){
            failureCriterion = "de.elamx.laminate.failure.Puck";
        }else{
            failureCriterion = "de.elamx.laminate.addFailureCriteria." + failureCriterion;
        }
        
        Criterion criterion = null;
        try{
            criterion = criterionMap.get(failureCriterion);
        }catch (NullPointerException ex){
        }
        if (criterion == null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ImporteLamX1Action.class, "Warning.CriterionNotFound"), NotifyDescriptor.WARNING_MESSAGE));
            criterion = criterionMap.get(Puck.class.getName());
        }

        Layer layer = new Layer(UUID.randomUUID().toString(), name, material, angle, thick, criterion);
        
        
        return layer;
    }
}
