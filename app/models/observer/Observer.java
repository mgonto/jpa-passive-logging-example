package models.observer;

import models.observer.Event.Action;

/**
 * An observer
 *
 *
 * @author Gonto
 * @since Dec 11, 2012
 */
public interface Observer<T> {

    /**
     * Wether this observer observes this class
     */
    boolean handles(Class<?> clazz);

    /**
     * Notifies a change
     */
    void notifyChange(T model, Action action, String field, Object previousValue, Object newValue);

}
