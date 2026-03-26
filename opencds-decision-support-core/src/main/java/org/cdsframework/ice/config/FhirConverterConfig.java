package org.cdsframework.ice.config;

import org.hl7.fhir.Code;
import org.hl7.fhir.DateTime;
import org.hl7.fhir.Decimal;
import org.hl7.fhir.Uri;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class FhirConverterConfig
{
    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, Code> stringToCodeConverter()
    {
        return new Converter<String, Code>()
        {
            @Override
            public Code convert(final String source)
            {
                final Code code = new Code();
                code.setValue(source);
                return code;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, org.hl7.fhir.String> stringToFhirStringConverter()
    {
        return new Converter<String, org.hl7.fhir.String>()
        {
            @Override
            public org.hl7.fhir.String convert(final String source)
            {
                final org.hl7.fhir.String fhirString = new org.hl7.fhir.String();
                fhirString.setValue(source);
                return fhirString;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, org.hl7.fhir.Boolean> stringToFhirBooleanConverter()
    {
        return new Converter<String, org.hl7.fhir.Boolean>()
        {
            @Override
            public org.hl7.fhir.Boolean convert(final String source)
            {
                final org.hl7.fhir.Boolean fhirBoolean = new org.hl7.fhir.Boolean();
                fhirBoolean.setValue(java.lang.Boolean.valueOf(source));
                return fhirBoolean;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, org.hl7.fhir.Integer> stringToFhirIntegerConverter()
    {
        return new Converter<String, org.hl7.fhir.Integer>()
        {
            @Override
            public org.hl7.fhir.Integer convert(final String source)
            {
                final org.hl7.fhir.Integer fhirInteger = new org.hl7.fhir.Integer();
                fhirInteger.setValue(java.lang.Integer.valueOf(source));
                return fhirInteger;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, Uri> stringToUriConverter()
    {
        return new Converter<String, Uri>()
        {
            @Override
            public Uri convert(final String source)
            {
                final Uri uri = new Uri();
                uri.setValue(source);
                return uri;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, Decimal> stringToDecimalConverter()
    {
        return new Converter<String, Decimal>()
        {
            @Override
            public Decimal convert(final String source)
            {
                final Decimal decimal = new Decimal();
                decimal.setValue(source);
                return decimal;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, DateTime> stringToDateTimeConverter()
    {
        return new Converter<String, DateTime>()
        {
            @Override
            public DateTime convert(final String source)
            {
                final DateTime dateTime = new DateTime();
                dateTime.setValue(source);
                return dateTime;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, org.hl7.fhir.Markdown> stringToMarkdownConverter()
    {
        return new Converter<String, org.hl7.fhir.Markdown>()
        {
            @Override
            public org.hl7.fhir.Markdown convert(final String source)
            {
                final org.hl7.fhir.Markdown md = new org.hl7.fhir.Markdown();
                md.setValue(source);
                return md;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<Number, org.hl7.fhir.Integer> numberToFhirIntegerConverter()
    {
        return new Converter<Number, org.hl7.fhir.Integer>()
        {
            @Override
            public org.hl7.fhir.Integer convert(final Number source)
            {
                final org.hl7.fhir.Integer fhirInteger = new org.hl7.fhir.Integer();
                fhirInteger.setValue(source == null ? null : source.intValue());
                return fhirInteger;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<Boolean, org.hl7.fhir.Boolean> booleanToFhirBooleanConverter()
    {
        return new Converter<Boolean, org.hl7.fhir.Boolean>()
        {
            @Override
            public org.hl7.fhir.Boolean convert(final Boolean source)
            {
                final org.hl7.fhir.Boolean fhirBoolean = new org.hl7.fhir.Boolean();
                fhirBoolean.setValue(source);
                return fhirBoolean;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<Number, Decimal> numberToDecimalConverter()
    {
        return new Converter<Number, Decimal>()
        {
            @Override
            public Decimal convert(final Number source)
            {
                final Decimal decimal = new Decimal();
                if (source != null)
                {
                    decimal.setValue(source.toString());
                }
                return decimal;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<Number, Code> numberToCodeConverter()
    {
        return new Converter<Number, Code>()
        {
            @Override
            public Code convert(final Number source)
            {
                final Code code = new Code();
                if (source != null)
                {
                    code.setValue(source.toString());
                }
                return code;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, org.hl7.fhir.CodeSystemContentMode> stringToCodeSystemContentModeConverter()
    {
        return new Converter<String, org.hl7.fhir.CodeSystemContentMode>()
        {
            @Override
            public org.hl7.fhir.CodeSystemContentMode convert(final String source)
            {
                final org.hl7.fhir.CodeSystemContentMode mode = new org.hl7.fhir.CodeSystemContentMode();
                if (source != null)
                {
                    mode.setValue(org.hl7.fhir.CodeSystemContentModeEnum.fromValue(source));
                }
                return mode;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, org.hl7.fhir.PublicationStatus> stringToPublicationStatusConverter()
    {
        return new Converter<String, org.hl7.fhir.PublicationStatus>()
        {
            @Override
            public org.hl7.fhir.PublicationStatus convert(final String source)
            {
                final org.hl7.fhir.PublicationStatus status = new org.hl7.fhir.PublicationStatus();
                if (source != null)
                {
                    status.setValue(org.hl7.fhir.PublicationStatusEnum.fromValue(source));
                }
                return status;
            }
        };
    }

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, org.hl7.fhir.PropertyType> stringToPropertyTypeConverter()
    {
        return new Converter<String, org.hl7.fhir.PropertyType>()
        {
            @Override
            public org.hl7.fhir.PropertyType convert(final String source)
            {
                final org.hl7.fhir.PropertyType pt = new org.hl7.fhir.PropertyType();
                if (source != null)
                {
                    pt.setValue(org.hl7.fhir.PropertyTypeEnum.fromValue(source));
                }
                return pt;
            }
        };
    }
}
