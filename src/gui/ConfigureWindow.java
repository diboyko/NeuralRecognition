package gui;

import main.Controller;
import exceptions.ConfigNotLoadedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by Me on 29.06.2016.
 */
class ConfigureWindow extends JFrame {
    private JFrame configurePane, parentFrame;
    private JPanel configurePanel;
    private JTextField configFilePath, testCasesPath;
    private File config;
    private File testCasesFolder;

    ConfigureWindow(final JFrame parentFrame) {
        prepareMainWindow();
        createAndAddLoadConfigFilePanel();
        createAndAddCreateNewConfigFilePanel();
        createAndAddLastRow();
        JFrame.setDefaultLookAndFeelDecorated(true);
        configurePane = new JFrame("Configure network");
        configurePane.getContentPane().add(configurePanel);
        configurePane.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                parentFrame.setEnabled(true);
                parentFrame.toFront();
            }
        });
        configurePane.pack();
        configurePane.setLocationRelativeTo(null);
        configurePane.setVisible(true);
        configurePane.setResizable(false);
        this.parentFrame = parentFrame;
    }

    private void prepareMainWindow() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFrame.setDefaultLookAndFeelDecorated(true);

        JLabel configureLabel = new JLabel("Choose existing config file or create new one");
        configureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        configureLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        configurePanel = new JPanel();
        configurePanel.setLayout(new BoxLayout(configurePanel, BoxLayout.Y_AXIS));
        configurePanel.add(configureLabel);
    }

    private void createAndAddLoadConfigFilePanel() {
        JPanel firstRow = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        JLabel chooseFileLabel = new JLabel("Choose configuration file:");
        chooseFileLabel.setPreferredSize(new Dimension(150, 18));
        chooseFileLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        firstRow.add(chooseFileLabel);
        configurePanel.add(firstRow);
        JPanel secondRow = new JPanel();
        configFilePath = new JTextField(10);
        configFilePath.setEditable(false);
        secondRow.setBorder(BorderFactory.createLineBorder(Color.black));
        JButton chooseFileButton = new JButton("Browse");
        chooseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                config = openfilechooser("file");
                try {
                    configFilePath.setText(config.getCanonicalPath());
                } catch (IOException f) {
                    f.printStackTrace();
                }
            }
        });
        JButton loadConfigButton = new JButton("Load file");
        loadConfigButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadConfig(config);
            }
        });

        secondRow.add(chooseFileButton);
        secondRow.add(configFilePath);
        secondRow.add(loadConfigButton);
        configurePanel.add(secondRow);
    }

    private void createAndAddCreateNewConfigFilePanel() {
        JPanel thirdRow = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        JLabel createConfigLabel = new JLabel("Create new config file: ");
        createConfigLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        createConfigLabel.setPreferredSize(new Dimension(150, 25));
        thirdRow.add(createConfigLabel);
        configurePanel.add(thirdRow);

        JPanel createConfigRowsContainer = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
        createConfigRowsContainer.setLayout(new BoxLayout(createConfigRowsContainer, BoxLayout.Y_AXIS));
        createConfigRowsContainer.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel fourthRow = new JPanel();
        JLabel createNewConfigLabel = new JLabel("To create new config file please choose location of a testcases folder");
        fourthRow.add(createNewConfigLabel);

        JPanel fifthRow = new JPanel();
        JButton chooseTestCaseFolderButton = new JButton("Browse");
        testCasesPath = new JTextField(10);
        chooseTestCaseFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                testCasesFolder = openfilechooser("folder");
                try {
                    testCasesPath.setText(testCasesFolder.getCanonicalPath());
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
        JButton loadTestCasesButton = new JButton("Load testcases");
        loadTestCasesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadTestCases(testCasesFolder);
            }
        });

        fifthRow.add(chooseTestCaseFolderButton);
        fifthRow.add(testCasesPath);
        fifthRow.add(loadTestCasesButton);
        createConfigRowsContainer.add(fourthRow);
        createConfigRowsContainer.add(fifthRow);
        configurePanel.add(createConfigRowsContainer);
    }


    private void createAndAddLastRow() {
        JPanel lastRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        JButton OkButton = new JButton("Initialize network");
        OkButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                configurePane.dispose();
                parentFrame.setEnabled(true);
                parentFrame.toFront();
            }
        });
        lastRow.add(OkButton);
        configurePanel.add(lastRow);
    }

    private File openfilechooser(String type) {
        JFileChooser fc = new JFileChooser();
        if (type.equals("folder")) {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setAcceptAllFileFilterUsed(false);
        }
        File file = new File(".");
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            System.out.println(file.getAbsolutePath());
        }
        return file;
    }

    private void loadConfig(File config) {
        try {
            Controller.restoreNetworkWeights(config);
            JOptionPane.showMessageDialog(null,
                    "Loaded successfully", "File loaded",
                    JOptionPane.WARNING_MESSAGE);
        } catch (ConfigNotLoadedException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage(), "Wrong parameters",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    private void loadTestCases(File testcaseFolder) {
        try {
            Controller.loadTestCases(testcaseFolder, 25);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}

