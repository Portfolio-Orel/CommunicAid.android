package com.orelzman.mymessages.presentation.components.util

import androidx.annotation.StringRes

sealed class CRUDResult<T>(val data: T? = null, @StringRes val message: Int? = null) {
    class Success<T>(data: T?): CRUDResult<T>(data)
    class Error<T>(@StringRes message: Int, data: T? = null): CRUDResult<T>(data, message)
    class Loading<T>(val isLoading: Boolean = true): CRUDResult<T>(null)
}