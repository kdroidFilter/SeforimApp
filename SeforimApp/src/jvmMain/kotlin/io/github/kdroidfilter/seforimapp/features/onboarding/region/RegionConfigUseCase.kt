package io.github.kdroidfilter.seforimapp.features.onboarding.region

import io.github.kdroidfilter.seforimapp.features.zmanim.data.worldPlaces

class RegionConfigUseCase {
    fun getCountries(): List<String> = worldPlaces.keys.toList()

    fun getCities(countryIndex: Int): List<String> {
        val countries = getCountries()
        if (countryIndex !in countries.indices) return emptyList()
        val country = countries[countryIndex]
        return worldPlaces[country]?.keys?.toList().orEmpty()
    }

    fun findCityIndexByLegacyName(countryIndex: Int, legacyName: String): Int {
        val countries = getCountries()
        if (countryIndex !in countries.indices) return -1
        val country = countries[countryIndex]
        val alias = legacyCityAliases[country]?.get(legacyName) ?: return -1
        return getCities(countryIndex).indexOf(alias)
    }

    companion object {
        private val legacyCityAliases: Map<String, Map<String, String>> = mapOf(
            "ארצות הברית" to mapOf(
                "ניו יורק" to "New York Citi / ניו יורק סיטי"
            )
        )
    }
}

