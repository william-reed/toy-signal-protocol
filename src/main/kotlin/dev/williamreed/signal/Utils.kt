package dev.williamreed.signal

/**
 * All but the first
 */
fun <T> List<T>.rest() = subList(1, size)
