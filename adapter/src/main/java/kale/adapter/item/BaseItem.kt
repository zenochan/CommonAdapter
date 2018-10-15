package kale.adapter.item

import android.view.View

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/9/26
 */
abstract class BaseItem<T : Any> : AdapterItem<T> {
  protected lateinit var root: View
  protected lateinit var data: T
  protected var position: Int = 0

  final override fun bindViews(root: View) {
    this.root = root
  }

  override fun handleData(t: T, position: Int) {
    this.data = t
    this.position = position
    render()
  }

  abstract fun render()
}