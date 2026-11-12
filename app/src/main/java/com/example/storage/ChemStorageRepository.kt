package com.example.storage

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import com.example.data.CustomShortcut
import com.example.data.GreekAndTemplatesData
import com.example.data.KeyboardSettings
import com.example.data.KeyboardTab
import com.example.data.SavedStructureSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class ChemStorageRepository private constructor(context: Context) {

    private val appContext = context.applicationContext
    private val prefs: SharedPreferences =
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _recentSymbols = MutableStateFlow(loadStringList(KEY_RECENT))
    val recentSymbols: StateFlow<List<String>> = _recentSymbols.asStateFlow()

    private val _favoriteSymbols = MutableStateFlow(
        loadStringList(KEY_FAVORITES).ifEmpty {
            listOf("⌬", "⇌", "→", "⁺", "⁻", "₂", "₃", "₄", "H₂SO₄", "–COOH", "–OH", "–NH₂", "ΔH", "ΔG")
        }
    )
    val favoriteSymbols: StateFlow<List<String>> = _favoriteSymbols.asStateFlow()

    private val _clipboardHistory = MutableStateFlow(loadStringList(KEY_CLIPBOARD))
    val clipboardHistory: StateFlow<List<String>> = _clipboardHistory.asStateFlow()

    private val _customShortcuts = MutableStateFlow(loadShortcuts())
    val customShortcuts: StateFlow<List<CustomShortcut>> = _customShortcuts.asStateFlow()

    private val _savedStructures = MutableStateFlow(loadSavedStructures())
    val savedStructures: StateFlow<List<SavedStructureSummary>> = _savedStructures.asStateFlow()

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<KeyboardSettings> = _settings.asStateFlow()

    fun recordRecentSymbol(symbol: String) {
        val clean = symbol.trim()
        if (clean.isEmpty() || clean == " ") return
        val updated = (listOf(clean) + _recentSymbols.value.filter { it != clean }).take(30)
        _recentSymbols.value = updated
        saveStringList(KEY_RECENT, updated)
    }

    fun clearRecentSymbols() {
        _recentSymbols.value = emptyList()
        saveStringList(KEY_RECENT, emptyList())
    }

    fun toggleFavorite(symbol: String) {
        val clean = symbol.trim()
        if (clean.isEmpty()) return
        val current = _favoriteSymbols.value
        val updated = if (current.contains(clean)) {
            current.filter { it != clean }
        } else {
            listOf(clean) + current
        }
        _favoriteSymbols.value = updated
        saveStringList(KEY_FAVORITES, updated)
    }

    fun isFavorite(symbol: String): Boolean = _favoriteSymbols.value.contains(symbol.trim())

    fun copyToClipboard(text: String, label: String = "Chemistry Symbol"): Boolean {
        if (text.isEmpty()) return false
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        clipboard?.setPrimaryClip(ClipData.newPlainText(label, text))
        addClipboardEntry(text)
        return true
    }

    fun readSystemClipboard(): String? {
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = clipboard?.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(appContext)?.toString()
    }

    fun addClipboardEntry(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val updated = (listOf(clean) + _clipboardHistory.value.filter { it != clean }).take(25)
        _clipboardHistory.value = updated
        saveStringList(KEY_CLIPBOARD, updated)
    }

    fun clearClipboardHistory() {
        _clipboardHistory.value = emptyList()
        saveStringList(KEY_CLIPBOARD, emptyList())
    }

    fun addOrUpdateShortcut(trigger: String, expansion: String) {
        val cleanTrigger = trigger.trim()
        val cleanExpansion = expansion.trim()
        if (cleanTrigger.isEmpty() || cleanExpansion.isEmpty()) return
        val current = _customShortcuts.value.filterNot { it.trigger.equals(cleanTrigger, ignoreCase = true) }
        val updated = listOf(
            CustomShortcut(
                id = "sc_${System.currentTimeMillis()}",
                trigger = cleanTrigger,
                expansion = cleanExpansion
            )
        ) + current
        _customShortcuts.value = updated
        saveShortcuts(updated)
    }

    fun deleteShortcut(id: String) {
        val updated = _customShortcuts.value.filterNot { it.id == id }
        _customShortcuts.value = updated
        saveShortcuts(updated)
    }

    fun findShortcutExpansion(triggerToken: String): CustomShortcut? {
        return _customShortcuts.value.firstOrNull {
            it.trigger.equals(triggerToken, ignoreCase = true)
        }
    }

    fun saveStructure(summary: SavedStructureSummary) {
        val updated = (listOf(summary) + _savedStructures.value.filterNot { it.id == summary.id }).take(20)
        _savedStructures.value = updated
        saveStructuresList(updated)
    }

    fun deleteStructure(id: String) {
        val updated = _savedStructures.value.filterNot { it.id == id }
        _savedStructures.value = updated
        saveStructuresList(updated)
    }

    fun updateSettings(transform: (KeyboardSettings) -> KeyboardSettings) {
        val updated = transform(_settings.value)
        _settings.value = updated
        prefs.edit()
            .putBoolean(KEY_HAPTIC, updated.hapticFeedback)
            .putBoolean(KEY_SUBSUP, updated.showSubSupBar)
            .putBoolean(KEY_COMPACT, updated.compactKeys)
            .putString(KEY_DEFAULT_TAB, updated.defaultTab.name)
            .apply()
    }

    private fun loadStringList(key: String): List<String> {
        val raw = prefs.getString(key, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { idx -> arr.optString(idx) }.filter { it.isNotEmpty() }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveStringList(key: String, list: List<String>) {
        val arr = JSONArray()
        list.forEach { arr.put(it) }
        prefs.edit().putString(key, arr.toString()).apply()
    }

    private fun loadShortcuts(): List<CustomShortcut> {
        val raw = prefs.getString(KEY_SHORTCUTS, null)
            ?: return GreekAndTemplatesData.defaultShortcuts
        return try {
            val arr = JSONArray(raw)
            val result = mutableListOf<CustomShortcut>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    CustomShortcut(
                        id = obj.optString("id"),
                        trigger = obj.optString("trigger"),
                        expansion = obj.optString("expansion")
                    )
                )
            }
            result
        } catch (_: Exception) {
            GreekAndTemplatesData.defaultShortcuts
        }
    }

    private fun saveShortcuts(list: List<CustomShortcut>) {
        val arr = JSONArray()
        list.forEach { sc ->
            arr.put(
                JSONObject().apply {
                    put("id", sc.id)
                    put("trigger", sc.trigger)
                    put("expansion", sc.expansion)
                }
            )
        }
        prefs.edit().putString(KEY_SHORTCUTS, arr.toString()).apply()
    }

    private fun loadSavedStructures(): List<SavedStructureSummary> {
        val raw = prefs.getString(KEY_STRUCTURES, null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            val result = mutableListOf<SavedStructureSummary>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                result.add(
                    SavedStructureSummary(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        formula = obj.optString("formula"),
                        serializedJson = obj.optString("json"),
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveStructuresList(list: List<SavedStructureSummary>) {
        val arr = JSONArray()
        list.forEach { st ->
            arr.put(
                JSONObject().apply {
                    put("id", st.id)
                    put("name", st.name)
                    put("formula", st.formula)
                    put("json", st.serializedJson)
                    put("timestamp", st.timestamp)
                }
            )
        }
        prefs.edit().putString(KEY_STRUCTURES, arr.toString()).apply()
    }

    private fun loadSettings(): KeyboardSettings {
        val tabName = prefs.getString(KEY_DEFAULT_TAB, KeyboardTab.ORGANIC.name)
        val tab = runCatching { KeyboardTab.valueOf(tabName ?: KeyboardTab.ORGANIC.name) }
            .getOrDefault(KeyboardTab.ORGANIC)
        return KeyboardSettings(
            hapticFeedback = prefs.getBoolean(KEY_HAPTIC, true),
            showSubSupBar = prefs.getBoolean(KEY_SUBSUP, true),
            compactKeys = prefs.getBoolean(KEY_COMPACT, false),
            defaultTab = tab
        )
    }

    companion object {
        private const val PREFS_NAME = "chem_keyboard_pro_prefs"
        private const val KEY_RECENT = "recent_symbols"
        private const val KEY_FAVORITES = "favorite_symbols"
        private const val KEY_CLIPBOARD = "clipboard_history"
        private const val KEY_SHORTCUTS = "custom_shortcuts"
        private const val KEY_STRUCTURES = "saved_structures"
        private const val KEY_HAPTIC = "setting_haptic"
        private const val KEY_SUBSUP = "setting_subsup"
        private const val KEY_COMPACT = "setting_compact"
        private const val KEY_DEFAULT_TAB = "setting_default_tab"

        @Volatile
        private var instance: ChemStorageRepository? = null

        fun getInstance(context: Context): ChemStorageRepository {
            return instance ?: synchronized(this) {
                instance ?: ChemStorageRepository(context).also { instance = it }
            }
        }
    }
}
