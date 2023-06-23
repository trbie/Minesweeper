package minesweeper;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

public abstract class DelayPrompt extends JPanel {
    private JSlider slider;
    private int value;

    public DelayPrompt(int defaultValue) {
        setLayout(new MigLayout());

        value = defaultValue;
        JLabel label = new JLabel(formatValue(value));

        slider = new JSlider(JSlider.HORIZONTAL, 0, 5000, value);
        slider.setMajorTickSpacing(250);
        slider.setSnapToTicks(true);
        slider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (slider.getValueIsAdjusting()) {
                    label.setText(formatValue(slider.getValue()));
                } else {
                    value = slider.getValue();
                    valueChanged();
                }
            }
        });

        add(label, "wrap");
        add(slider);
    }

    public int getValue() { return value; }

    public abstract void valueChanged();

    private String formatValue(int val) {
        String text = "";
        double seconds = val / 1000.0;

        if (seconds % 1 == 0) text += (int)seconds;
        else text += seconds;

        if (seconds == 1) text += " second";
        else text += " seconds";

        return text;
    }
}
