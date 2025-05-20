package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.data.api.GenericErrorResponse
import com.piledrive.brainhelper.data.api.Response
import com.piledrive.brainhelper.data.api.SuccessResponse
import com.piledrive.brainhelper.data.supabase.safeSupabaseCall
import com.piledrive.brainhelper.datastore.SessionDataStore
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ViewModelScoped
class AuthRepo @Inject constructor(
	private val dataStore: SessionDataStore,
	private val supabaseClient: SupabaseClient
) {

	suspend fun checkSessionAuthenticated(): Boolean {
		return dataStore.checkAccessToken() != null && dataStore.checkRefreshToken() != null
	}

	suspend fun anonLogin() {
		supabaseClient.auth.signInAnonymously()
	}

	fun grabAuthStatusFlow(): Flow<SessionStatus> {
		return supabaseClient.auth.sessionStatus
			// intercept to deduplicate session/cache handling
			.map { status ->
				when (status) {
					is SessionStatus.Initializing -> {}
					is SessionStatus.Authenticated -> {
						dataStore.updateAccessToken(status.session.accessToken)
						dataStore.updateRefreshToken(status.session.refreshToken)
					}

					is SessionStatus.RefreshFailure -> {
						dataStore.clear()
					}

					is SessionStatus.NotAuthenticated -> {
						dataStore.clear()
					}
				}
				status
			}
	}

	/**
	 * From docs:
	 *
	 * By default, the user needs to verify their email address before logging in. To turn this off, disable Confirm email in your project.
	 *
	 * Confirm email determines if users need to confirm their email address after signing up.
	 * - (1) If Confirm email is enabled, the return value is the user and you won't be logged in automatically.
	 * - (2) If Confirm email is disabled, the return value is null and you will be logged in instead.
	 *
	 * When the user confirms their email address, they are redirected to the SITE_URL by default. You can modify your SITE_URL or add additional redirect URLs in your project.
	 *
	 * To learn how to handle OTP links & OAuth refer to initializing
	 *
	 * If signUpWith() is called for an existing confirmed user:
	 * - (3) When both Confirm email and Confirm phone (even when phone provider is disabled) are enabled in your project, an obfuscated/fake user object is returned.
	 * - (4) When either Confirm email or Confirm phone (even when phone provider is disabled) is disabled, the error message, User already registered is returned.
	 */
	suspend fun register(userEmail: String, userPassword: String): Response<UserInfo?> {
		val wrappedResponse = safeSupabaseCall {
			supabaseClient.auth.signUpWith(Email) {
				email = userEmail
				password = userPassword
			}
		}

		// todo - handle case 3, 4
		when (wrappedResponse) {
			is SuccessResponse -> {
				wrappedResponse.data ?: run {
					// (2) confirmation disabled, treated as login, will be handled by auth status flow
					return wrappedResponse
				}

				// (1) confirmation enabled, just show that confirmation needed
				return GenericErrorResponse("Confirmation email sent to ${wrappedResponse.data.email}")
			}

			else -> {}
		}

		return wrappedResponse
	}

	suspend fun login(userEmail: String, userPassword: String): Response<Unit> {
		val wrappedResponse = safeSupabaseCall {
			supabaseClient.auth.signInWith(Email) {
				email = userEmail
				password = userPassword
			}
		}

		when (wrappedResponse) {
			is SuccessResponse -> {
				val session = supabaseClient.auth.currentSessionOrNull() ?: return GenericErrorResponse("")
				session.accessToken
				session.refreshToken
			}

			else -> {}
		}

		return wrappedResponse
	}


	suspend fun logout() {
		val wrappedResponse = safeSupabaseCall {
			supabaseClient.auth.signOut()
		}

		when (wrappedResponse) {
			is SuccessResponse -> {
			}

			else -> {}
		}
	}

	suspend fun refreshToken() {
		val wrappedResponse = safeSupabaseCall {
			supabaseClient.auth.refreshCurrentSession()
		}

		when (wrappedResponse) {
			is SuccessResponse -> {
				val session = supabaseClient.auth.refreshSession("")
				session.accessToken
				session.refreshToken
			}

			else -> {}
		}
	}

	suspend fun fetchUser() {
		val wrappedResponse = safeSupabaseCall {
			supabaseClient.auth.retrieveUserForCurrentSession(true)
		}

		when (wrappedResponse) {
			is SuccessResponse -> {
			}

			else -> {}
		}
	}
}