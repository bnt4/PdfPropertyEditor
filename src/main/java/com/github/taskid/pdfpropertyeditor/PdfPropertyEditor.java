package com.github.taskid.pdfpropertyeditor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

public class PdfPropertyEditor extends JFrame {

    private static File backupDir;

    public static void main(String[] args) {
        try {
            backupDir = new File(new File(System.getProperty("java.io.tmpdir")), "PdfPropertyEditor/backup/");
            backupDir.mkdirs();

            try {
                File configFile;
                if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                    configFile = new File(System.getenv("APPDATA") + "/PdfPropertyEditor/config.ini");
                } else {
                    configFile = new File("/home/" + System.getProperty("user.name") + "/.config/PdfPropertyEditor/config.ini");
                }

                Properties properties = new Properties();
                if (!configFile.exists()) {
                    configFile.getParentFile().mkdirs();
                    properties.setProperty("theme_class", "com.formdev.flatlaf.FlatIntelliJLaf");
                    try (FileWriter writer = new FileWriter(configFile)) {
                        properties.store(writer, "");
                    }
                } else {
                    try (FileReader reader = new FileReader(configFile)) {
                        properties.load(reader);
                    }
                }

                UIManager.setLookAndFeel(properties.getProperty("theme_class"));
            } catch (Exception e) {
                System.err.println("Cannot set theme: " + e.getMessage());
            }

            new PdfPropertyEditor();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error creating Temp dir.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss", Locale.US).withZone(ZoneId.systemDefault());


    private JPanel panel;

    private JButton chooseButton;
    private JLabel currentFileLabel;

    private JTextField titleInput;
    private JTextField subjectInput;
    private JTextField authorsInput;
    private JTextField producerInput;
    private JTextField creatorInput;
    private JTextField keywordsInput;
    private JTextField modificationDateInput;
    private JTextField creationDateInput;
    private JComboBox<String> trappedComboBox;

    private JButton saveButton;
    private JButton saveAsButton;
    private JButton encryptionButton;
    private JLabel creationDateLabel;
    private JLabel modificationDateLabel;

    private final EncryptionDialog encryptionDialog;

    private File currentFile = null;
    private PDDocument document = null;

    public PdfPropertyEditor() {

        this.setTitle("PDF Property Editor");
        this.setPreferredSize(new Dimension(500, 450));
        this.setContentPane(panel);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.encryptionDialog = new EncryptionDialog();
        this.encryptionDialog.setLocationRelativeTo(this.panel);
        this.encryptionDialog.setModal(true);

        if (JOptionPane.showOptionDialog(this.panel, "Use this application at your own risk. Do not trust the encryption. It cannot be guaranteed\nthat files will not be corrupted or that the encryption is working properly.\n\nFile backups are in the OS Temp folder (\"PdfPropertyEditor/backup\").", "Disclaimer", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"OK, I use it at my own risk", "Exit"}, "Exit") != 0) {
            System.exit(0);
            return;
        }

        this.chooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select PDF File");
            chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
            if (chooser.showOpenDialog(panel) == JFileChooser.APPROVE_OPTION) {
                currentFile = chooser.getSelectedFile();
                loadFile();
            }
        });

        this.encryptionButton.addActionListener(e -> {
            this.encryptionDialog.setVisible(true);
        });

        this.saveButton.addActionListener(e -> {
            saveFile(this.currentFile);
        });

        this.saveAsButton.addActionListener(e -> showSaveAsDialog());

        this.creationDateInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(creationDateInput, creationDateLabel);
            }
        });
        this.modificationDateInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyReleased(modificationDateInput, modificationDateLabel);
            }
        });
    }

    private void showSaveAsDialog() {
        if (this.document == null) return;

        JFileChooser saveChooser = new JFileChooser(this.currentFile == null || this.currentFile.getParentFile() == null ? null : this.currentFile.getParentFile());
        saveChooser.addChoosableFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        if (saveChooser.showSaveDialog(this.panel) == JFileChooser.APPROVE_OPTION) {
            File file = saveChooser.getSelectedFile();
            if (!file.getName().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }
            if (file.isDirectory()) {
                JOptionPane.showMessageDialog(this.panel, file.getName() + " is a folder", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            saveFile(file);
        }
    }

    private void loadFile() {
        if (this.currentFile == null || !this.currentFile.exists() || !this.currentFile.getName().endsWith(".pdf")) {
            JOptionPane.showMessageDialog(panel, "Invalid file", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String password = null;
        do {
            try {
                document = PDDocument.load(this.currentFile, password);
                break;
            } catch (InvalidPasswordException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this.panel, "Invalid/Corrupt PDF file\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } while ((password = passwordDialog()) != null);

        if (document == null) {
            return;
        }

        PDDocumentInformation info = document.getDocumentInformation();
        titleInput.setText(info.getTitle());
        subjectInput.setText(info.getSubject());
        authorsInput.setText(info.getAuthor());
        producerInput.setText(info.getProducer());
        creatorInput.setText(info.getCreator());
        keywordsInput.setText(info.getKeywords());
        if (info.getTrapped() == null) {
            trappedComboBox.setSelectedIndex(0);
        } else {
            trappedComboBox.setSelectedItem(info.getTrapped());
        }
        if (info.getCreationDate() != null) {
            creationDateInput.setText(FORMATTER.format(info.getCreationDate().toInstant()));
        }
        if (info.getModificationDate() != null) {
            modificationDateInput.setText(FORMATTER.format(info.getModificationDate().toInstant()));
        }

        currentFileLabel.setText(currentFile.getAbsolutePath());


        encryptionDialog.encryptFileCheckBox.setSelected(document.isEncrypted());
        encryptionDialog.userPasswordInput.setText("");
        encryptionDialog.ownerPasswordInput.setText("");

        AccessPermission perms = document.isEncrypted() ? new AccessPermission(document.getEncryption().getPermissions()) : new AccessPermission();
        encryptionDialog.canAssembleDocumentCheckBox.setSelected(perms.canAssembleDocument());
        encryptionDialog.canExtractContentCheckBox.setSelected(perms.canExtractContent());
        encryptionDialog.canExtractForAccessibilityCheckBox.setSelected(perms.canExtractForAccessibility());
        encryptionDialog.canFillInFormCheckBox.setSelected(perms.canFillInForm());
        encryptionDialog.canModifyCheckBox.setSelected(perms.canModify());
        encryptionDialog.canModifyAnnotationsCheckBox.setSelected(perms.canModifyAnnotations());
        encryptionDialog.canPrintCheckBox.setSelected(perms.canPrint());
        encryptionDialog.canPrintFaithfulCheckBox.setSelected(perms.canPrintFaithful());

        creationDateLabel.setForeground(null);
        modificationDateLabel.setForeground(null);
    }

    private void saveFile(File file) {
        if (document == null) return;

        PDDocumentInformation info = document.getDocumentInformation();
        info.setTitle(titleInput.getText());
        info.setSubject(subjectInput.getText());
        info.setAuthor(authorsInput.getText());
        info.setProducer(producerInput.getText());
        info.setKeywords(keywordsInput.getText());
        info.setTrapped(trappedComboBox.getSelectedIndex() == 0 ? null : (String) trappedComboBox.getSelectedItem());

        try {
            if (creationDateInput.getText().length() != 0) {
                Calendar creation = Calendar.getInstance();
                creation.setTime(Date.from(LocalDateTime.parse(creationDateInput.getText(), FORMATTER).atZone(ZoneId.systemDefault()).toInstant()));
                info.setCreationDate(creation);
            }
            if (modificationDateInput.getText().length() != 0) {
                Calendar modify = Calendar.getInstance();
                modify.setTime(Date.from(LocalDateTime.parse(modificationDateInput.getText(), FORMATTER).atZone(ZoneId.systemDefault()).toInstant()));
                info.setModificationDate(modify);
            }
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(panel, "Invalid date format:\n" + ex.getParsedString() + "\n\n(DD.MM.YYYY HH:MM:SS)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        if (encryptionDialog.encryptFileCheckBox.isSelected()) {
            if (encryptionDialog.userPasswordInput.getPassword().length != 0 && encryptionDialog.ownerPasswordInput.getPassword().length == 0) {
                JOptionPane.showOptionDialog(panel, "You cannot set an user password without setting an owner password.", "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"Close"}, "Close");
                return;
            }
            if (encryptionDialog.userPasswordInput.getPassword().length == 0 && encryptionDialog.ownerPasswordInput.getPassword().length == 0) {
                if (JOptionPane.showOptionDialog(panel, "Document encryption is turned on,\nbut no password is given. The file will\nbe encrypted without password.", "Warning", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Continue", "Cancel"}, "Cancel") != 0) {
                    return;
                }
            }
            try {
                AccessPermission accessPermission = new AccessPermission();
                accessPermission.setCanPrint(encryptionDialog.canPrintCheckBox.isSelected());
                accessPermission.setCanPrintFaithful(encryptionDialog.canPrintFaithfulCheckBox.isSelected());
                accessPermission.setCanModify(encryptionDialog.canModifyCheckBox.isSelected());
                accessPermission.setCanModifyAnnotations(encryptionDialog.canModifyAnnotationsCheckBox.isSelected());
                accessPermission.setCanFillInForm(encryptionDialog.canFillInFormCheckBox.isSelected());
                accessPermission.setCanExtractContent(encryptionDialog.canExtractContentCheckBox.isSelected());
                accessPermission.setCanExtractForAccessibility(encryptionDialog.canExtractForAccessibilityCheckBox.isSelected());
                accessPermission.setCanAssembleDocument(encryptionDialog.canAssembleDocumentCheckBox.isSelected());

                StandardProtectionPolicy protectionPolicy = new StandardProtectionPolicy(new String(encryptionDialog.ownerPasswordInput.getPassword()), new String(encryptionDialog.userPasswordInput.getPassword()), accessPermission);
                protectionPolicy.setEncryptionKeyLength(256);
                document.protect(protectionPolicy);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Failed to protect file:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            if (document.isEncrypted()) {
                document.setAllSecurityToBeRemoved(true);
            }
        }

        if (file.exists()) {
            int replaceSelection = JOptionPane.showConfirmDialog(this.panel, "Replace " + file.getName() + "?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            System.out.println(replaceSelection);
            if (replaceSelection == 1) {
                showSaveAsDialog();
            }
            if (replaceSelection != 0) {
                return;
            }
        }

        try {
            FileUtils.copyFile(this.currentFile, new File(backupDir, new SimpleDateFormat("ddMMyyyy-HHmmss").format(new Date()) + "_" + file.getName()), true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.panel, "Failed to create file backup:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            document.setDocumentInformation(info);
            document.save(file);

            int result = JOptionPane.showOptionDialog(this.panel, "Saved file as:\n" + file.getAbsolutePath(), "Success", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Open File", "Open Folder", "Close"}, "Close");
            if (result == 0) {
                openFile(file);
            } else if (result == 1) {
                if (System.getProperty("os.name").toLowerCase().startsWith("windows")) {
                    try {
                        Runtime.getRuntime().exec("explorer.exe /select,\"" + file.getAbsolutePath() + "\"");
                        return;
                    } catch (Exception ignored) {
                    }
                }
                openFile(file.getParentFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (JOptionPane.showOptionDialog(this.panel, "Failed to edit document:\n" + e.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{"OK", "Open Backup Folder"}, "Open Backup Folder") == 1) {
                openFile(backupDir);
            }
        }
    }

    private void openFile(File file) {
        try {
            Desktop.getDesktop().open(file);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.panel, "Cannot open file.\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String passwordDialog() {
        JPanel panel = new JPanel();
        JPasswordField pass = new JPasswordField(16);
        panel.add(new JLabel("Password:"));
        panel.add(pass);
        int option = JOptionPane.showOptionDialog(this.panel, panel, "File is password protected.", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Open", "Cancel"}, null);
        if (option == 0) {
            return new String(pass.getPassword());
        } else {
            return null;
        }
    }

    private void handleKeyReleased(JTextField textField, JLabel label) {
        try {
            if (textField.getText().length() > 0) {
                LocalDateTime.parse(textField.getText(), FORMATTER);
            }
            label.setForeground(null);
        } catch (DateTimeParseException ex) {
            label.setForeground(Color.RED);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel = new JPanel();
        panel.setLayout(new GridLayoutManager(4, 1, new Insets(12, 12, 12, 12), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        chooseButton = new JButton();
        chooseButton.setText("Choose");
        panel1.add(chooseButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        currentFileLabel = new JLabel();
        currentFileLabel.setText("");
        panel1.add(currentFileLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel1.add(spacer1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(9, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Title");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        titleInput = new JTextField();
        panel2.add(titleInput, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Subject");
        panel2.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        subjectInput = new JTextField();
        panel2.add(subjectInput, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        authorsInput = new JTextField();
        panel2.add(authorsInput, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Author");
        panel2.add(label3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        producerInput = new JTextField();
        panel2.add(producerInput, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Producer");
        panel2.add(label4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        creatorInput = new JTextField();
        panel2.add(creatorInput, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Creator");
        panel2.add(label5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keywordsInput = new JTextField();
        panel2.add(keywordsInput, new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Keywords");
        panel2.add(label6, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        creationDateLabel = new JLabel();
        creationDateLabel.setText("Creation Date");
        panel2.add(creationDateLabel, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modificationDateLabel = new JLabel();
        modificationDateLabel.setText("Modify Date");
        panel2.add(modificationDateLabel, new GridConstraints(8, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        creationDateInput = new JTextField();
        panel2.add(creationDateInput, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        modificationDateInput = new JTextField();
        panel2.add(modificationDateInput, new GridConstraints(8, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Trapped");
        panel2.add(label7, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        trappedComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("Unknown (NULL)");
        defaultComboBoxModel1.addElement("True");
        defaultComboBoxModel1.addElement("False");
        trappedComboBox.setModel(defaultComboBoxModel1);
        panel2.add(trappedComboBox, new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_SOUTH, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveAsButton = new JButton();
        saveAsButton.setText("Save As");
        panel3.add(saveAsButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        panel3.add(saveButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        encryptionButton = new JButton();
        encryptionButton.setText("Encryption");
        panel3.add(encryptionButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}
