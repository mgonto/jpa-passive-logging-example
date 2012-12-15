package models.observer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import models.observer.Event.Action;
import play.Logger;
import play.templates.JavaExtensions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * This class has all of the hooks for auditing
 *
 *
 * @author Gonto
 * @since Dec 15, 2012
 */
public class AuditableModelListener {

    public List<String> ignoredFields = Arrays.asList("willbesaved", "id");

    /**
     * After we load the Model into memory, we transform the object to a {@link Map}
     * of PropertyName => Object
     * I save this map in a transient property at the AuditableModel.
     * Then, when we're going to update this object, I'm going to check for the map of loaded values
     * and compare this to the map of the modified object. From that, I'm going to get all of the changes
     */
    @PostLoad
    public void postLoad(AuditableModel model) {
        model.loadedValues = getValues(model);
    }

    /**
     * Let's log a Model Removal
     */
    @PostRemove
    public void preRemove(AuditableModel model) {
        Logger.debug("PreRemove %s", model.getClass().getSimpleName());
        ObserverRegistry.notifyChange(model, Action.DELETE, null, model, null);
    }

    /**
     * Let's log a model creation
     */
    @PostPersist
    public void prePersist(AuditableModel model) {
        Logger.debug("PrePersist %s", model.getClass().getSimpleName());
        ObserverRegistry.notifyChange(model, Action.CREATE, null, null, model);
    }

    /**
     * Here we compare the loaded values vs modified values and emit logging for references
     */
    @PreUpdate
    public void preUpdate(AuditableModel model) {
        Logger.debug("PreUpdate %s", model.getClass().getSimpleName());
        Map<String, Object> newValues = getValues(model);
        Map<String, Object> oldValues = model.loadedValues;
        if (oldValues == null) {
            Logger.warn("The old values for model %s are null", model.toString());
            return;
        }
        for (Difference difference : getDifferences(oldValues, newValues)) {
            ObserverRegistry.notifyChange(model, Action.MODIFY, difference.field, difference.oldValue,
                    difference.newValue);
        }

    }

    private Set<Difference> getDifferences(Map<String, Object> oldValues, Map<String, Object> newValues) {
        Set<Difference> differences = Sets.newHashSet();
        for (Entry<String, Object> entry : newValues.entrySet()) {
            Object oldForNew = oldValues.get(entry.getKey());
            if (oldForNew == null) {
                if (entry.getValue() != null) {
                    differences.add(new Difference(null, entry));
                }
            } else {
                if (!oldForNew.equals(entry.getValue())) {
                    differences.add(new Difference(oldForNew, entry));
                }
            }
        }
        return differences;
    }

    private Map<String, Object> getValues(AuditableModel model) {
        Class<?> clazz = model.getClass();
        Map<String, Object> map = Maps.newHashMap();
        for (Field field : clazz.getFields()) {

            // Skip fields
            if (field.getAnnotation(Transient.class) != null ||
                    ignoredFields.contains(field.getName().toLowerCase())) {
                continue;
            }

            String capitalizedField = JavaExtensions.capitalizeWords(field.getName().toLowerCase());
            String getterName = "get" + capitalizedField;
            String isName = "is" + capitalizedField;
            MethodResult methodResult = runMethod(getterName, model);
            if (methodResult.run) {
                map.put(field.getName(), methodResult.value);
                continue;
            }

            methodResult = runMethod(isName, model);
            if (methodResult.run) {
                map.put(field.getName(), methodResult.value);
                continue;
            }

            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(model));
            } catch (IllegalArgumentException e) {
                // Does nothing in this case
            } catch (IllegalAccessException e) {
                // Does nothing in this case
            }
        }

        return map;
    }

    private MethodResult runMethod(String name, Object model) {
        return invokeMethod(getMethod(model.getClass(), name), model);
    }

    private MethodResult invokeMethod(Method method, Object object) {
        if (method == null) {
            return MethodResult.didntRun();
        }
        try {
            return new MethodResult(true, method.invoke(object));
        } catch (IllegalArgumentException e) {
            return MethodResult.didntRun();
        } catch (IllegalAccessException e) {
            return MethodResult.didntRun();
        } catch (InvocationTargetException e) {
            return MethodResult.didntRun();
        }

    }

    private Method getMethod(Class<?> clazz, String getterName) {
        Method method = null;
        try {
            method = clazz.getMethod(getterName);
        } catch (SecurityException e) {
            // Does nothing will try other thing
            method = null;
        } catch (NoSuchMethodException e) {
            // Does nothing will try other thing
            method = null;
        }
        return method;
    }

    public static class MethodResult {
        private boolean run;
        private Object value = null;

        public MethodResult(boolean run, Object value) {
            this.run = run;
            this.value = value;
        }

        public static MethodResult didntRun() {
            return new MethodResult(false, null);
        }

    }

    public static class Difference {
        public Object oldValue;
        public Object newValue;
        public String field;

        public Difference(Object oldValue, Entry<String, Object> newValue) {
            this.oldValue = oldValue;
            this.newValue = newValue.getValue();
            this.field = newValue.getKey();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((field == null) ? 0 : field.hashCode());
            result = prime * result + ((newValue == null) ? 0 : newValue.hashCode());
            result = prime * result + ((oldValue == null) ? 0 : oldValue.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            Difference other = (Difference) obj;
            if (field == null) {
                if (other.field != null) return false;
            } else if (!field.equals(other.field)) return false;
            if (newValue == null) {
                if (other.newValue != null) return false;
            } else if (!newValue.equals(other.newValue)) return false;
            if (oldValue == null) {
                if (other.oldValue != null) return false;
            } else if (!oldValue.equals(other.oldValue)) return false;
            return true;
        }




    }

}
