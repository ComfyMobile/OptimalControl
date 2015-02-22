package form;

import javax.swing.*;
import java.awt.*;

/**
 * Author Grinch
 * Date: 25.11.2014
 * Time: 18:25
 */
public class MainForm extends JFrame{
    //private JTabbedPane tabbedPane1;
    private int tabsCount;
    private JPanel panel1;
    private JTabbedPane tabbedPane1;

    public MainForm(){
        super("Лабораторная работа");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().add(panel1);
        //pack();
        setSize(new Dimension(640, 480));
        setLocationRelativeTo(null);
    }

    public void addGraphic(String name, JComponent content){
        JComponent newPanel = new JPanel();
        newPanel.setLayout(new GridLayout());
        if (content != null){
            newPanel.add(content);
        }else{
            System.out.println("Пустой контент в табе");
        }
        tabbedPane1.addTab("Tab "+(++tabsCount)+": "+name, newPanel);
        getContentPane().removeAll();
        getContentPane().add(panel1);
    }
}
