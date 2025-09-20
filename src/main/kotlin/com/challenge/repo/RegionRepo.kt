package com.challenge.repo

import com.challenge.model.Region
import org.springframework.stereotype.Repository



@Repository
class RegionRepo {

    private lateinit var regionsByCode: Map<String, Region>
    private lateinit var regionsByName: Map<String, Region>

    fun loadAll(list: List<Region>) {
        val codeMap = list.associateBy { it.code }
        val nameMap = list.associateBy { it.name.uppercase() } // case-insensitive lookup
        regionsByCode = codeMap.toMap()
        regionsByName = nameMap.toMap()
    }

    fun findByCodeOrName(key: String): Region? =
        regionsByCode[key] ?: regionsByName[key.uppercase()]

    fun findAll(): Collection<Region> = regionsByCode.values

    /**
     * Returns list of region codes from most specific to most general
     * e.g. for "Chennai" -> [CHENNAI-TAMILNADU-INDIA, TAMILNADU-INDIA, INDIA]
     */
    fun getHierarchy(key: String): List<String> {
        val out = mutableListOf<String>()
        var cur = findByCodeOrName(key)
        while (cur != null) {
            out.add(cur.code)
            cur = cur.parent?.let { regionsByCode[it] }
        }
        return out
    }

    fun exists(key: String): Boolean = findByCodeOrName(key) != null
}
