package com.challenge.repo

import com.challenge.model.Distributor
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap


@Repository
class DistributorRepo {
    private val map = ConcurrentHashMap<String, Distributor>()


    fun save(d: Distributor): Mono<Distributor> {
        map[d.id] = d
        return Mono.just(d)
    }


    fun findById(id: String): Mono<Distributor> = Mono.justOrEmpty(map[id])


    fun findAll(): Flux<Distributor> = Flux.fromIterable(map.values)


    fun delete(id: String): Mono<Void> {
        map.remove(id)
        return Mono.empty()
    }
}