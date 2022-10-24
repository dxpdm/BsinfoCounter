package org.example;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

abstract class LimitedTextField extends JTextField {
    LimitedTextField(String text) {
        super(text);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setEditable(check(e.getKeyCode()));
            }
        });
    }

    abstract boolean check(int c);
}

class IntegerTextField extends LimitedTextField {
    IntegerTextField(String text) {
        super(text);
    }

    public boolean check(int input) {
        return input >= '0' && input <= '9' || input == KeyEvent.VK_BACK_SPACE;
    }
}

class DoubleTextField extends LimitedTextField {
    DoubleTextField(String text) {
        super(text);
    }

    public boolean check(int input) {
        return input >= '0' && input <= '9' || input == '.' || input == ',' || input == KeyEvent.VK_BACK_SPACE;
    }
}