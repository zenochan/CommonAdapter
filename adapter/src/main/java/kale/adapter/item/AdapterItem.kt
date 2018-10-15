package kale.adapter.item

import android.content.Context
import androidx.annotation.LayoutRes
import android.view.View
import android.view.ViewGroup

/**
 * [layout] 如果有返回值，将不再使用 layoutResId 解析布局
 * [layoutResId] 布局 id， layout 和 layoutResId 必须实现其中之一
 */
interface AdapterItem<in T : Any> {

  fun layout(context: Context, parent: ViewGroup): View? {
    return null
  }

  /**
   * @return item布局文件的layoutId
   */
  @get:LayoutRes
  val layoutResId: Int
    get() = -1

  /**
   * 初始化views
   */
  fun bindViews(root: View)

  /**
   * 设置view的参数
   */
  fun setViews() {}

  /**
   * 根据数据来设置item的内部views
   *
   * @param t    数据list内部的model
   * @param position 当前adapter调用item的位置
   */
  fun handleData(t: T, position: Int)

}  