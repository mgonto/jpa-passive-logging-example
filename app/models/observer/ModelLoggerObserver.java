package models.observer;

import jobs.EventLoggerJob;
import models.observer.Event.Action;

/**
 * This observer logs changes to a certain model
 *
 *
 * @author Gonto
 * @since Dec 11, 2012
 */
public class ModelLoggerObserver implements Observer<AuditableModel> {

    @Override
    public boolean handles(Class<?> clazz) {
        return AuditableModel.class.isAssignableFrom(clazz);
    }

    @Override
    public void notifyChange(AuditableModel model, Action action, String field, Object previousValue,
            Object newValue) {

        //Mock current user getting
        //You would get this by using Security.connectedUser() or something like that
        Long userId = 5L;

        //Mock account geting
        Long accountId = 5L;
        Event event = new Event(model, action, field, ToStringHelper.toString(previousValue),
                ToStringHelper.toString(newValue), userId, accountId);
        //We need to save the Event to the database in another thread
        //As otherwise, this will be a StackoverflowEx
        new EventLoggerJob(event).now();
    }
}
