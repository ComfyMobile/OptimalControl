package edu.main;

import edu.genetic.ControlFitness;
import edu.genetic.ControlMutation;
import edu.genetic.ControlPopulations;
import form.MainForm;
import integrators.RungeKutt;
import matrix.Matrix;
import matrix.operations.Sub2;
import ru.javainside.genetic.system.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author Grinch
 * Date: 05.03.2015
 * Time: 16:34
 */
public class GrinkoSimulator {

    public List<Matrix> calc(Matrix init){
        GrinkoModel model = new GrinkoModel(init,1);
        new RungeKutt(model,0,40,1).integrate();
        return model.getX();
    }

    public void draw(){
        Matrix endXPsi = new Matrix(new double[][]{
                {600},{0},{0},{0},{0},{0},{0},{0},{0},{1},{0}
        });
        Matrix initXPsi = new Matrix(new double[][]{
                {0},                   //0 x
                {500},                 //1 y
                {0},                   //2 Vx
                {0},                   //3 Vy
                {500},                 //4 m
                {0.02483268384954118},//5 psi1
                {-0.2154620916108404}, //6 psi2
                {0.20123802653598644},   //7 psi3
                {-0.17191356051854947}, //8 psi4
                {0.008188465524382512}, //9 psi5
                {Double.NaN}           //10 teta
        });

        List<Matrix> model = calc(initXPsi);

        Matrix delta = Sub2.calc(model.get(model.size() - 1), endXPsi);

        MainForm mainForm = new MainForm();
        mainForm.addGraphic("X",MainForm.drawChartT("X",model,0,1));
        mainForm.addGraphic("Y",MainForm.drawChartT("Y",model,1,1));
        mainForm.addGraphic("Vx",MainForm.drawChartT("Vx",model,2,1));
        mainForm.addGraphic("Vy",MainForm.drawChartT("Vy",model,3,1));
        mainForm.addGraphic("m",MainForm.drawChartT("m",model,4,1));

        mainForm.addGraphic("psi1",MainForm.drawChartT("psi1",model,5,1));
        mainForm.addGraphic("psi2",MainForm.drawChartT("psi2",model,6,1));
        mainForm.addGraphic("psi3",MainForm.drawChartT("psi3",model,7,1));
        mainForm.addGraphic("psi4",MainForm.drawChartT("psi4",model,8,1));
        mainForm.addGraphic("psi5",MainForm.drawChartT("psi5",model,9,1));

        mainForm.addGraphic("teta",MainForm.drawChartT("teta",model,10,1));
        mainForm.setVisible(true);
    }


    public static void main(String[] args) {
        new GrinkoSimulator().draw();
        PersonFactory simpleFactory = new SimpleFactory();
        List<Person> initPersons = new ArrayList<Person>();
        for (int i = 0; i < 100; i++){
            initPersons.add(simpleFactory.revivePerson(new ControlMutation(),
                                                        new ControlFitness(),
                                                        new SimpleCrossover(),
                                                        1.,1.,5,true));
        }

        Person person = initPersons.get(0);
        try {
            ControlPopulations controlPopulations = new ControlPopulations(new Population(initPersons),simpleFactory);
            for (int i = 0; i < 1E4; i++) {
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
}

