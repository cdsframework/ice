package org.opencds.vmr.v1_0.internal;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class EvaluatedPersonAgeAtEvalTime
{
    @Getter
    @Setter
    public static String AGE_UNIT_YEAR = "1";

    @Getter
    @Setter
    public static String AGE_UNIT_MONTH = "2";

    @Getter
    @Setter
    public static String AGE_UNIT_WEEK = "3";

    @Getter
    @Setter
    public static String AGE_UNIT_DAY = "5";

    @Getter
    @Setter
    public static String AGE_UNIT_HOUR = "11";

    @Getter
    @Setter
    public static String AGE_UNIT_MINUTE = "12";

    @Getter
    @Setter
    public static String AGE_UNIT_SECOND = "13";

    private String personId;
    private Integer age;
    private String ageUnit;
}
