package com.spe.calculator.service;

import com.spe.calculator.exception.DivisionByZeroException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalculatorServiceTest {

    private final CalculatorService service = new CalculatorService();

    // BigDecimal.equals() also compares scale (5 != 5.0), so compare numerically instead
    private static void assertNumEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "expected " + expected + " but was " + actual);
    }

    @ParameterizedTest
    @CsvSource({"2,3,5", "-2,3,1", "0.1,0.2,0.3", "0,0,0"})
    void add_returnsSum(String a, String b, String expected) {
        assertNumEquals(expected, service.add(new BigDecimal(a), new BigDecimal(b)));
    }

    @Test
    void subtract_returnsDifference() {
        assertNumEquals("6", service.subtract(new BigDecimal("10"), new BigDecimal("4")));
        assertNumEquals("-5", service.subtract(new BigDecimal("3"), new BigDecimal("8")));
    }

    @Test
    void multiply_returnsProduct() {
        assertNumEquals("42", service.multiply(new BigDecimal("6"), new BigDecimal("7")));
        assertNumEquals("0", service.multiply(new BigDecimal("0"), new BigDecimal("99")));
    }

    @Test
    void divide_returnsQuotient() {
        assertNumEquals("2.5", service.divide(new BigDecimal("10"), new BigDecimal("4")));
        assertNumEquals("-3", service.divide(new BigDecimal("-9"), new BigDecimal("3")));
    }

    @Test
    void divide_byZero_throwsException() {
        DivisionByZeroException ex = assertThrows(DivisionByZeroException.class,
                () -> service.divide(new BigDecimal("5"), BigDecimal.ZERO));
        assertEquals("Division by zero is not allowed", ex.getMessage());
    }

    @Test
    void divide_byZeroWithDecimals_alsoThrowsException() {
        assertThrows(DivisionByZeroException.class,
                () -> service.divide(new BigDecimal("5"), new BigDecimal("0.00")));
    }
}
