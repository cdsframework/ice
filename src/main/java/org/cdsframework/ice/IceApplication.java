package org.cdsframework.ice;

import java.util.TimeZone;

import org.cdsframework.ice.config.IceProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class IceApplication extends SpringBootServletInitializer
{
    private static final TimeZone easternTime = TimeZone.getTimeZone("America/New_York");

    @SuppressWarnings("UnnecessaryModifier")
    public static void main(final String[] argv)
    {
        if (!easternTime.equals(TimeZone.getDefault()))
        {
            log.warn("Changing timezone from {} to {}", TimeZone.getDefault().getDisplayName(), easternTime.getDisplayName());
            TimeZone.setDefault(easternTime);
        }

        SpringApplication.run(IceApplication.class, argv);
    }

    public IceApplication(final IceProperties iceProperties)
    {
        log.info("Setting fire limit to {}", iceProperties.getFireLimit());
        System.setProperty("org.jbpm.rule.task.firelimit", Integer.toString(iceProperties.getFireLimit()));
    }
}
