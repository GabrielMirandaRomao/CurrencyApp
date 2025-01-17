package com.gabriel_miranda.currencyapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import com.gabriel_miranda.currencyapp.domain.model.CurrencyType
import com.gabriel_miranda.currencyapp.ui.component.CurrencyPickerDialog
import com.gabriel_miranda.currencyapp.ui.component.HomeBody
import com.gabriel_miranda.currencyapp.ui.component.HomeHeader
import com.gabriel_miranda.currencyapp.ui.theme.surfaceColor

class HomeScreen: Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()

        val allCurrencies = viewModel.allCurrencies
        val rateStatus by viewModel.ratesStatus
        val sourceCurrency by viewModel.sourceCurrency
        val targetCurrency by viewModel.targetCurrency

        var amount by rememberSaveable { mutableStateOf(0.0) }

        var selectedCurrencyType: CurrencyType by remember {
            mutableStateOf(CurrencyType.None)
        }
        var dialogOpened by remember { mutableStateOf(false) }

        if (dialogOpened && selectedCurrencyType != CurrencyType.None) {
            CurrencyPickerDialog(
                currencies = allCurrencies,
                currencyType = selectedCurrencyType,
                onConfirmClick = { currencyCode ->
                    when(selectedCurrencyType) {
                        is CurrencyType.Source -> {
                            viewModel.sendEvent(
                                HomeUiEvent.SaveSourceCurrencyCode(
                                    code = currencyCode.name
                                )
                            )
                        }

                        is CurrencyType.Target -> {
                            viewModel.sendEvent(
                                HomeUiEvent.SaveTargetCurrencyCode(
                                    code = currencyCode.name
                                )
                            )
                        }

                        is CurrencyType.None -> TODO()
                    }
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                },
                onDismiss = {
                    selectedCurrencyType = CurrencyType.None
                    dialogOpened = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surfaceColor)
        ) {
            HomeHeader(
                status = rateStatus,
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount,
                onAmountChange = {
                    amount = it
                },
                onRatesRefresh = {
                    viewModel.sendEvent(
                        HomeUiEvent.RefreshRates
                    )
                },
                onSwitchClick = {
                    viewModel.sendEvent(
                        HomeUiEvent.SwitchCurrencies
                    )
                },
                onCurrencyTypeSelect = { currencyType ->
                    selectedCurrencyType = currencyType
                    dialogOpened = true
                }
            )

            HomeBody(
                source = sourceCurrency,
                target = targetCurrency,
                amount = amount
            )
        }
    }
}