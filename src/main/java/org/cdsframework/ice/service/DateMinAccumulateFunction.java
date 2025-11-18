package org.cdsframework.ice.service;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.drools.core.base.accumulators.AbstractAccumulateFunction;

import lombok.Getter;
import lombok.Setter;

public class DateMinAccumulateFunction extends AbstractAccumulateFunction<DateMinAccumulateFunction.MinData>
{
    @Getter
    @Setter
    public static class MinData implements Externalizable
    {
        private Date min;

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException
        {
            min = (Date) in.readObject();
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException
        {
            out.writeObject(min);
        }

        @Override
        public String toString()
        {
            return "min";
        }
    }

    @Override
    public void readExternal(final ObjectInput in)
    {
    }

    @Override
    public void writeExternal(final ObjectOutput out)
    {
    }

    @Override
    public MinData createContext()
    {
        return new MinData();
    }

    @Override
    public void init(final MinData data)
    {
        data.setMin(null);
    }

    @Override
    public void accumulate(final MinData data, final Object value)
    {
        if (value instanceof final Date dateValue)
            data.setMin(data.getMin() == null || data.getMin().compareTo(dateValue) > 0 ? dateValue : data.getMin());
    }

    @Override
    public boolean tryReverse(final MinData data, final Object value)
    {
        if (!(value instanceof final Date dateValue))
            return true;

        return data.getMin().compareTo(dateValue) < 0;
    }

    @Override
    public void reverse(final MinData data, final Object value)
    {
    }

    @Override
    public Object getResult(final MinData data)
    {
        return data.getMin();
    }

    @Override
    public boolean supportsReverse()
    {
        return false;
    }

    @Override
    public Class<?> getResultType()
    {
        return Date.class;
    }
}
