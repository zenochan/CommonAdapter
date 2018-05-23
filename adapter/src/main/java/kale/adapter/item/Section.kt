package kale.adapter.item

import android.support.annotation.IntRange

/**
 * Create Date: 16/6/20
 *
 * @author 陈治谋 (513500085@qq.com)
 */
interface Section<T> {
  @get:IntRange(from = 0)
  val itemCount: Int

  val sectionType: Int

  fun getItem(position: Int): T

  fun hasFooter(): Boolean
}
