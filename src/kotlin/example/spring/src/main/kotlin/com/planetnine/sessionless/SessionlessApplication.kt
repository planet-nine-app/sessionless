package com.planetnine.sessionless

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SessionlessApplication

fun main(args: Array<String>) {
	runApplication<SessionlessApplication>(*args)
}
