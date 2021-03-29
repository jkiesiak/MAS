package gui.video;

import environment.world.agent.Agent;
import util.event.Event;
import util.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class UserPanel extends JPanel implements ActionListener, ChangeListener,
    Listener {

    private VideoFrame frame;

    final JLabel scoreLabel;
    final JLabel dquestionLabel;
    final JLabel pquestionLabel;
    final JLabel conflictLabel;
    final JLabel deliveredLabel;
    final JLabel cycliLabel;

    int score = 0;
    int pmsgs = 0;
    int dmsgs = 0;
    int conflicts = 0;
    int delivered = 0;
    int nbCycli = 0;

    final JButton startButton;
    final JButton playButton;
    final JButton stepButton;
    final JButton pauseButton;
    //JButton resumeButton;
    final JButton restartButton;
    final JSlider playSpeed;
    final int speed;

    /**
     * @label singleton
     */
    static UserPanel instance = new UserPanel();

    public static UserPanel getInstance() {
        if (instance == null) {
            instance = new UserPanel();
        }
        return instance;
    }

    public void catchEvent(Event e) {
        if (e instanceof AgentActionEvent) {
            int action = ( (AgentActionEvent) e).getAction();
            switch (action) {
                case AgentActionEvent.PICK:
                case AgentActionEvent.PUT:
                    score += Agent.BATTERY_DECAY_SKIP;
                    break;
                case AgentActionEvent.DELIVER:
                    score += Agent.BATTERY_DECAY_SKIP;
                    delivered++;
                    break;
                case AgentActionEvent.STEP:
                    if ( ( (AgentActionEvent) e).getAgent().hasCarry()) {
                        score += Agent.BATTERY_DECAY_STEP_WITH_CARRY;
                    } else {
                        score += Agent.BATTERY_DECAY_STEP;
                    }
                    break;
                case AgentActionEvent.LOADENERGY:
                    // skip
                    break;
                default:
                    score += Agent.BATTERY_DECAY_SKIP;
                    break;
            }
        } else if (e instanceof MsgSentEvent) {
            if ( ( (MsgSentEvent) e).isQuestion()) {
                if (((MsgSentEvent) e).getMsg().getMessage().charAt(13) == '1') {
                    dmsgs++;
                } else if (((MsgSentEvent) e).getMsg().getMessage().charAt(13) == '0') {
                    pmsgs++;
                }
            }
        } else if (e instanceof WorldProcessedEvent) {
            nbCycli++;
        }
        this.repaint();
    }

    public void setFrame(VideoFrame frame) {
        getInstance().frame = frame;
    }

    public void shutdown() {
        instance = null;
    }

    private void addButton(JButton button, JPanel panel) {
        button.addActionListener(this);
        panel.add(button);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (scoreLabel != null) {
            scoreLabel.setText(" Energy = " + score);
            dquestionLabel.setText("Request dest = " + dmsgs);
            pquestionLabel.setText("Request packet = " + pmsgs);
            conflictLabel.setText("Conflicts = " + conflicts);
            deliveredLabel.setText("Packets delivered = " + delivered);
            cycliLabel.setText(" Cycles = " + nbCycli);
        }
    }


    private UserPanel() {
        EventManager.getInstance().addListener(this, AgentActionEvent.class);
        EventManager.getInstance().addListener(this, MsgSentEvent.class);
        EventManager.getInstance().addListener(this, WorldProcessedEvent.class);

        setLayout(new BorderLayout());

        add(new JLabel(), "North");

        JPanel buttonPanel = new JPanel(new GridLayout(2, 6, 5, 0));

        scoreLabel = new JLabel(" Energy = " + score);
        pquestionLabel = new JLabel(" Requests");
        dquestionLabel = new JLabel(" Requests");
        conflictLabel = new JLabel("Conflicts = " + conflicts);
        deliveredLabel = new JLabel("Packets delivered = " + delivered);
        cycliLabel = new JLabel(" Cycles = " + nbCycli);

        buttonPanel.add(scoreLabel);
        buttonPanel.add(conflictLabel);
        buttonPanel.add(pquestionLabel);
        buttonPanel.add(dquestionLabel);
        buttonPanel.add(deliveredLabel);
        buttonPanel.add(cycliLabel);


        startButton = new JButton("start");
        addButton(startButton, buttonPanel);
        playButton = new JButton("play");
        addButton(playButton, buttonPanel);
        playButton.setEnabled(false);
        stepButton = new JButton("step");
        addButton(stepButton, buttonPanel);
        stepButton.setEnabled(false);
        pauseButton = new JButton("pause");
        addButton(pauseButton, buttonPanel);
        pauseButton.setEnabled(false);

        restartButton = new JButton("reset");
        addButton(restartButton, buttonPanel);
        restartButton.setEnabled(false);
        speed = 500;
        playSpeed = new JSlider(0, 2000, speed);
        playSpeed.setMajorTickSpacing(1);
        playSpeed.setMinorTickSpacing(1);
        playSpeed.addChangeListener(this);
        buttonPanel.add(playSpeed);
        add(new JLabel(" "), BorderLayout.CENTER);
        add(buttonPanel, "South");
    }

    public void reset() {
        this.score = 0;
        this.dmsgs = 0;
        this.pmsgs = 0;
        this.conflicts = 0;
        this.delivered = 0;
        this.nbCycli = 0;

        repaint();
    }

    public void stateChanged(ChangeEvent evt) {
        JSlider source = (JSlider) evt.getSource();
        VideoPanel.getInstance().setPlaySpeed(source.getValue());
    }

    // step and pause are provisionally disabled
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == startButton) {
            getInstance().startVideo();
            startButton.setEnabled(false);
            playButton.setEnabled(true);
            stepButton.setEnabled(true);
            restartButton.setEnabled(false);
        } else if (source == playButton) {
            getInstance().playVideo();
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            restartButton.setEnabled(false);
            playButton.setEnabled(false);
            stepButton.setEnabled(false);
        } else if (source == stepButton) {
            stepButton.setEnabled(false);
            getInstance().stepVideo();
            stepButton.setEnabled(true);
        } else if (source == pauseButton) {
            getInstance().pauseVideo();
            pauseButton.setEnabled(false);
            playButton.setEnabled(true);
            restartButton.setEnabled(true);
            stepButton.setEnabled(true);
        } else if (source == restartButton) {
            getInstance().restartVideo();
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            playButton.setEnabled(false);
            stepButton.setEnabled(false);
        }
    }

    private void startVideo() {
        VideoPanel.getInstance().startVideo();
    }

    private void playVideo() {
        VideoPanel.getInstance().playVideo();
    }

    private void stepVideo() {
        VideoPanel.getInstance().stepVideo();
    }

    private void pauseVideo() {
        VideoPanel.getInstance().pauseVideo();
    }

    private void restartVideo() {
        VideoPanel.getInstance().restartVideo();
    }
}
