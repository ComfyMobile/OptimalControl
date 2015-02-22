package edu.main;

import form.MainForm;

import javax.swing.*;

/**
 * Author Grinch
 * Date: 22.02.2015
 * Time: 14:27
 */
public class Main {
    public static void main(String[] args){
        MainForm main = new MainForm();
        main.addGraphic("Тест", new JButton("OLOLO"));
        main.setVisible(true);
    }

}
