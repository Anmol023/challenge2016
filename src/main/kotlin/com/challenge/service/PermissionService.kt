package com.challenge.service


import com.challenge.model.Distributor
import com.challenge.model.Permission
import com.challenge.model.PermissionType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import com.challenge.repo.DistributorRepo
import com.challenge.repo.RegionRepo


@Service
class PermissionService(
    private val distributorRepo: DistributorRepo,
    private val regionRepo: RegionRepo
) {

    // Returns true if distributor (or its parents) can distribute to the given region (by code or name) */
    fun canDistribute(distributorId: String, regionKey: String): Mono<Boolean> {
        if (!regionRepo.exists(regionKey)) return Mono.just(false)
        val hierarchy = regionRepo.getHierarchy(regionKey)
        return distributorRepo.findById(distributorId)
            .flatMap { dist -> resolveAllowed(dist, hierarchy) }
    }

    private fun resolveAllowed(dist: Distributor, hierarchy: List<String>): Mono<Boolean> {
        // Check parent recursively
        return if (dist.parentId != null) {
            distributorRepo.findById(dist.parentId)
                .flatMap { parent -> resolveAllowed(parent, hierarchy) }
                .flatMap { parentAllowed ->
                    if (!parentAllowed) Mono.just(false)
                    else checkCurrent(dist, hierarchy)
                }
                .switchIfEmpty(Mono.just(false)) // parent not found
        } else {
            checkCurrent(dist, hierarchy)
        }
    }

    // Check current distributor permissions
    private fun checkCurrent(dist: Distributor, hierarchy: List<String>): Mono<Boolean> {
        // EXCLUDE takes priority
        if (dist.permissions.any { it.type == PermissionType.EXCLUDE && it.regionCode in hierarchy }) {
            return Mono.just(false)
        }
        if (dist.permissions.any { it.type == PermissionType.INCLUDE && it.regionCode in hierarchy }) {
            return Mono.just(true)
        }
        return Mono.just(false)
    }

    // Add permission fully reactively
    fun addPermission(distributorId: String, permission: Permission): Mono<Distributor> {
        // Validate region exists
        if (!regionRepo.exists(permission.regionCode)) {
            return Mono.error(IllegalArgumentException("Unknown region: ${permission.regionCode}"))
        }

        return distributorRepo.findById(distributorId).flatMap { dist ->
            // Parent must allow this region
            val parentCheck: Mono<Boolean> = dist.parentId?.let { parentId ->
                canDistribute(parentId, permission.regionCode)
            } ?: Mono.just(true)

            parentCheck.flatMap { isAllowed ->
                if (!isAllowed) {
                    Mono.error<Distributor>(
                        IllegalArgumentException("Parent does not allow region ${permission.regionCode}")
                    )
                } else {
                    // Add permission and save
                    dist.permissions.add(permission)
                    distributorRepo.save(dist)
                }
            }
        }

    }
}
