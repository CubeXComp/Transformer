import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = prefs.edit()

    private val IS_FIRST_LAUNCH = "isFirstLaunch"

    var isFirstLaunch: Boolean
        get() = prefs.getBoolean(IS_FIRST_LAUNCH, true)
        set(value) {
            editor.putBoolean(IS_FIRST_LAUNCH, value)
            editor.apply()
        }
}