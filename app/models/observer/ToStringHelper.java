package models.observer;

import play.db.jpa.Model;

/**
 * This is a helper for creating a nicer {@link Object#toString()}
 *
 *
 * @author Gonto
 * @since Dec 11, 2012
 */
public class ToStringHelper {

    public static String toString(Object obj) {
        if (obj == null) {
            return "null";
        }
        if (Iterable.class.isAssignableFrom(obj.getClass())) {
            return toStringIterable((Iterable) obj);
        } else {
            return obj.toString();
        }
    }

    private static String compactToString(Model model) {
        return model.getClass().getSimpleName() + "[" + model.id + "]";
    }

    private static String toStringIterable(Iterable iterable) {
        StringBuilder builder = new StringBuilder("Iterable(");
        boolean hasElements = false;
        for(Object element : iterable ) {
            hasElements = true;
            if (Model.class.isAssignableFrom(element.getClass())) {
                builder.append(compactToString((Model) element));
            } else {
                builder.append(element.toString());
            }
            builder.append(", ");
        }
        if (hasElements) {
            builder.delete(builder.length() - 3, builder.length() - 1);
        }
        builder.append(")");
        return builder.toString();
    }

}
