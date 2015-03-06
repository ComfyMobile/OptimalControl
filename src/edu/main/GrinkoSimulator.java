package edu.main;

import edu.genetic.ControlFactory;
import edu.genetic.ControlFitness;
import edu.genetic.ControlMutation;
import edu.genetic.ControlPopulations;
import form.MainForm;
import integrators.RungeKutt;
import matrix.Matrix;
import ru.javainside.genetic.system.GeneticUtils;
import ru.javainside.genetic.system.Person;
import ru.javainside.genetic.system.Population;
import ru.javainside.genetic.system.SimpleCrossover;

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
        new RungeKutt(model,0,500,1).integrate();
        return model.getX();
    }

    public void draw(){
        Matrix initXPsi = new Matrix(new double[][]{
                {0},   //0 x
                {500},//1 y
                {0},  //2 Vx
                {0}, //3 Vy
                {500}, //4 m
                {0.029689713470413312},   //5 psi1
                {-0.2134202317299473},   //6 psi2
                {0.235492129187748},   //7 psi3
                {-0.1645852225066035},   //8 psi4
                {0.04754434302302391},   //9 psi5
                {Double.NaN}    //10 teta
        });

        List<Matrix> model = calc(initXPsi);

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
        ControlFactory controlFactory = new ControlFactory();
        List<Person> initPersons = new ArrayList<Person>();
        for (int i = 0; i < 20; i++){
            initPersons.add(controlFactory.revivePerson(new ControlMutation(),
                                                        new ControlFitness(),
                                                        new SimpleCrossover(),
                                                        1.));
        }

        Person person = initPersons.get(0);
        try {
            ControlPopulations controlPopulations = new ControlPopulations(new Population(initPersons),controlFactory);
            for (int i = 0; i < 1E5; i++) {
                Person bestPerson = GeneticUtils.getBestPerson(controlPopulations.getLastPopulation());
                if (bestPerson.fitness() < person.fitness()) {
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
}

