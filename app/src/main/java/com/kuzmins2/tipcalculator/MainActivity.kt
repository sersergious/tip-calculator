package com.kuzmins2.tipcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuzmins2.tipcalculator.ui.theme.TipCalculatorTheme
import kotlin.math.ceil
import kotlin.text.toDouble

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyTipCalculator(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
// This is the main skeleton function of the entire app.
// It is more than 40 lines of code because the way I have the program
// set up is I have multiple states to track all the input and output.
// Additionally, the input validation is the huge part of the reason the
// function is so large. Most of the components are in their dedicated
// functions.
fun MyTipCalculator(modifier: Modifier = Modifier) {

    var subTotalInput by rememberSaveable { mutableStateOf("") }
    var tipPercentageInput by rememberSaveable { mutableStateOf("15") }
    var numPeopleInput by rememberSaveable { mutableStateOf("") }

    var subTotalValue by rememberSaveable { mutableDoubleStateOf(0.0) }
    var tipPercentageValue by rememberSaveable { mutableDoubleStateOf(15.0) }
    var numPeopleValue by rememberSaveable { mutableIntStateOf(0) }

    var totalCost by rememberSaveable { mutableDoubleStateOf(0.0) }
    var amountPerPerson by rememberSaveable { mutableDoubleStateOf(0.0) }

    var checkedState by rememberSaveable { mutableStateOf(false) }

    val isSubTotalValid by remember(subTotalInput) {
        derivedStateOf { (subTotalInput.toDoubleOrNull() ?: 0.0) > 0.0 }
    }
    val isTipValid by remember(tipPercentageInput) {
        derivedStateOf { (tipPercentageInput.toDoubleOrNull() ?: 0.0) > 0.0 }
    }
    val isNumPeopleValid by remember(numPeopleInput) {
        derivedStateOf { (numPeopleInput.toIntOrNull() ?: 0) >= 1 }
    }
    val isEnabled by remember(isSubTotalValid, isTipValid, isNumPeopleValid) {
        derivedStateOf { isSubTotalValid && isTipValid && isNumPeopleValid }
    }

    val radioOptions = listOf(15, 20, 25)
    var selectedOption: Int? by rememberSaveable { mutableStateOf(radioOptions[0]) }
    val myColors = RadioButtonDefaults.colors(
        selectedColor = Color.Black,
        unselectedColor = Color.Blue
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Subtotal(
            subTotalInput, isSubTotalValid,
            onValueChanged = { inputStr ->
                subTotalInput = inputStr
                val parsedValue = inputStr.toDoubleOrNull()
                if (parsedValue != null && parsedValue > 0) {
                    subTotalValue = parsedValue
                }
            }
        )

        TipPercentage(
            tipPercentageInput,
            onValueChanged = { inputStr ->
                tipPercentageInput = inputStr
                val parsedValue = inputStr.toDoubleOrNull()
                if (parsedValue != null && parsedValue > 0) {
                    tipPercentageValue = parsedValue
                    selectedOption = radioOptions.firstOrNull {
                        it.toDouble() == parsedValue
                    }
                } else {
                    tipPercentageValue = 15.0
                    selectedOption = 0
                }
            },
            isTipValid,
            radioOptions,
            selectedOption,
            myColors,
            onClicked = { value ->
                selectedOption = value
                tipPercentageValue = value.toDouble()
                tipPercentageInput = value.toString()
            },
        )

        NumberOfPeople(
            numPeopleInput, isNumPeopleValid,
            onValueChanged = { inputStr ->
                numPeopleInput = inputStr
                val parsedValue = inputStr.toIntOrNull()
                if (parsedValue != null && parsedValue > 0) {
                    numPeopleValue = parsedValue
                }
            }
        )

        Buttons(
            onCalculateClicked = {
                Log.d("BUTTON", "calculated")
                totalCost = subTotalValue + subTotalValue * (tipPercentageValue / 100)
                amountPerPerson = String.format("%.2f", (totalCost / numPeopleValue)).toDouble()
            },
            onResetClicked = {
                Log.d("BUTTON", "reset")
                subTotalInput = ""
                tipPercentageInput = "15.0"
                numPeopleInput = ""
                subTotalValue = 0.0
                tipPercentageValue = 15.0
                numPeopleValue = 0
                totalCost = 0.0
                amountPerPerson = 0.0
                selectedOption = radioOptions[0]
                checkedState = false
            },
            isEnabled
        )

        Costs(
            totalCost = totalCost,
            amountPerPerson = amountPerPerson,
            checkedState = checkedState,
            isEnabled = isEnabled,
            modifier = modifier,
            onCheckedChanged = { isChecked ->
                checkedState = isChecked
                if (isChecked) {
                    amountPerPerson = ceil(amountPerPerson)
                    totalCost = amountPerPerson * numPeopleValue
                } else {
                    totalCost = subTotalValue + subTotalValue * (tipPercentageValue / 100)
                    amountPerPerson = String.format("%.2f", (totalCost / numPeopleValue)).toDouble()
                }
            }
        )
    }
}

@Composable
fun Subtotal(subTotalInput: String,
             isSubTotalValid: Boolean,
             onValueChanged: (String) -> Unit) {
    //Input
    OutlinedTextField( //subtotal amount
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = subTotalInput,
        prefix = {Text("$")},
        placeholder = {Text("20.00")},
        label = { Text("Subtotal amount") },
        onValueChange = onValueChanged,
        isError = !isSubTotalValid && subTotalInput.isNotEmpty(),
        supportingText = {
            if(!isSubTotalValid && subTotalInput.isNotEmpty()) {Text(text="Number must be positive")}
        },
        singleLine = true
    )
}

@Composable
fun TipPercentage(tipPercentageInput: String,
                  onValueChanged: (String) -> Unit,
                  isTipValid: Boolean,
                  radioOptions: List<Int>,
                  selectedOption: Int?,
                  myColors: RadioButtonColors,
                  onClicked: (Int) -> Unit
                  ) {

    Column(modifier = Modifier.fillMaxWidth()) {

        OutlinedTextField( //tip percentage input
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = tipPercentageInput,
            suffix = {Text("%")},
            placeholder = {Text("15")},
            label = { Text("Tip percentage") },
            onValueChange = onValueChanged,
            isError = !isTipValid && tipPercentageInput.isNotEmpty(),
            supportingText = {
                if(!isTipValid && tipPercentageInput.isNotEmpty()) {Text(text="Number must be positive")}
            },
            singleLine = true
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            radioOptions.forEach { value ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RadioButton(
                        selected = (value == selectedOption),
                        colors = myColors,
                        onClick = {onClicked(value)}
                    )
                    Text(text = value.toString())
                }
            }
        }

    }
}

@Composable
fun NumberOfPeople(numPeopleInput: String,
                   isNumPeopleValid: Boolean,
                   onValueChanged: (String) -> Unit) {
    OutlinedTextField( //num of people
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = numPeopleInput,
        label = {Text("Number of people")},
        placeholder = {Text("0")},
        onValueChange = onValueChanged,
        isError = !isNumPeopleValid && numPeopleInput.isNotEmpty(),
        supportingText = {
            if(!isNumPeopleValid && numPeopleInput.isNotEmpty()) {Text(text="Number must be at least 1")}
        },
        singleLine = true
    )
}

@Composable
fun Buttons(onCalculateClicked: () -> Unit,
            onResetClicked: () -> Unit,
            isEnabled: Boolean) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp) // space between buttons
    ) {
        ElevatedButton(
            onClick = onCalculateClicked,
            enabled = isEnabled,
            shape = CircleShape,
            modifier = Modifier.weight(1f), // takes half the row
            contentPadding = PaddingValues(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Blue
            )
        ) {
            Text("Calculate", fontSize = 24.sp)
        }

        ElevatedButton(
            onClick = onResetClicked,
            enabled = isEnabled,
            shape = CircleShape,
            modifier = Modifier.weight(1f), // takes half the row
            contentPadding = PaddingValues(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Blue
            )
        ) {
            Text("Reset", fontSize = 24.sp)
        }
    }
}

@Composable
fun Costs(totalCost: Double,
          amountPerPerson: Double,
          checkedState: Boolean,
          onCheckedChanged: (Boolean) -> Unit,
          isEnabled: Boolean,
          modifier: Modifier) {

    if(isEnabled) {
        Column(
            modifier = modifier.fillMaxWidth(), //might change to modifier.fillMaxWidth
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total cost: $${totalCost}",
                color = Color.Blue,
                fontSize = 26.sp, // increased font size
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 4.dp) // smaller space below
            )


            Text(
                text = "Amount per person: $${amountPerPerson}",
                color = Color.Blue,
                fontSize = 26.sp, // increased font size
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 0.dp) // optional, minimal padding
            )

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Round up? ",
                    fontSize = 20.sp, // increased font size
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

                Checkbox(
                    checked = checkedState,
                    onCheckedChange = onCheckedChanged
                )
            }
        }
    }
}