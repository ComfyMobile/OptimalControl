package edu.genetic;

import javafx.util.Pair;
import ru.javainside.genetic.system.Person;
import ru.javainside.genetic.system.PersonFactory;
import ru.javainside.genetic.system.Population;
import ru.javainside.genetic.system.Populations;

import java.util.*;

/**
 * Author Grinch
 * Date: 05.03.2015
 * Time: 17:49
 */
public class ControlPopulations extends Populations {

    public ControlPopulations(Population firstPopulation, PersonFactory factory) {
        super(firstPopulation, factory);
    }

    @Override
    public HashMap<Person,Double> getFitnessPercentage() {
        Population p = getLastPopulation();
        List<Person> persons = p.getPopulation();
        double s = 0;
        for (Person person : persons){
            s += 1./person.getFitness();
        }
        HashMap<Person,Double> percents = new HashMap<Person,Double>();
        for (Person person : p.getPopulation()){
            percents.put(person, (1./person.getFitness())/s);
        }
        return percents;
    }

    @Override
    public Pair<Person, Person> getParents() {
        Random r = new Random();
        HashMap<Person,Double> percents = getFitnessPercentage();
        Population population = getLastPopulation();
        List<Person> persons = population.getPopulation();
        //Collections.sort(persons, new PersonComparator());
        Person p1 = null;
        Person p2 = null;
        int index = r.nextInt(persons.size());
        while (p1 == null){
            Person person = persons.get(index);
            if (r.nextDouble() < percents.get(person)){
                p1 = person;
            }
            index = r.nextInt(persons.size());
        }
        index = r.nextInt(persons.size());
        while (p2 == null){
            Person person = persons.get(index);
            if (person != p1 && r.nextDouble() < percents.get(person)){
                p2 = person;
            }
            index = r.nextInt(persons.size());
        }
        return new Pair<Person, Person>(p1,p2);
    }

    @Override
    public void nextGen() {
        List<Person> persons = new ArrayList<Person>();
        for (int i = 0; i < getLastPopulation().getPopulation().size(); i++){
            Pair<Person,Person> pair = getParents();
            Person newPerson = pair.getKey().reproduction(pair.getValue(), getFactory());
            persons.add(newPerson);
        }
        addPopulation(new Population(persons));
    }
}
