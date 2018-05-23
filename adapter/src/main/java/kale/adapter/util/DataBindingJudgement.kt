package kale.adapter.util

/**
 * @author Kale
 * @date 2016/4/16
 */
object DataBindingJudgement {

  val SUPPORT_DATABINDING: Boolean

  init {
    var hasDependency: Boolean
    try {
      Class.forName("android.databinding.ObservableList")
      hasDependency = true
    } catch (e: ClassNotFoundException) {
      hasDependency = false
    }

    SUPPORT_DATABINDING = hasDependency
  }
}
