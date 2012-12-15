package models.observer;

import java.util.Set;

import models.observer.Event.Action;

import com.google.common.collect.Sets;

/**
 * This is the registry for all of the observers
 *
 *
 * @author Gonto
 * @since Dec 11, 2012
 */
public class ObserverRegistry {

    private static Set<Observer<?>> observers = Sets.newHashSet();

    public static void notifyChange(Object model, Action action, String field, Object previousValue,
            Object newValue) {
        for (Observer observer : observers) {
            if (observer.handles(model.getClass())) {
                observer.notifyChange(model, action, field, previousValue, newValue);
            }
        }

    }

    public static void register(Observer<?> observer) {
        observers.add(observer);
    }

    public static void deregister(Observer<?> observer) {
        observers.remove(observer);
    }

}
