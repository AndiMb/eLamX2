/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview.nodes;

import de.elamx.fileview.nodefactories.MaterialsNodeFactory;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author Andreas Hauffe
 */
public class MaterialsNode extends AbstractNode {

    @SuppressWarnings("this-escape")
    public MaterialsNode() {
        super(Children.create(new MaterialsNodeFactory(), true));
        this.setName(NbBundle.getMessage(MaterialsNode.class,"MaterialNode.name"));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/elamx/fileview/resources/materials.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> laminateActions = Utilities.actionsForPath("eLamXActions/Materials");
        return laminateActions.toArray(new Action[laminateActions.size()]);
    }
}
