package ru.ifmo.vladyaglamunov.calculator;

/**
 * Created by Влад on 17.09.2016.
 */
abstract public class Operation {
    abstract double calculate(double a, double b);

    abstract int getSign();

    static class Nothing extends Operation {
        double calculate(double a, double b) {
            return b;
        }

        @Override
        int getSign() {
            return 0;
        }
    }

    static class Add extends Operation {
        double calculate(double a, double b) {
            return a + b;
        }

        @Override
        int getSign() {
            return R.string.add;
        }
    }

    static class Subtract extends Operation {
        double calculate(double a, double b) {
            return a - b;
        }

        @Override
        int getSign() {
            return R.string.sub;
        }
    }

    static class Multiply extends Operation {
        double calculate(double a, double b) {
            return a * b;
        }

        @Override
        int getSign() {
            return R.string.mul;
        }
    }

    static class Divide extends Operation {
        double calculate(double a, double b) {
            return a / b;
        }

        @Override
        int getSign() {
            return R.string.div;
        }
    }

}
