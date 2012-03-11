package org.movsim.viewer.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.movsim.simulator.vehicles.longitudinalmodel.acceleration.CCS;
import org.movsim.simulator.vehicles.longitudinalmodel.acceleration.CCS.Waves;
import org.movsim.viewer.util.SwingHelper;

@SuppressWarnings("synthetic-access")
public class AppletMenu extends JPanel {
    private static final long serialVersionUID = -1741830983719200790L;
    private final Applet frame;
    final CanvasPanel canvasPanel;
    private final ResourceBundle resourceBundle;

    private LogWindow logWindow;
    private StatusPanel statusPanel;

    public AppletMenu(Applet mainFrame, CanvasPanel canvasPanel, StatusPanel statusPanel, ResourceBundle resourceBundle) {
        this.frame = mainFrame;
        this.canvasPanel = canvasPanel;
        this.statusPanel = statusPanel;
        this.resourceBundle = resourceBundle;
    }

    public void initMenus() {
        final JMenuBar menuBar = new JMenuBar();

        final JMenu scenarioMenu = scenarioMenu();
        final JMenu helpMenu = helpMenu();
        // final JMenu modelMenu = modelMenu();
        final JMenu viewMenu = viewMenu();
        // final JMenu outputMenu = outputMenu();

        menuBar.add(scenarioMenu);
        // menuBar.add(modelMenu);
        // menuBar.add(outputMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);
    }

    private JMenu scenarioMenu() {
        final JMenu scenarioMenu = new JMenu((String) resourceBundle.getObject("ScenarioMenu"));
        final JMenuItem menuItemVasaLoppet = new JMenuItem(new AbstractAction(resourceBundle.getString("Vasaloppet")) {

            private static final long serialVersionUID = 4633365854029111923L;

            @Override
            public void actionPerformed(ActionEvent e) {
                canvasPanel.simulator.loadScenarioFromXml("vasa_CCS", "/sim/examples/");
                canvasPanel.trafficCanvas.reset();
                canvasPanel.trafficCanvas.start();
                statusPanel.reset();
                uiDefaultReset();
                canvasPanel.trafficCanvas.setVmaxForColorSpectrum(22);
                canvasPanel.trafficCanvas.setSleepTime(0);
                canvasPanel.trafficCanvas.setxOffset(400);
                canvasPanel.trafficCanvas.setyOffset(700);
                canvasPanel.trafficCanvas.setDrawSources(false);
                CCS.setWave(Waves.NOWAVE);
            }
        });
        scenarioMenu.add(menuItemVasaLoppet);

        final JMenuItem menuItemVasaLoppetThreeWaves = new JMenuItem(new AbstractAction(
                resourceBundle.getString("VasaloppetThreeWaves")) {

            private static final long serialVersionUID = 4633365854029111923L;

            @Override
            public void actionPerformed(ActionEvent e) {
                canvasPanel.simulator.loadScenarioFromXml("vasa_CCS", "/sim/examples/");
                canvasPanel.trafficCanvas.reset();
                canvasPanel.trafficCanvas.start();
                statusPanel.reset();
                uiDefaultReset();
                canvasPanel.trafficCanvas.setVmaxForColorSpectrum(22);
                canvasPanel.trafficCanvas.setSleepTime(0);
                canvasPanel.trafficCanvas.setxOffset(400);
                canvasPanel.trafficCanvas.setyOffset(1100);
                canvasPanel.trafficCanvas.setDrawSources(false);
                CCS.setWave(Waves.THREEWAVES);
            }
        });
        scenarioMenu.add(menuItemVasaLoppetThreeWaves);

        final JMenuItem menuItemVasaLoppetTenWaves = new JMenuItem(new AbstractAction(
                resourceBundle.getString("VasaloppetTenWaves")) {

            private static final long serialVersionUID = 4633365854029111923L;

            @Override
            public void actionPerformed(ActionEvent e) {
                canvasPanel.simulator.loadScenarioFromXml("vasa_CCS", "/sim/examples/");
                canvasPanel.trafficCanvas.reset();
                canvasPanel.trafficCanvas.start();
                statusPanel.reset();
                uiDefaultReset();
                canvasPanel.trafficCanvas.setVmaxForColorSpectrum(22);
                canvasPanel.trafficCanvas.setSleepTime(0);
                canvasPanel.trafficCanvas.setxOffset(400);
                canvasPanel.trafficCanvas.setyOffset(1100);
                canvasPanel.trafficCanvas.setDrawSources(false);
                CCS.setWave(Waves.TENWAVES);
            }
        });
        scenarioMenu.add(menuItemVasaLoppetTenWaves);

        // menuItemRoundAbout.setEnabled(false);
        // menuItemCityInterSection.setEnabled(false);

        return scenarioMenu;
    }

    private JMenu viewMenu() {
        final JMenu viewMenu = new JMenu((String) resourceBundle.getObject("ViewMenu"));
        final JMenu colorVehicles = new JMenu(resourceBundle.getString("VehicleColors"));

        final JMenuItem menuItemVehicleColorSpeedDependant = new JMenuItem(
                resourceBundle.getString("VehicleColorSpeedDependant"));
        final JMenuItem menuItemVehicleColorRandom = new JMenuItem(resourceBundle.getString("VehicleColorRandom"));
        colorVehicles.add(menuItemVehicleColorSpeedDependant);
        colorVehicles.add(menuItemVehicleColorRandom);

        menuItemVehicleColorSpeedDependant.setEnabled(false);
        menuItemVehicleColorRandom.setEnabled(false);

        viewMenu.add(colorVehicles);

        viewMenu.addSeparator();
        final JCheckBoxMenuItem cbDrawRoadIds = new JCheckBoxMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("DrawRoadIds")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        handleDrawRoadIds(actionEvent);
                    }
                });
        viewMenu.add(cbDrawRoadIds);

        final JCheckBoxMenuItem cbSources = new JCheckBoxMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("DrawSources")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        handleDrawSources(actionEvent);
                    }
                });
        viewMenu.add(cbSources);

        final JCheckBoxMenuItem cbSinks = new JCheckBoxMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("DrawSinks")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        handleDrawSinks(actionEvent);
                    }
                });
        viewMenu.add(cbSinks);

        final JCheckBoxMenuItem cbSpeedLimits = new JCheckBoxMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("DrawSpeedLimits")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        handleDrawSpeedLimits(actionEvent);
                    }
                });
        viewMenu.add(cbSpeedLimits);

        final JCheckBoxMenuItem cbflowConservingBootleNecks = new JCheckBoxMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("DrawFlowConservingBootleNecks")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                    }
                });
        viewMenu.add(cbflowConservingBootleNecks);

        viewMenu.addSeparator();

        final JCheckBoxMenuItem cbRoutesTravelTimes = new JCheckBoxMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("DrawRoutesTravelTime")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                    }
                });
        viewMenu.add(cbRoutesTravelTimes);

        final JCheckBoxMenuItem cbRoutesSpatioTemporal = new JCheckBoxMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("DrawRoutesSpatioTemporal")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                    }
                });
        viewMenu.add(cbRoutesSpatioTemporal);

        cbRoutesSpatioTemporal.setEnabled(false);
        cbflowConservingBootleNecks.setEnabled(false);
        cbRoutesSpatioTemporal.setEnabled(false);
        cbRoutesTravelTimes.setEnabled(false);

        cbSpeedLimits.setSelected(canvasPanel.trafficCanvas.isDrawSpeedLimits());
        cbDrawRoadIds.setSelected(canvasPanel.trafficCanvas.isDrawRoadId());
        cbSources.setSelected(canvasPanel.trafficCanvas.isDrawSources());
        cbSinks.setSelected(canvasPanel.trafficCanvas.isDrawSinks());
        return viewMenu;
    }

    private JMenu helpMenu() {
        final JMenu helpMenu = new JMenu((String) resourceBundle.getObject("HelpMenu")); //$NON-NLS-1$

        helpMenu.add(new JMenuItem(new AbstractAction((String) resourceBundle.getObject("HelpMenuAbout")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        handleAbout(actionEvent);
                    }
                }));

        final JMenuItem menuItemDocumentation = new JMenuItem(new AbstractAction(
                (String) resourceBundle.getObject("HelpMenuDocumentation")) {//$NON-NLS-1$
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        handleAbout(actionEvent);
                    }
                });
        helpMenu.add(menuItemDocumentation);

        final JMenu language = new JMenu(resourceBundle.getString("LanguageChooser"));
        final JMenuItem menuItemEnglish = new JMenuItem(new AbstractAction(resourceBundle.getString("English")) {

            private static final long serialVersionUID = -2576202988909465055L;

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        language.add(menuItemEnglish);

        final JMenuItem menuItemGerman = new JMenuItem(new AbstractAction(resourceBundle.getString("German")) {

            private static final long serialVersionUID = 7733985567454234949L;

            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        language.add(menuItemGerman);

        menuItemGerman.setEnabled(false);
        menuItemDocumentation.setEnabled(false);

        helpMenu.add(language);

        return helpMenu;
    }

    public void startbuttonToPauseAtScenarioChange() {
        if (canvasPanel.simulator.getSimulationRunnable().isPaused()) {
            canvasPanel.controller.commandTogglePause();
        }
    }

    private void handleAbout(EventObject event) {
        final String titleString = (String) resourceBundle.getObject("AboutTitle"); //$NON-NLS-1$
        final String aboutString = (String) resourceBundle.getObject("AboutText"); //$NON-NLS-1$
        JOptionPane.showMessageDialog(canvasPanel, aboutString, titleString, JOptionPane.INFORMATION_MESSAGE);
    }

    protected void handleTravelTimeDiagram(ActionEvent actionEvent) {
        // final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        // if (trafficUi.getStatusPanel().isWithTravelTimes()) {
        // if (cb.isSelected()) {
        // travelTimeDiagram = new TravelTimeDiagram(resourceBundle, cb);
        // } else {
        // SwingHelper.closeWindow(travelTimeDiagram);
        // }
        // } else {
        // JOptionPane.showMessageDialog(frame, resourceBundle.getString("NoTravelTime"));
        // cb.setSelected(false);
        // }

    }

    protected void handleSpatioTemporalDiagram(ActionEvent actionEvent) {
        // final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        // if (cb.isSelected()) {
        // spatioTemporalDiagram = new SpatioTemporalView(resourceBundle, cb);
        // } else {
        // SwingHelper.closeWindow(spatioTemporalDiagram);
        // }
    }

    protected void handleFloatingCarsDiagram(ActionEvent actionEvent) {
        // final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        // if (cb.isSelected()) {
        // fcAcc = new FloatingCarsAccelerationView();
        // fcSpeed = new FloatingCarsSpeedView();
        // fcTrajectories = new FloatingCarsTrajectoriesView();
        // } else {
        // SwingHelper.closeWindow(fcAcc);
        // SwingHelper.closeWindow(fcSpeed);
        // SwingHelper.closeWindow(fcTrajectories);
        // }
    }

    protected void handleDetectorsDiagram(ActionEvent actionEvent) {
        // final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        // if (cb.isSelected()) {
        // detectorsDiagram = new DetectorsView(resourceBundle, cb);
        // } else {
        // SwingHelper.closeWindow(detectorsDiagram);
        // }
    }

    protected void handleFuelConsumptionDiagram(ActionEvent actionEvent) {
        SwingHelper.notImplemented(canvasPanel);
    }

    protected void handleLogOutput(ActionEvent actionEvent) {
        final JCheckBoxMenuItem cbMenu = (JCheckBoxMenuItem) actionEvent.getSource();
        if (cbMenu.isSelected()) {
            logWindow = new LogWindow(resourceBundle, cbMenu);
        } else {
            SwingHelper.closeWindow(logWindow);
        }
    }

    protected void handleDrawRoadIds(ActionEvent actionEvent) {
        final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        if (cb.isSelected()) {
            canvasPanel.trafficCanvas.setDrawRoadId(true);
        } else {
            canvasPanel.trafficCanvas.setDrawRoadId(false);
        }
        canvasPanel.trafficCanvas.forceRepaintBackground();
    }

    protected void handleDrawSources(ActionEvent actionEvent) {
        final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        if (cb.isSelected()) {
            canvasPanel.trafficCanvas.setDrawSources(true);
        } else {
            canvasPanel.trafficCanvas.setDrawSources(false);
        }
        canvasPanel.trafficCanvas.forceRepaintBackground();
    }

    protected void handleDrawSinks(ActionEvent actionEvent) {
        final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        if (cb.isSelected()) {
            canvasPanel.trafficCanvas.setDrawSinks(true);
        } else {
            canvasPanel.trafficCanvas.setDrawSinks(false);
        }
        canvasPanel.trafficCanvas.forceRepaintBackground();
    }

    protected void handleDrawSpeedLimits(ActionEvent actionEvent) {
        final JCheckBoxMenuItem cb = (JCheckBoxMenuItem) actionEvent.getSource();
        if (cb.isSelected()) {
            canvasPanel.trafficCanvas.setDrawSpeedLimits(true);
        } else {
            canvasPanel.trafficCanvas.setDrawSpeedLimits(false);
        }
        canvasPanel.trafficCanvas.forceRepaintBackground();
    }

    public void uiDefaultReset() {
        startbuttonToPauseAtScenarioChange();
        statusPanel.setWithProgressBar(false);
        statusPanel.reset();
        canvasPanel.trafficCanvas.roadLineColor = Color.LIGHT_GRAY;
        canvasPanel.trafficCanvas.roadEdgeColor = Color.DARK_GRAY;
        canvasPanel.trafficCanvas.backgroundColor = Color.WHITE;
//        setScale(initialScale);
//        for (RoadSegment segment: roadNetwork) {
//            segment.roadMapping().setRoadColor(Color.WHITE);
//        }
    }

}