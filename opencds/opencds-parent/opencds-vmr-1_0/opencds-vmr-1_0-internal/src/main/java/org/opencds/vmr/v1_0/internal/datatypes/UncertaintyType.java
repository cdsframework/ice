/**
 * Copyright 2011 OpenCDS.org
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
 */

package org.opencds.vmr.v1_0.internal.datatypes;

import javax.xml.bind.annotation.XmlEnumValue;


/**
 * <p>Java class for UncertaintyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 */

public enum UncertaintyType {


    /**
     * Uniform : The uniform distribution assigns a constant probability over the entire interval of possible outcomes, while all outcomes outside this interval are assumed to have zero probability. The width of this interval is 2 s v3. Thus, the uniform distribution assigns the probability densities f(x) = (2 s v3)-1 to values µ - s v3 = x = µ + s v3 and f(x) = 0 otherwise
     * 
     */
    U("U"),

    /**
     * Normal (Gaussian) : This is the well-known bell-shaped normal distribution. Because of the central limit theorem, the normal distribution is the distribution of choice for an unbounded random variable that is an outcome of a combination of many stochastic processes. Even for values bounded on a single side (i.e. greater than 0) the normal distribution may be accurate enough if the mean is "far away" from the bound of the scale measured in terms of standard deviations
     * 
     */
    N("N"),

    /**
     * Log-Normal : The logarithmic normal distribution is used to transform skewed random variable X into a normally distributed random variable U = log X. The log-normal distribution can be specified with the properties mean µ and standard deviation s. Note however that mean µ and standard deviation s are the parameters of the raw value distribution, not the transformed parameters of the lognormal distribution that are conventionally referred to by the same letters. Those log-normal parameters µ log and slog relate to the mean µ and standard deviation s of the data value through slog2 = log (s2/µ2 + 1) and µlog = log µ - slog2/2
     * 
     */
    LN("LN"),

    /**
     * ? (gamma) : The gamma-distribution used for data that is skewed and bounded to the right, i.e. where the maximum of the distribution curve is located near the origin. The ?-distribution has two parameters a and ß. The relationship to mean µ and variance s2 is µ = a ß and s2 = a ß2
     * 
     */
    G("G"),

    /**
     * Exponential : Used for data that describes extinction. The exponential distribution is a special form of ?-distribution where a = 1, hence, the relationship to mean µ and variance s2 are µ = ß and s2 = ß2
     * 
     */
    E("E"),

    /**
     * ? : Used to describe the sum of squares of random variables that occurs when a variance is estimated (rather than presumed) from the sample. The only parameter of the ?2-distribution is ?, so called the number of degrees of freedom (which is the number of independent parts in the sum). The ?2-distribution is a special type of ?-distribution with parameter a = ? /2 and ß = 2. Hence, µ = ? and s2 = 2 ?
     * 
     */
    @XmlEnumValue("X2")
    X_2("X2"),

    /**
     * t (student) : Used to describe the quotient of a normal random variable and the square root of a ?2 random variable. The t-distribution has one parameter ?, the number of degrees of freedom. The relationship to mean µ and variance s2 are: µ = 0 and s2 = ? / (? - 2)
     * 
     */
    T("T"),

    /**
     * F : Used to describe the quotient of two ?2 random variables. The F-distribution has two parameters ?1 and ?2, which are the numbers of degrees of freedom of the numerator and denominator variable respectively. The relationship to mean µ and variance s2 are: µ = ?2 / (?2 - 2) and s 2 = (2 ?22 (? 2 + ?1 - 2)) / (?1 (?2 - 2)2 (?2 - 4))
     * 
     */
    F("F"),

    /**
     * ?(beta) : The beta-distribution is used for data that is bounded on both sides and may or may not be skewed (e.g., occurs when probabilities are estimated.) Two parameters a and ß are available to adjust the curve. The mean µ and variance s2 relate as follows: µ = a / (a + ß) and (s2 = a ß/((a + ß)2 (a + ß + 1))
     * 
     */
    B("B");
    private final String value;

    UncertaintyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UncertaintyType fromValue(String v) {
        for (UncertaintyType c: UncertaintyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
