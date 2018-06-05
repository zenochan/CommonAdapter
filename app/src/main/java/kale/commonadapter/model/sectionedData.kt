package kale.commonadapter.model

import kale.adapter.ISpan
import kale.adapter.item.Section
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author [陈治谋](mailto:zenochan@qq.com)
 * @since 2018/6/4
 */

class StringSection : Section<String> {

  val data = Array(2 + Random().nextInt(3)) { "$it" }
  override val itemCount: Int
    get() = data.size
  override val sectionType: Int
    get() = 1

  override fun getItem(position: Int): String = data[position]

  override fun hasFooter(): Boolean = false
}

class FullString(string: String) : ISpan {
  override val span: Int = -1
}

class FullStringSection : Section<FullString> {
  val data = Array(2 + Random().nextInt(3)) { FullString("$it") }
  override val itemCount: Int = data.size
  override val sectionType: Int = 2
  override fun getItem(position: Int): FullString = data[position]
  override fun hasFooter(): Boolean = false
}

class HeaderSection : Section<Any> {
  override val itemCount: Int = 0
  override val sectionType: Int = 3

  override fun getItem(position: Int): Any {
    return Any()
  }

  override fun hasFooter(): Boolean {
    return false
  }
}
