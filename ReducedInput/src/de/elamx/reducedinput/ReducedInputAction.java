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
package de.elamx.reducedinput;

import de.elamx.laminate.DataLayer;
import de.elamx.laminate.DefaultMaterial;
import de.elamx.laminate.failure.Criterion;
import de.elamx.laminate.failure.Puck;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.NbBundle;
import org.xml.sax.SAXException;

@ActionID(
        category = "File",
        id = "de.elamx.reducedinput.ReducedInputAction"
)
@ActionRegistration(
        displayName = "#CTL_ReducedInputAction"
)
@ActionReference(path = "Menu/File", position = 110)
public final class ReducedInputAction implements ActionListener {
    
    HashMap<String, Criterion> criterionMap = new HashMap<>();

    @Override
    public void actionPerformed(ActionEvent e) {
        File basePath = new File(System.getProperty("user.home"));
        File file = new FileChooserBuilder("import-dir").setTitle(NbBundle.getMessage(ReducedInputAction.class, "ReducedImportAction.Title")).setDefaultWorkingDirectory(basePath).setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName().toLowerCase();
                return name.endsWith(".elamxb") || name.endsWith(".ELAMXB") || (f.isDirectory() && !f.isHidden());
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(ReducedInputAction.class, "ReducedImportAction.Description");
            }
        }).setSelectionApprover(new FileChooserBuilder.SelectionApprover() {

            @Override
            public boolean approve(File[] selection) {
                if (selection.length > 1) {
                    return false;
                }
                return selection[0].getName().endsWith(".elamxb") | selection[0].getName().endsWith(".ELAMXB");
            }
        }).setApproveText(NbBundle.getMessage(ReducedInputAction.class, "ReducedImportAction.ApproveText")).setFileHiding(true).showOpenDialog();

        if (file != null && file.exists()) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            try {
                    SAXParser saxParser = factory.newSAXParser();
                    ReducedInputHandler handler = new ReducedInputHandler();
                    saxParser.parse(file, handler);
                } catch (ParserConfigurationException | SAXException | IOException ex) {
                    Logger.getLogger(ReducedInputAction.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
    }
    
    private DataLayer readLayer(Properties prop, int layerInd){
        
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
        
        String materialName = NbBundle.getMessage(ReducedInputAction.class, "ImporteLamX1Action.LayerMaterialName", layerInd+1);
        
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
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(ReducedInputAction.class, "Warning.CriterionNotFound"), NotifyDescriptor.WARNING_MESSAGE));
            criterion = criterionMap.get(Puck.class.getName());
        }

        DataLayer layer = new DataLayer(UUID.randomUUID().toString(), name, material, angle, thick, criterion);
        
        
        return layer;
    }
}
