class SettingHolder {
    private val observers = mutableListOf<(Setting) -> Unit>()
    private var _setting = Setting()

    var setting: Setting
        get() = _setting
        set(setting) {
            if(_setting != setting) {
                _setting = setting
//                Api.log(_setting.toString())
                notifyObservers()
            }
        }

    fun addObserver(observer: (Setting) -> Unit) {
        observers.add(observer)
    }

    private fun notifyObservers() {
        observers.forEach { it(_setting) }
    }

}