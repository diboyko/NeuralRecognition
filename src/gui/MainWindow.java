package gui;

import main.Controller;
import exceptions.ConfigNotLoadedException;
import exceptions.ImageReadingException;
import utility.Letters;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;


@SuppressWarnings("serial")
public class MainWindow extends JFrame {

    private Container mainContentWindow;
    private JFrame mainFrame, configureWindow, trainWindow;
    private JPanel panelTop, panelMid, panelMidLeft, panelMidRight, panelBot, panelInnerMid;
    private JLabel helperLabelForImageDisplay = new JLabel();

    private File letterPath;

    private FlowLayout flowLay = new FlowLayout(FlowLayout.LEADING, 75, 0);
    private DecimalFormat df = new DecimalFormat("#");

    public MainWindow() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    prepareMainFrame();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void prepareMainFrame() {
        mainFrame = new JFrame("LetterRecognition");
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        createGUI();
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void createGUI() {
        mainContentWindow = mainFrame.getContentPane();
        mainContentWindow.setLayout(new BorderLayout(0, 0));
        createAndAddTopPanel();
        createAndAddMidPanel();
        createAndAddBotPanel();
        formTableForResults();
        createAndAddButtons();
    }

    private void createAndAddTopPanel() {
        panelTop = new JPanel();
        panelTop.setLayout(flowLay);
        mainContentWindow.add(panelTop, BorderLayout.PAGE_START);
    }

    private void createAndAddMidPanel() {
        panelMid = new JPanel();
        panelMid.setLayout(flowLay);

        panelMidLeft = new JPanel();
        panelMidLeft.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelMidLeft.setPreferredSize(new Dimension(128, 128));

        panelMidRight = new JPanel();
        panelMidRight.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        panelMidRight.setPreferredSize(new Dimension(128, 128));

        panelInnerMid = new JPanel();
        panelInnerMid.setPreferredSize(new Dimension(128, 128));
        panelInnerMid.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 25));
        panelMid.add(panelMidLeft);
        panelMid.add(panelInnerMid);
        panelMid.add(panelMidRight);
        mainContentWindow.add(panelMid, BorderLayout.CENTER);
    }

    private void createAndAddBotPanel() {
        panelBot = new JPanel();
        panelBot.setPreferredSize(new Dimension(700, 75));
        mainContentWindow.add(panelBot, BorderLayout.PAGE_END);
    }

    private void createAndAddButtons() {
        createAndAddBrowseButton();
        createAndAddConfigureButton();
        createAndAddSaveButton();
        createAndAddRecogniseButton();
        createAndAddTrainButton();

    }

    private void createAndAddSaveButton() {
        JButton btnSaveNetworkWeights = new JButton("Save network");
        btnSaveNetworkWeights.setPreferredSize(new Dimension(128, 25));
        btnSaveNetworkWeights.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveNetworkWeights();
            }
        });
        panelTop.add(btnSaveNetworkWeights);
    }

    private void createAndAddConfigureButton() {
        JButton btnConfigureNetwork = new JButton("Configure network");
        btnConfigureNetwork.setPreferredSize(new Dimension(128, 25));
        btnConfigureNetwork.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openConfigureWindow();
            }
        });
        panelTop.add(btnConfigureNetwork);
    }

    private void createAndAddBrowseButton() {
        JButton btnBrowse = new JButton("Choose letter");
        btnBrowse.setPreferredSize(new Dimension(128, 25));
        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openBrowseDialog();
            }
        });
        panelTop.add(btnBrowse);
    }

    private void createAndAddRecogniseButton() {
        JButton btnRecogniseLetter = new JButton("Recognize>>");
        btnRecogniseLetter.setPreferredSize(new Dimension(128, 25));
        btnRecogniseLetter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                recognizeLetter();
            }
        });
        panelInnerMid.add(btnRecogniseLetter);
    }

    private void createAndAddTrainButton() {
        JButton btnOpenTrainWindow = new JButton("Train as...");
        btnOpenTrainWindow.setPreferredSize(new Dimension(128, 25));
        btnOpenTrainWindow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openTrainWindow();
            }
        });
        panelInnerMid.add(btnOpenTrainWindow);
    }

    private void formTableForResults() {
        JTable tableWithResults = new JTable();
        tableWithResults.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tableWithResults.setModel(new AbstractTableModel() {

            public String getColumnName(int col) {
                return Letters.Alphabet[col];
            }

            public int getRowCount() {
                return 1;
            }

            public int getColumnCount() {
                return Controller.getNetwork().getOutputLayerNodes().length;
            }

            public Object getValueAt(int row, int col) {

                double dValue = Controller.getNetwork().getOutputLayerNodes()[col] * 100;
                return df.format(dValue);
            }

            public void setValueAt(Object value, int row, int col) {
                Controller.getNetwork().getOutputLayerNodes()[col] = (double) value;
                fireTableCellUpdated(row, col);
            }
        });
        tableWithResults.setPreferredScrollableViewportSize(new Dimension(700, 25));
        tableWithResults.setFillsViewportHeight(true);
        panelBot.add(new JScrollPane(tableWithResults));
    }


    private BufferedImage rescaleImage(BufferedImage originalImage) {
        int rescaleResolution = 128;
        BufferedImage resizedImage = new BufferedImage(rescaleResolution, rescaleResolution, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, rescaleResolution, rescaleResolution, null);
        g.dispose();
        return resizedImage;
    }

    private void displayImgInLabel(File imageReference, Container targetLabel) {
        BufferedImage imgToDisplay = null;
        try {
            imgToDisplay = rescaleImage(ImageIO.read(imageReference));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        targetLabel.setLayout(new BorderLayout(0, 0));
        targetLabel.add(helperLabelForImageDisplay);
        ImageIcon icon = new ImageIcon(imgToDisplay);
        helperLabelForImageDisplay.setIcon(icon);
        targetLabel.revalidate();
        targetLabel.repaint();
        helperLabelForImageDisplay.repaint();
        helperLabelForImageDisplay.revalidate();

    }

    private void openBrowseDialog() {
        JFileChooser fc;
        if (letterPath != null) {
            fc = new JFileChooser(letterPath.getPath());
        } else fc = new JFileChooser();
        int res = fc.showOpenDialog(null);
        try {
            if (res == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                if (file.getPath().endsWith(".png")) {
                    letterPath = file;
                    displayImgInLabel(file, panelMidLeft);
                    JOptionPane.showMessageDialog(null, "Image loaded", "Resizing...",  JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "File is not an image or wrong format", "Aborting...", JOptionPane.WARNING_MESSAGE);
                }
            } else if (letterPath == null) {
                JOptionPane.showMessageDialog(null, "You must select image.", "Aborting...", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void recognizeLetter() {
        try {
            Letters recognizedLetter = Controller.identifyLetter(letterPath);
            String letterToDisplayPath = Letters.getPathOfLetterExample(recognizedLetter);
            File file = new File(letterToDisplayPath);
            displayImgInLabel(file, panelMidRight);
            JOptionPane.showMessageDialog(null, "Recognized", "Succes", JOptionPane.INFORMATION_MESSAGE);
            panelBot.repaint();
        } catch (ConfigNotLoadedException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Configuration is not loaded ", JOptionPane.ERROR_MESSAGE);
        } catch (ImageReadingException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Image is not chosen", JOptionPane.WARNING_MESSAGE);
        }

    }

    private void saveNetworkWeights() {
        try {
            Controller.saveNetworkWeights();
            JOptionPane.showMessageDialog(null, "Configuration saved", "Succes", JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Problems saving weights", "Failure", JOptionPane.WARNING_MESSAGE);
        }
        catch (ConfigNotLoadedException e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Image is not chosen", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openTrainWindow() {
        if (letterPath != null) {
            trainWindow = new TrainWindow(mainFrame);
            mainFrame.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(null, "Choose a letter", "Failure", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openConfigureWindow() {
            configureWindow = new ConfigureWindow(mainFrame);
            mainFrame.setEnabled(false);

    }

    public File getLetterPath() {
        return letterPath;
    }

}


