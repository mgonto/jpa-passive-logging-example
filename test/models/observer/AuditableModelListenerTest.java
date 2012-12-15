package models.observer;

import java.util.List;

import models.ExampleModel;
import models.observer.Event.Action;

import org.junit.Before;
import org.junit.Test;

import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 * Test for {@link AuditableModel}
 * 
 * 
 * @author Gonto
 * @since Dec 15, 2012
 */
public class AuditableModelListenerTest extends UnitTest {

    private TestObserver observer;

    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        observer = new TestObserver();
        
        ObserverRegistry.register(observer);
    }

    @Test
    public void testLoggerForCreate() {
        List<Event> events = observer.events;
        assertEquals(0, events.size());
        
        new ExampleModel().save();
        
        assertEquals(1, events.size());
        assertEquals(Action.CREATE, events.iterator().next().action);
    }
    
    @Test
    public void testLoggerForDelete() {
        List<Event> events = observer.events;
        assertEquals(0, events.size());
        
        ExampleModel model= new ExampleModel().save();
        
        assertEquals(1, events.size());
        
        model.delete();
        
        assertEquals(2, events.size());
    }
    
    @Test
    public void testLoggerForUpdate() {
        List<Event> events = observer.events;
        assertEquals(0, events.size());
        
        long id = new ExampleModel().<ExampleModel>save().id;
        
        assertEquals(1, events.size());
        
        //Force Clear in order to load this. This will happen in real usage in controller by default
        // With every new transaction
        JPA.em().flush();
        JPA.em().clear();
        
        ExampleModel model = ExampleModel.findById(id);
        
        model.age = 23L;
        model.name = "gonto";
        
        model.save();
        
        assertEquals(3, events.size());
    }
    
    

}
