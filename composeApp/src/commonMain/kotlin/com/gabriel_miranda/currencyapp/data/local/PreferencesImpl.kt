package com.gabriel_miranda.currencyapp.data.local

import com.gabriel_miranda.currencyapp.domain.PreferencesRepository
import com.gabriel_miranda.currencyapp.domain.model.CurrencyCode
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class PreferencesImpl(private val settings: Settings) : PreferencesRepository {

    companion object {
        val DEFAULT_SOURCE_CURRENCY = CurrencyCode.USD.name
        val DEFAULT_TARGET_CURRENCY = CurrencyCode.BRL.name
        const val TIMESTAMP_KEY = "lastUpdated"
        const val SOURCE_CURRENCY_KEY = "sourceCurrency"
        const val TARGET_CURRENCY_KEY = "targetCurrency"
    }

    @OptIn(ExperimentalSettingsApi::class)
    private val flowSettings: FlowSettings = (settings as ObservableSettings).toFlowSettings()

    @OptIn(ExperimentalSettingsApi::class)
    override suspend fun saveLastUpdated(lastUpdated: String) {
        flowSettings.putLong(
            key = TIMESTAMP_KEY,
            value = Instant.parse(lastUpdated).toEpochMilliseconds()
        )
    }

    @OptIn(ExperimentalSettingsApi::class)
    override suspend fun isDataFresh(currentTimestamp: Long): Boolean {
        val savedTimestamp = flowSettings.getLong(
            key = TIMESTAMP_KEY,
            defaultValue = 0
        )

        return if (savedTimestamp != 0L) {

            val currentInstant = Instant.fromEpochMilliseconds(currentTimestamp)
            val savedInstant = Instant.fromEpochMilliseconds(savedTimestamp)

            val currentDataTime = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault())
            val savedDataTime = savedInstant.toLocalDateTime(TimeZone.currentSystemDefault())

            val daysDifference = currentDataTime.date.dayOfYear - savedDataTime.date.dayOfYear

            daysDifference < 1

        } else {
            false
        }
    }

    @OptIn(ExperimentalSettingsApi::class)
    override suspend fun saveSourceCurrencyCode(code: String) {
        flowSettings.putString(
            key = SOURCE_CURRENCY_KEY,
            value = code
        )
    }

    @OptIn(ExperimentalSettingsApi::class)
    override suspend fun saveTargetCurrencyCode(code: String) {
        flowSettings.putString(
            key = TARGET_CURRENCY_KEY,
            value = code
        )
    }

    @OptIn(ExperimentalSettingsApi::class)
    override fun readSourceCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = SOURCE_CURRENCY_KEY,
            defaultValue = DEFAULT_SOURCE_CURRENCY
        ).map {
            CurrencyCode.valueOf(it)
        }
    }

    @OptIn(ExperimentalSettingsApi::class)
    override fun readTargetCurrencyCode(): Flow<CurrencyCode> {
        return flowSettings.getStringFlow(
            key = TARGET_CURRENCY_KEY,
            defaultValue = DEFAULT_TARGET_CURRENCY
        ).map {
            CurrencyCode.valueOf(it)
        }
    }
}