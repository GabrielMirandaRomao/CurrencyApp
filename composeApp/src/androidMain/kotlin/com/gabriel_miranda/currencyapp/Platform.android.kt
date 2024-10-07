package com.gabriel_miranda.currencyapp

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()