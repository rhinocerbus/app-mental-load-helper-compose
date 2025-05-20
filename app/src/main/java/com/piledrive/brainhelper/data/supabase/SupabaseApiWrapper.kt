package com.piledrive.brainhelper.data.supabase

import com.piledrive.brainhelper.data.api.GenericErrorResponse
import com.piledrive.brainhelper.data.api.Response
import com.piledrive.brainhelper.data.api.SuccessResponse
import io.github.jan.supabase.auth.exception.AuthRestException
import timber.log.Timber


suspend fun <T> safeSupabaseCall(call: suspend () -> T): Response<T> {
	try {
		val response = call.invoke()
		return SuccessResponse(response)
	} catch (e: Exception) {
		Timber.w(e)
		return when (e) {
			is AuthRestException -> {
				GenericErrorResponse(e.errorDescription)
			}

			else -> {
				GenericErrorResponse("${e.message}\n${e.cause}")
			}
		}
	}
}