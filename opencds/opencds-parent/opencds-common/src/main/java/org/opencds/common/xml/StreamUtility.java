package org.opencds.common.xml;

/*
 * StreamUtility does utility functions related to stream conversions.
 *
 * @author Kensaku Kawamoto
 * @version 1.0
 */

import java.io.*;

public class StreamUtility
{
    private static StreamUtility instance = new StreamUtility();  //singleton instance

    private StreamUtility()
    {
    }

    public static StreamUtility getInstance()
    {
        return instance;
    }

    public InputStream getInputStreamFromString(String str)
    {
        try
        {
            byte[] bytes = str.getBytes("UTF8");
            return new ByteArrayInputStream(bytes);
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }

    /**
     * Gets String from inputStream.  Note that the inputStream is depleted and can no longer be read.
     *
     * @param inputStream
     * @return
     */
    public String getStringFromInputStream_depleteStream(InputStream inputStream)
    {
        String stringToReturn = "";

        try
        {
            int k;
            int aBuffSize = 50000;
            byte buff[] = new byte[aBuffSize];
            ByteArrayOutputStream xOutputStream = new ByteArrayOutputStream(aBuffSize);

            while ((k = inputStream.read(buff)) != -1)
            {
                xOutputStream.write(buff, 0, k);
            }
            // I can now grab the string I want
            stringToReturn = stringToReturn + xOutputStream.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return stringToReturn;
    }

    //
    /**
     * Returns the contents of the file in a byte array.  Got from http://javaalmanac.com/egs/java.io/File2ByteArray.html.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public byte[] getBytesFromFile(File file) throws IOException
    {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE)
        {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * Saves content of the outputStream to the file designated.
     *
     * @param outputFile
     */
    public void saveByteArrayOutputStreamToFile(ByteArrayOutputStream outputStream, File outputFile) throws IOException
    {
        try
        {
            OutputStream out = new FileOutputStream(outputFile);
            out = new BufferedOutputStream(out);
            outputStream.writeTo(out);
            out.close();
            outputStream.close();
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    /**
     * Saves contents of InputStream to file.
     *
     * @param inputStream
     * @param outputFile
     */
    public void saveInputStreamToFile(InputStream inputStream, File outputFile) throws IOException
    {
        try
        {
            OutputStream out = new FileOutputStream(outputFile);
            out = new BufferedOutputStream(out);

            byte[] buf = new byte[2048];
            int length;
            while ((length = inputStream.read(buf)) != -1)
            {
                out.write(buf, 0, length);
            }
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    public ByteArrayInputStream convertByteArrayOutputStreamToInputStream(ByteArrayOutputStream outputStream)
    {
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public void printInputStreamToScreen(InputStream inputStream)
    {
        // read contents of InputStream
        int c = 0;
        byte data[] = new byte[4096];

        OutputStream out = System.out;

        try
        {
            while ((c = inputStream.read(data)) != -1)
            {
                out.write(data, 0, c);
            }
            out.flush();
            out.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.println();
    }
}