package org.opencds.evaluation.service;

import java.lang.Thread.UncaughtExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class EvaluationExceptionHandler implements UncaughtExceptionHandler
{
    @Override
    public void uncaughtException(final Thread t, final Throwable e)
    {
        log.error("UncaughtException in thread '{}' message: {}", t.getName(), e.getMessage(), e);
    }
}
