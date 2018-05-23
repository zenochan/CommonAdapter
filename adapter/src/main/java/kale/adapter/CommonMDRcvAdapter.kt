package kale.adapter

import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.internal.MDAdapter

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/2/27.
 */
abstract class CommonMDRcvAdapter<T> protected constructor(data: List<T>?) : CommonRcvAdapter<T>(data), MDAdapter {
  private var dialog: MaterialDialog? = null

  override fun setDialog(dialog: MaterialDialog) {
    this.dialog = dialog
  }

  protected fun dismiss() {
    dialog?.dismiss()
  }
}