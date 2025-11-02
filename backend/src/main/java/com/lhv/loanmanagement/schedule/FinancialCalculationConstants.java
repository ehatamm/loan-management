package com.lhv.loanmanagement.schedule;

import java.math.MathContext;
import java.math.RoundingMode;

public final class FinancialCalculationConstants {
    
    private FinancialCalculationConstants() {
        // Utility class
    }
    
    public static final int CALCULATION_SCALE = 10;
    public static final int RESULT_SCALE = 2;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final MathContext MATH_CONTEXT = new MathContext(CALCULATION_SCALE + 2, ROUNDING_MODE);
    
    public static final int PERCENTAGE_DIVISOR = 100;
    public static final int MONTHS_PER_YEAR = 12;
}

