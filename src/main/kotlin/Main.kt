import burp.api.montoya.BurpExtension
import burp.api.montoya.MontoyaApi
import javax.swing.JTabbedPane

val settingHolder = SettingHolder()
val routine = Routine()
class Main : BurpExtension {
    override fun initialize(api: MontoyaApi) {
        Api.initializeApi(api)
        api.extension().setName("AuditNotify")
        Api.log("========================================\r\nloaded AuditNotify ${DefaultData.VER}\r\nThis extension is developed by github.com/riiim400th. \r\nFor more information, visit: ${DefaultData.REPOSITORY_URL}\r\n========================================\r\n")
        Load(api.persistence().extensionData()).load()
        api.http().registerHttpHandler(RequestHandler())
        api.userInterface().registerSuiteTab("AuditNotify", Tab())

        api.extension().registerUnloadingHandler(Save())
    }
}