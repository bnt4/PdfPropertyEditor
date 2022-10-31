package com.github.taskid.pdfpropertyeditor;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class EncryptionDialog extends JDialog {

    private JPanel panel;

    public JCheckBox encryptFileCheckBox;
    public JPasswordField userPasswordInput;
    public JPasswordField ownerPasswordInput;

    public JCheckBox canAssembleDocumentCheckBox;
    public JCheckBox canExtractContentCheckBox;
    public JCheckBox canExtractForAccessibilityCheckBox;
    public JCheckBox canFillInFormCheckBox;
    public JCheckBox canModifyCheckBox;
    public JCheckBox canModifyAnnotationsCheckBox;
    public JCheckBox canPrintCheckBox;
    public JCheckBox canPrintFaithfulCheckBox;

    public EncryptionDialog() {
        this.setContentPane(panel);
        this.setTitle("PDF Encryption");
        this.setSize(new Dimension(750, 550));
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
        panel.setLayout(new GridLayoutManager(3, 1, new Insets(4, 12, 12, 12), -1, -1));
        panel.setAutoscrolls(false);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        userPasswordInput = new JPasswordField();
        userPasswordInput.setEchoChar('•');
        userPasswordInput.setEditable(true);
        userPasswordInput.putClientProperty("JPasswordField.cutCopyAllowed", Boolean.TRUE);
        panel1.add(userPasswordInput, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("User Password");
        panel1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Owner Password");
        panel1.add(label2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        ownerPasswordInput = new JPasswordField();
        ownerPasswordInput.putClientProperty("JPasswordField.cutCopyAllowed", Boolean.TRUE);
        panel1.add(ownerPasswordInput, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        encryptFileCheckBox = new JCheckBox();
        encryptFileCheckBox.setText("Encrypt File");
        panel1.add(encryptFileCheckBox, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1, true, false));
        panel.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder(null, "Permissions for Users (Owners always have all)", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        canPrintCheckBox = new JCheckBox();
        canPrintCheckBox.setSelected(true);
        canPrintCheckBox.setText("<html><b>Can Print</b><br> If the user can print the document. </html>");
        panel2.add(canPrintCheckBox, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        canExtractContentCheckBox = new JCheckBox();
        canExtractContentCheckBox.setSelected(true);
        canExtractContentCheckBox.setText("<html><b>Can Extract Content</b><br>\nIf the user can extract content from the document.</html>");
        panel2.add(canExtractContentCheckBox, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        canExtractForAccessibilityCheckBox = new JCheckBox();
        canExtractForAccessibilityCheckBox.setSelected(true);
        canExtractForAccessibilityCheckBox.setText("<html><b>Can Extract for Accessibility</b><br>\nIf the user can extract content from the document for accessibility purposes.</html>");
        panel2.add(canExtractForAccessibilityCheckBox, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        canFillInFormCheckBox = new JCheckBox();
        canFillInFormCheckBox.setSelected(true);
        canFillInFormCheckBox.setText("<html><b>Can Fill In Form</b><br>\nIf the user can fill in interactive form fields (including signature fields), even if \"Can Modify Annotations\" is disabled.\n</html>");
        panel2.add(canFillInFormCheckBox, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        canPrintFaithfulCheckBox = new JCheckBox();
        canPrintFaithfulCheckBox.setSelected(true);
        canPrintFaithfulCheckBox.setText("<html><b>Can Print Faithful</b><br>\nIf the user can print the document in a faithful or in a degraded format (if \"Can Print\" is enabled).\n</html>");
        panel2.add(canPrintFaithfulCheckBox, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        canModifyCheckBox = new JCheckBox();
        canModifyCheckBox.setSelected(true);
        canModifyCheckBox.setText("<html><b>Can Modify</b><br>\nIf the user can modify the document.</html>");
        panel2.add(canModifyCheckBox, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        canModifyAnnotationsCheckBox = new JCheckBox();
        canModifyAnnotationsCheckBox.setSelected(true);
        canModifyAnnotationsCheckBox.setText("<html><b>Can Modify Annotations</b><br>\nIf the user can add or modify text annotations and fill in interactive form fields. Also, if \"Can Modify\" is enabled, the user will be able to create interactive form fields (including signature fields).</html>");
        panel2.add(canModifyAnnotationsCheckBox, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        canAssembleDocumentCheckBox = new JCheckBox();
        canAssembleDocumentCheckBox.setSelected(true);
        canAssembleDocumentCheckBox.setText("<html><b>Can Assemble Document</b><br>\nIf the user can insert/rotate/delete pages.</html>");
        panel2.add(canAssembleDocumentCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel.add(panel3, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("<html>When authenticating with the user password, you'll have the permissions specified above.<br> When authenticating with the owner password, you'll have all permissions, regardless of what is set above.<br> If the user password is empty but the owner password isn't, you'll have the permissions specified above when<br>opening the file with a PDF viewer, because you'll be \"authenticated\" as user, not owner, with no password.</html>");
        panel3.add(label3, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }

}