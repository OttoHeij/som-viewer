package org.tum.opensim.somview;

import java.awt.BasicStroke;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.opensim.utils.TheApp;

/**
* Viewer for SOMs that combines umatrix display with control elements
**/

public class SOMView extends javax.swing.JPanel 
                    implements TableModelListener, ProgressListener {

    private static SOMMap associated_map;
    private static TrajectoryTableModel tableModel;
    
    /** Creates new form SOMView */
    public SOMView() {
        initComponents();        
        // init values
        zoomSlider.setValue(50);
        qualitySlider.setValue(10);
        scaleHitsSlider.setValue(3);
        
        
        //Include the control panel for the contour lines
        contourPanel.setUMatrix(uMatrix);
        //Listen to the progress events of the umatrix
        uMatrix.addProgressListener(this);
        //Initialize associated_map
        associated_map = null;
    }

    
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        somFileChooserDialog = new javax.swing.JFileChooser();
        trajectoryFileChooserDialog = new javax.swing.JFileChooser();
        splitPane = new javax.swing.JSplitPane();
        controlsPanel = new javax.swing.JPanel();
        menuPane = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        loadSOMButton = new javax.swing.JButton();
        displayDotsCheckbox = new javax.swing.JCheckBox();
        renderToImageButton = new javax.swing.JButton();
        greyscaleCheckBox = new javax.swing.JCheckBox();
        interpolateCheckbox = new javax.swing.JCheckBox();
        trajectoryPanel = new javax.swing.JPanel();
        loadTrajectoriesButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        trajectoryTable = new javax.swing.JTable(tableModel);
        scaleHitsPanel = new javax.swing.JPanel();
        scaleHitsLabel = new javax.swing.JLabel();
        scaleHitsSlider = new javax.swing.JSlider();
        contourPanel = new org.tum.opensim.somview.ContourLineControls();
        bottomControlsPanel = new javax.swing.JPanel();
        zoomLabel = new javax.swing.JLabel();
        zoomSlider = new javax.swing.JSlider();
        qualitySlider = new javax.swing.JSlider();
        qualityLabel = new javax.swing.JLabel();
        statusPanel = new javax.swing.JPanel();
        statusText = new javax.swing.JLabel();
        uMatrixScrollPanel = new javax.swing.JScrollPane();
        uMatrix = new org.tum.opensim.somview.UMatrix();

        somFileChooserDialog.setAcceptAllFileFilterUsed(false);
        somFileChooserDialog.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".cod") || f.isDirectory();
            }
            public String getDescription() {
                return "Self-organizing maps(*.cod)";
            }
        });

        trajectoryFileChooserDialog.setDialogTitle(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryFileChooserDialog.dialogTitle")); // NOI18N
        trajectoryFileChooserDialog.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toLowerCase().endsWith(".data") || f.isDirectory();
            }
            public String getDescription() {
                return "trajectory file(*.data)";
            }
        });
        trajectoryFileChooserDialog.setToolTipText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryFileChooserDialog.toolTipText")); // NOI18N

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS));

        controlsPanel.setMaximumSize(new java.awt.Dimension(8000, 42767));
        controlsPanel.setLayout(new javax.swing.BoxLayout(controlsPanel, javax.swing.BoxLayout.Y_AXIS));

        menuPane.setAutoscrolls(true);
        menuPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        menuPane.setPreferredSize(new java.awt.Dimension(250, 527));
        menuPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                menuPaneStateChanged(evt);
            }
        });

        mainPanel.setPreferredSize(new java.awt.Dimension(300, 498));

        loadSOMButton.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.loadSOMButton.text")); // NOI18N
        loadSOMButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadSOMButtonActionPerformed(evt);
            }
        });

        displayDotsCheckbox.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.displayDotsCheckbox.text")); // NOI18N
        displayDotsCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                displayDotsCheckboxItemStateChanged(evt);
            }
        });

        renderToImageButton.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.renderToImageButton.text")); // NOI18N
        renderToImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renderToImageButtonActionPerformed(evt);
            }
        });

        greyscaleCheckBox.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.greyscaleCheckBox.text")); // NOI18N
        greyscaleCheckBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                greyscaleCheckBoxItemStateChanged(evt);
            }
        });
        greyscaleCheckBox.setSelected(true);

        interpolateCheckbox.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.interpolateCheckbox.text")); // NOI18N
        interpolateCheckbox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                interpolateCheckboxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(displayDotsCheckbox)
                    .addComponent(greyscaleCheckBox)
                    .addComponent(interpolateCheckbox)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(loadSOMButton)
                        .addGap(18, 18, 18)
                        .addComponent(renderToImageButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadSOMButton)
                    .addComponent(renderToImageButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(displayDotsCheckbox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(greyscaleCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(interpolateCheckbox)
                .addContainerGap(387, Short.MAX_VALUE))
        );

        menuPane.addTab(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.mainPanel.TabConstraints.tabTitle"), mainPanel); // NOI18N

        trajectoryPanel.setPreferredSize(new java.awt.Dimension(300, 450));
        trajectoryPanel.setLayout(new javax.swing.BoxLayout(trajectoryPanel, javax.swing.BoxLayout.Y_AXIS));

        loadTrajectoriesButton.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.loadTrajectoriesButton.text")); // NOI18N
        loadTrajectoriesButton.setAlignmentX(0.5F);
        loadTrajectoriesButton.setMaximumSize(new java.awt.Dimension(5000, 23));
        loadTrajectoriesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadTrajectoriesButtonActionPerformed(evt);
            }
        });
        trajectoryPanel.add(loadTrajectoriesButton);

        trajectoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "display", "label", "sync", "color", "linestyle", "width", "offset"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.String.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        trajectoryTable.getTableHeader().setReorderingAllowed(false);
        scrollPane.setViewportView(trajectoryTable);
        trajectoryTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        trajectoryTable.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title0")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        trajectoryTable.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title1")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(2).setPreferredWidth(10);
        trajectoryTable.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title2")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(3).setPreferredWidth(10);
        trajectoryTable.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title3")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(3).setCellEditor(getColorCellEditor());
        trajectoryTable.getColumnModel().getColumn(3).setCellRenderer(getColorCellRenderer());
        trajectoryTable.getColumnModel().getColumn(4).setPreferredWidth(10);
        trajectoryTable.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title4")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(4).setCellEditor(getStrokeCellEditor());
        trajectoryTable.getColumnModel().getColumn(4).setCellRenderer(getStrokeCellRenderer());
        trajectoryTable.getColumnModel().getColumn(5).setPreferredWidth(10);
        trajectoryTable.getColumnModel().getColumn(5).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title5")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(6).setPreferredWidth(10);
        trajectoryTable.getColumnModel().getColumn(6).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title6")); // NOI18N

        trajectoryPanel.add(scrollPane);

        scaleHitsPanel.setLayout(new javax.swing.BoxLayout(scaleHitsPanel, javax.swing.BoxLayout.X_AXIS));

        scaleHitsLabel.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.scaleHitsLabel.text")); // NOI18N
        scaleHitsPanel.add(scaleHitsLabel);

        scaleHitsSlider.setMajorTickSpacing(5);
        scaleHitsSlider.setMaximum(40);
        scaleHitsSlider.setPaintTicks(true);
        scaleHitsSlider.setValue(5);
        scaleHitsSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scaleHitsSliderStateChanged(evt);
            }
        });
        scaleHitsPanel.add(scaleHitsSlider);

        trajectoryPanel.add(scaleHitsPanel);

        menuPane.addTab(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryPanel.TabConstraints.tabTitle"), trajectoryPanel); // NOI18N

        contourPanel.setPreferredSize(new java.awt.Dimension(300, 484));
        menuPane.addTab(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.contourPanel.TabConstraints.tabTitle"), contourPanel); // NOI18N

        controlsPanel.add(menuPane);

        bottomControlsPanel.setMaximumSize(new java.awt.Dimension(600, 500));
        bottomControlsPanel.setMinimumSize(new java.awt.Dimension(80, 31));
        bottomControlsPanel.setPreferredSize(new java.awt.Dimension(250, 91));
        java.awt.GridBagLayout bottomControlsPanelLayout = new java.awt.GridBagLayout();
        bottomControlsPanelLayout.columnWidths = new int[] {60, 100};
        bottomControlsPanel.setLayout(bottomControlsPanelLayout);

        zoomLabel.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.zoomLabel.text")); // NOI18N
        zoomLabel.setMaximumSize(new java.awt.Dimension(40, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomControlsPanel.add(zoomLabel, gridBagConstraints);

        zoomSlider.setMajorTickSpacing(50);
        zoomSlider.setMaximum(300);
        zoomSlider.setMinimum(10);
        zoomSlider.setMinorTickSpacing(10);
        zoomSlider.setPaintTicks(true);
        zoomSlider.setSnapToTicks(true);
        zoomSlider.setToolTipText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.zoomSlider.toolTipText")); // NOI18N
        zoomSlider.setValue(80);
        zoomSlider.setMaximumSize(new java.awt.Dimension(500, 31));
        zoomSlider.setMinimumSize(new java.awt.Dimension(200, 31));
        zoomSlider.setPreferredSize(new java.awt.Dimension(300, 31));
        zoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                zoomSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomControlsPanel.add(zoomSlider, gridBagConstraints);

        qualitySlider.setMajorTickSpacing(10);
        qualitySlider.setMaximum(35);
        qualitySlider.setMinimum(5);
        qualitySlider.setMinorTickSpacing(2);
        qualitySlider.setPaintTicks(true);
        qualitySlider.setSnapToTicks(true);
        qualitySlider.setValue(6);
        qualitySlider.setMaximumSize(new java.awt.Dimension(400, 31));
        qualitySlider.setPreferredSize(new java.awt.Dimension(300, 31));
        qualitySlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                qualitySliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomControlsPanel.add(qualitySlider, gridBagConstraints);

        qualityLabel.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.qualityLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomControlsPanel.add(qualityLabel, gridBagConstraints);

        statusPanel.setMinimumSize(new java.awt.Dimension(100, 20));
        statusPanel.setPreferredSize(new java.awt.Dimension(282, 30));
        statusPanel.setLayout(new javax.swing.BoxLayout(statusPanel, javax.swing.BoxLayout.X_AXIS));

        statusText.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        statusText.setText(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.statusText.text")); // NOI18N
        statusText.setAlignmentX(0.5F);
        statusText.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        statusText.setMaximumSize(new java.awt.Dimension(500, 20));
        statusPanel.add(statusText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        bottomControlsPanel.add(statusPanel, gridBagConstraints);

        controlsPanel.add(bottomControlsPanel);

        splitPane.setLeftComponent(controlsPanel);

        uMatrixScrollPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        uMatrixScrollPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        uMatrixScrollPanel.setPreferredSize(new java.awt.Dimension(400, 29));

        uMatrix.setMaximumSize(new java.awt.Dimension(10000000, 1000000));
        uMatrix.setPreferredSize(new java.awt.Dimension(400, 10));

        javax.swing.GroupLayout uMatrixLayout = new javax.swing.GroupLayout(uMatrix);
        uMatrix.setLayout(uMatrixLayout);
        uMatrixLayout.setHorizontalGroup(
            uMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 415, Short.MAX_VALUE)
        );
        uMatrixLayout.setVerticalGroup(
            uMatrixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 597, Short.MAX_VALUE)
        );

        uMatrixScrollPanel.setViewportView(uMatrix);

        splitPane.setRightComponent(uMatrixScrollPanel);

        add(splitPane);
    }// </editor-fold>//GEN-END:initComponents

    public void loadTrajectories(LinkedList<SOMTrajectory> trajectories)
    {
        //If trajectories were successfully loaded => register them in the umatrix
        uMatrix.setTrajectories(trajectories);          
        
        tableModel = new TrajectoryTableModel(trajectories);
        tableModel.addTableModelListener(uMatrix);
        tableModel.addTableModelListener(this);
        trajectoryTable.setModel(tableModel);

        //Just renew the table model, don't get confused with the long lines
        trajectoryTable.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title0")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title1")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(2).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title2")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(3).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title3")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(3).setCellEditor(getColorCellEditor());
        trajectoryTable.getColumnModel().getColumn(3).setCellRenderer(getColorCellRenderer());
        trajectoryTable.getColumnModel().getColumn(4).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title4")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(4).setCellEditor(getStrokeCellEditor());
        trajectoryTable.getColumnModel().getColumn(4).setCellRenderer(getStrokeCellRenderer());
        trajectoryTable.getColumnModel().getColumn(5).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title5")); // NOI18N
        trajectoryTable.getColumnModel().getColumn(6).setHeaderValue(org.openide.util.NbBundle.getMessage(SOMView.class, "SOMView.trajectoryTable.columnModel.title6")); // NOI18N

        //Redraw the table
        trajectoryTable.invalidate();
    }
    
    /**
    * Is called when the trajectory load button is clicked
    **/
    private void loadTrajectoriesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadTrajectoriesButtonActionPerformed
        //Only load trajectories if a SOM has already been loaded
        if(associated_map == null){
            JOptionPane.showMessageDialog(null, "Please load a SOM before you load its trajectories");
            return;
        }
        
        //Set opendialog to last visited directory
        String exportPath = Preferences.userNodeForPackage(TheApp.class)
                                .get("SOMViewTrajectories-LastPath", 
                                    System.getProperty("user.home"));
        trajectoryFileChooserDialog.setCurrentDirectory(new File(exportPath));
        //Initialize trajectory list
        LinkedList<SOMTrajectory> trajectories = null;
        //Open a file open dialog
        int returnVal = trajectoryFileChooserDialog.showOpenDialog(this);   
        //If user has made some file selection...
        if (returnVal == JFileChooser.APPROVE_OPTION) {         
            //Get the file and to load the trajectories
            File file = trajectoryFileChooserDialog.getSelectedFile(); 
            try { 
                //Since the reading process may take some time - notify the user
                progressUpdate(new ProgressEvent(this, 
                        "Reading trajectories from file - this may take some time", true));
                trajectories = SOMTrajectory.readFile(file, associated_map.getDim(), associated_map);
                progressUpdate(new ProgressEvent(this, 
                        "Reading trajectories from file finished", false));
            } catch (FileNotFoundException ex) {                 
                Logger.getLogger(SOMView.class.getName()).log(Level.SEVERE, null, ex);             
            } catch (IOException ex) {                 
                Logger.getLogger(SOMView.class.getName()).log(Level.SEVERE, null, ex);             
            }              
            //Finally: update the table where the trajectories and some options
            //for each of them are displayed
            loadTrajectories(trajectories);
            //Remember the current trajectorypath
            Preferences.userNodeForPackage(TheApp.class)
                .put("SOMViewTrajectories-LastPath", file.getParent());
        }
        
        
        
    }//GEN-LAST:event_loadTrajectoriesButtonActionPerformed


    /**
    * Is called when the "display dots" checkbox was clicked
    **/
    private void displayDotsCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_displayDotsCheckboxItemStateChanged
        //Simply set the respective option in the umatrix display class
        // and repaint the umatrix
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            uMatrix.setDisplayDots(true);
        } else {
            uMatrix.setDisplayDots(false);
        } 
        uMatrix.invalidate();
        uMatrix.repaint();     
        
	}//GEN-LAST:event_displayDotsCheckboxItemStateChanged

    /**
    * Open file  chooser when the "load SOM" button is pressed.
    **/
    private void loadSOMButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadSOMButtonActionPerformed
        //Set file dialog to previously used directory
        String exportPath = Preferences.userNodeForPackage(TheApp.class)
                                .get("SOMViewUmatrix-LastPath", 
                                    System.getProperty("user.home"));
        somFileChooserDialog.setCurrentDirectory(new File(exportPath));
        //Shows a file open dialog
        int returnVal = somFileChooserDialog.showOpenDialog(this);  
        //If the user selects a file..
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {  
            //.. get a file object and try to load a SOM class from it
            // the file has to be a .cod file as specified by the SOM Toolbox documentation
            // see javadoc of SOMMap.readFile for details
            File file = somFileChooserDialog.getSelectedFile();             
            try {
                progressUpdate(new ProgressEvent(this, 
                        "Reading SOM from file - this may take some time", true));
                associated_map = SOMMap.readFile(file); 
                progressUpdate(new ProgressEvent(this, 
                        "Reading SOM from file finished", false));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SOMView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SOMView.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Set the map we just loaded as SOM in the umatrix display class
            uMatrix.setMap(associated_map);
            this.zoomSlider.setValue(this.zoomSlider.getValue());
            uMatrix.invalidate(); 
            uMatrix.repaint();
            //Remember the current umatrix path
            Preferences.userNodeForPackage(TheApp.class)
                .put("SOMViewUmatrix-LastPath", file.getParent());
            //Reset the trajectories
            LinkedList<SOMTrajectory> trajectories = null;
            //Finally: update the table where the trajectories and some options
            //for each of them are displayed
            loadTrajectories(trajectories);
        }   
        
	}//GEN-LAST:event_loadSOMButtonActionPerformed

	/**
	* Is call when the user chooses to render the umatrix to an image file
	**/
    private void renderToImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renderToImageButtonActionPerformed
        //Get a "save" file dialog
        JFileChooser saveTo = new JFileChooser();
        saveTo.setDialogTitle("Save image of uMatrix to:");
        saveTo.setDialogType(JFileChooser.SAVE_DIALOG);
        //PNGs only!
        saveTo.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics", "png"));
        if (saveTo.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            //If the user has selected a file ..
            File selection = saveTo.getSelectedFile();
            String result = selection.getName();
            //.. change the filename such that it has .png as extension
            String[] parts = result.split("\\.");
            if (parts.length == 1) {
                result = result + ".png";
            } else if (!parts[parts.length - 1].equals("png")) {
                //Extension is not .png
                result = result + ".png";
            }
            String path = selection.getParent() + "\\" + result;
            //Render uMatrix to Image
            selection = new File(path);
            //Call the render method of the umatrix and show status message..
            progressUpdate(new ProgressEvent(this, 
                        "Rendering the image - this may take a bit", true));
            BufferedImage image = uMatrix.renderUMatrixImage();
            //.. and save the resulting image to file in case it is not null
            if (image != null) {
                try {
                    ImageIO.write(image, "png", selection);
                } catch (IOException ex) {
                    Logger.getLogger(SOMView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            progressUpdate(new ProgressEvent(this, 
                    "Image rendering finished", false));
        }

    }//GEN-LAST:event_renderToImageButtonActionPerformed

    /**
    * Is called when user chooses to display the umatrix in grayscale mode
    **/
    private void greyscaleCheckBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_greyscaleCheckBoxItemStateChanged
        //Simply set the respective option in the umatrix display class
        //and trigger a repaint
        if(evt.getStateChange() == ItemEvent.SELECTED)        {
            uMatrix.setColorMode(UMatrix.GREYSCALE_COLOR_MODEL);
        }
        else{
            uMatrix.setColorMode(UMatrix.COLORED_COLOR_MODEL);
        }
        uMatrix.repaint();
        uMatrix.invalidate();
    }//GEN-LAST:event_greyscaleCheckBoxItemStateChanged

    /**
    * Is called when the zoom slider state is changed
    **/
    private void zoomSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_zoomSliderStateChanged
        //Change the scale in the umatrix class
        uMatrix.setZoomScale((double)(((JSlider)evt.getSource()).getValue())/10.0);
        uMatrix.repaint();
        uMatrix.revalidate();
    }//GEN-LAST:event_zoomSliderStateChanged

    /**
    * Quality slider changed
    **/
    private void qualitySliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_qualitySliderStateChanged
        //Change the hexagon size in the umatrix display class to a different number of pixels
        //in order to adjust the quality
        uMatrix.setQuality(((JSlider)evt.getSource()).getValue());
        uMatrix.repaint();
        uMatrix.revalidate();
    }//GEN-LAST:event_qualitySliderStateChanged

    /**
    * Is called when the user chooses to set the node hexagon colors to a value the is interpolated
    * between the colors of its surrounding distance hexagons
    **/
    private void interpolateCheckboxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_interpolateCheckboxItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED)
        {
            uMatrix.setInterpolateNodeDistances(true);
        }
        else
        {
            uMatrix.setInterpolateNodeDistances(false);
        }
        uMatrix.createHexagonMap();
        uMatrix.refreshUMatrixImageBuffer();
        uMatrix.repaint();
    }//GEN-LAST:event_interpolateCheckboxItemStateChanged

    private void menuPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_menuPaneStateChanged
        //If the contour lines and the interpolation that is necessary
        //for them is activated, disable the "interpolation" checkbox,
        //as this is implicitly used by the interpolation for the contourlines
        //and must not be deactivated as long as the contourline view is active
        this.interpolateCheckbox.setEnabled(!uMatrix.isContourlinesActive());
        this.greyscaleCheckBox.setEnabled(!uMatrix.isContourlinesActive());
    }//GEN-LAST:event_menuPaneStateChanged

    private void scaleHitsSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scaleHitsSliderStateChanged
        uMatrix.setHitCountScale(((JSlider)evt.getSource()).getValue());
        uMatrix.repaint();
        uMatrix.revalidate();
    }//GEN-LAST:event_scaleHitsSliderStateChanged

//#####
//## Just some getters for table cell rederer and editor objects
    
    public TableCellRenderer getColorCellRenderer() {
        return new ColorRenderer();
    }

    public TableCellEditor getColorCellEditor() {
        return new ColorChooserEditor();
    }

    public TableCellRenderer getStrokeCellRenderer() {
        return new StrokeTableCellRenderer();
    }

    public TableCellEditor getStrokeCellEditor() {
        JComboBox box = new JComboBox();
        box.setRenderer(new ComboStrokeRenderer());
        for(BasicStroke s : StyleLine.getAllLineTypes())
        {
            box.addItem(s);
        }
        return new DefaultCellEditor(box);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomControlsPanel;
    private org.tum.opensim.somview.ContourLineControls contourPanel;
    private javax.swing.JPanel controlsPanel;
    private javax.swing.JCheckBox displayDotsCheckbox;
    private javax.swing.JCheckBox greyscaleCheckBox;
    private javax.swing.JCheckBox interpolateCheckbox;
    private javax.swing.JButton loadSOMButton;
    private javax.swing.JButton loadTrajectoriesButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane menuPane;
    private javax.swing.JLabel qualityLabel;
    private javax.swing.JSlider qualitySlider;
    private javax.swing.JButton renderToImageButton;
    private javax.swing.JLabel scaleHitsLabel;
    private javax.swing.JPanel scaleHitsPanel;
    private javax.swing.JSlider scaleHitsSlider;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JFileChooser somFileChooserDialog;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel statusText;
    private javax.swing.JFileChooser trajectoryFileChooserDialog;
    private javax.swing.JPanel trajectoryPanel;
    private javax.swing.JTable trajectoryTable;
    private org.tum.opensim.somview.UMatrix uMatrix;
    private javax.swing.JScrollPane uMatrixScrollPanel;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JSlider zoomSlider;
    // End of variables declaration//GEN-END:variables

    
    @Override
    public void tableChanged(TableModelEvent e) {
        // This event is not handled. The method has to be overridden, however,
        // in order to implement the TableModelListener.
    }

    @Override
    public void progressUpdate(ProgressEvent e) {
        if(e.isShow())
        {
            this.statusText.setText(e.getStatusMessage());
            this.statusText.paintImmediately(statusText.getVisibleRect());
            this.statusText.invalidate();
            this.statusText.repaint();
        }else{
            this.statusText.setText("idle");
        }
    }
}
