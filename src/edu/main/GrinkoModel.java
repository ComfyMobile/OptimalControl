package edu.main;

import matrix.Matrix;
import model.AbstractModel;

/**
 * Author Grinch
 * Date: 26.02.2015
 * Time: 16:31
 */
public class GrinkoModel extends AbstractModel {
    public final double P = 4753;      //м^2*кг/с
    public final double g0 = 9.81;
    public final double rp = 6371000;
    public final double beta = 1;

    protected GrinkoModel(Matrix initCondition, double step) {
        super(initCondition, step);
    }

    @Override
    public boolean isEnough(Object... objects) {
        Matrix X = getLast();
        double y = X.getData(1,0);
        return  y < 0.5;
    }

    @Override
    public Matrix F(double v, Matrix X) {
        double x = X.getData(0,0);
        double y = X.getData(1,0);
        double Vx = X.getData(2,0);
        double Vy = X.getData(3,0);
        double m = X.getData(4,0);

        double psi1 = X.getData(5,0);
        double psi2 = X.getData(6,0);
        double psi3 = X.getData(7,0);
        double psi4 = X.getData(8,0);
        double psi5 = X.getData(9,0);
        double teta = Math.atan2(psi4, psi3);

        double dx =    Vx;
        double dy =    Vy;
        double dVx =   P/m*Math.cos(teta);
        double dVy =   P/m*Math.sin(teta)-g0*Math.pow(rp/(rp+y),2);
        double dm =    -beta;
        double dPsi1 = 0;
        double dPsi2 = -(psi4*g0*rp*rp)/Math.pow(rp+y,3);
        double dPsi3 = -psi1;
        double dPsi4 = -psi2;
        double dPsi5 = psi3*P*Math.cos(teta)/m/m + psi4*P*Math.sin(teta)/m/m;

        X.setData(0,0,dx);
        X.setData(1,0,dy);
        X.setData(2,0,dVx);
        X.setData(3,0,dVy);
        X.setData(4,0,dm);
        X.setData(5,0,dPsi1);
        X.setData(6,0,dPsi2);
        X.setData(7,0,dPsi3);
        X.setData(8,0,dPsi4);
        X.setData(9,0,dPsi5);
        return X;
    }

    @Override
    public void setX(Matrix X) {
        double psi3 = X.getData(7,0);
        double psi4 = X.getData(8,0);
        double teta = Math.atan2(psi4, psi3);
        X.setData(10,0,teta);
        super.setX(X);
    }
}
