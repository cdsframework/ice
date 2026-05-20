package org.opencds.config.api.dao.file;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencds.common.exceptions.OpenCDSRuntimeException;

public record StreamCacheElement(String id,
                                 InputStream inputStream) implements CacheElement
{
    @Override
    public long length()
    {
        return -1;
    }

    @Override
    public boolean exists()
    {
        return inputStream != null;
    }

    @Override
    public File getFile()
    {
        File file = null;
        try
        {
            file = File.createTempFile(id, ".accdb");
            try (final DataOutputStream out = new DataOutputStream(new FileOutputStream(file)))
            {
                try (final DataInputStream in = new DataInputStream(inputStream))
                {
                    final byte[] b = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = in.read(b)) != -1)
                        out.write(b, 0, bytesRead);
                    out.flush();
                }
            }
            return file;
        }
        catch (final IOException e)
        {
            throw new OpenCDSRuntimeException();
        }
        finally
        {
            if (file != null)
                file.deleteOnExit();
        }
    }
}
