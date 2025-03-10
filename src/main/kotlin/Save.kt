import burp.api.montoya.extension.ExtensionUnloadingHandler


class Save : ExtensionUnloadingHandler {
    override fun extensionUnloaded() {
        Api.api.persistence().extensionData().setString("data", IOJson().getJson(settingHolder.setting))
    }
}