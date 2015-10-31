package org.opencds.common.utilities;

/**
 * <p>EncoderUtility is used to encode and decode data.
 * <p/>
 *
 * @author Kensaku Kawamoto
 * @version 1.00
 */

//import org.apache.commons.*;
//import org.apache.commons.codec.binary.Base64;
import sun.misc.BASE64Encoder;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

public abstract class EncoderUtility
{
    // URL encoding/decoding functions

    /**
     * Encodes String in URL-safe, UTF-8 format.
     *
     * @param sourceString
     * @return
     */
    public static String encodeStringAsUrlString(String sourceString)
    {
        try
        {
            return URLEncoder.encode(sourceString, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // should not happen; if it does, print an error and return the original String
            System.err.println("Error in EncoderUtility.encodeStringAsUrlString; returning original string.  Details: " + e.getMessage());
            return sourceString;
        }
    }

    public static String decodeUrlStringIntoOriginalString(String urlEncodedString)
    {
        try
        {
            return URLDecoder.decode(urlEncodedString, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // should not happen; if it does, print an error and return the original String
            System.err.println("Error in EncoderUtility.decodeUrlStringIntoOriginalString; returning URL encoded string.  Details: " + e.getMessage());
            return urlEncodedString;
        }
    }

    public static void main(String args[])
    {
        String originalString = "http://www.duke.edu/p1 = asdfasd & p2 = ad ds";

        /**
         String encodedString = utility.encodeStringAsBase64String(originalString);
         String decodedString = utility.decodeBase64StringIntoOriginalString(encodedString);
         System.out.println("Encoded string: " + encodedString);
         System.out.println("Decoded string: " + decodedString);
         **/
        System.out.println("Original string: " + originalString);
        String encodedString = encodeStringAsUrlString(originalString);
        System.out.println("Encoded string: " + encodedString);
        System.out.println("Decoded string: " + decodeUrlStringIntoOriginalString(encodedString));
    }

    // SHA-1 encoding function

    /**
     * Returns SHA-1 hashed string.  See http://www.evolt.org/article/Password_encryption_rationale_and_Java_example/18/60122/?format=print&rating=true&comments=false
     * for details.
     *
     * @param originalText
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String encodeStringAsSha1Digest(String originalText) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest md = null;
        try
        {
            md = MessageDigest.getInstance("SHA"); //step 2
        }
        catch (NoSuchAlgorithmException e)
        {
            throw e;
        }
        try
        {
            md.update(originalText.getBytes("UTF-8")); //step 3
        }
        catch (UnsupportedEncodingException e)
        {
            throw e;
        }

        byte raw[] = md.digest(); //step 4
        String hash = (new BASE64Encoder()).encode(raw); //step 5
        return hash; //step 6
    }

}