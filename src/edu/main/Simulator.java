package edu.main;

import integrators.RungeKutt;
import matrix.Matrix;
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

    public static void main(String[] args) {
        new Simulator().calc();
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
