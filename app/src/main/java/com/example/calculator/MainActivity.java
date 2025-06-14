package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvExpression, tvResult;
    private StringBuilder expression = new StringBuilder();
    private WebView webView;
    private boolean justEvaluated = false; // ✅ Track if equals was just pressed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvExpression = findViewById(R.id.tvExpression);
        tvResult = findViewById(R.id.tvResult);
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);

        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn00, R.id.btnDecimal, R.id.btnAdd, R.id.btnSubtract,
                R.id.btnMultiply, R.id.btnDivide
        };

        View.OnClickListener buttonClickListener = v -> {
            Button b = (Button) v;
            String input = b.getText().toString();

            if (justEvaluated) {
                expression.setLength(0);
                expression.append(tvResult.getText().toString());
                justEvaluated = false;
            }

            if ("+−×÷".contains(input)) {
                if (expression.length() > 0) {
                    char lastChar = expression.charAt(expression.length() - 1);
                    if ("+−×÷".indexOf(lastChar) != -1) {
                        // Replace last operator
                        expression.setCharAt(expression.length() - 1, input.charAt(0));
                    } else {
                        expression.append(input);
                    }
                }
            } else {
                expression.append(input);
            }

            tvResult.setText(expression.toString());
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(buttonClickListener);
        }

        findViewById(R.id.btnPercent).setOnClickListener(v -> {
            if (justEvaluated) {
                expression.setLength(0);
                expression.append(tvResult.getText().toString());
                justEvaluated = false;
            }
            expression.append("%");
            tvResult.setText(expression.toString());
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            expression.setLength(0);
            tvExpression.setText("");
            tvResult.setText("0");
            justEvaluated = false;
        });

        findViewById(R.id.btnBackspace).setOnClickListener(v -> {
            if (expression.length() > 0) {
                expression.deleteCharAt(expression.length() - 1);
                tvResult.setText(expression.toString());
            }
        });

        findViewById(R.id.btnEquals).setOnClickListener(v -> {
            try {
                String expr = expression.toString()
                        .replace("÷", "/")
                        .replace("×", "*")
                        .replace("−", "-")
                        .replaceAll("(\\d+)%(\\d+)", "($1/100)*$2")
                        .replace("%of", "/100*");

                tvExpression.setText(expression.toString()); // Show original equation

                webView.evaluateJavascript("eval('" + expr + "')", value -> {
                    value = value.replaceAll("\"", "");
                    tvResult.setText(value);
                    justEvaluated = true; // ✅ Ready for next operation
                });

            } catch (Exception e) {
                tvResult.setText("Error");
            }
        });
    }
}
