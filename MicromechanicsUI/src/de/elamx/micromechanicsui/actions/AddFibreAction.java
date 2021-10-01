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
package de.elamx.micromechanicsui.actions;

import de.elamx.laminate.eLamXLookup;
import de.elamx.micromechanics.Fiber;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

@ActionID(category = "Fibres",
        id = "de.elamx.micromechanicsui.actions.AddFibreAction")
@ActionRegistration(iconBase = "de/elamx/micromechanicsui/resources/addfibre.png",
        displayName = "#CTL_AddFibreAction")
@ActionReferences({
    @ActionReference(path = "Menu/Materials", position = 25),
    @ActionReference(path = "Toolbars/Materials", position = 25),
    @ActionReference(path = "eLamXActions/Fibres", position = 10)
})
public final class AddFibreAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        /*Fiber[] fibres = new Fiber[7];
         fibres[0] = new Fiber(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
         fibres[0].setEpar(225000.0);
         fibres[0].setEnor(15000.0);
         fibres[0].setNue12(0.2);
         fibres[0].setG(15000.0);
         fibres[0].setName("C-'AS4'");
         fibres[0].setAlphaTPar(-0.5);
         fibres[0].setAlphaTNor(15.0);
         fibres[0].setBetaPar(0.0);
         fibres[0].setBetaNor(0.0);
         fibres[0].setRho(1.488E-9);
         fibres[1] = new Fiber(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
         fibres[1].setEpar(230000.0);
         fibres[1].setEnor(15000.0);
         fibres[1].setNue12(0.2);
         fibres[1].setG(15000.0);
         fibres[1].setName("C-'T300'");
         fibres[1].setAlphaTPar(-0.7);
         fibres[1].setAlphaTNor(12.0);
         fibres[1].setBetaPar(0.0);
         fibres[1].setBetaNor(0.0);
         fibres[1].setRho(1.0860000000000002E-9);
         fibres[2] = new Fiber(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
         fibres[2].setEpar(230000.0);
         fibres[2].setEnor(40000.0);
         fibres[2].setNue12(0.36);
         fibres[2].setG(24000.0);
         fibres[2].setName("C-'T300'");
         fibres[2].setAlphaTPar(-0.7);
         fibres[2].setAlphaTNor(10.0);
         fibres[2].setBetaPar(0.0);
         fibres[2].setBetaNor(0.0);
         fibres[2].setRho(1.3E-9);
         fibres[3] = new Fiber(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
         fibres[3].setEpar(72000.0);
         fibres[3].setEnor(72000.0);
         fibres[3].setNue12(0.3);
         fibres[3].setG(27700.0);
         fibres[3].setName("E-G-'-'");
         fibres[3].setAlphaTPar(5.4);
         fibres[3].setAlphaTNor(5.4);
         fibres[3].setBetaPar(0.0);
         fibres[3].setBetaNor(0.0);
         fibres[3].setRho(4.8E-9);
         fibres[4] = new Fiber(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
         fibres[4].setEpar(72000.0);
         fibres[4].setEnor(72000.0);
         fibres[4].setNue12(0.3);
         fibres[4].setG(27700.0);
         fibres[4].setName("E-G-'-'");
         fibres[4].setAlphaTPar(5.4);
         fibres[4].setAlphaTNor(5.4);
         fibres[4].setBetaPar(0.0);
         fibres[4].setBetaNor(0.0);
         fibres[4].setRho(4.8E-9);
         fibres[5] = new Fiber(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
         fibres[5].setEpar(80000.0);
         fibres[5].setEnor(80000.0);
         fibres[5].setNue12(0.2);
         fibres[5].setG(33330.0);
         fibres[5].setName("E-G-'21xK43 Gevetex'");
         fibres[5].setAlphaTPar(4.9);
         fibres[5].setAlphaTNor(4.9);
         fibres[5].setBetaPar(0.0);
         fibres[5].setBetaNor(0.0);
         fibres[5].setRho(2.687E-9);
         fibres[6] = new Fiber(UUID.randomUUID().toString(), "New Material", 141000.0, 9340.0, 0.35, 4500.0, 1.7, false);
         fibres[6].setEpar(74000.0);
         fibres[6].setEnor(74000.0);
         fibres[6].setNue12(0.2);
         fibres[6].setG(30800.0);
         fibres[6].setName("E-G-'Silenka E-Glass 1200tex'");
         fibres[6].setAlphaTPar(4.9);
         fibres[6].setAlphaTNor(4.9);
         fibres[6].setBetaPar(0.0);
         fibres[6].setBetaNor(0.0);
         fibres[6].setRho(2.9049999999999998E-9);
         for (Fiber fiber : fibres) {
         eLamXLookup.getDefault().add(fiber);
         }*/

        Fiber fiber = new Fiber(UUID.randomUUID().toString(), NbBundle.getMessage(AddFibreAction.class, "AddFibreAction.NewFibre"), 230000.0, 15000.0, 0.23, 30000.0, 1.80E-9, true);
        fiber.setAlphaTPar(-0.5E-6);
        fiber.setAlphaTNor(1.0E-5);
        fiber.setBetaPar(0.0);
        fiber.setBetaNor(0.0);
        eLamXLookup.getDefault().add(fiber);

        (new PropertiesAction(fiber)).actionPerformed(e);
    }
}
