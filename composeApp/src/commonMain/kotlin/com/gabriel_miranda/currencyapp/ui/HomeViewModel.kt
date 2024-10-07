package com.gabriel_miranda.currencyapp.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.gabriel_miranda.currencyapp.domain.CurrencyApiService
import com.gabriel_miranda.currencyapp.domain.MongoRepository
import com.gabriel_miranda.currencyapp.domain.PreferencesRepository
import com.gabriel_miranda.currencyapp.domain.model.Currency
import com.gabriel_miranda.currencyapp.domain.model.RateStatus
import com.gabriel_miranda.currencyapp.domain.model.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

sealed class HomeUiEvent {
    data object RefreshRates : HomeUiEvent()
    data object SwitchCurrencies : HomeUiEvent()
    data class SaveSourceCurrencyCode(val code: String) : HomeUiEvent()
    data class SaveTargetCurrencyCode(val code: String) : HomeUiEvent()
}

class HomeViewModel(
    private val preferences: PreferencesRepository,
    private val mongoDb: MongoRepository,
    private val api: CurrencyApiService
) : ScreenModel {

    private var _ratesStatus: MutableState<RateStatus> = mutableStateOf(RateStatus.Idle)
    val ratesStatus: State<RateStatus> = _ratesStatus

    private var _sourceCurrency: MutableState<RequestState<Currency>> = mutableStateOf(RequestState.Idle)
    val sourceCurrency: State<RequestState<Currency>> = _sourceCurrency

    private var _targetCurrency: MutableState<RequestState<Currency>> = mutableStateOf(RequestState.Idle)
    val targetCurrency: State<RequestState<Currency>> = _targetCurrency

    private var _allCurrencies = mutableStateListOf<Currency>()
    val allCurrencies: List<Currency> = _allCurrencies

    init {
        screenModelScope.launch {
            fetchNewRates()
            readSourceCurrency()
            readTargetCurrency()
        }
    }

    fun sendEvent(event: HomeUiEvent) {
        when(event) {
            is HomeUiEvent.RefreshRates -> {
                screenModelScope.launch {
                    fetchNewRates()
                }
            }

            is HomeUiEvent.SwitchCurrencies -> {
                switchCurrencies()
            }

            is HomeUiEvent.SaveSourceCurrencyCode -> {
                saveSourceCurrencyCode(event.code)
            }

            is HomeUiEvent.SaveTargetCurrencyCode -> {
                saveTargetCurrencyCode(event.code)
            }
        }
    }

    private fun readSourceCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readSourceCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _sourceCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _sourceCurrency.value = RequestState.Error(message = "Couldn't find the currency")
                }
            }
        }
    }

    private fun readTargetCurrency() {
        screenModelScope.launch(Dispatchers.Main) {
            preferences.readTargetCurrencyCode().collectLatest { currencyCode ->
                val selectedCurrency = _allCurrencies.find { it.code == currencyCode.name }
                if (selectedCurrency != null) {
                    _targetCurrency.value = RequestState.Success(data = selectedCurrency)
                } else {
                    _targetCurrency.value = RequestState.Error(message = "Couldn't find the currency")
                }
            }
        }
    }


    private suspend fun fetchNewRates() {
        try {
            val localCache = mongoDb.readCurrencyData().first()

            if (localCache.isSuccess()) {
                if (localCache.getSuccessData().isNotEmpty()) {
                    println("HomeViewModel: DATABASE IS FULL")
                    _allCurrencies.clear()
                    _allCurrencies.addAll(localCache.getSuccessData())
//                    if (!preferences.isDataFresh(Clock.System.now().toEpochMilliseconds())) {
//                        println("HomeViewModel: DATA NOT FRESH")
//                        cacheTheData()
//                    } else {
//                        println("HomeViewModel: DATA IS FRESH")
//                    }
                } else {
                    println("HomeViewModel: DATABASE NEEDS DATA")
//                    cacheTheData()
                }
            } else if (localCache.isError()) {
                println("HomeViewModel: ERROR READING LOCAL DATABASE ${localCache.getErrorMessage()}")
            }
            getRateStatus()
        } catch (e: Exception) {
            println(e.message)
        }
    }

    private fun cacheTheData() {
        screenModelScope.launch {
            val fetchedData = api.getLatestExchangeRates()
            if (fetchedData.isSuccess()){
                mongoDb.cleanUp()
                fetchedData.getSuccessData().forEach {
                    println("HomeViewModel: ADDING ${it.code}")
                    mongoDb.insertCurrencyData(it)
                }
                println("HomeViewModel: UPDATING _allCurrencies")
                _allCurrencies.clear()
                _allCurrencies.addAll(fetchedData.getSuccessData())
            }else if (fetchedData.isError()){
                println("HomeViewModel: FETCHING FAILED ${fetchedData.getErrorMessage()}")
            }
        }
    }

    private suspend fun getRateStatus() {
        _ratesStatus.value = if (!preferences.isDataFresh(
                currentTimestamp = Clock.System.now().toEpochMilliseconds()
            )
        ) {
            RateStatus.Fresh
        } else {
            RateStatus.Stale
        }

    }

    private fun switchCurrencies() {
        val source = _sourceCurrency.value
        val target = _targetCurrency.value
        _sourceCurrency.value = target
        _targetCurrency.value = source
    }

    private fun saveSourceCurrencyCode(code: String) {
        screenModelScope.launch(Dispatchers.IO) {
            preferences.saveSourceCurrencyCode(code)
        }
    }

    private fun saveTargetCurrencyCode(code: String) {
        screenModelScope.launch(Dispatchers.IO) {
            preferences.saveTargetCurrencyCode(code)
        }
    }
}