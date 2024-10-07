package com.gabriel_miranda.currencyapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform