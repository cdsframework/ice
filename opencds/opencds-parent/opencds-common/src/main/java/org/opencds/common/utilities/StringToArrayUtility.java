/**
 * Copyright 2011 OpenCDS.org
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package org.opencds.common.utilities;

/*
 * StringToArrayUtility.java
 *
 * Used to create a primitive array from a delimited string
 */

/**
 *
 * @author  Ken
 * @version
 */
import java.util.ArrayList;

public class StringToArrayUtility
{
  private static StringToArrayUtility instance = new StringToArrayUtility();  //singleton instance

  private StringToArrayUtility()
  {
  }

  public static StringToArrayUtility getInstance()
  {
    return instance;
  }

  public String[] getPrimitiveArrayFromString(String source, String delimiter)
  // source is a delimiter-delimited sequence of strings
  // post: returns primitive array containing strings in source
  //
  // Note: the whole delimiter string is taken as one instance
  // of the delimiter substring in the source, which was the original intention.
  // The previous implementation actually treated the delimiter as a set of individual delimiter characters,
  // which usually worked since the delimiter string often contains a single character.
  // Empty fields between delimiters (or at the beginning or end) are returned as zero-length strings.
  {
     ArrayList<String> regularArray = getRegularArrayFromString(source, delimiter);

     String item = null;

     int numberItems = regularArray.size();

     String[] primitiveArray = new String[numberItems];

     for (int j=0; j < regularArray.size(); j++)
     {
         item = (String) regularArray.get(j);
         primitiveArray[j] = item;
     }
     return primitiveArray;
  }

  public ArrayList<String> getRegularArrayFromString(String source, String delimiter)
  // source is a delimiter-delimited sequence of strings
  // post: returns regular array containing strings in source
  //
  // Note: the whole delimiter string is taken as one instance
  // of the delimiter substring in the source, which was the original intention.
  // The previous implementation actually treated the delimiter as a set of individual delimiter characters,
  // which usually worked since the delimiter string often contains a single character.
  // Empty fields between delimiters (or at the beginning or end) are returned as zero-length strings.
  {
     ArrayList<String> regularArray = new ArrayList<String>();

      if (source == null || delimiter == null)
      {
          return regularArray;
      }

      int sourceLength = source.length();
      int delimiterLength = delimiter.length();

      if (sourceLength == 0 || delimiterLength == 0)
      {
          return regularArray;
      }

      int beginIndex = 0, endIndex;
      while ((endIndex = source.indexOf(delimiter, beginIndex)) >= 0)
      {
          regularArray.add(source.substring(beginIndex, endIndex));
          beginIndex = endIndex + delimiterLength;
      }

      // Add the last component, which does not end in a delimiter.
      if (beginIndex <= sourceLength)
      {
          regularArray.add(source.substring(beginIndex));
      }

     return regularArray;
  }

  public void printPrimitiveArray(String[] primitiveArray)
  {
     for (int k = 0; k < primitiveArray.length; k++)
     {
        System.out.println(primitiveArray[k]);
     }
  }

  public static void main(String args[])
  {
      String source = "DontHaveCopay->don't have co-pay|DontHaveBusFare->don't have bus fare";
//      String source = "DontHaveCopay->don't have co-pay->testing>";
//      String source = "->Dont->";
//      String source = "->";
//      String source = "Dont";
//      String source = "";

      String[] array = StringToArrayUtility.getInstance().getPrimitiveArrayFromString(source, "->");
     StringToArrayUtility.getInstance().printPrimitiveArray(array);
  }
}
