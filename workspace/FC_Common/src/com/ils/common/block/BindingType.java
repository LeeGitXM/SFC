/**
 *   (c) 2013  ILS Automation. All rights reserved. 
 */
package com.ils.common.block;


/**
 * This enumeration class describes the types of bindings available for a property
 */
public enum BindingType
{
            NONE,
            SCRIPT,
            TAG 
            ;
           
 /**
  * @return  a comma-separated list of all binding types in a single String.
  */
  public static String names()
  {
    StringBuffer names = new StringBuffer();
    for (BindingType type : BindingType.values())
    {
      names.append(type.name()+", ");
    }
    return names.substring(0, names.length()-2);
  }
}
