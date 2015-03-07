package edu.main;

import matrix.Matrix;
import model.AbstractModel;
import optimisation.Function;
import optimisation.GoldenSearch;

/**
 * Created by Nixy on 01.03.2015.
 */
public class MihModel extends AbstractModel {
    public static final double RE = 6371;
    public static final double RZ = 200 ;
    private static final double EPS = 0.01;
    private static final double G0 = 9.81*1e-3;
    private static final double J = 788;
    private static final double P = 31.000;
    private static final double M0 = 600;
    public static final double V1 = 8;
    private static double BETA = P/J;

    protected MihModel(Matrix initCondition, double step) {
        super(initCondition, step);
    }

    private double P(double m){
        return m > 0 ? P : 0;
    }

    @Override
    public boolean isEnough(Object... objects) {
        Matrix X = getLast();
        double y = X.getData(1, 0);
        double x = X.getData(0,0);
        double r = Math.sqrt(x*x+y*y)-RE;
        return Math.abs(r - RZ )<0.1 ;
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

                double gx = -G0*x/RE;
                double gy = -G0*y/RE;
                double f = ps1*vx+ps2*vy+ps3*( P(m)/(M0+m)*Math.cos(teta)+gx)+ps4*(P(m)/(M0+m)*Math.sin(teta)+gy)-ps5*BETA;
                return f;
            }
        },X,new double[]{-Math.PI/2,Math.PI/2},EPS,10);
        BETA = P(m)/J;
        teta = opt[0];
    //    teta = Math.atan2(ps3,ps4);

        double gx = -G0*x/RE;
        double gy = -G0*y/RE;

        double dx    =   vx;
        double dy    =   vy;
        double dvx   =   P(m)/(M0+m)*Math.cos(teta)+gx;
        double dvy   =   P(m)/(M0+m)*Math.sin(teta)+gy;
        double dm    =   -BETA;
        double dps1  =   G0/RE*ps3;
        double dps2  =   G0/RE*ps4;
        double dps3  =   -ps1;
        double dps4  =   -ps2;
        double dps5  =   P(m)*Math.pow(M0+m,-2)*(ps3*Math.cos(teta)+ps4*Math.sin(teta));
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

    @Override
    public void setX(Matrix X) {
        double[] opt = new GoldenSearch().optimise(new Function() {
            @Override
            public double calc(Matrix X) {
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
                double gx = -G0*x/RE;
                double gy = -G0*y/RE;
                double f = ps1*vx+ps2*vy+ps3*( P(m)/(M0+m)*Math.cos(teta)+gx)+ps4*(P(m)/(M0+m)*Math.sin(teta)+gy)-ps5*BETA;
                return f;
            }
        },X,new double[]{-Math.PI/2,Math.PI/2},EPS,10);
        X.setData(10,0,opt[0]);
        super.setX(X);
    }
}
