package form;

import matrix.Matrix;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

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

    public static JComponent drawChartXY(String title,java.util.List<Matrix> x,int ix,java.util.List<Matrix> y,int iy,double step){
        XYSeries series = new XYSeries("Функция");
        for (int j = 0; j < y.size(); j++) {
            Matrix yM = y.get(j);
            Matrix xM = x.get(j);
            series.add(xM.getData(ix, 0), yM.getData(iy, 0));
        }
        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "x",
                "y",
                collection
        );
        String fontName = "Lucida Sans";
        StandardChartTheme theme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();

        theme.setTitlePaint( Color.decode( "#4572a7" ) );
        theme.setExtraLargeFont( new Font(fontName,Font.PLAIN, 16) ); //title
        theme.setLargeFont( new Font(fontName,Font.BOLD, 15));        //axis-title
        theme.setRegularFont( new Font(fontName,Font.PLAIN, 11));
        theme.setRangeGridlinePaint( Color.decode("#C0C0C0"));
        theme.setPlotBackgroundPaint( Color.white );
        theme.setChartBackgroundPaint( Color.white );
        theme.setGridBandPaint( Color.red );
        theme.setAxisOffset( new RectangleInsets(5,5,5,5) );
        theme.setBarPainter(new StandardBarPainter());
        theme.setAxisLabelPaint( Color.decode("#666666")  );
        theme.apply( chart );
        chart.removeLegend();

        XYPlot plot = (XYPlot)chart.getPlot();
        XYItemRenderer render = plot.getRenderer(0);
        render.setSeriesStroke(0,new BasicStroke(3.0f));
        plot.setRenderer(render);
        ChartPanel panel = new ChartPanel(chart);
        return panel;
    }

    public static JComponent drawChartT(String key, java.util.List<Matrix> y, int i, double step) {
        XYSeries series = new XYSeries("Функция");
        for (int j = 0; j < y.size(); j++) {
            Matrix a = y.get(j);
            series.add(j * step, a.getData(i, 0));
        }

        XYSeriesCollection collection = new XYSeriesCollection();
        collection.addSeries(series);
        JFreeChart chart = ChartFactory.createXYLineChart(
                key,
                "t, сек",
                key,
                collection
        );


        String fontName = "Lucida Sans";
        StandardChartTheme theme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();

        theme.setTitlePaint( Color.decode( "#4572a7" ) );
        theme.setExtraLargeFont( new Font(fontName,Font.PLAIN, 16) ); //title
        theme.setLargeFont( new Font(fontName,Font.BOLD, 15));        //axis-title
        theme.setRegularFont( new Font(fontName,Font.PLAIN, 11));
        theme.setRangeGridlinePaint( Color.decode("#C0C0C0"));
        theme.setPlotBackgroundPaint( Color.white );
        theme.setChartBackgroundPaint( Color.white );
        theme.setGridBandPaint( Color.red );
        theme.setAxisOffset( new RectangleInsets(5,5,5,5) );
        theme.setBarPainter(new StandardBarPainter());
        theme.setAxisLabelPaint( Color.decode("#666666")  );
        theme.apply( chart );
        chart.removeLegend();

        XYPlot plot = (XYPlot)chart.getPlot();
        XYItemRenderer render = plot.getRenderer(0);
        render.setSeriesStroke(0,new BasicStroke(3.0f));
        plot.setRenderer(render);
        ChartPanel panel = new ChartPanel(chart);
        return panel;
    }
}
