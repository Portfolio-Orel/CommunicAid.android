package com.orelzman.mymessages.util.extension

import java.util.*

fun Date.compareToBallPark(date: Date): Boolean = time.inSeconds > date.time.inSeconds - 10
        && time.inSeconds < date.time.inSeconds + 10