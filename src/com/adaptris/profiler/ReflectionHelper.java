package com.adaptris.profiler;

import java.lang.reflect.Method;

public class ReflectionHelper {

  /**
   * Get the uniqueID of a component.
   * 
   * @param obj the object.
   * @return the result of {@code getUniqueId}, {@code getId} or null.
   */
  public static String getUniqueId(Object obj) {
    String[] methods =
    {
        "getUniqueId", "getId"
    };
    String result = null;
    for (String method : methods) {
      try {
        String s = invokeGetter(obj, method);
        if (s != null) {
          result = s;
          break;
        }
      }
      catch (Exception e) {
        ;
      }
    }
    return result;
  }

  private static String invokeGetter(Object obj, String methodName) throws Exception {
    Method m = obj.getClass().getMethod(methodName, (Class[]) null);
    String result = null;
    if (m != null) {
      if (String.class.equals(m.getReturnType()) && m.getExceptionTypes().length == 0) {
        result = (String) m.invoke(obj, (Object[]) null);
      }
    }
    return result;
  }
}
