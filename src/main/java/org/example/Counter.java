package org.example;

import javax.swing.*;

public class Counter extends JFrame {
    Counter() {
        super("Zähler");



        setSize(800, 600);
        setVisible(true);
    }
    public static void main(String[] args) {
        new Counter();
    }
}