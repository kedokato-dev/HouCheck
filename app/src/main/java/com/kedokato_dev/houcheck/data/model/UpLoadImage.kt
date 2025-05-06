package com.kedokato_dev.houcheck.data.model

import java.io.File

data class ImageUploadRequestWithUrl (
    val token: String,
    val image_url: String,
    val server: String
)

data class ImageUploadRequestWithFile (
    val token: String,
    val images: File,
    val server: String
)

data class Result(
    val success: Boolean,
    val filename: String,
    val url: String,
)

data class UpLoadImageResponse(
    val success: Boolean,
    val results: Result,
)