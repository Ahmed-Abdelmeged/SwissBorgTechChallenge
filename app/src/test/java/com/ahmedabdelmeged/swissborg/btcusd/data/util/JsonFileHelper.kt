package com.ahmedabdelmeged.swissborg.btcusd.data.util

import okio.Okio
import java.io.IOException
import java.nio.charset.StandardCharsets

object JsonFileHelper {

    @Throws(IOException::class)
    fun readJsonFile(filename: String): String {
        val inputStream = javaClass.classLoader?.getResourceAsStream("api-responses/$filename")
        val source = Okio.buffer(Okio.source(inputStream!!))
        return source.readString(StandardCharsets.UTF_8)
    }

}