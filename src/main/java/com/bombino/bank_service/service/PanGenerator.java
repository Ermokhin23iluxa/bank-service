package com.bombino.bank_service.service;

import java.util.Random;

public class PanGenerator {
    private static final Random RAND = new Random();

    private static final String BIN = "552280";

    public static String generatePanWithLuhn() {
        // BIN (6) + payload (9) + check(1) = 16
        int payloadLength = 9;
        StringBuilder sb = new StringBuilder(BIN);
        for (int i = 0; i < payloadLength; i++) {
            sb.append(RAND.nextInt(10));
        }
        String withoutCheck = sb.toString();
        char check = computeLuhnCheckDigit(withoutCheck);
        return withoutCheck + check;
    }

    private static char computeLuhnCheckDigit(String number) {
        int sum = 0;
        boolean alternate = true; // because we'll process from rightmost (without check). Adapt accordingly
        for (int i = number.length() - 1; i >= 0; i--) {
            int n = number.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n -= 9;
            }
            sum += n;
            alternate = !alternate;
        }
        int check = (10 - (sum % 10)) % 10;
        return (char) ('0' + check);
    }
    public static boolean luhnValidate(String pan) {
        int sum = 0;
        boolean alternate = false;
        for (int i = pan.length() - 1; i >= 0; i--) {
            int n = pan.charAt(i) - '0';
            if (alternate) { n *= 2; if (n > 9) n -= 9; }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }
}
