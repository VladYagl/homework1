package ru.ifmo.vladyaglamunov.calculator;

import android.graphics.Path;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String KEY_BUFFER = "buffer_number";
    private static final String KEY_CURRENT = "current_number";
    private static final String KEY_OPERATION = "operation";
    private static final String KEY_RESET = "reset";

    private static final String NOTHING_VALUE = "NOTHING";

    TextView resultTextView;
    TextView operationTextView;
    TextView bufferTextView;

    final Button[] digitButtons = new Button[10];
    Button clearButton;
    Button signButton;
    Button equalsButton;
    Button squareRootButton;
    Button dotButton;

    Button addButton;
    Button divideButton;
    Button subtractButton;
    Button multiplyButton;

    double currentNumber;
    double bufferNumber;
    double power = 1;
    boolean reset = false;

    Operation lastOperation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = (TextView) findViewById(R.id.result);
        bufferTextView = (TextView) findViewById(R.id.buffer);
        operationTextView = (TextView) findViewById(R.id.operation);

        digitButtons[0] = (Button) findViewById(R.id.d0);
        digitButtons[1] = (Button) findViewById(R.id.d1);
        digitButtons[2] = (Button) findViewById(R.id.d2);
        digitButtons[3] = (Button) findViewById(R.id.d3);
        digitButtons[4] = (Button) findViewById(R.id.d4);
        digitButtons[5] = (Button) findViewById(R.id.d5);
        digitButtons[6] = (Button) findViewById(R.id.d6);
        digitButtons[7] = (Button) findViewById(R.id.d7);
        digitButtons[8] = (Button) findViewById(R.id.d8);
        digitButtons[9] = (Button) findViewById(R.id.d9);

        clearButton = (Button) findViewById(R.id.clear);
        signButton = (Button) findViewById(R.id.sign);
        equalsButton = (Button) findViewById(R.id.eqv);
        squareRootButton = (Button) findViewById(R.id.sqrt);
        addButton = (Button) findViewById(R.id.add);
        divideButton = (Button) findViewById(R.id.div);
        multiplyButton = (Button) findViewById(R.id.mul);
        subtractButton = (Button) findViewById(R.id.sub);
        dotButton = (Button) findViewById(R.id.dot);

        clearButton.setOnClickListener(this);
        signButton.setOnClickListener(this);
        equalsButton.setOnClickListener(this);
        squareRootButton.setOnClickListener(this);
        addButton.setOnClickListener(this);
        divideButton.setOnClickListener(this);
        multiplyButton.setOnClickListener(this);
        subtractButton.setOnClickListener(this);
        dotButton.setOnClickListener(this);

        for (int i = 0; i < 10; i++) {
            digitButtons[i].setOnClickListener(this);
        }

        lastOperation = new Operation.Nothing();
        bufferNumber = 0;
        currentNumber = 0;

        if (savedInstanceState != null) {
            bufferNumber = savedInstanceState.getDouble(KEY_BUFFER);
            currentNumber = savedInstanceState.getDouble(KEY_CURRENT);
            reset = savedInstanceState.getBoolean(KEY_RESET);
            String c = savedInstanceState.getString(KEY_OPERATION);
            if (Objects.equals(c, getString(R.string.add))) {
                lastOperation = new Operation.Add();
            } else if (Objects.equals(c, getString(R.string.sub))) {
                lastOperation = new Operation.Subtract();
            } else if (Objects.equals(c, getString(R.string.mul))) {
                lastOperation = new Operation.Multiply();
            } else if (Objects.equals(c, getString(R.string.div))) {
                lastOperation = new Operation.Divide();
            } else if (Objects.equals(c, NOTHING_VALUE)) {
                lastOperation = new Operation.Nothing();
            }
        }

        update();
    }

    private String convert(double a, int presicion) {
        return String.format(Locale.forLanguageTag("RUS"), "%." + presicion + "g", a);
    }

    private String beautification(boolean keepDot, String number) {
        if (number.contains("e")) {
            return number;
        } else {
            int last = number.length() - 1;
            while ((number.charAt(last) == '0') && last > 0) {
                last--;
                if ((number.charAt(last) == ',' || number.charAt(last) == '.') && (power >= 1 || !keepDot)) {
                    last--;
                    break;
                }
            }
            return (number.substring(0, last + 1));
        }
    }

    private void update() {
        resultTextView.setText(beautification(true, convert(currentNumber, 6)));
        if (bufferNumber == 0) {
            bufferTextView.setText("");
            operationTextView.setText("");
        } else {
            operationTextView.setText(getString(lastOperation.getSign()));
            bufferTextView.setText(beautification(false, convert(bufferNumber, 6)));
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < 10; i++) {
            if (v == digitButtons[i]) {
                if (reset) {
                    currentNumber = 0;
                    bufferNumber = 0;
                    lastOperation = new Operation.Nothing();
                    power = 1;
                    reset = false;
                }
                double newNumber;
                if (power < 1) {
                    newNumber = currentNumber + i * power;
                } else {
                    newNumber = currentNumber * 10 + i * power;
                }
                String number = convert(newNumber, 6);
                if (!number.contains("e")) {
                    currentNumber = newNumber;
                    if (power < 1) {
                        power /= 10;
                    }
                }
            }
        }

        if (v == dotButton) {
            power = 0.1;
        }
        if (v == signButton) {
            currentNumber *= -1;
        }
        if (v == clearButton) {
            currentNumber = 0;
            bufferNumber = 0;
            lastOperation = new Operation.Nothing();
            power = 1;
            reset = false;
        }
        if (v == squareRootButton) {
            currentNumber = Math.sqrt(lastOperation.calculate(bufferNumber, currentNumber));
            lastOperation = new Operation.Nothing();
            bufferNumber = 0;
            power = 1;
            reset = true;
        }
        if (v == equalsButton) {
            currentNumber = lastOperation.calculate(bufferNumber, currentNumber);
            bufferNumber = 0;
            lastOperation = new Operation.Nothing();
            power = 1;
            reset = true;
        }

        if (v == addButton) {
            bufferNumber = lastOperation.calculate(bufferNumber, currentNumber);
            lastOperation = new Operation.Add();
            currentNumber = 0;
            power = 1;
            reset = false;
        }
        if (v == subtractButton) {
            bufferNumber = lastOperation.calculate(bufferNumber, currentNumber);
            lastOperation = new Operation.Subtract();
            currentNumber = 0;
            power = 1;
            reset = false;
        }
        if (v == divideButton) {
            bufferNumber = lastOperation.calculate(bufferNumber, currentNumber);
            lastOperation = new Operation.Divide();
            currentNumber = 0;
            power = 1;
            reset = false;
        }
        if (v == multiplyButton) {
            bufferNumber = lastOperation.calculate(bufferNumber, currentNumber);
            lastOperation = new Operation.Multiply();
            currentNumber = 0;
            power = 1;
            reset = false;
        }

        update();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(KEY_BUFFER, bufferNumber);
        outState.putDouble(KEY_CURRENT, currentNumber);
        if (lastOperation instanceof Operation.Nothing) {
            outState.putString(KEY_OPERATION, NOTHING_VALUE);
        } else {
            outState.putString(KEY_OPERATION, getString(lastOperation.getSign()));
        }
        outState.putBoolean(KEY_RESET, reset);
    }
}
