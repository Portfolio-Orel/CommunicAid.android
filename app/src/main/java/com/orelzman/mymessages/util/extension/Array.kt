package com.orelzman.mymessages.util.extension

fun <T> ArrayList<T>.addUnique(element: T) {
    if(!contains(element)) {
        add(element)
    }
}

fun <T> List<T>.appendAll(list: List<T>): List<T> {
    val array = ArrayList(this)
    array.addAll(list)
    return array
}
