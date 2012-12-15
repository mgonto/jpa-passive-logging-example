package models.observer;

import models.ExampleModel;

import org.junit.Before;
import org.junit.Test;

import play.db.jpa.JPA;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 * Test end to end Logging operation  
 * 
 * 
 * @author Gonto
 * @since Dec 15, 2012
 */
public class AuditEndToEndTest extends UnitTest {
    
    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        ObserverRegistry.register(new ModelLoggerObserver());
    }
    
    
    @Test
    public void testEndToEnd() throws InterruptedException {
        assertEquals(0, Event.count());
        
        ExampleModel model = new ExampleModel();
        model.age = 55L;
        model.save();
        
        //Let job do the magic
        Thread.sleep(500);
        
        assertEquals(1, Event.count());
        
        //Regular cleaning as you would do after controller transaction
        JPA.em().flush();
        JPA.em().clear();
        
        
        ExampleModel loadedModel = ExampleModel.findById(model.id);
        
        loadedModel.age = 23L;
        
        loadedModel.save();
        
        //Let job do the magic
        Thread.sleep(500);
        assertEquals(2, Event.count());
        
    }

}
