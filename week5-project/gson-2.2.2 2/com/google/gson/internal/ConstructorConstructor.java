package com.google.gson.internal;

import com.google.gson.InstanceCreator;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public final class ConstructorConstructor
{
  private final Map<Type, InstanceCreator<?>> instanceCreators;

  public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators)
  {
    this.instanceCreators = instanceCreators;
  }

  public ConstructorConstructor() {
    this(Collections.emptyMap());
  }

  public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
    final Type type = typeToken.getType();
    Class rawType = typeToken.getRawType();

    final InstanceCreator creator = (InstanceCreator)this.instanceCreators.get(type);
    if (creator != null) {
      return new ObjectConstructor() {
        public T construct() {
          return creator.createInstance(type);
        }
      };
    }

    ObjectConstructor defaultConstructor = newDefaultConstructor(rawType);
    if (defaultConstructor != null) {
      return defaultConstructor;
    }

    ObjectConstructor defaultImplementation = newDefaultImplementationConstructor(rawType);
    if (defaultImplementation != null) {
      return defaultImplementation;
    }

    return newUnsafeAllocator(type, rawType);
  }

  private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
    try {
      final Constructor constructor = rawType.getDeclaredConstructor(new Class[0]);
      if (!constructor.isAccessible()) {
        constructor.setAccessible(true);
      }
      return new ObjectConstructor()
      {
        public T construct() {
          try {
            Object[] args = null;
            return constructor.newInstance(args);
          }
          catch (InstantiationException e) {
            throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
          }
          catch (InvocationTargetException e)
          {
            throw new RuntimeException("Failed to invoke " + constructor + " with no args", e.getTargetException());
          }
          catch (IllegalAccessException e) {
            throw new AssertionError(e);
          }
        } } ;
    } catch (NoSuchMethodException e) {
    }
    return null;
  }

  private <T> ObjectConstructor<T> newDefaultImplementationConstructor(Class<? super T> rawType)
  {
    if (Collection.class.isAssignableFrom(rawType)) {
      if (SortedSet.class.isAssignableFrom(rawType))
        return new ObjectConstructor() {
          public T construct() {
            return new TreeSet();
          }
        };
      if (Set.class.isAssignableFrom(rawType))
        return new ObjectConstructor() {
          public T construct() {
            return new LinkedHashSet();
          }
        };
      if (Queue.class.isAssignableFrom(rawType)) {
        return new ObjectConstructor() {
          public T construct() {
            return new LinkedList();
          }
        };
      }
      return new ObjectConstructor() {
        public T construct() {
          return new ArrayList();
        }

      };
    }

    if (Map.class.isAssignableFrom(rawType)) {
      return new ObjectConstructor()
      {
        public T construct() {
          return new LinkedHashMap();
        }

      };
    }

    return null;
  }

  private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> rawType)
  {
    return new ObjectConstructor() {
      private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();

      public T construct() {
        try {
          return this.unsafeAllocator.newInstance(rawType);
        }
        catch (Exception e) {
          throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", e);
        }
      }
    };
  }

  public String toString()
  {
    return this.instanceCreators.toString();
  }
}