package com.piledrive.brainhelper.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.piledrive.lib_datastore.abstracts.AbstractPrefsDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject


private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
	name = "session_store"
)

open class SessionDataStore @Inject constructor(@ApplicationContext appCtx: Context) : AbstractPrefsDataStore(appCtx) {

	companion object {
		private const val PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN"
		private const val PREF_KEY_REFRESH_TOKEN = "PREF_KEY_REFRESH_TOKEN"
		private const val PREF_KEY_USER_ID = "PREF_KEY_USER_ID"
		private const val PREF_KEY_ACTIVE_FAMILY_ID = "PREF_KEY_ACTIVE_FAMILY_ID"
	}

	override val VERSION: Int
		get() = 0
	override val dataStore: DataStore<Preferences>
		get() = appCtx.sessionDataStore

	override fun doUpgrade(oldVersion: Int) {
	}

	suspend fun clear() {
		clearAllImpl()
	}

	suspend fun updateAccessToken(token: String) {
		saveStringImpl(PREF_KEY_ACCESS_TOKEN, token)
	}

	suspend fun checkAccessToken(): String? {
		return loadStringImpl(PREF_KEY_ACCESS_TOKEN)
	}

	suspend fun updateRefreshToken(token: String) {
		saveStringImpl(PREF_KEY_REFRESH_TOKEN, token)
	}

	suspend fun checkRefreshToken(): String? {
		return loadStringImpl(PREF_KEY_REFRESH_TOKEN)
	}

	suspend fun updateUserId(id: String) {
		saveStringImpl(PREF_KEY_USER_ID, id)
	}

	suspend fun checkUserId(): String? {
		return loadStringImpl(PREF_KEY_USER_ID)
	}

	suspend fun updateActiveFamilyId(id: String?) {
		saveStringImpl(PREF_KEY_ACTIVE_FAMILY_ID, id)
	}

	suspend fun checkActiveFamilyId(): String? {
		return loadStringImpl(PREF_KEY_ACTIVE_FAMILY_ID)
	}

	private val activeFamilyIdKey = stringPreferencesKey(PREF_KEY_ACTIVE_FAMILY_ID)
	fun watchActiveFamilyId(): Flow<String?> {
		return dataStore.data
			.map {
				it[activeFamilyIdKey]
			}
			.distinctUntilChanged { old, new -> old == new }
	}
}