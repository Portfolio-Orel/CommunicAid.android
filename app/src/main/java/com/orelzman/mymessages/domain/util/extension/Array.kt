package com.orelzman.mymessages.domain.util.extension

fun <T> ArrayList<T>.addUnique(element: T) {
    if (!contains(element)) {
        add(element)
    }
}

fun <T> ArrayList<T>.addUniqueWithPredicate(element: T, predicate: (T) -> Boolean) {
    if (predicate(element)) {
        add(element)
    }
}

fun <T> List<T>.appendAll(list: List<T>): List<T> {
    val array = ArrayList(this)
    array.addAll(list)
    return array
}