package com.orels.auth.domain.interactor

interface Logger {
    fun error(exception: Exception, extras: Map<String, Any?> = emptyMap())
}