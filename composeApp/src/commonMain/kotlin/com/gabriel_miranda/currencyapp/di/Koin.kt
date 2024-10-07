package com.gabriel_miranda.currencyapp.di

import com.gabriel_miranda.currencyapp.data.local.MongoImpl
import com.gabriel_miranda.currencyapp.data.local.PreferencesImpl
import com.gabriel_miranda.currencyapp.data.remote.api.CurrencyApiServiceImpl
import com.gabriel_miranda.currencyapp.domain.CurrencyApiService
import com.gabriel_miranda.currencyapp.domain.MongoRepository
import com.gabriel_miranda.currencyapp.domain.PreferencesRepository
import com.gabriel_miranda.currencyapp.ui.HomeViewModel
import com.russhwolf.settings.Settings
import org.koin.core.context.startKoin
import org.koin.dsl.module

val appModule = module {
    single { Settings() }
    single<MongoRepository> { MongoImpl() }
    single<PreferencesRepository> { PreferencesImpl(settings = get()) }
    single<CurrencyApiService> { CurrencyApiServiceImpl(preferences = get()) }
    factory {
        HomeViewModel(
            preferences = get(),
            mongoDb = get(),
            api = get()
        )
    }
}

fun initializeKoin() {
    startKoin {
        modules(appModule)
    }
}