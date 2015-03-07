package edu.main;

import edu.genetic.ControlPopulations;
import form.MainForm;
import integrators.RungeKutt;
import matrix.Matrix;
import model.AbstractModel;
import ru.javainside.genetic.system.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nixy on 06.03.2015.
 */
public class MihSimulator {

    public static final double STEP = 1e-1;
    public static final double ENDT = 300;

    public static Matrix calc(Matrix init){
        AbstractModel model = new MihModel(init,1);
        new RungeKutt(model,0,ENDT,2).integrate();
        return model.getLast();
    }

    public static void draw(){
        MainForm mainForm = new MainForm();
        MihModel model;
        Matrix init = new Matrix(
                0./*x*/, 6370./*y*/, 0./*vx*/, 0./*vy*/, 1500./*m*/,
                -0.003952904873625426,0.006405754472199271,0.12224549957585386,-0.07718487673899813,0.002303315318029697
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

        mainForm.addGraphic("teta",MainForm.drawChartT("teta",X,10,STEP));
        mainForm.setVisible(true);
    }

    public static void main(String[] args) {
        draw();
        PersonFactory controlFactory = new SimpleFactory();
        List<Person> initPersons = new ArrayList<Person>();
        Chromosome chromosome =  new Chromosome<Double>(new CopyOnWriteArrayList<Double>(new  Double[]{
                -0.004187374610764838,
                0.0062338800504635646,
                0.12949659054251905,
                -0.07950517190584563,
                0.002372556431778326,
                1.6091317134715802E-5,
                -0.021703291422299376,
                -2.3369619281589385E-4}));
        for (int i = 0; i < 20; i++){
            initPersons.add(controlFactory.revivePerson(new MyMutation(),
                    new MyControlFitness(),
                    new SimpleCrossover(),
                    -1.,1.,8));
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
                //System.out.println("Fitness mean: " + GeneticUtils.getFitnessMean(controlPopulations.getLastPopulation()));
                controlPopulations.nextGen();
            }
        }finally {
            System.out.println("Best of the best: "+person);
        }
    }

    private static class MyMutation implements Mutation{

        @Override
        public void mutation(Person person) {
            Random r = new Random();
            double per = person.getFitness()*0.01;
            Chromosome<Double> c = person.getChromosome();
            for (int i = 0; i < c.getSize(); i++){
                if (r.nextBoolean()){
                    double z = r.nextBoolean()?-1.:1.;
                    c.setGene(i, c.getGene(i) + (c.getGene(i)/per)*z);
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
                    {1500/*m*/},
                    {chromosome.getGene(0)/*ps1*/},
                    {chromosome.getGene(1)/*ps2*/},
                    {chromosome.getGene(2)/*ps3*/},
                    {chromosome.getGene(3)/*ps4*/},
                    {chromosome.getGene(4)/*ps5*/},
                    {Math.atan2(1, 0)/*teta*/}
            });
            Matrix X = MihSimulator.calc(initX);
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


            double d1 = Math.sqrt(x*x+y*y)-MihModel.RE -MihModel.RZ;
            double d2 = vx*vx+vy*vy-MihModel.V1*MihModel.V1;
            double d3 = x*vx+y*vy;
            double d4 = ps1-2*x*lam1-vx*lam3;
            double d5 = ps2-2*y*lam1-vy*lam3;
            double d6 = ps3-2*vx*lam2-x*lam3;
            double d7 = ps4-2*vy*lam2-y*lam3;
            double d8 = ps5;
            return Math.sqrt(d1*d1+d2*d2+d3*d3+d4*d4+d5*d5+d6*d6+d7*d7+d8*d8);
        }
    }
}
