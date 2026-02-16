package com.kuzmins2.tipcalculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kuzmins2.tipcalculator.ui.theme.TipCalculatorTheme

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
fun MyTipCalculator(modifier: Modifier = Modifier) {

    var subTotalInput by remember { mutableStateOf("") }
    var tipPercentageInput by remember { mutableStateOf("") }
    var numPeopleInput by remember { mutableStateOf("") }

    var subTotalValue by remember { mutableDoubleStateOf(0.0) }
    var tipPercentageValue by remember { mutableDoubleStateOf(0.0) }
    var numPeopleValue by remember { mutableIntStateOf(0) }

    var totalCost by remember { mutableDoubleStateOf(0.0)}
    var amountPerPerson by remember { mutableDoubleStateOf(0.0)}

    val isSubTotalValid by remember {
        derivedStateOf { (subTotalInput.toDoubleOrNull() ?: 0.0) > 0.0 }
    }
    val isTipValid by remember {
        derivedStateOf { (tipPercentageInput.toDoubleOrNull() ?: 0.0) > 0.0 }
    }
    val isNumPeopleValid by remember {
        derivedStateOf { (numPeopleInput.toIntOrNull() ?: 0) >= 1 }
    }

    val isEnabled by remember {
        derivedStateOf { isSubTotalValid && isTipValid && isNumPeopleValid }
    }

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {

        //Input
        OutlinedTextField( //subtotal amount
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = subTotalInput,
            label = { Text("Subtotal amount") },
            onValueChange = {inputStr ->
                subTotalInput = inputStr
                val parsedValue = inputStr.toDoubleOrNull()
                if(parsedValue != null && parsedValue > 0 ) {
                    subTotalValue = parsedValue
                }

            },
            isError = !isSubTotalValid && subTotalInput.isNotEmpty(),
            supportingText = {
                if(!isSubTotalValid && subTotalInput.isNotEmpty()) {Text(text="Number must be positive")}
            }
        )

        OutlinedTextField( //tip percentage
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = tipPercentageInput,
            label = { Text("Tip percentage") },
            onValueChange = {inputStr ->
                tipPercentageInput = inputStr
                val parsedValue = inputStr.toDoubleOrNull()
                if(parsedValue != null && parsedValue > 0 ) {
                    tipPercentageValue = parsedValue
                }

            },
            isError = !isTipValid && tipPercentageInput.isNotEmpty(),
            supportingText = {
                if(!isTipValid && tipPercentageInput.isNotEmpty()) {Text(text="Number must be positive")}
            }
        )

        OutlinedTextField( //num of people
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = numPeopleInput,
            label = { Text("Number of people") },
            onValueChange = {inputStr ->
                numPeopleInput = inputStr
                val parsedValue = inputStr.toIntOrNull()
                if(parsedValue != null && parsedValue > 0 ) {
                    numPeopleValue = parsedValue
                }

            },
            isError = !isNumPeopleValid && numPeopleInput.isNotEmpty(),
            supportingText = {
                if(!isNumPeopleValid && numPeopleInput.isNotEmpty()) {Text(text="Number must be at least 1")}
            }
        )

        //Button22
        ElevatedButton(
            enabled = isEnabled,
            shape = CircleShape,
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray, contentColor = Color.Blue),
            onClick = {
                    Log.d(" BUTTON", "calculated")
                    totalCost = subTotalValue + subTotalValue * (tipPercentageValue / 100)
                    amountPerPerson = String.format("%.2f", (totalCost / numPeopleValue)).toDouble()
            }
        ) {
            Text( "Click to Calculate", fontSize = 24.sp)
        }


        //Output
        if(isEnabled) {
            Text( //total cost
                text = "Total cost: $totalCost",
                color = Color.Red,
                fontSize = 30.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )

            Text( //amount per person

                text = "Amount per person: $amountPerPerson",
                color = Color.Red,
                fontSize = 25.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            )
        }

    }

}
