@file:Suppress("unused")

package com.orels.domain.util.extension

fun <T> ArrayList<T>.addUnique(element: T) {
    if (!contains(element)) {
        add(element)
    }
}

fun <T> ArrayList<T>.addUniqueIf(element: T, predicate: (T) -> Boolean) {
    if (predicate(element)) {
        add(element)
    }
}

fun <T> List<T>.appendAll(list: List<T>): List<T> {
    val array = ArrayList(this)
    array.addAll(list)
    return array
}

fun <T> List<T>.equalsTo(list: List<T>): Boolean {
    list.forEach { if (!contains(it)) return false }
    return true
}

fun <T> List<T>.notEqualsTo(list: List<T>): Boolean = !equalsTo(list)

fun List<String>.containsNumber(number: String) =
    map { it.withoutPrefix() }.contains(number.withoutPrefix())

fun List<String>.distinctNumbers() = map { it.withoutPrefix() }.distinct()
