/*
    Copyright 2015 Adaptris Ltd.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

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
