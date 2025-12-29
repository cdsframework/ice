package org.cdsframework.ice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class IceApplication extends SpringBootServletInitializer
{
    @SuppressWarnings("UnnecessaryModifier")
    public static void main(final String[] argv)
    {
        SpringApplication.run(IceApplication.class, argv);
    }
}
