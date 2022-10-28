package com.orels.domain.model.entities

import java.util.*

abstract class Uploadable {
    var uploadState: UploadState = UploadState.NotUploaded
        private set

    var statusChangedAt: Date = Date()

    fun setUploadState(uploadState: UploadState) {
        this.uploadState = uploadState
        this.statusChangedAt = Date()
    }

    /**
     * Checks if the object should be uploaded.
     */
    fun shouldBeUploaded(): Boolean {
        if (uploadState == UploadState.Uploaded) return false
        return uploadState == UploadState.NotUploaded
                || Date().time - statusChangedAt.time > TimeToInvalidateMilliseconds
    }

    companion object {
        /**
         * How long before the status is invalidated and if the status is not uploaded
         * it is considered NotUploaded
         */
        private const val TimeToInvalidateMilliseconds: Long = 30000
    }
}

enum class UploadState(val value: String) {
    NotUploaded("NotUploaded"),
    BeingUploaded("BeingUploaded"),
    Uploaded("Uploaded");

    companion object {
        fun fromString(value: String): UploadState {
            values().forEach {
                if (it.value == value) return it
            }
            return NotUploaded
        }
    }
}