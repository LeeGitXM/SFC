/**
 *   (c) 2013  ILS Automation. All rights reserved. 
 */
package com.ils.common.block;


/**
 * This enumeration class represents permissible data types for a block property
 */
public enum PropertyType
{
            STRING,
            DOUBLE,
            INTEGER,
            BOOLEAN,
            OBJECT               // Untyped primitive
            ;
           
 /**
  * @return  a comma-separated list of all attribute types in a single String.
  */
  public static String names()
  {
    StringBuffer names = new StringBuffer();
    for (PropertyType type : PropertyType.values())
    {
      names.append(type.name()+", ");
    }
    return names.substring(0, names.length()-2);
  }
}
