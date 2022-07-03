package com.orelzman.mymessages.util.extension

fun <T> ArrayList<T>.addUnique(element: T) {
    if(!contains(element)) {
        add(element)
    }
}

