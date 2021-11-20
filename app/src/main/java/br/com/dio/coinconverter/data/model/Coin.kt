package br.com.dio.coinconverter.data.model

import android.util.Log
import java.util.*

enum class Coin(val locale: Locale) {
    USD(Locale.US),
    CAD(Locale.CANADA),
    BRL(Locale("pt", "BR")),
    ARS(Locale("es", "AR"))
    ;

    companion object {
        fun getByName(name: String) = values().find { it.name == name } ?: BRL

        fun getAllMinusThis(name: String) : List<Coin> {
            var result : MutableList<Coin> = values().toMutableList()
            result.clear()
            for (value in values()) {
                if (value.name != name)
                    result.add(value)
            }

            return result
        }
    }
}
