package com.challenge.model

enum class PermissionType { INCLUDE, EXCLUDE }

data class Permission(
    val type: PermissionType,
    val regionCode: String
)