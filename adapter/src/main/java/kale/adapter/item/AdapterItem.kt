package kale.adapter.item

import android.support.annotation.LayoutRes
import android.view.View

interface AdapterItem<in T : Any> {

  /**
   * @return item布局文件的layoutId
   */
  @get:LayoutRes
  val layoutResId: Int

  /**
   * 初始化views
   */
  fun bindViews(root: View)

  /**
   * 设置view的参数
   */
  fun setViews()

  /**
   * 根据数据来设置item的内部views
   *
   * @param t    数据list内部的model
   * @param position 当前adapter调用item的位置
   */
  fun handleData(t: T, position: Int)

}  