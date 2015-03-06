package edu.genetic;

import ru.javainside.genetic.system.Person;
import ru.javainside.genetic.system.PersonFactory;

/**
 * Author Grinch
 * Date: 05.03.2015
 * Time: 17:45
 */
public class ControlFactory extends PersonFactory {
    @Override
    protected Person createPerson(Object... objects) {
        switch (objects.length){
            case 0: return new ControlPerson(0,0);
            case 1: return new ControlPerson((Double)objects[0], (Double)objects[0]);
            case 2: return new ControlPerson((Double)objects[0], (Double)objects[1]);
            default: return new ControlPerson(0,0);
        }
    }
}
