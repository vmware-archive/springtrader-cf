/*
 * Copyright 2002-2012 the original author or authors.
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
package org.springframework.nanotrader.data.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @author Brian Dussault
 * Code was borrowed from original Day Trader application
 * http://svn.apache.org/repos/asf/geronimo/daytrader/trunk/javaee6/modules/web/src/main/java/org/apache/geronimo/daytrader/javaee6/core/direct/FinancialUtils.java
 */

public class FinancialUtils {
	//TO DO: Get rid of nasty static mess
	public final static int ROUND = BigDecimal.ROUND_HALF_UP;
	public final static int SCALE = 2;
	private final static BigDecimal ZERO = ( BigDecimal.valueOf(0.00))
			.setScale(SCALE);
	private final static BigDecimal ONE = ( BigDecimal.valueOf(1.00)).setScale(SCALE);
	private final static BigDecimal HUNDRED = ( BigDecimal.valueOf(100.00))
			.setScale(SCALE);

	public static BigDecimal computeGainPercent(BigDecimal currentBalance,
			BigDecimal openBalance) {
		if (openBalance.doubleValue() == 0.0)
			return ZERO;
		BigDecimal gainPercent = currentBalance.divide(openBalance, ROUND)
				.subtract(ONE).multiply(HUNDRED);
		return gainPercent;
	}


	public static BigDecimal calculateGainPercentage(BigDecimal gain, BigDecimal totalGains) { 
		BigDecimal percent =  BigDecimal.valueOf(0);
		percent = gain.divide(totalGains, 4, RoundingMode.HALF_UP);
		percent = percent.multiply( BigDecimal.valueOf(100),
					new MathContext(4, RoundingMode.HALF_UP));
		return percent;
	}
	
}



