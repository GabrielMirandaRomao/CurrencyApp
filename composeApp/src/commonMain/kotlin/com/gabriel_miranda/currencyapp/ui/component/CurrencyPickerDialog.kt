package com.gabriel_miranda.currencyapp.ui.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.gabriel_miranda.currencyapp.domain.model.Currency
import com.gabriel_miranda.currencyapp.domain.model.CurrencyCode
import com.gabriel_miranda.currencyapp.domain.model.CurrencyType
import com.gabriel_miranda.currencyapp.ui.theme.primaryLight
import com.gabriel_miranda.currencyapp.ui.theme.surfaceColor

@Composable
fun CurrencyPickerDialog(
    currencies: List<Currency>,
    currencyType: CurrencyType,
    onConfirmClick: (CurrencyCode) -> Unit,
    onDismiss: () -> Unit
) {

    val allCurrencies = remember {
        mutableStateListOf<Currency>().apply { addAll(currencies) }
    }

    var searchQuery by remember { mutableStateOf("") }

    var selectedCurrencyCode by remember(currencyType) {
        mutableStateOf(currencyType.code)
    }

    AlertDialog(
        containerColor = surfaceColor,
        title = {
            Text(
                text = "Select a currency",
                color = primaryLight
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(size = 99.dp)),
                    value = searchQuery,
                    onValueChange = { query ->
                        searchQuery = query.uppercase()

                        if(query.isNotEmpty()) {
                            val filteredCurrencies = allCurrencies.filter {
                                it.code.contains(query.uppercase())
                            }
                            allCurrencies.clear()
                            allCurrencies.addAll(filteredCurrencies)
                        } else {
                            allCurrencies.clear()
                            allCurrencies.addAll(currencies)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Search here",
                            color = MaterialTheme.colorScheme.outline,
                            fontSize = MaterialTheme.typography.bodySmall.fontSize
                        )
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Black.copy(alpha = 0.05f),
                        unfocusedContainerColor = Color.Black.copy(alpha = 0.05f),
                        disabledContainerColor = Color.Black.copy(alpha = 0.05f),
                        errorContainerColor = Color.Black.copy(alpha = 0.05f),
                        focusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedContent(
                    targetState = allCurrencies
                ) { availableCurrencies ->
                    if (availableCurrencies.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = availableCurrencies,
                                key = { it._id.toHexString() }
                            ) { currency ->
                                CurrencyCodePickerView(
                                    code = CurrencyCode.valueOf(currency.code),
                                    isSelected = selectedCurrencyCode.name == currency.code,
                                    onSelect = { selectedCurrencyCode = it }
                                )
                            }
                        }
                    } else {
                        ErrorScreen(modifier = Modifier.height(250.dp))
                    }
                }
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirmClick(selectedCurrencyCode) }) {
                Text(
                    text = "Confirm",
                    color = primaryLight
                )
            }
        }
    )
}