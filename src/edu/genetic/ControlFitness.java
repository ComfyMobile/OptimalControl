package edu.genetic;

import edu.main.GrinkoSimulator;
import matrix.Matrix;
import matrix.operations.Sub2;
import ru.javainside.genetic.system.Chromosome;
import ru.javainside.genetic.system.FitnessFunction;
import ru.javainside.genetic.system.Person;

import java.util.List;

/**
 * Author Grinch
 * Date: 05.03.2015
 * Time: 17:51
 */
public class ControlFitness implements FitnessFunction {
    Matrix endXPsi = new Matrix(new double[][]{
            {600},{0},{0},{0},{0},{0},{0},{0},{0},{1},{0}
    });


    @Override
    public double getFitness(Person person) {
        GrinkoSimulator simulator = new GrinkoSimulator();
        Chromosome<Double> chromosome = person.getChromosome();
        Matrix initXPsi = new Matrix(new double[][]{
                {0},   //0 x
                {500},//1 y
                {0},  //2 Vx
                {0}, //3 Vy
                {500}, //4 m
                {chromosome.getGene(0)},   //5 psi1
                {chromosome.getGene(1)},   //6 psi2
                {chromosome.getGene(2)},   //7 psi3
                {chromosome.getGene(3)},   //8 psi4
                {chromosome.getGene(4)},   //9 psi5
                {Double.NaN}    //10 teta
        });
        List<Matrix> result = simulator.calc(initXPsi);

        Matrix delta = Sub2.calc(result.get(result.size()-1),endXPsi);
        double d = Math.pow(delta.getData(0,0),2) + Math.pow(delta.getData(1,0),2) + Math.pow(delta.getData(2,0),2) +
                   Math.pow(delta.getData(3,0),2) + Math.pow(delta.getData(9,0),2);
        return d;
    }
}
