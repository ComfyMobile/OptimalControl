package edu.main;


import edu.genetic.ControlFitness;
import edu.genetic.ControlMutation;
import edu.genetic.ControlPopulations;
import edu.tool.GoldenOptimisation;
import form.MainForm;
import integrators.RungeKutt;
import matrix.Matrix;
import model.AbstractModel;
import optimisation.Function;
import ru.javainside.genetic.system.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nixy on 06.03.2015.
 */
public class MihSimulator {

    public static final double STEP = 0.1;
    public static final double ENDT = 300;



    public static void main(String[] args) {
        draw();
        calc();
    }


    public static void calc(){
        PersonFactory controlFactory = new SimpleFactory();
        List<Person> initPersons = new ArrayList<Person>();
        Chromosome chromosome =  new Chromosome<Double>(new CopyOnWriteArrayList<Double>(new  Double[]{
                -4.69609539781137E-9,2.8686153591662652E-11,5.7948765231885016E-8,9.024033036462866E-7,2.2563555875600664E-13,2.476921263347809E-11,3.839605772391448E-17,-3.4590288846744773E-13
        }));
        for (int i = 0; i < 20; i++){
            initPersons.add(controlFactory.revivePerson(new MyMutation(),
                    new MyControlFitness(),
                    new SimpleCrossover(),
                    chromosome));
                    //-40.,40.,8));
        }

        Person person = initPersons.get(0);
        try {
            ControlPopulations controlPopulations = new ControlPopulations(new Population(initPersons),controlFactory);
            for (int i = 0; i < 5E2; i++) {
                Person bestPerson = GeneticUtils.getBestPerson(controlPopulations.getLastPopulation());
                if (bestPerson.getFitness() < person.getFitness()) {
                    person = bestPerson;
                }
                System.out.println("Population " + (i + 1) + ". Best person: " + bestPerson);
                System.out.println("Fitness mean: " + GeneticUtils.getFitnessMean(controlPopulations.getLastPopulation()));
                controlPopulations.nextGen();
            }
        }finally {
            System.out.println("Best of the best: "+person);
        }

    }

    public static void draw(){
        MainForm mainForm = new MainForm();
        MihModel model;
        Matrix init = new Matrix(
                0./*x*/, 6370./*y*/, 0./*vx*/, 0./*vy*/, 600./*m*/,
                -4.69609539781137E-9,2.8686153591662652E-11,5.7948765231885016E-8,9.024033036462866E-7,2.2563555875600664E-13
                ,Math.PI/2
        );

        model = new MihModel(init, STEP);
        new RungeKutt(model, 0, ENDT, STEP).integrate();

        List<Matrix> X = model.getX();
        mainForm.addGraphic("X",MainForm.drawChartT("X",X,0,STEP));
        mainForm.addGraphic("Y",MainForm.drawChartT("Y",X,1,STEP));
        mainForm.addGraphic("Vx",MainForm.drawChartT("Vx",X,2,STEP));
        mainForm.addGraphic("Vy",MainForm.drawChartT("Vy",X,3,STEP));
        mainForm.addGraphic("m",MainForm.drawChartT("m",X,4,STEP));

        mainForm.addGraphic("psi1",MainForm.drawChartT("psi1",X,5,STEP));
        mainForm.addGraphic("psi2",MainForm.drawChartT("psi2",X,6,STEP));
        mainForm.addGraphic("psi3",MainForm.drawChartT("psi3",X,7,STEP));
        mainForm.addGraphic("psi4",MainForm.drawChartT("psi4",X,8,STEP));
        mainForm.addGraphic("psi5",MainForm.drawChartT("psi5",X,9,STEP));
        mainForm.addGraphic("Траектория",MainForm.drawChartXY("Траектория",X,0,X,1,STEP));

        mainForm.addGraphic("teta",MainForm.drawChartT("teta",X,10,STEP));
        mainForm.setVisible(true);
    }



    private static class MyMutation implements Mutation{
        Random r = new Random();
        @Override
        public void mutation(Person person) {

            double per = 40.;
            Chromosome<Double> c = person.getChromosome();
            for (int i = 0; i < c.getSize(); i++){
                if (r.nextBoolean()){
                    double z = r.nextBoolean()?-1.:1.;
                    c.setGene(i, c.getGene(i) + (c.getGene(i)*per/100.)*z);
                    if(r.nextDouble() > 0.3){
                        c.setGene(i,-c.getGene(i));
                    }
                }
                if(c.getGene(i) == 0){
                    c.setGene(i,r.nextDouble());
                }
            }
        }
    }

    private static class MyControlFitness implements FitnessFunction{

        @Override
        public double getFitness(Person person) {
            Chromosome<Double> chromosome = person.getChromosome();
            Matrix initX = new Matrix(new double[][]{
                    {0/*x*/},
                    {6370/*y*/},
                    {0/*vx*/},
                    {0/*vy*/},
                    {1600/*m*/},
                    {chromosome.getGene(0)/*ps1*/},
                    {chromosome.getGene(1)/*ps2*/},
                    {chromosome.getGene(2)/*ps3*/},
                    {chromosome.getGene(3)/*ps4*/},
                    {chromosome.getGene(4)/*ps5*/},
                    {Math.atan2(1, 0)/*teta*/}
            });
            AbstractModel model = new MihModel(initX,1);
            new RungeKutt(model,0,ENDT,STEP).integrate();
            Matrix X = model.getLast();
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
            double lam1 = chromosome.getGene(5);
            double lam2 = chromosome.getGene(6);
            double lam3 = chromosome.getGene(7);

            double r =  Math.sqrt(x*x+y*y)-MihModel.RE;
            double d1 =  r-MihModel.RZ;
            double d2 = vx*vx+vy*vy-MihModel.V1*MihModel.V1;
            double d3 = x*vx+y*vy;
            double d4 = ps1-2*x*lam1-vx*lam3;
            double d5 = ps2-2*y*lam1-vy*lam3;
            double d6 = ps3-2*vx*lam2-x*lam3;
            double d7 = ps4-2*vy*lam2-y*lam3;
            double d8 = ps5;
            return d1*d1+d2*d2+d3*d3+d4*d4+d5*d5+d6*d6+d7*d7+d8*d8;
        }
    }
}
