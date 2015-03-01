package edu.main;

import form.MainForm;
import integrators.RungeKutt;
import matrix.Matrix;
import matrix.operations.Sub2;
import model.AbstractModel;
import optimisation.Function;
import optimisation.GoldenSearch;

/**
 * Created by Nixy on 23.02.2015.
 */
public class Simulator {

    public AbstractModel calc(){
        MyModel myModel;
        Matrix init = new Matrix(new double[][]{
                {0/*x*/},{6370/*y*/},{0/*vx*/},{0/*vy*/},{50000/*m*/},
                {0/*ps1*/},{0/*ps2*/},{1/*ps3*/},{0/*ps4*/},{0/*ps5*/},
                {0/*teta*/},
        });
        myModel = new MyModel(init,1);
        new RungeKutt(myModel,0,288,1).integrate();
        return myModel;
    }

    public void calc2(){
        MainForm mainForm = new MainForm();
        Matrix endPsi = new Matrix(new double[][]{
                {0},{0},{10},{10},{0},{0},{0},{0},{0},{1},{0}
        });
        Matrix init = new Matrix(new double[][]{
                {0},   //x
                {1800},//y
                {0},   //Vx
                {0},   //Vy
                {500}, //m
                {0},   //psi1
                {0},   //psi2
                {0},   //psi3
                {0},   //psi4
                {0},   //psi5
                {0}    //teta
        });
        GrinkoModel model = new GrinkoModel(init,1);
        new RungeKutt(model,0,288,1).integrate();
        Matrix delta = Sub2.calc(model.getLast(),endPsi);
        mainForm.addGraphic("X",MainForm.drawChartT("X",model.getX(),0,1));
        mainForm.addGraphic("Y",MainForm.drawChartT("Y",model.getX(),1,1));
        mainForm.addGraphic("Vx",MainForm.drawChartT("Vx",model.getX(),2,1));
        mainForm.addGraphic("Vy",MainForm.drawChartT("Vy",model.getX(),3,1));
        mainForm.addGraphic("m",MainForm.drawChartT("m",model.getX(),4,1));

        mainForm.addGraphic("psi1",MainForm.drawChartT("psi1",model.getX(),5,1));
        mainForm.addGraphic("psi2",MainForm.drawChartT("psi2",model.getX(),6,1));
        mainForm.addGraphic("psi3",MainForm.drawChartT("psi3",model.getX(),7,1));
        mainForm.addGraphic("psi4",MainForm.drawChartT("psi4",model.getX(),8,1));
        mainForm.addGraphic("psi5",MainForm.drawChartT("psi5",model.getX(),9,1));

        mainForm.addGraphic("teta",MainForm.drawChartT("teta",model.getX(),10,1));
        mainForm.setVisible(true);
    }


    public static void main(String[] args) {
        //new Simulator().calc();
        new Simulator().calc2();
    }

    private class MyModel extends AbstractModel{
        private static final double RE = 6371;
        private static final double RZ = 200 ;
        private static final double EPS = 0.01;
        private static final double G0 = 9.81*1e-3;
        private static final double J = 288;
        private static final double P = 1002.6;
        private static final double M0 = 11000;
        private static final double BETA = P/J;

        protected MyModel(Matrix initCondition, double step) {
            super(initCondition, step);
        }

        @Override
        public boolean isEnough(Object... objects) {
            Matrix X = getLast();
            double x = X.getData(0,0);
            double y = X.getData(1,0);
            double r = Math.sqrt(x*x+y*y)-RE;
            if (getCount() == 200)
                x = X.getData(0,0);
            return Math.abs(r - RZ )<0.5;
        }

        @Override
        public Matrix F(double t, Matrix X) {
            double x = X.getData(0,0);
            double y = X.getData(1,0);
            double vx = X.getData(2,0);
            double vy = X.getData(3,0);
            double m = X.getData(4,0);
            double ps1 = X.getData(5,0);
            double ps2 = X.getData(6,0);
            double ps3 = X.getData(7,0);
            double ps4 = X.getData(8,0);
            double ps5 = X.getData(9,0);
            double teta = X.getData(10,0);

            double[] opt = new GoldenSearch().optimise(new Function() {
                @Override
                public double calc(Matrix X) {
                    double m = X.getData(4,0);
                    double ps3 = X.getData(7,0);
                    double ps4 = X.getData(8,0);
                    double teta = X.getData(10,0);
                    double f = P*(Math.sin(teta)*ps3-Math.cos(teta)*ps4)/(M0+m);
                    return -f;
                }
            },X,new double[]{0,Math.PI},EPS,10);

            teta = opt[0];

            double g = G0*Math.pow(RE /  y,2);
            double gx = g*x/RE;
            double gy = g*(1-2*y/RE);

            double dx    =   vx;
            double dy    =   vy;
            double dvx   =   P/(M0+m)*Math.cos(teta)+gx;
            double dvy   =   P/(M0+m)*Math.sin(teta)+gy;
            double dm    =   -BETA;
            double dps1  =   -G0*RE*ps3*Math.pow(y,-3);
            double dps2  =   2*G0*RE*(ps3*x+3*ps4*y)*Math.pow(y,-3);
            double dps3  =   -ps1;
            double dps4  =   -ps2;
            double dps5  =   P*Math.pow(M0+m,-2)*(ps3*Math.cos(teta)+ps4*Math.sin(teta));
            double dteta =   0;

            X.setData(0,0,dx);
            X.setData(1,0,dy);
            X.setData(2,0,dvx);
            X.setData(3,0,dvy);
            X.setData(4,0,dm);
            X.setData(5,0,dps1);
            X.setData(6,0,dps2);
            X.setData(7,0,dps3);
            X.setData(8,0,dps4);
            X.setData(9,0,dps5);
            X.setData(10,0,dteta);

            return X;
        }
    }
}
