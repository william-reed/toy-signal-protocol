package dev.williamreed.signal.common

/**
 * All but the first
 */
fun <T> List<T>.rest() = subList(1, size)
