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
package de.elamx.welcome;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Andreas Hauffe
 */
public class TabButton extends javax.swing.JPanel {

    private boolean isSelected = false;
    private ActionListener actionListener;
    private final int tabIndex;
    private boolean isMouseOver = false;

    /**
     * Creates new form TabButton
     */
    public TabButton(String text, int tabIndex) {
        this.tabIndex = tabIndex;
        initComponents();
        init();
        setText(text);
    }

    public TabButton() {
        this("   ", 0);
    }

    public final void setText(String text) {
        textLabel.setText(text);
    }

    private void init() {
        textLabel.setFont( Constants.TAB_FONT );
        setFocusable(true);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
//                        setSelected( !isSelected );
                    if (null != actionListener) {
                        actionListener.actionPerformed(new ActionEvent(TabButton.this, 0, "clicked"));
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                    setSelected( !isSelected );
                if (null != actionListener) {
                    actionListener.actionPerformed(new ActionEvent(TabButton.this, 0, "clicked"));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    setCursor(Cursor.getDefaultCursor());
                }
                isMouseOver = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
                isMouseOver = false;
                repaint();
            }
        });

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                isMouseOver = true;
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                isMouseOver = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (isSelected) {
            setBackground(new java.awt.Color(255, 255, 255));
            textLabel.setForeground(new java.awt.Color(51, 51, 51));
        } else if (isMouseOver || isFocusOwner() || textLabel.isFocusOwner()) {
            setBackground(new java.awt.Color(204, 214, 223));
            textLabel.setForeground(new java.awt.Color(51, 51, 51));
        } else {
            setBackground(new java.awt.Color(164, 174, 184));
            textLabel.setForeground(new java.awt.Color(255, 255, 255));
        }
        super.paintComponent(g);
    }

    public void addActionListener(ActionListener l) {
        assert null == actionListener;
        this.actionListener = l;
    }

    public void setSelected(boolean sel) {
        this.isSelected = sel;

        setFocusable(!sel);
        repaint();
    }

    public int getTabIndex() {
        return tabIndex;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        textLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(164, 174, 184));
        setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, new java.awt.Color(136, 136, 136), new java.awt.Color(255, 255, 255), new java.awt.Color(136, 136, 136), new java.awt.Color(204, 204, 204)));
        setLayout(new java.awt.BorderLayout());

        textLabel.setForeground(new java.awt.Color(255, 255, 255));
        textLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        textLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10));
        add(textLabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel textLabel;
    // End of variables declaration//GEN-END:variables
}
