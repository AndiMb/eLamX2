/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elamx.fileview.nodes;

import de.elamx.fileview.nodefactories.LaminatesNodeFactory;
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
public class LaminatesNode extends AbstractNode {

    public LaminatesNode() {
        super(Children.create(new LaminatesNodeFactory(), true));
        this.setName(NbBundle.getMessage(LaminatesNode.class,"LaminatesNode.name"));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("de/elamx/fileview/resources/laminates.png");
    }

    @Override
    public Image getOpenedIcon(int i) {
        return getIcon(i);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> laminateActions = Utilities.actionsForPath("eLamXActions/Laminates");
        return laminateActions.toArray(new Action[laminateActions.size()]);
    }
}
