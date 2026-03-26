package org.opencds.common.utilities;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MiscUtility
{
    private record QueueFiller(BlockingQueue<String> queue) implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                while (true)
                {
                    final int val = key.getAndIncrement();
                    queue.put(Integer.toString(val));
                }
            }
            catch (final InterruptedException ignored)
            {
            }
        }
    }

    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000000);
    private static final ExecutorService es = Executors.newFixedThreadPool(10);
    private static final AtomicInteger key = new AtomicInteger(0);

    static
    {
        for (int i = 0; i < 50; i++)
            es.submit(new QueueFiller(queue));
    }

    public static String getIDAsString()
    {
        try
        {
            return queue.take();
        }
        catch (final InterruptedException e)
        {
            log.error(e.getMessage(), e);
        }
        return UUID.randomUUID().toString();
    }
}
