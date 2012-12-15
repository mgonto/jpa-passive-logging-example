package models;

import javax.persistence.Entity;

import models.observer.AuditableModel;

/**
 * Some example model
 *
 *
 * @author Gonto
 * @since Dec 15, 2012
 */
@Entity
public class ExampleModel extends AuditableModel {

    public String name;

    public Long age;

}
