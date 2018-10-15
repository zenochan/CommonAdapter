package kale.adapter.item

import android.view.View

import kale.adapter.R


/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2016/12/21.
 */
class DefaultEmptyItem : AdapterItem<Any> {
  override val layoutResId: Int
    get() = R.layout.item_empty_default

  override fun bindViews(root: View) {}

  override fun setViews() {}

  override fun handleData(t: Any, position: Int) {}
}
