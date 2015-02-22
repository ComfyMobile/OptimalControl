package edu.main;

import matrix.Matrix;
import model.AbstractModel;

/**
 * Created by Nixy on 22.02.2015.
 */
public class TestModel extends AbstractModel {

    public TestModel(Matrix initCondition, double step) {
        super(initCondition, step);
    }

    @Override
    public boolean isEnough(Object... objects) {
        return false;
    }

    @Override
    public Matrix F(double v, Matrix matrix) {
        return null;
    }
}
