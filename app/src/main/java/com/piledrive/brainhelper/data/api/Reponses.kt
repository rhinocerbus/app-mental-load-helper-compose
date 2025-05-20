package com.piledrive.brainhelper.data.api

sealed interface Response<T>
class SuccessResponse<T>(val data: T): Response<T>
class GenericErrorResponse<T>(val errMsg: String, val blockUi: Boolean = false): Response<T>
