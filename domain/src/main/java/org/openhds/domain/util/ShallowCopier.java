package org.openhds.domain.util;


import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.openhds.domain.model.UuidIdentifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Use reflection to make a shallow copy of a UuidIdentifiable object.
 *
 * This ShallowCopier uses reflection to build a field-by-field copy of a given
 * UuidIdentifiable.  Where any field refers to another UuidIdentifiable object
 * (directly or through a Collection), the copy will point to a "stub" object instead
 * of the original object.  The "stub" is a default instance of the same class with
 * only its uuid and no other fields set.
 *
 * Stubs give external clients enough information to rebuild object references.  Since
 * the stubs don't refer to any other objects, they prevent runaway recursion by tools
 * like automatic XML Marshallers.
 *
 * This ShallowCopier assumes that all objects being copied or converted to stubs have
 * no-argument constructors that produce "default" or "blank" objects.  It also assumes
 * that Collection fields will be initialized statically or in the constructor.
 *
 * This ShallowCopier had an unfortunate dependency on Hibernate.  This is necessary
 * in order to avoid instantiating Hibernate proxy objects.  Instead we always want to
 * instantiate "real" objects using the classes as written.  This avoids issues with
 * tools like automatic JSON Marshallers, which may not know how to Marshall proxies.
 *
 * "Shallow copy" has special meaning for OpenHDS because shallow copies are what we
 * send over the wire to external clients, like the OpenHDS tablet.  This is different
 * from the meaning of "shallow copy" in the context of the Java Cloneable interface.
 *
 * BSH
 */
public class ShallowCopier {

    private static final Logger logger = LoggerFactory.getLogger(ShallowCopier.class);

    // Make a shallow copy of the given object.
    public static <T extends UuidIdentifiable> T makeShallowCopy(T original) {
        if (null == original) {
            return null;
        }

        T copy = (T) newDefaultBlank(original);
        Set<Field> allFields = getAllFields(original);
        assignAllFields(original, copy, allFields);
        return copy;
    }

    // Walk up the inheritance hierarchy for the given object.
    private static Set<Class<?>> getInheritanceHierarchy(UuidIdentifiable original) {
        Set<Class<?>> superclasses = new HashSet<>();

        if (null == original) {
            return superclasses;
        }

        Class<?> currentClass = original.getClass();
        while (!currentClass.equals(Object.class)) {
            superclasses.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        return superclasses;
    }

    // Get all declared fields of the given object and its superclasses.
    private static Set<Field> getAllFields(UuidIdentifiable original) {
        Set<Field> allFields = new HashSet<>();

        if (null == original) {
            return allFields;
        }

        Set<Class<?>> superclasses = getInheritanceHierarchy(original);
        for (Class<?> clazz : superclasses) {
            allFields.addAll(getDeclaredFields(clazz));
        }
        return allFields;
    }

    // Get all declared fields for the given class.
    private static Set<Field> getDeclaredFields(Class<?> clazz) {
        Set<Field> declaredFields = new HashSet<>();

        if (null == clazz) {
            return declaredFields;
        }

        declaredFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        return declaredFields;
    }

    // Make a new blank object of the same concrete class as the given object using its no-argument constructor.
    private static Object newDefaultBlank(Object original) {

        if (null == original) {
            return null;
        }

        // Unfortunate dependency on Hibernate to avoid instantiating new HibernateProxy objects.
        Class<?> currentClass;
        if (original instanceof HibernateProxy) {
            currentClass = ((HibernateProxy)original).getHibernateLazyInitializer().getPersistentClass();
        } else {
            currentClass = original.getClass();
        }

        Constructor<?> constructor;
        try {
            constructor = currentClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            logger.error("Can't find constructor NoSuchMethodException: " + e.getMessage());
            return null;
        }

        Object blank = null;
        try {
            blank = constructor.newInstance();
        } catch (InstantiationException e) {
            logger.error("Can't invoke constructor InstantiationException: " + e.getMessage());
        } catch (IllegalAccessException e) {
            logger.error("Can't invoke constructor IllegalAccessException: " + e.getMessage());
        } catch (InvocationTargetException e) {
            logger.error("Can't invoke constructor InvocationTargetException: " + e.getMessage());
        }

        return blank;
    }

    // Make a new stub object of the same concrete class as the given object with only its uuid set.
    private static UuidIdentifiable makeStub(UuidIdentifiable original) {
        if (null == original) {
            return null;
        }
        UuidIdentifiable stub = (UuidIdentifiable) newDefaultBlank(original);
        String uuid;
        if (original instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) original;
            LazyInitializer initializer = proxy.getHibernateLazyInitializer();
            uuid = (String) initializer.getIdentifier();
        } else {
            uuid = original.getUuid();
        }
        stub.setUuid(uuid);
        return stub;
    }

    // Copy multiple fields from an original object to a target of a compatible class.  Make stubs as necessary.
    private static void assignAllFields(UuidIdentifiable original, UuidIdentifiable target, Set<Field> fields) {
        if (null == original || null == target || null == fields) {
            return;
        }

        for (Field field : fields) {

            if (UuidIdentifiable.class.isAssignableFrom(field.getType())) {
                assignStub(original, target, field);
                continue;
            }

            // Collection may contain UuidIdentifiables
            if (Collection.class.isAssignableFrom(field.getType())) {
                addStubsToCollection(original, target, field);
                continue;
            }

            // default simple assignment
            assignField(original, target, field);
        }
    }

    // Copy the given field verbatim from an original object to a target of a compatible class.
    private static void assignField(UuidIdentifiable original, UuidIdentifiable target, Field field) {
        if (null == original || null == target || null == field) {
            return;
        }

        if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
            return;
        }

        try {
            field.setAccessible(true);
            field.set(target, field.get(original));
        } catch (IllegalAccessException e) {
            logger.error("Can't assign field <" + field.getName() + "> IllegalAccessException: " + e.getMessage());
        }
    }

    // Make a stub based on original an object's UuidIdentifiable field and assign it to a target of a compatible class.
    private static void assignStub(UuidIdentifiable original, UuidIdentifiable target, Field field) {
        if (null == original || null == target || null == field) {
            return;
        }

        if (!UuidIdentifiable.class.isAssignableFrom(field.getType())) {
            logger.error("Can't assign <"
                    + field.getType()
                    + "> to UuidIdentifiable field named <"
                    + field.getName()
                    + ">");
            return;
        }

        try {
            field.setAccessible(true);
            /*
               Due to using hibernate with field access, we can't fetch id fields without init'ing
               the proxy.
            */
            UuidIdentifiable originalEntity = (UuidIdentifiable) field.get(original);
            UuidIdentifiable stub = makeStub(originalEntity);
            field.set(target, stub);
        } catch (IllegalAccessException e) {
            logger.error("Can't assign UuidIdentifiable stub to field <"
                    + field.getName()
                    + "> IllegalAccessException: "
                    + e.getMessage());
        }
    }

    // Copy elements from an original object's Collection to a compatible target's Collection.  Make stubs as necessary.
    private static void addStubsToCollection(UuidIdentifiable original, UuidIdentifiable target, Field field) {
        if (null == original || null == target || null == field) {
            return;
        }

        if (!Collection.class.isAssignableFrom(field.getType())) {
            logger.error("Can't assign <" + field.getType() + "> to Collection field named <" + field.getName() + ">");
            return;
        }

        try {
            field.setAccessible(true);

            Collection<?> originalCollection = (Collection<?>) field.get(original);
            if (originalCollection == null || !Hibernate.isInitialized(originalCollection)) {
                return;
            }

            Collection<Object> stubCollection = (Collection<Object>) field.get(target);
            if (null == stubCollection) {
                logger.warn("Skipping uninitialized Collection in field <"
                        + field.getName()
                        + "> of type <"
                        + field.getType().getName()
                        + "> in class <"
                        + original.getClass().getName()
                        + ">");
                return;
            }

            for (Object object : originalCollection) {
                if (UuidIdentifiable.class.isAssignableFrom(object.getClass())) {
                    UuidIdentifiable stub = makeStub((UuidIdentifiable) object);
                    stubCollection.add(stub);
                } else {
                    stubCollection.add(object);
                }
            }

        } catch (IllegalAccessException e) {
            logger.error("Can't add element to Collection field <"
                    + field.getName()
                    + "> IllegalAccessException: "
                    + e.getMessage());
        }
    }
}
