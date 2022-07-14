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
package de.elamx.filesupport;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.SwingUtilities;
import org.netbeans.spi.actions.AbstractSavable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.NotifyDescriptor.Confirmation;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.xml.XMLUtil;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@MIMEResolver.Registration(
    displayName="de.elamx.filesupport.Bundle#Services/MIMEResolver/eLamXFileResolver.xml",
    position=0,
    resource="eLamXFileResolver.xml"
)
public class eLamXFileDataObject extends MultiDataObject {
    
    private final Lookup lookup;
    private final InstanceContent lookupContents = new InstanceContent();

    private static final int acteLamXFileVersion = 1;

    public eLamXFileDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        lookup = new ProxyLookup(getCookieSet().getLookup(), new AbstractLookup(lookupContents));
        loadData();
        this.addVetoableChangeListener(new VetoableChangeListener() {

            @Override
            public void vetoableChange(PropertyChangeEvent e) throws PropertyVetoException {
                if (isModified()) {
                    Confirmation message = new NotifyDescriptor.Confirmation(NbBundle.getMessage(eLamXFileDataObject.class, "SaveFileMessage", eLamXFileDataObject.this.getPrimaryFile().getName()),
                            NotifyDescriptor.YES_NO_OPTION,
                            NotifyDescriptor.QUESTION_MESSAGE);

                    Object result = DialogDisplayer.getDefault().notify(message);
                    //When user clicks "Yes", indicating they really want to save,
                    //we need to disable the Save action,
                    //so that it will only be usable when the next change is made
                    //to the JTextField:
                    if (NotifyDescriptor.YES_OPTION.equals(result)) {
                        MySavable saveC = getLookup().lookup(MySavable.class);
                        try {
                            saveC.save();
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        });
    }

    @Override
    protected Node createNodeDelegate() {
        return new DataNode(this, Children.LEAF, getLookup());
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void setModified(final boolean isModified) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setModified(isModified);
                }
            });
        } else {
            // I tied the SaveCookie implementation into this such that
            // the Save action is enabled whenever the object is modified.
            if (isModified) {
                if (getLookup().lookup(MySavable.class) == null) {
                    lookupContents.add(new MySavable());
                }
            } else {
                MySavable savable = getLookup().lookup(MySavable.class);
                if (savable != null) {
                    lookupContents.remove(savable);
                    savable.myUnregister();
                }
            }
            super.setModified(isModified);
        }
    }

    private void loadData() {
        try {
            Document doc;
            //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
            try (InputStream is = getPrimaryFile().getInputStream()) {
                //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
                doc = XMLUtil.parse(new InputSource(is), false, false, null, null);
            }

            //optional, but recommended
            //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
            doc.getDocumentElement().normalize();

            //Find the car node:
            org.w3c.dom.Node nNode = doc.getElementsByTagName("elamx").item(0);
            String versionString = ((Element) nNode).getAttribute("version");
            int version = 1;
            if (!versionString.isEmpty()) {
                version = Integer.parseInt(versionString);
            }
            if (version > acteLamXFileVersion) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(eLamXFileDataObject.class, "Error.wrongFileVersion"), NotifyDescriptor.ERROR_MESSAGE));
            } else {
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eLamXElement = (Element) nNode;
                    for (LoadSaveHook lsh : Lookup.getDefault().lookupAll(LoadSaveHook.class)) {
                        lsh.load(eLamXElement);
                    }
                }
            }
        } catch (IOException | SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void storeData() {
        try {
            Document doc;
            //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
            try (InputStream is = getPrimaryFile().getInputStream()) {
                //Use the NetBeans org.openide.xml.XMLUtil class to create an org.w3c.dom.Document:
                doc = XMLUtil.parse(new InputSource(is), true, true, null, null);
                //Find the car node:
                org.w3c.dom.Node nNode = doc.getElementsByTagName("elamx").item(0);
                Attr attr = doc.createAttribute("version");
                attr.setValue("" + acteLamXFileVersion);
                ((Element) nNode).setAttributeNode(attr);
                if (nNode.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                    Element eLamXElement = (Element) nNode;
                    for (LoadSaveHook lsh : Lookup.getDefault().lookupAll(LoadSaveHook.class)) {
                        lsh.store(doc, eLamXElement);
                    }
                }
            }
            //Write the changed document to the underlying file:
            OutputStream fos = null;
            try {
                // fos = new FileOutputStream(FileUtil.toFile(getPrimaryFile().));
                fos = getPrimaryFile().getOutputStream();
                XMLUtil.write(doc, fos, "UTF-8"); // NOI18N
            } catch (IOException e) {
                Exceptions.printStackTrace(e);
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    Exceptions.printStackTrace(e);
                }
            }
        } catch (IOException | SAXException ex) {
            Exceptions.printStackTrace(ex);
        }
        setModified(false);
    }

    private class MySavable extends AbstractSavable {

        MySavable() {
            register();
        }

        @Override
        protected String findDisplayName() {
            return eLamXFileDataObject.this.getName();
        }

        @Override
        protected void handleSave() throws IOException {
            storeData();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof MySavable) {
                return obj == this;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return eLamXFileDataObject.this.hashCode();
        }

        public void myUnregister() {
            this.unregister();
        }
    }
}
