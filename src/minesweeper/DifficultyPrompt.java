package minesweeper;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;

import net.miginfocom.swing.MigLayout;

public class DifficultyPrompt extends JDialog {
	private Difficulty customDifficulty;
	
    public DifficultyPrompt() {
    	customDifficulty = null;
    	
    	setLayout(new MigLayout("fill"));
    	
    	NumberFormat intFormat = NumberFormat.getInstance();
        NumberFormatter intFormatter = new NumberFormatter(intFormat);
        intFormatter.setValueClass(Integer.class);
        intFormatter.setMinimum(1);
        intFormatter.setMaximum(Integer.MAX_VALUE);
        intFormatter.setAllowsInvalid(true);
        intFormatter.setCommitsOnValidEdit(true);
        
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        NumberFormatter percentFormatter = new NumberFormatter(percentFormat);
        percentFormatter.setValueClass(Double.class);
        percentFormatter.setMinimum(1);
        percentFormatter.setMaximum(100);
        percentFormatter.setAllowsInvalid(true);
        percentFormatter.setCommitsOnValidEdit(true);
        
    	
    	JPanel sizeGroup = new JPanel(new MigLayout("insets 2"));
    	sizeGroup.setBackground(Color.WHITE);
    	sizeGroup.setBorder(BorderFactory.createTitledBorder("Size"));  
    	
    	sizeGroup.add(new JLabel("Width:"));
    	JFormattedTextField widthTextField = new JFormattedTextField(intFormatter);
    	widthTextField.setValue(9);
    	sizeGroup.add(widthTextField, "wrap");
    	
    	sizeGroup.add(new JLabel("Height:"));
    	JFormattedTextField heightTextField = new JFormattedTextField(intFormatter);
    	heightTextField.setValue(9);
    	sizeGroup.add(heightTextField, "wrap");
    	
    	add(sizeGroup);
    	
    	JPanel bombGroup = new JPanel(new MigLayout("insets 2"));
    	bombGroup.setBackground(Color.WHITE);
    	bombGroup.setBorder(BorderFactory.createTitledBorder("Bombs"));  
    	
    	bombGroup.add(new JLabel("Amount:"));
    	JFormattedTextField amountTextField = new JFormattedTextField(intFormatter);
    	amountTextField.setValue(10);
    	bombGroup.add(amountTextField, "wrap");
    	
    	bombGroup.add(new JLabel("Percent:"));
    	JFormattedTextField percentTextField = new JFormattedTextField(percentFormatter);
    	percentTextField.setValue(0.12);
    	percentTextField.setEnabled(false);
    	bombGroup.add(percentTextField, "wrap");
    	
    	add(bombGroup, "wrap");
    	
    	JPanel buttonGroup = new JPanel(new MigLayout("insets 0"));
    	buttonGroup.setBackground(Color.WHITE);
    	
    	JButton okBtn = new JButton("Ok");
    	okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Difficulty d = new Difficulty("Custom", 
					Integer.parseInt(amountTextField.getText().replaceAll(",", "")),
					Integer.parseInt(widthTextField.getText().replaceAll(",", "")),
					Integer.parseInt(heightTextField.getText().replaceAll(",", "")));
				
				if (d.bombs >= d.width * d.height) {
					JOptionPane.showMessageDialog(okBtn, "Amount of bombs has to be less than the amount of tiles", getTitle(), JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				customDifficulty = d;
				setVisible(false);
				dispose();
			}
    	});
    	buttonGroup.add(okBtn, "tag ok");
    	
    	JButton cancelBtn = new JButton("Cancel");
    	cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
    	});
    	buttonGroup.add(cancelBtn, "tag cancel");
    	
    	add(buttonGroup, "newline, wrap, span 2, right");
    	
    	getContentPane().setBackground(Color.WHITE);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setModal(true);
		setTitle("Custom Difficulty");
        setVisible(true);
    }
    
    public Difficulty getDifficulty() {
    	return customDifficulty;
    }
}
