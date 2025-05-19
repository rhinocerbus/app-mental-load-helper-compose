package com.piledrive.brainhelper.repo

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

	suspend fun register(userEmail: String, userPassword: String): UserInfo? {
		val userInfo = supabaseClient.auth.signUpWith(Email) {
			email = userEmail
			password = userPassword
		}
		return userInfo
	}

	suspend fun login(userEmail: String, userPassword: String) {
		supabaseClient.auth.signInWith(Email) {
			email = userEmail
			password = userPassword
		}
		val session = supabaseClient.auth.currentSessionOrNull() ?: return
		session.accessToken
		session.refreshToken
	}

	suspend fun logout() {
		supabaseClient.auth.signOut()
	}

	suspend fun refreshToken() {
		supabaseClient.auth.refreshCurrentSession()
		val session = supabaseClient.auth.refreshSession("")
		session.accessToken
		session.refreshToken
	}

	suspend fun fetchUser() {
		val userInfo = supabaseClient.auth.retrieveUserForCurrentSession(true)
	}
}