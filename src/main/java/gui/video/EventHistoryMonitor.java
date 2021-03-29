package gui.video;

import environment.Coordinate;
import environment.Environment;
import gui.setup.Setup;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Variables;
import util.event.Event;
import util.event.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class EventHistoryMonitor extends JFrame {

    public EventHistoryMonitor() {
        this.table = new JTable(new DefaultTableModel(new Object[]{"Cycle", "Agent ID", "Action"}, 0));
        this.scrollPane = new JScrollPane(this.table);
        this.exportButton = new JButton("Export data");

        this.exportButton.addActionListener(this::export);

        this.historyPackets = new HashMap<>();
        this.historyMoves = new HashMap<>();
        this.historyEnergy = new HashMap<>();
        this.totalCycles = -1;
        this.totalPackets = getEnvironment().getPacketWorld().getNbPackets();

        EventManager.getInstance().addListener(this::addAgentAction, AgentActionEvent.class);
        EventManager.getInstance().addListener(this::addEnergyEvent, EnergyUpdateEvent.class);
        EventManager.getInstance().addListener(e -> this.totalCycles = this.getEnvironment().getTime(), GameOverEvent.class);
    }


    /**
     * Initialize the GUI components
     */
    public void initialize() {
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(1).setMaxWidth(80);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        setSize(450, 300);

        setTitle("Actions");
        scrollPane.getViewport().add(table);
        getContentPane().add(this.exportButton, BorderLayout.SOUTH);

        setLocation(0, 400);
    }


    /**
     * Add the given agent action to the history of actions (if actions should be logged).
     *
     * @param e The AgentAction event.
     */
    private void addAgentAction(Event e) {
        AgentActionEvent event = (AgentActionEvent) e;
        int time = this.getEnvironment().getTime();

        this.addCycle(time);

        var packet = event.getPacket();
        var agentId = event.getAgent().getID();

        switch (event.getAction()) {
            case AgentActionEvent.PICK: {
                var action = new PacketAction(packet.getX(), packet.getY(),
                        PacketAction.Mode.Pickup, agentId);
                this.historyPackets.get(time).add(action);
                this.addRowTable(time, agentId, action.toString());
                break;
            }
            case AgentActionEvent.PUT: {
                var action = new PacketAction(event.getToX(), event.getToY(),
                        PacketAction.Mode.Drop, agentId);
                this.historyPackets.get(time).add(action);
                this.addRowTable(time, agentId, action.toString());
                break;
            }
            case AgentActionEvent.DELIVER: {
                var action = new PacketAction(event.getToX(), event.getToY(),
                        PacketAction.Mode.Delivery, agentId);
                this.historyPackets.get(time).add(action);
                this.addRowTable(time, agentId, action.toString());
                break;
            }
            case AgentActionEvent.STEP: {
                var action = new AgentMove(event.getFromX(), event.getFromY(),
                        event.getToX(), event.getToY(), agentId);
                this.historyMoves.get(time).add(action);
                this.addRowTable(time, agentId, action.toString());
                break;
            }
        }

        repaint();
    }

    /**
     * Add the given energy update to the history of energy updates.
     *
     * @param e The event containing the energy update.
     */
    private void addEnergyEvent(Event e) {
        EnergyUpdateEvent event = (EnergyUpdateEvent) e;

        int time = this.getEnvironment().getTime();
        this.addCycle(time);

        var agentId = event.getAgent().getID();
        var action = new EnergyUpdate(event.getEnergyPercentage(), event.isIncreased(), agentId);
        this.historyEnergy.get(time).add(action);
        this.addRowTable(time, agentId, action.toString());

        repaint();
    }



    /**
     * Add a row to the table of occurred events.
     *
     * @param cycle The cycle in which the event occurred.
     * @param agentId The Id of the agent that performed the action.
     * @param message The action the agent performed, represented by a String.
     */
    private void addRowTable(int cycle, int agentId, String message) {
        ((DefaultTableModel) this.table.getModel()).addRow(new Object[]{Integer.toString(cycle),
                Integer.toString(agentId), message});

        // https://stackoverflow.com/questions/5147768/scroll-jscrollpane-to-bottom
        var scrollBar = scrollPane.getVerticalScrollBar();
        AdjustmentListener downScroller = new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                scrollBar.removeAdjustmentListener(this);
            }
        };
        scrollBar.addAdjustmentListener(downScroller);
    }


    /**
     * Internal: add new entries to the maps that keep track of the history for the given cycle,
     *  if not added yet.
     *
     * @param cycle The cycle for which to add new entries to the maps.
     */
    private void addCycle(int cycle) {
        // Add entries to history in case none is present yet for the given cycle
        if (!historyPackets.containsKey(cycle)) {
            historyPackets.put(cycle, new ArrayList<>());
        }
        if (!historyMoves.containsKey(cycle)) {
            historyMoves.put(cycle, new ArrayList<>());
        }
        if (!historyEnergy.containsKey(cycle)) {
            historyEnergy.put(cycle, new ArrayList<>());
        }
    }


    /**
     * Functionality for the export button. Asks the user to choose a file in which to store the history.
     *  If an appropriate file is chosen, the history is written to this file.
     *
     * @param actionEvent The actionEvent originating from clicking the export button.
     */
    private void export(ActionEvent actionEvent) {
        if (this.totalCycles == -1) {
            var result = JOptionPane.showConfirmDialog(this, "The run is not finished yet. Are you sure you want to proceed?",
                    "Unfinished run", JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.NO_OPTION) {
                return;
            }
        }

        // File dialog to save the history
        var fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getName().endsWith(".json");
            }

            @Override
            public String getDescription() {
                return "*.json";
            }
        });

        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setCurrentDirectory(new File(Variables.OUTPUT_PATH));
        var resultDialog = fileChooser.showSaveDialog(this);

        if (resultDialog == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.getSelectedFile();

            if (!file.getName().endsWith(".json")) {
                file = new File(file.getAbsolutePath() + ".json");
            }
            if (file.exists()) {
                var sure = JOptionPane.showConfirmDialog(this, "File already exists. Overwrite?",
                        "Overwrite file?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (sure != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            this.saveHistoryToFile(file);
        }
    }


    /**
     * Save the history observed so far to the given file in JSON format.
     *
     * @param file The file in which to write the history of actions.
     */
    private void saveHistoryToFile(File file) {
        JSONObject head = new JSONObject();

        // Meta information about the run
        JSONObject meta = new JSONObject();
        meta.put("TotalCycles", this.totalCycles == -1 ? getEnvironment().getTime() : this.totalCycles);
        meta.put("TotalPackets", this.totalPackets);
        meta.put("PacketsDelivered", this.historyPackets.values().stream()
                .flatMap(Collection::stream)
                .filter(p -> p.mode == PacketAction.Mode.Delivery)
                .count());
        meta.put("EnergyConsumed", UserPanel.getInstance().score);


        JSONObject moves = new JSONObject();
        moves.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "FromX", "FromY", "ToX", "ToY"}));
        moves.put("Data", new JSONArray(historyMoves.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(l -> l.toJSONArray(entry.getKey())))
                .collect(Collectors.toList())));


        JSONObject packetPickup = new JSONObject();
        packetPickup.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "PacketX", "PacketY"}));
        packetPickup.put("Data", new JSONArray(historyPackets.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .filter(p -> p.mode == PacketAction.Mode.Pickup)
                        .map(p -> p.toJSONArray(entry.getKey())))
                .collect(Collectors.toList())));


        JSONObject packetDelivery = new JSONObject();
        packetDelivery.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "DestinationX", "DestinationY"}));
        packetDelivery.put("Data", new JSONArray(historyPackets.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .filter(p -> p.mode == PacketAction.Mode.Delivery)
                        .map(l -> l.toJSONArray(entry.getKey())))
                .collect(Collectors.toList())));


        JSONObject packetDrop = new JSONObject();
        packetDrop.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "DropX", "DropY"}));
        packetDrop.put("Data", new JSONArray(historyPackets.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .filter(p -> p.mode == PacketAction.Mode.Drop)
                        .map(l -> l.toJSONArray(entry.getKey())))
                .collect(Collectors.toList())));


        JSONObject energyUpdate = new JSONObject();
        energyUpdate.put("Key", new JSONArray(new String[] {"Cycle", "AgentID", "Operator", "Percentage"}));
        energyUpdate.put("Data", new JSONArray(historyEnergy.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream()
                        .map(l -> l.toJSONArray(entry.getKey())))
                .collect(Collectors.toList())));


        head.put("Meta", meta);
        head.put("Moves", moves);
        head.put("PacketPickups", packetPickup);
        head.put("PacketDeliveries", packetDelivery);
        head.put("PacketDrops", packetDrop);
        head.put("EnergyUpdates", energyUpdate);

        try {
            FileWriter writer = new FileWriter(file);
            head.write(writer, 0, 0);
            writer.close();
        } catch (IOException e) {
            System.err.println("Failed to write history to file " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }


    private Environment getEnvironment() {
        return Setup.getInstance().getEnvironment();
    }


    public void reset() {
        this.totalCycles = -1;
        this.historyMoves.clear();
        this.historyPackets.clear();
        ((DefaultTableModel) this.table.getModel()).setRowCount(0);
        this.totalPackets = getEnvironment().getPacketWorld().getNbPackets();
    }


    private final JScrollPane scrollPane;
    private final JTable table;
    private final JButton exportButton;
    private int totalCycles;
    private int totalPackets;

    // Cycle -> packet pickup, put or delivery
    private final Map<Integer, List<PacketAction>> historyPackets;
    // Cycle -> agent move
    private final Map<Integer, List<AgentMove>> historyMoves;
    // Cycle -> energy update
    private final Map<Integer, List<EnergyUpdate>> historyEnergy;


    private static class AgentMove {
        int agentId;
        Coordinate moveFrom;
        Coordinate moveTo;

        AgentMove(int fromX, int fromY, int toX, int toY, int agentId) {
            this.agentId = agentId;
            this.moveFrom = new Coordinate(fromX, fromY);
            this.moveTo = new Coordinate(toX, toY);
        }

        public String toString() {
            return "Moved from " + this.moveFrom.toString() + " to " + this.moveTo.toString();
        }

        public JSONArray toJSONArray(int cycle) {
            return new JSONArray(new Integer[] {cycle, this.agentId, this.moveFrom.getX(), this.moveFrom.getY(),
                    this.moveTo.getX(), this.moveTo.getY()});
        }
    }

    private static class PacketAction {
        int agentId;
        Coordinate packetLocation;
        Mode mode;

        PacketAction(int x, int y, Mode mode, int agentId) {
            this.agentId = agentId;
            this.packetLocation = new Coordinate(x, y);
            this.mode = mode;
        }

        public String toString() {
            return this.mode.toString() + " of packet at location " + this.packetLocation.toString();
        }

        public JSONArray toJSONArray(int cycle) {
            return new JSONArray(new Integer[] {cycle, this.agentId, this.packetLocation.getX(), this.packetLocation.getY()});
        }

        enum Mode {
            Pickup,
            Drop,
            Delivery
        }
    }

    private static class EnergyUpdate {
        int agentId;
        int percentage;
        boolean isIncreased;

        EnergyUpdate(int percentage, boolean isIncreased, int agentId) {
            this.agentId = agentId;
            this.percentage = percentage;
            this.isIncreased = isIncreased;
        }

        public String toString() {
            return String.format("Energy %s %d%%", this.isIncreased ? "increased above (or equal to)" : "dropped below", this.percentage);
        }

        public JSONArray toJSONArray(int cycle) {
            return new JSONArray(new Object[] {cycle, this.agentId, this.isIncreased ? ">=" : "<", this.percentage});
        }
    }
}
