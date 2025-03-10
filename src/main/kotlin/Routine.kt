import java.time.Duration
import java.time.Instant

class Routine {
    @Volatile
    private var running = false
    private var thread: Thread? = null
    private val routineTimeSec  = settingHolder.setting.auditTouchExpires.toLong()

    init {
        settingHolder.addObserver { newSetting ->
            when(newSetting.enable){
                true->start()
                false->stop()
            }
        }
    }

    private fun start() {
        if (running) {
            return
        }

        running = true
        thread = Thread {
            while (running) {
                pollAndCheck()
                try {
                    Thread.sleep(Duration.ofSeconds(routineTimeSec))
                } catch (e: InterruptedException) {
                    Api.log("The thread was stopped by an interrupt")
                }
            }
            Api.log("Stopped the thread")
        }

        thread?.start()
        Api.log("Started the thread")
    }
    private fun stop() {
        if (!running) {
            Api.log("The thread is already stopped")
            return
        }
        running = false
        thread?.interrupt()
        thread?.join()
        Api.log("Stopped the thread")
    }
    private fun isNowOverStampedTime():Boolean {
        val now = Instant.now().epochSecond
        return now - Timer.stampedTime.epochSecond > routineTimeSec
    }
    private fun pollAndCheck() {
        when(Timer.auditState){
            AuditState.AUDITING -> {
                if(isNowOverStampedTime()) {
                    Sender().send(false)
                    Timer.auditState = AuditState.STOP
                    Api.log("Routine: status changed to ${Timer.auditState}")

                } else {
                    Api.log("Routine: now status is ${Timer.auditState}")
                }

            }
            else -> {
                if(!isNowOverStampedTime()){
                    Timer.auditState = AuditState.AUDITING
                    Api.log("Routine: status changed to ${Timer.auditState}")

                } else {Api.log("Routine: now status is ${Timer.auditState}")}


            }
        }
    }
}
