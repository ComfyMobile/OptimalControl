package edu.tool;

import javafx.collections.transformation.SortedList;
import javafx.util.Pair;
import matrix.Matrix;
import model.AbstractModel;
import optimisation.Function;
import optimisation.GoldenSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nixy on 09.03.2015.
 */
public class GoldenOptimisation {

    public static Matrix calc(  Function f, Matrix X0,double[][] range,  double eps){
        Matrix X = new Matrix(X0);
        double y;
        double dy;
        double prevy = f.calc(X);;
        int n = 0;
        int i = new Random().nextInt(X.getRow());
        do{
            System.out.println("Iteration " + n);
            printMatrix(X);

            double x = X.getData(i,0);
            X.setData(i,0,new GoldenSearch().optimise(f,X,range[i],eps,i)[0]);
            range[i][0] = (range[i][0]-X.getData(i,0))*2;
            range[i][1] = (range[i][1]+X.getData(i,0))*2;


            y = f.calc(X);

            if (y >= prevy){
                X.setData(i,0,x);
                printMatrix(X);
                System.out.println(" y =  " + y + " more than " + prevy);
                i = new Random().nextInt(X.getRow());
            }else {
                printMatrix(X);
                System.out.println(" y =  " + y);
                prevy = y;
            }

            n++;

        }while (y >eps && n < 1e6);
        return X;
    }

    private static void printMatrix(Matrix x) {
        System.out.print("[ ");
        StringBuilder s = new StringBuilder();
        for (int i = 0 ; i < x.getRow(); i++){
            s.append(x.getData(i,0)).append(",");
        }
        s.deleteCharAt(s.lastIndexOf(","));
        System.out.println(s.toString()+"] ");
    }



}
