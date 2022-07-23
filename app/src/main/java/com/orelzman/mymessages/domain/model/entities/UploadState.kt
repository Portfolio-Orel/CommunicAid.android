package com.orelzman.mymessages.domain.model.entities

//interface Uploadable<T> {
//    var uploadState: UploadState
//    fun setState(uploadState: UploadState): T
//}
abstract class Uploadable {
    var uploadState: UploadState = UploadState.NotUploaded
    private set

    fun setUploadState(uploadState: UploadState) {
        this.uploadState = uploadState
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