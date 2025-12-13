package com.taiseidev.yoin

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform