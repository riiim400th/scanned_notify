import java.io.File
import java.io.FileReader
import java.io.IOException
import burp.api.montoya.utilities.json.JsonNode
import burp.api.montoya.utilities.json.JsonObjectNode
import java.io.FileWriter

class IOJson() {
    fun getSetting(jsonString: String): Setting {
        val jsonObject = JsonNode.jsonNode(jsonString).asObject()
        try {
            val setting =  settingHolder.setting.copy(
                enable = jsonObject.get("enable").asBoolean(),
                loginId = jsonObject.get("loginId").asString(),
                password = jsonObject.get("password").asString(),
                from = jsonObject.get("from").asString(),
                to = jsonObject.get("to").asString(),
                bcc = jsonObject.get("bcc").asString(),
                subject = jsonObject.get("subject").asString(),
                body = jsonObject.get("body").asString(),
                auditTouchExpires = jsonObject.get("auditTouchExpires").asNumber().toInt()
            )
            return setting
        }catch (e:NullPointerException){
            Api.log("Failed to load previous Setting")
            return Setting()
        }
    }

    fun getJson(setting: Setting): String {
        val jsonObject = JsonObjectNode.jsonObjectNode()
        jsonObject.putBoolean("enable",setting.enable)
        jsonObject.putString("loginId",setting.loginId)
        jsonObject.putString("password","")
        jsonObject.putString("from",setting.from)
        jsonObject.putString("to",setting.to)
        jsonObject.putString("bcc",setting.bcc)
        jsonObject.putString("subject",setting.subject)
        jsonObject.putString("body",setting.body)
        jsonObject.putNumber("auditTouchExpires",setting.auditTouchExpires)
        return jsonObject.toJsonString()
    }

    fun exportJson(file: File) {
        // Log before writing to file
        Api.log("Preparing to write to file: ${file.name}")
        val jsonContent = getJson(settingHolder.setting)
        Api.log("JSON content: $jsonContent")

        try {
            FileWriter(file).use { writer ->
                Api.log("Attempting to write JSON to file")
                writer.write(jsonContent)
                Api.log("Successfully wrote to ${file.name}")
            }
        } catch (e: IOException) {
            Api.api.logging().raiseErrorEvent("Error writing to file: ${e.message}")
            throw e
        }
    }



    fun loadJson(file: File) {

        Api.log("Preparing to load to file: ${file.name}")

        try {
            FileReader(file).use { reader ->
                Api.log("Attempting to write JSON to file")
                val jsonString = reader.readText()
                if (Api.api.utilities().jsonUtils().isValidJson(jsonString)) {
                    val newSetting = getSetting(jsonString)
                    settingHolder.setting = newSetting
                    Api.log("Successfully load from ${file.name}")
                } else {
                    Api.api.logging().raiseErrorEvent("Error: Json format")
                }
            }
        } catch (e: IOException) {
            Api.api.logging().raiseErrorEvent("Error loading file: ${e.message}")
            throw e
        }

    }

}