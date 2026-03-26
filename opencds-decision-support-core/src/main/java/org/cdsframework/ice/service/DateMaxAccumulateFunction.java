package org.cdsframework.ice.service;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import org.drools.core.base.accumulators.AbstractAccumulateFunction;

import lombok.Getter;
import lombok.Setter;

public class DateMaxAccumulateFunction extends AbstractAccumulateFunction<DateMaxAccumulateFunction.MaxData>
{
    @Getter
    @Setter
    public static class MaxData implements Externalizable
    {
        private Date max;

        @Override
        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException
        {
            max = (Date) in.readObject();
        }

        @Override
        public void writeExternal(final ObjectOutput out) throws IOException
        {
            out.writeObject(max);
        }

        @Override
        public String toString()
        {
            return "max";
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
    public MaxData createContext()
    {
        return new MaxData();
    }

    @Override
    public void init(final MaxData data)
    {
        data.setMax(null);
    }

    @Override
    public void accumulate(final MaxData data, final Object value)
    {
        if (value instanceof final Date dateValue)
            data.setMax(data.getMax() == null || data.getMax().compareTo(dateValue) < 0 ? dateValue : data.getMax());
    }

    @Override
    public boolean tryReverse(final MaxData data, final Object value)
    {
        if (!(value instanceof final Date dateValue))
            return true;

        return data.getMax().compareTo(dateValue) > 0;
    }

    @Override
    public void reverse(final MaxData data, final Object value)
    {
    }

    @Override
    public Object getResult(final MaxData data)
    {
        return data.getMax();
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
