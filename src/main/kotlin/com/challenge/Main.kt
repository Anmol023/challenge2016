package com.challenge

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication(exclude = [
    org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration::class
])
class Main

fun main(args: Array<String>) {
    runApplication<Main>(*args)
}


