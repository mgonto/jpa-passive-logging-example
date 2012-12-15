package models.observer;

import java.util.List;

import models.ExampleModel;
import models.observer.Event.Action;

import com.google.common.collect.Lists;

public class TestObserver implements Observer<ExampleModel> {

    public List<Event> events = Lists.newArrayList();

    @Override
    public boolean handles(Class<?> clazz) {
        return clazz.equals(ExampleModel.class);
    }

    @Override
    public void notifyChange(ExampleModel model, Action action, String field, Object previousValue,
            Object newValue) {
        Event event = new Event(model, action, field, ToStringHelper.toString(previousValue),
                ToStringHelper.toString(newValue), null, null);
        events.add(event);

    }

}
