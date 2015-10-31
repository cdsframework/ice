/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 *	
 */

package org.opencds.common.utilities;

/**
 * <p>StringUtility.
 * <p/>
 * <p>Initial Create Date: 11-29-04</p>
 * <p>Last Update Date: 11-29-04</p>
 * <p>Version history: v1.00 (11-29-04).  Initial creation.</p>
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

public class StringUtility {
    /**
     * Returns source string as is, except in the case where the source string
     * is null, in which case returns "".
     * 
     * @param sourceString_mayBeNull
     * @return
     */
    public static String getStringAsEmptyStringIfNull(String sourceString_mayBeNull) {
        if (sourceString_mayBeNull == null) {
            return "";
        } else {
            return sourceString_mayBeNull;
        }
    }

    /**
     * Takes contents of sourceArray and returns a String. e.g. sourceArray =
     * [Apple, Orange, Grape] stringWrapper = "'" stringDelimiter = ", " returns
     * 'Apple', 'Orange', 'Grape'
     *
     * @param sourceArrayOfStrings
     *            Must contain Strings only
     * @param stringWrapper
     * @param stringDelimiter
     * @return
     */
    public static String getArrayAsString(ArrayList<String> sourceArrayOfStrings, String stringWrapper,
            String stringDelimiter) {
        StringBuffer buffer = new StringBuffer();

        for (int k = 0; k < sourceArrayOfStrings.size(); k++) {
            buffer.append(stringWrapper);
            String token = (String) sourceArrayOfStrings.get(k);
            buffer.append(token);
            buffer.append(stringWrapper);
            if (k != sourceArrayOfStrings.size() - 1) {
                buffer.append(stringDelimiter);
            }
        }
        return buffer.toString();
    }

    /**
     * Converts "Yes" (ignoring case) to true, otherwise converts to false. If
     * not "Yes" or "No" (ignoring case), returns false but error message
     * printed to screen.
     *
     * @param YesOrNo
     * @return
     */
    public static boolean convertYesOrNoToBoolean(String YesOrNo) {
        if (YesOrNo.equalsIgnoreCase("Yes")) {
            return true;
        } else if (YesOrNo.equalsIgnoreCase("No")) {
            return false;
        } else {
            System.err.println("Error in StringUtility.convertYesOrNoToBoolean: " + YesOrNo + " not recognized.");
            return false;
        }
    }

    /**
     * Converts "Y" (ignoring case) to true, otherwise converts to false. If not
     * "Y" or "N" (ignoring case), returns false but error message printed to
     * screen.
     *
     * @param YorN
     * @return
     */
    public static boolean convertYorNToBoolean(String YorN) {
        if (YorN.equalsIgnoreCase("Y")) {
            return true;
        } else if (YorN.equalsIgnoreCase("N")) {
            return false;
        } else {
            System.err.println("Error in StringUtility.convertYorNToBoolean: " + YorN + " not recognized.");
            return false;
        }
    }

    /**
     * Converts boolean to "Yes" or "No".
     *
     * @param trueOrFalse
     * @return
     */
    public static String convertBooleanToYesOrNo(boolean trueOrFalse) {
        if (trueOrFalse == true) {
            return "Yes";
        } else {
            return "No";
        }
    }

    /**
     * Converts boolean to "Y" or "N".
     *
     * @param trueOrFalse
     * @return
     */
    public static String convertBooleanToYorN(boolean trueOrFalse) {
        if (trueOrFalse == true) {
            return "Y";
        } else {
            return "N";
        }
    }

    /**
     * Returns an int for a String. Throws exception if cannot be done.
     *
     * @param numberAsString
     * @return
     * @throws NumberFormatException
     */
    public static int convertStringToInt(String numberAsString) throws NumberFormatException {
        int intToReturn = 0;

        try {
            intToReturn = Integer.parseInt(numberAsString);
        } catch (NumberFormatException e) {
            throw e;
        }
        return intToReturn;
    }

    /**
     * Returns a longfor a String. Throws exception if cannot be done.
     *
     * @param numberAsString
     * @return
     * @throws NumberFormatException
     */
    public static long convertStringToLong(String numberAsString) throws NumberFormatException {
        long longToReturn = 0;

        try {
            longToReturn = Long.parseLong(numberAsString);
        } catch (NumberFormatException e) {
            throw e;
        }
        return longToReturn;
    }

    /**
     * Returns a double for a String. Throws exception if cannot be done.
     *
     * @param numberAsString
     * @return
     * @throws NumberFormatException
     */
    public static double convertStringToDouble(String numberAsString) throws NumberFormatException {
        double doubleToReturn = 0;

        try {
            doubleToReturn = Double.valueOf(numberAsString).doubleValue();
        } catch (NumberFormatException e) {
            throw e;
        }
        return doubleToReturn;
    }

    /**
     * Returns formatted text, where: (1) each line has leftIndentWidth spaces
     * at the front, (2) each line has a new line added if adding the next word
     * would make the charCount > pageWidth, and (3) adds tabWidth of space when
     * a "\t" character is encountered.
     * <p/>
     * Suitable parameters for an email message would be: leftIndentWidth --> 0
     * tabWidth --> 2 pageWidth --> 70
     *
     * @param textToFormat
     * @param leftIndentWidth
     * @param pageWidth
     * @return
     */
    public static String getFormattedText(String textToFormat, int leftIndentWidth, int tabWidth, int pageWidth) {
        StringBuffer myFormattedStringAsBuffer = new StringBuffer(10000);
        // temporarily place newly created string
        // in buffer to avoid inefficient String reconstruction

        // StringTokenizer tokenizer = new StringTokenizer(stringToFormat);
        // get new token if separated by any white space character

        StringTokenizer tokenizer = new StringTokenizer(textToFormat, " \t\n\r\f", true);
        // true means delimiter characters will also be returned

        String nextWord = null;

        int numChars = 0;
        int numNonIndentChars = 0;

        // add indents
        for (int k = 0; k < leftIndentWidth; k++) {
            myFormattedStringAsBuffer.append(' ');
            numChars++;
        }

        while (tokenizer.hasMoreTokens()) {
            // get the next word and its length
            nextWord = tokenizer.nextToken();
            int wordLength = nextWord.length();

            if ((nextWord.equals("\n")) || (nextWord.equals("\r")) || (nextWord.equals("\f"))) {
                // if the text specifies a new line character, then
                // start a new line {
                // go ahead and add a new line character and reset
                // numChars
                myFormattedStringAsBuffer.append('\n');
                numChars = 0;
                numNonIndentChars = 0;

                // add indents and adjust numChars
                for (int j = 0; j < leftIndentWidth; j++) {
                    myFormattedStringAsBuffer.append(' ');
                    numChars++;
                }
            } else if (numChars + wordLength > pageWidth) {
                // if adding this next word would make the limit be exceeded {
                // go ahead and add a new line character and reset
                // numChars
                myFormattedStringAsBuffer.append('\n');
                numChars = 0;
                numNonIndentChars = 0;

                // add indents and adjust numChars
                for (int j = 0; j < leftIndentWidth; j++) {
                    myFormattedStringAsBuffer.append(' ');
                    numChars++;
                }

                // add word (as long as it isn't white space) and adjust
                // numChars
                if ((nextWord.equals(" ")) || (nextWord.equals("\t"))) {
                    // do nothing
                } else {
                    myFormattedStringAsBuffer.append(nextWord);
                    numChars += wordLength + 1;
                    numNonIndentChars += wordLength + 1;
                }
            } else {
                // otherwise, if there is still space for the next word on this
                // line {
                if (nextWord.equals(" ")) {
                    if (numNonIndentChars != 0) {
                        myFormattedStringAsBuffer.append(' ');
                        numChars++;
                        numNonIndentChars++;
                    }
                } else if (nextWord.equals("\t")) {
                    for (int k = 0; k < tabWidth; k++) {
                        myFormattedStringAsBuffer.append(' ');
                        numChars++;
                        numNonIndentChars++;
                    }
                } else {
                    myFormattedStringAsBuffer.append(nextWord);
                    numChars += wordLength + 1;
                    numNonIndentChars += wordLength + 1;
                }
            }
        }
        return new String(myFormattedStringAsBuffer);
    }

    /**
     * Returns a String with illegal XML characters convereted to XML-safe
     * characters, so that the String can be placed in attributes or non-CDATA
     * XML elements without causing an error. Characters changed are: > --> &gt;
     * < --> &lt; " --> &quot; ' --> &apos; & --> &amp;
     *
     * @param stringWithPotentiallyIllegalXmlCharacters
     *
     * @return
     */
    public static String getStringWithLegalXmlCharacters(String stringWithPotentiallyIllegalXmlCharacters) {
        String stringToReturn = stringWithPotentiallyIllegalXmlCharacters;
        stringToReturn = stringToReturn.replace("&", "&amp;");
        stringToReturn = stringToReturn.replace(">", "&gt;");
        stringToReturn = stringToReturn.replace("<", "&lt;");
        stringToReturn = stringToReturn.replace("\"", "&quot;");
        stringToReturn = stringToReturn.replace("'", "&apos;");

        return stringToReturn;
    }

    /**
     * Returns a String with ' and \ characters escaped, so that they can be
     * inserted within SQL statements delimited by '' without error. Characters
     * changed are: ' --> \' \ --> \\
     * 
     * @param originalString
     * @return
     */
    public static String getStringWithSingleQuoteAndBackSlashEscaped(String originalString) {
        String stringToReturn = originalString;
        stringToReturn = stringToReturn.replace("\\", "\\\\"); // note: order is
                                                               // important
        stringToReturn = stringToReturn.replace("\'", "\\\'");

        return stringToReturn;
    }

    /**
     * Returns a String with leading and trailing spaces removed. Also, makes
     * sure there is at most only one space between characters in the string.
     * e.g. " Hello    Ken  " --> "Hello Ken"
     *
     * @param originalStringUnchangedByFunction
     *
     * @return
     */
    public static String getStringTrimmedInsideAndOut(String originalStringUnchangedByFunction) {
        String stringToReturn = originalStringUnchangedByFunction.trim();
        return stringToReturn.replaceAll("\\s+", " ");
    }

    public static boolean stringContainsOnlyWhiteSpace(String stringToEvaluate) {
        for (int a = 0; a < stringToEvaluate.length(); a++) {
            if ((stringToEvaluate.charAt(a) != ' ') && (stringToEvaluate.charAt(a) != '\n') &&
                    (stringToEvaluate.charAt(a) != '\f') && (stringToEvaluate.charAt(a) != '\r') &&
                    (stringToEvaluate.charAt(a) != '\t')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether string only contains chars included in specifiedChars.
     * Note that chars (e.g. 'X') must be placed in the HashSet, not Strings
     * (e.g. "X").
     *
     * @param stringToEvaluate
     * @param specifiedChars
     * @return
     */
    public static boolean stringContainsOnlySpecifiedChars(String stringToEvaluate, HashSet<Character> specifiedChars) {
        for (int a = 0; a < stringToEvaluate.length(); a++) {
            if (!specifiedChars.contains(stringToEvaluate.charAt(a))) {
                return false;
            }
        }
        return true;
    }

    public static boolean stringBufferContainsOnlyWhiteSpace(StringBuffer stringBufferToEvaluate) {
        for (int a = 0; a < stringBufferToEvaluate.length(); a++) {
            if ((stringBufferToEvaluate.charAt(a) != ' ') && (stringBufferToEvaluate.charAt(a) != '\n') &&
                    (stringBufferToEvaluate.charAt(a) != '\f') && (stringBufferToEvaluate.charAt(a) != '\r') &&
                    (stringBufferToEvaluate.charAt(a) != '\t')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns String in reverse order; i.e. "ABC" returned as "CBA"
     *
     * @param originalString
     * @return
     */
    public static String getReversedString(String originalString) {
        if (originalString == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        for (int a = originalString.length() - 1; a >= 0; a--) {
            buffer.append(originalString.charAt(a));
        }
        return buffer.toString();
    }

    /**
     * Returns String with any chars at the front placed at the back. e.g. if
     * targetChar == '=', and String == "==abc", returns "abc=="
     *
     * @param originalString
     * @param targetChar
     * @return
     */
    public static String getStringWithCharsAtFrontPlacedAtBack(String originalString, char targetChar) {
        if ((originalString == null) || (originalString.length() < 1)) {
            return originalString;
        } else if (originalString.charAt(0) != targetChar) {
            return originalString;
        } else {// first character is the target character {
            StringBuffer buffer = new StringBuffer();
            int numberTargetCharsAtFront = 0;
            boolean lastCharEncounteredWasTargetChar = true;
            for (int a = 0; (a < originalString.length()) && (lastCharEncounteredWasTargetChar); a++) {
                char originalChar = originalString.charAt(a);
                if (originalChar == targetChar) {
                    numberTargetCharsAtFront++;
                } else {
                    lastCharEncounteredWasTargetChar = false;
                }
            }

            buffer.append(originalString.substring(numberTargetCharsAtFront));
            for (int b = 0; b < numberTargetCharsAtFront; b++) {
                buffer.append(targetChar);
            }
            return buffer.toString();
        }
    }

    /**
     * Returns String with any chars at the back placed at the front. e.g. if
     * targetChar == '=', and String == "abc==", returns "==abc"
     *
     * @param originalString
     * @param targetChar
     * @return
     */
    public static String getStringWithCharsAtBackPlacedAtFront(String originalString, char targetChar) {
        if ((originalString == null) || (originalString.length() < 1)) {
            return originalString;
        } else if (originalString.charAt(originalString.length() - 1) != targetChar) {
            return originalString;
        } else {// last character is the target character {
            StringBuffer buffer = new StringBuffer();
            int numberTargetCharsAtBack = 0;
            boolean lastCharEncounteredWasTargetChar = true;
            for (int a = originalString.length() - 1; (a >= 0) && (lastCharEncounteredWasTargetChar); a--) {
                char originalChar = originalString.charAt(a);
                if (originalChar == targetChar) {
                    numberTargetCharsAtBack++;
                } else {
                    lastCharEncounteredWasTargetChar = false;
                }
            }

            for (int b = 0; b < numberTargetCharsAtBack; b++) {
                buffer.append(targetChar);
            }
            buffer.append(originalString.substring(0, originalString.length() - numberTargetCharsAtBack));

            return buffer.toString();
        }
    }

    /**
     * Returns originalString repeated as necessary, then truncated until it is
     * the designatedLength.
     *
     * @param originalString
     *            Must be at least one character long
     * @param designatedLength
     *            Must be >= 0
     * @return
     * @throws ParseException
     */
    public static String getStringRepeatedAndTruncatedToDesignatedLength(String originalString, int designatedLength)
            throws ParseException {
        if ((originalString == null) || (originalString.length() < 1)) {
            throw new ParseException("Original string must be at least one character long", 0);
        }

        if (designatedLength < 0) {
            throw new ParseException("Designated length must be >= 0", 0);
        }

        StringBuffer buffer = new StringBuffer();
        while (buffer.length() < designatedLength) {
            buffer.append(originalString);
        }
        buffer.setLength(designatedLength);
        return buffer.toString();
    }

    /**
     * Returns originalString with a space added before mid-word capital
     * letters. e.g. "EmailAlert" --> "Email Alert"
     *
     * @param originalString
     * @return
     */
    public static String getStringWithSpaceAddedBeforeMidWordCap(String originalString) {
        StringBuffer buffer = new StringBuffer();

        for (int a = 0; a < originalString.length(); a++) {
            String ch = new String("" + originalString.charAt(a));
            if (a > 0) {
                if (ch.matches("[A-Z]")) {
                    buffer.append(" ");
                }
            }
            buffer.append(ch);
        }
        return buffer.toString();
    }

    /**
     * Returns string with any '\n' characters replaced by ' ' characters
     * 
     * @param originalString
     * @return
     */
    public static String getStringWithNewLineCharsReplacedBySpace(String originalString) {
        StringBuffer buffer = new StringBuffer();

        for (int a = 0; a < originalString.length(); a++) {
            char ch = originalString.charAt(a);
            if (ch == '\n') {
                buffer.append(" ");
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    /**
     * Returns string where any chars specified in charsToReplace is replaced by
     * the replacementString. Note: replacementString can be "" Note: make sure
     * charsToReplace contains chars (e.g. '!'), not Strings (e.g. "!")
     *
     * @param originalString
     * @param charsToReplace
     * @param replacementString
     * @return
     */
    public static String getStringWithCharsReplaced(String originalString, HashSet<Character> charsToReplace,
            String replacementString) {
        StringBuffer buffer = new StringBuffer();

        for (int a = 0; a < originalString.length(); a++) {
            char ch = originalString.charAt(a);
            if (charsToReplace.contains(ch)) {
                buffer.append(replacementString);
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    /**
     * Returns true if targetString == null or "", false otherwise
     *
     * @param targetString
     * @return
     */
    public static boolean stringIsNullOrEmpty(String targetString) {
        if ((targetString == null) || (targetString.equals(""))) {
            return true;
        }
        return false;
    }

    /**
     * Returns string with initial characters capitalized. e.g. "HERE", "heRE",
     * "here" --> "Here" e.g. "Hello Ken", "HELLO KEN", "hello ken" -->
     * "Hello Ken"
     *
     * @param originalString
     * @return
     */
    public static String getStringWithOnlyInitialCharsCapitalized(String originalString) {
        if (originalString == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        boolean lastCharWasWhiteSpace = true;
        for (int pos = 0; pos < originalString.length(); pos++) {
            String charAsString = new String("" + originalString.charAt(pos));
            if (lastCharWasWhiteSpace) {
                buffer.append(charAsString.toUpperCase());
            } else {
                buffer.append(charAsString.toLowerCase());
            }

            if (stringContainsOnlyWhiteSpace(charAsString)) {
                lastCharWasWhiteSpace = true;
            } else {
                lastCharWasWhiteSpace = false;
            }
        }
        return buffer.toString();
    }

    /**
     * Returns String with the first letter capitalized, other characters
     * unchanged.
     *
     * @param originalString
     * @return
     */
    public static String getStringWithFirstLetterCapitalized(String originalString) {
        if (originalString == null) {
            return null;
        }

        int originalStringLenth = originalString.length();

        if (originalStringLenth == 0) {
            return originalString; // ""
        } else if (originalStringLenth == 1) {
            return originalString.toUpperCase();
        } else {// if 2 or more chars {
            String firstChar = "" + originalString.charAt(0);
            return firstChar.toUpperCase() + originalString.substring(1);
        }
    }

    /**
     * Takes in ICD9 code with or without period --> if period there, just
     * returns as is. If not, inserts period using the following algorithm: - If
     * code starts with "E" or "e" --> inserts period after first four
     * characters, if there are at least 5 characters (nothing changed if less
     * than 5 characters) - Otherwise, inserts period after first three
     * characters, if there are at least 4 characters (nothing changed if less
     * than 4 characters)
     * <p/>
     * Note: algorithm developed by Jay Kennedy at MPMC, and delineated in email
     * from Pam Phillips on 4/29/05
     *
     * @param icd9CodeWithOrWithoutPeriod
     * @return
     */
    public static String getIcd9CodeWithPeriodInserted(String icd9CodeWithOrWithoutPeriod) {
        if (!icd9CodeWithOrWithoutPeriod.contains(".")) {
            if ((icd9CodeWithOrWithoutPeriod.startsWith("E")) || (icd9CodeWithOrWithoutPeriod.startsWith("e"))) {
                if (icd9CodeWithOrWithoutPeriod.length() >= 5) {
                    return icd9CodeWithOrWithoutPeriod.substring(0, 4) + "." + icd9CodeWithOrWithoutPeriod.substring(4);
                }
            } else {
                if (icd9CodeWithOrWithoutPeriod.length() >= 4) {
                    return icd9CodeWithOrWithoutPeriod.substring(0, 3) + "." + icd9CodeWithOrWithoutPeriod.substring(3);
                }
            }
        }
        return icd9CodeWithOrWithoutPeriod;
    }

    /**
     * Returns true if str is not null and contains at least one digit, false
     * otherwise.
     * 
     * @param str
     * @return
     */
    public static boolean containsDigit(String str) {
        if (str == null) {
            return false;
        } else {
            if (str.matches(".*\\d.*")) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String parseTextForKeyValues(String foundValue, String notFoundValue, String[] keyValues, String text) {
        String result = notFoundValue;
        for (String key : keyValues) {
            if (text.contains(key)) {
                result = foundValue;
                break;
            }
        }
        return result;
    }

}