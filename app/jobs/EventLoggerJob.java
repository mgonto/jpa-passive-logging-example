package jobs;

import models.observer.Event;
import play.jobs.Job;

/**
 * Logs the {@link Event}
 *
 *
 * @author Gonto
 * @since Dec 14, 2012
 */
public class EventLoggerJob extends Job {

    private Event event;

    public EventLoggerJob(Event event) {
        this.event = event;
    }

    @Override
    public void doJob() throws Exception {
        event.save();
    }

}
