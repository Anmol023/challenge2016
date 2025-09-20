package com.challenge.config

import com.challenge.model.Region
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import com.challenge.repo.RegionRepo
import java.io.BufferedReader
import java.io.InputStreamReader

@Configuration
class RegionLoaderConfig(
    private val regionRepo: RegionRepo
) {

    @Bean
    fun loadRegions(): ApplicationRunner = ApplicationRunner { _: ApplicationArguments ->

        val stream = javaClass.getResourceAsStream("/cities.csv")
            ?: throw IllegalStateException("/cities.csv not found in resources")

        val regionsMap = mutableMapOf<String, Region>()

        BufferedReader(InputStreamReader(stream)).use { br ->
            br.lineSequence()
                .drop(1) // skip header
                .filter { it.isNotBlank() }
                .forEach { line ->
                    val parts = line.split(",")
                    if (parts.size < 6) return@forEach

                    val cityCode = parts[0].trim()
                    val provinceCode = parts[1].trim()
                    val countryCode = parts[2].trim()

                    val cityName = parts[3].trim()
                    val provinceName = parts[4].trim()
                    val countryName = parts[5].trim()

                    // Add country if not already present
                    regionsMap.computeIfAbsent(countryCode) { Region(countryCode, countryName, null) }

                    // Add province if not already present
                    regionsMap.computeIfAbsent(provinceCode) { Region(provinceCode, provinceName, countryCode) }

                    // Add city
                    regionsMap[cityCode] = Region(cityCode, cityName, provinceCode)
                }
        }

        regionRepo.loadAll(regionsMap.values.toList())
        println("Loaded regions: ${regionsMap.size}")
    }
}
