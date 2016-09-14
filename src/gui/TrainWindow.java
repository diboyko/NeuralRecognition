package gui;

import main.Controller;
import exceptions.ConfigNotLoadedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;


/**
 * Created by Me on 01.06.2016.
 */
class TrainWindow extends JFrame {

    private JFrame trainPane, parentFrame;
    private JPanel trainPanel;
    private JTextField letterField, momentField, learnField;
    private FlowLayout flowLay = new FlowLayout(FlowLayout.LEADING, 0, 0);

    TrainWindow(final JFrame parentFrame) {
        prepareMainWindow();
        createAndAddFirstRow();
        createAndAddSecondRow();
        createAndAddThirdRow();
        createAndAddFourthRow();
        JFrame.setDefaultLookAndFeelDecorated(true);
        trainPane = new JFrame("Train");
        trainPane.getContentPane().add(trainPanel);
        trainPane.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                parentFrame.setEnabled(true);
                parentFrame.toFront();
            }
        });
        trainPane.pack();
        trainPane.setLocationRelativeTo(null);
        trainPane.setVisible(true);
        this.parentFrame = parentFrame;
    }

    private void prepareMainWindow() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame.setDefaultLookAndFeelDecorated(true);

        JLabel trainLabel = new JLabel("Specify parameters or leave blank to use defaults");
        trainLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        trainPanel = new JPanel();
        trainPanel.setLayout(new BoxLayout(trainPanel, BoxLayout.Y_AXIS));
        trainPanel.add(trainLabel);
    }

    private void createAndAddFirstRow() {
        JPanel firstRow = new JPanel(flowLay);
        JLabel letterLable = new JLabel("Enter correct letter:");
        letterLable.setPreferredSize(new Dimension(150, 18));
        letterField = new JTextField(10);
        letterField.setToolTipText("Capital english letters between A and Z");
        firstRow.add(letterLable);
        firstRow.add(letterField);
        trainPanel.add(firstRow);
    }

    private void createAndAddSecondRow() {
        JPanel secondRow = new JPanel(flowLay);
        JLabel momentumLable = new JLabel("Enter momentum factor:");
        momentumLable.setPreferredSize(new Dimension(150, 18));
        momentField = new JTextField(Controller.getNetwork().getMomentumFactor() + "", 10);
        momentField.setToolTipText("The momentum should be between 0 and 1");
        secondRow.add(momentumLable);
        secondRow.add(momentField);
        trainPanel.add(secondRow);
    }

    private void createAndAddThirdRow() {
        JPanel thirdRow = new JPanel(flowLay);
        JLabel learnLabel = new JLabel("Enter learning rate:");
        learnLabel.setPreferredSize(new Dimension(150, 18));
        learnField = new JTextField(Controller.getNetwork().getLearningRate() + "", 10);
        learnField.setToolTipText("The learning rate should be between 0 and 1");
        thirdRow.add(learnLabel);
        thirdRow.add(learnField);
        trainPanel.add(thirdRow);
    }

    private void createAndAddFourthRow() {
        JPanel fouthRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton trainButton = new JButton("Train");
        trainButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trainWithLetter();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                trainPane.dispose();
                parentFrame.setEnabled(true);
                parentFrame.toFront();
            }
        });
        fouthRow.add(trainButton);
        fouthRow.add(cancelButton);
        trainPanel.add(fouthRow);
    }


    private void trainWithLetter() {
        try {
            Controller.trainWithLetter(momentField.getText(), learnField.getText(), letterField.getText());
            trainPane.dispose();
            parentFrame.setEnabled(true);
            parentFrame.toFront();
        } catch (ConfigNotLoadedException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "Configuration file not loaded",
                    JOptionPane.WARNING_MESSAGE);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "Wrong parameters",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
