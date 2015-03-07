package edu.genetic;

import ru.javainside.genetic.system.Chromosome;
import ru.javainside.genetic.system.Mutation;
import ru.javainside.genetic.system.Person;

import java.util.Random;

/**
 * Author Grinch
 * Date: 05.03.2015
 * Time: 17:40
 */
public class ControlMutation implements Mutation {
    Random r = new Random();

    @Override
    public void mutation(Person person) {
        double per = person.getFitness()/1000.;
        Chromosome<Double> c = person.getChromosome();
        for (int i = 0; i < c.getSize(); i++){
            if (r.nextBoolean()){
                double z = r.nextBoolean()?-1.:1.;
                c.setGene(i, c.getGene(i) + r.nextDouble()*(c.getGene(i)/per)*z);
            }
        }
    }
}
