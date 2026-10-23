package com.spe.calculator.service;

import com.spe.calculator.exception.DivisionByZeroException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;

@Service
public class CalculatorService {

    // 16 significant digits, so 1/3 doesn't try to produce infinite digits
    private static final MathContext DIVISION_PRECISION = MathContext.DECIMAL64;

    public BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }

    public BigDecimal subtract(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }

    public BigDecimal multiply(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }

    public BigDecimal divide(BigDecimal a, BigDecimal b) {
        if (b.signum() == 0) {          // true for 0, 0.0, 0.00 ...
            throw new DivisionByZeroException();
        }
        return a.divide(b, DIVISION_PRECISION);
    }
}
