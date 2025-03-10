
import burp.api.montoya.persistence.PersistedObject

class Load(private val savedData: PersistedObject) {
    fun load() {
        val jsonData = savedData.getString("data")
        when {
            (jsonData != null) -> settingHolder.setting = IOJson().getSetting(jsonData)
        }
    }
}