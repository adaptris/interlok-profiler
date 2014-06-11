package com.adaptris.profiler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionHelper {

  public static String getUniqueId(Object object) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Method getUniqueId = object.getClass().getMethod("getUniqueId", new Class<?>[]{});
    Object result = getUniqueId.invoke(object, new Object[]{});
    
    return (String)result;
  }
}
