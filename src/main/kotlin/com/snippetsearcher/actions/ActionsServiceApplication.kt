package com.snippetsearcher.actions

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ActionsServiceApplication

@SuppressWarnings("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<ActionsServiceApplication>(*args)
}
