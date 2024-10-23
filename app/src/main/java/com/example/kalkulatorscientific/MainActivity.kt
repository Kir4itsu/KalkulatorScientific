package com.example.kalkulatorscientific

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var currentNumber by remember { mutableStateOf("") }
    var operator by remember { mutableStateOf("") }
    var firstNumber by remember { mutableStateOf(0.0) }
    var isNewNumber by remember { mutableStateOf(true) }
    var expressionHistory by remember { mutableStateOf("") }
    var isSecondNumber by remember { mutableStateOf(false) }
    var calculationHistory by remember { mutableStateOf(listOf<String>()) }
    var showHistory by remember { mutableStateOf(false) }

    val purpleColor = Color(0xFF8B00FF)
    val darkGray = Color(0xFF1E1E1E)
    val lightGray = Color(0xFF363636)
    val tealColor = Color(0xFF00BCD4)

    // Local functions
    fun handleNumber(number: String) {
        if (isNewNumber) {
            currentNumber = number
            isNewNumber = false
        } else {
            currentNumber += number
        }
    }

    fun handleOperator(op: String) {
        if (currentNumber.isNotEmpty()) {
            if (operator.isEmpty()) {
                firstNumber = currentNumber.toDouble()
                operator = op
                expressionHistory = "$currentNumber ${getOperatorSymbol(op)}"
                isNewNumber = true
                isSecondNumber = true
            } else {
                val secondNumber = currentNumber.toDouble()
                val result = calculateResult(firstNumber, secondNumber, operator)
                firstNumber = result
                currentNumber = result.toString()
                operator = op
                expressionHistory = "$result ${getOperatorSymbol(op)}"
                isNewNumber = true
                isSecondNumber = true
            }
        }
    }

    fun handleEquals() {
        if (currentNumber.isNotEmpty() && operator.isNotEmpty()) {
            val secondNumber = currentNumber.toDouble()
            val calculation = "$firstNumber ${getOperatorSymbol(operator)} $secondNumber ="
            val result = calculateResult(firstNumber, secondNumber, operator)
            currentNumber = if (result.isNaN()) "Error" else result.toString()
            calculationHistory = (calculationHistory + "$calculation $currentNumber").takeLast(10)
            operator = ""
            isNewNumber = true
            isSecondNumber = false
            expressionHistory = ""
        }
    }

    fun applyScientificFunction(function: (Double) -> Double) {
        if (currentNumber.isNotEmpty()) {
            try {
                val number = currentNumber.toDouble()
                val result = function(number)
                currentNumber = if (result.isNaN()) "Error" else result.toString()
                isNewNumber = true
            } catch (e: Exception) {
                currentNumber = "Error"
                isNewNumber = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Expression History Display
        Text(
            text = expressionHistory,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            fontSize = 24.sp,
            textAlign = TextAlign.End,
            color = Color.Gray
        )

        // Current Number Display
        Text(
            text = if (isNewNumber && isSecondNumber) "0" else currentNumber.ifEmpty { "0" },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            fontSize = 48.sp,
            textAlign = TextAlign.End,
            color = Color.White
        )

        if (showHistory) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(darkGray)
                    .padding(16.dp)
            ) {
                Text(
                    text = "History",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                calculationHistory.forEach { calculation ->
                    Text(
                        text = calculation,
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }

        // First row (AC, C, (, ))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("AC", "C", "(", ")").forEach { symbol ->
                Button(
                    onClick = {
                        when (symbol) {
                            "AC" -> {
                                currentNumber = ""
                                operator = ""
                                firstNumber = 0.0
                                isNewNumber = true
                                isSecondNumber = false
                                expressionHistory = ""
                            }
                            "C" -> {
                                if (currentNumber.isNotEmpty()) {
                                    currentNumber = currentNumber.dropLast(1)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightGray,
                        contentColor = if (symbol == "AC" || symbol == "C") purpleColor else Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = symbol, fontSize = 24.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scientific functions row (sin, cos, tan, ÷)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("sin", "cos", "tan", "÷").forEach { symbol ->
                Button(
                    onClick = {
                        when (symbol) {
                            "sin" -> applyScientificFunction { sin(it.toRadians()) }
                            "cos" -> applyScientificFunction { cos(it.toRadians()) }
                            "tan" -> applyScientificFunction { tan(it.toRadians()) }
                            "÷" -> handleOperator("/")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightGray,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = symbol, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Scientific functions row (log, ln, √, ×)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("log", "ln", "√", "×").forEach { symbol ->
                Button(
                    onClick = {
                        when (symbol) {
                            "log" -> applyScientificFunction { log10(it) }
                            "ln" -> applyScientificFunction { ln(it) }
                            "√" -> applyScientificFunction { sqrt(it) }
                            "×" -> handleOperator("*")
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lightGray,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = symbol, fontSize = 20.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Numbers 7-9 with minus
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (7..9).forEach { number ->
                Button(
                    onClick = { handleNumber(number.toString()) },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkGray,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = number.toString(), fontSize = 24.sp)
                }
            }
            Button(
                onClick = { handleOperator("-") },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkGray,
                    contentColor = purpleColor
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "-", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Numbers 4-6 with plus
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            (4..6).forEach { number ->
                Button(
                    onClick = { handleNumber(number.toString()) },
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkGray,
                        contentColor = Color.White
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = number.toString(), fontSize = 24.sp)
                }
            }
            Button(
                onClick = { handleOperator("+") },
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkGray,
                    contentColor = purpleColor
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "+", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Modified layout for numbers 1-3 and equals button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Container for numbers 1-3 and bottom row
            Column(
                modifier = Modifier.weight(3f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Numbers 1-3
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..3).forEach { number ->
                        Button(
                            onClick = { handleNumber(number.toString()) },
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = darkGray,
                                contentColor = Color.White
                            ),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text(text = number.toString(), fontSize = 24.sp)
                        }
                    }
                }

                // Bottom row (H, 0, .)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showHistory = !showHistory },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = lightGray,
                            contentColor = purpleColor
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "H", fontSize = 24.sp)
                    }

                    Button(
                        onClick = { handleNumber("0") },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = darkGray,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = "0", fontSize = 24.sp)
                    }

                    Button(
                        onClick = {
                            if (!currentNumber.contains(".")) {
                                currentNumber += if (currentNumber.isEmpty()) "0." else "."
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = darkGray,
                            contentColor = Color.White
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(text = ".", fontSize = 24.sp)
                    }
                }
            }

            // Equals button with fixed size
            Button(
                onClick = { handleEquals() },
                modifier = Modifier
                    .width(75.dp)
                    .height(162.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = purpleColor,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = "=", fontSize = 24.sp)
            }
        }
    }
}

private fun calculateResult(firstNumber: Double, secondNumber: Double, operator: String): Double {
    return when (operator) {
        "+" -> firstNumber + secondNumber
        "-" -> firstNumber - secondNumber
        "*" -> firstNumber * secondNumber
        "/" -> if (secondNumber != 0.0) firstNumber / secondNumber else Double.NaN
        else -> 0.0
    }
}

private fun getOperatorSymbol(operator: String): String {
    return when (operator) {
        "+" -> "+"
        "-" -> "-"
        "*" -> "×"
        "/" -> "÷"
        else -> operator
    }
}

private fun Double.toRadians(): Double = this * PI / 180