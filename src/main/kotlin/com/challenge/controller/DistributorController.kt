package com.challenge.controller

import com.challenge.model.Distributor
import com.challenge.model.Permission
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import com.challenge.repo.DistributorRepo
import com.challenge.service.PermissionService


@RestController
@RequestMapping("/api/distributors")
class DistributorController(
    private val repo: DistributorRepo,
    private val permissionService: PermissionService
) {


    @PostMapping
    fun create(@RequestBody distributor: Distributor): Mono<Distributor> =
        repo.save(distributor)


    @GetMapping
    fun list(): Flux<Distributor> = repo.findAll()


    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Mono<Distributor> = repo.findById(id)


    @PostMapping("/{id}/permissions")
    fun addPermission(@PathVariable id: String, @RequestBody permission: Permission): Mono<Distributor> =
        permissionService.addPermission(id, permission)


    @GetMapping("/{id}/can/{region}")
    fun can(@PathVariable id: String, @PathVariable region: String): Mono<String> =
        permissionService.canDistribute(id, region).map { if (it) "YES" else "NO" }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: String): Mono<Void> = repo.delete(id)
}