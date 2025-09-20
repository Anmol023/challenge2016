package com.challenge.model

data class Distributor(
    val id: String,
    val parentId: String? = null,
    val permissions: MutableList<Permission> = mutableListOf()
)