import java.time.Instant

object Timer {
    var auditState = AuditState.IDLING
    var stampedTime: Instant = Instant.now().minusSeconds(settingHolder.setting.auditTouchExpires.toLong()+1)
    fun touch() {
        stampedTime = Instant.now()
    }
}