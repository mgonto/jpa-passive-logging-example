package models.observer;

import java.lang.reflect.Field;
import java.util.Map;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import com.google.common.collect.Maps;

import play.db.jpa.Model;

/**
 * This is the base class for every model that is going to be observed and audited
 *
 *
 * @author Gonto
 * @since Dec 11, 2012
 */
@MappedSuperclass
@EntityListeners(AuditableModelListener.class)
public class AuditableModel extends Model {

    @Transient
    public transient Map<String, Object> loadedValues;

    @Override
    public String toString() {
        return new ReflectionToStringBuilder(this).toString();
    }

}
