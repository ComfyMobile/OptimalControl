package edu.genetic;


import ru.javainside.genetic.system.Chromosome;
import ru.javainside.genetic.system.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author Grinch
 * Date: 05.03.2015
 * Time: 16:53
 */
public class ControlPerson extends Person {
    final int count = 5;
    Random r = new Random();

    public ControlPerson(double min, double max){
        List<Double> init = new ArrayList<Double>();
        boolean z = true;
        for (int i = 0; i < count; i++){
            if (z){
                init.add(new Double(r.nextDouble()*(max-min)+min));
                z = false;
            }else{
                init.add(new Double(-(r.nextDouble()*(max-min)+min)));
                z = true;
            }
        }
        setChromosome(new Chromosome(init));
    }

    @Override
    public int getSeparator() {
        return r.nextInt(count-1)+1;
    }
}
