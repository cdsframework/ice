/*
 * Copyright 2016-2020 OpenCDS.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencds.plugin;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.opencds.plugin.SepsisPredictionInstance.Attribute;

public class AcuityScore {
    private int tempScore;
    private int systolicScore;
    private int respScore;
    private int pulseScore;

    private int mewsScore;

    private Set<Attribute> missingValues = new HashSet<>();

    public AcuityScore(Double temp, BigDecimal systolic, Double respRate, Double pulse) {
        scoreTemperature(temp);
        scoreSystolic(systolic);
        scoreRespRate(respRate);
        scorePulse(pulse);

        calculateMews();
    }

    public int getScore() {
        return mewsScore;
    }

    public boolean hasMissingValues() {
        return missingValues.size() > 0;
    }

    public Set<Attribute> getMissingValues() {
        return missingValues;
    }

    private void calculateMews() {
        int score = 0;
        if (tempScore == -1) {
            missingValues.add(Attribute.TEMP_TTL_NNUL);
            score = -1;
        }
        if (systolicScore == -1) {
            missingValues.add(Attribute.SBP_TTL_NNUL);
            score = -1;
        }
        if (respScore == -1) {
            missingValues.add(Attribute.RESP_RATE_TTL_NNUL);
            score = -1;
        }
        if (pulseScore == -1) {
            missingValues.add(Attribute.PULSE_TTL_NNUL);
            score = -1;
        }
        if (score != -1) {
            score += systolicScore;
            score += tempScore;
            score += respScore;
            score += pulseScore;
        }
        mewsScore = score;
    }

    /**
     * Scores the temperature.
     *
     * @param temperature
     * 		Temperature in Celsius.
     */
    private void scoreTemperature(Double temperature) {
        int result = -1;

        if (temperature != null) {

            if (temperature <= 35.0) {
                result = 2;
            } else if (temperature > 35 && temperature <= 35.5) {
                result = 1;
            } else if (temperature > 35.5 && temperature <= 38.0) {
                result = 0;
            } else if (temperature > 38 && temperature <= 39.0) {
                result = 1;
            } else if (temperature > 39 && temperature <= 40.9){
				result = 2;
			} else if (temperature > 40.9){
            	result = 3;
			}
        }
        this.tempScore = result;
    }

    private void scoreSystolic(BigDecimal systolicBD) {
        int result = -1;

        if (systolicBD != null) {
            double systolic = systolicBD.doubleValue();
            if (systolic <= 80) {
                result = 3;
            } else if (systolic > 80 && systolic <= 90) {
                result = 2;
            } else if (systolic > 90 && systolic <= 100) {
                result = 1;
            } else if (systolic > 100 && systolic <= 180) {
                result = 0;
            } else if (systolic > 180 && systolic <= 200) {
                result = 1;
            } else if (systolic > 200 && systolic <= 220) {
                result = 2;
            } else if (systolic > 220){
                result = 3;
            }
        }
        this.systolicScore = result;
    }

    private void scoreRespRate(Double respRate) {
        int result = -1;
        if (respRate != null) {
            if (respRate <= 8) {
                result = 3;
            } else if (respRate > 8 && respRate <= 11) {
                result = 1;
            } else if (respRate > 11 && respRate <= 20) {
                result = 0;
            } else if (respRate > 20 && respRate <= 25) {
                result = 1;
            } else if (respRate > 25 && respRate <= 29) {
                result = 2;
            } else if (respRate > 29){
                result = 3;
            }
        }
        this.respScore = result;
    }

    private void scorePulse(Double pulse) {
        int result = -1;
        if (pulse != null) {
            if (pulse <= 30) {
                result = 3;
            } else if (pulse > 30 && pulse <= 39) {
                result = 2;
            } else if (pulse > 39 && pulse <= 100) {
                result = 0;
            } else if (pulse > 100 && pulse <= 110) {
                result = 1;
            } else if (pulse > 110 && pulse <= 130) {
                result = 2;
            } else if (pulse > 130){
                result = 3;
            }
        }
        this.pulseScore = result;
    }

}
