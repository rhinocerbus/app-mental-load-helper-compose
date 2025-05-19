package com.piledrive.brainhelper.repo

import com.piledrive.brainhelper.datastore.SessionDataStore
import dagger.hilt.android.scopes.ViewModelScoped
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ViewModelScoped
class AuthRepo @Inject constructor(
	private val dataStore: SessionDataStore,
	private val supabaseClient: SupabaseClient
) {

	suspend fun checkSessionAuthenticated(): Boolean {
		return dataStore.checkAccessToken() != null
	}

	suspend fun anonLogin() {
		supabaseClient.auth.signInAnonymously()
	}

	fun grabAtuhStatusFlow(): Flow<SessionStatus> {
		return supabaseClient.auth.sessionStatus
	}

	suspend fun register(userEmail: String, userPassword: String) {
		supabaseClient.auth.signUpWith(Email) {
			email = userEmail
			password = userPassword
		}
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