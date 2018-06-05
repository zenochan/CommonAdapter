package kale.commonadapter.item

import android.view.View
import android.widget.TextView
import kale.adapter.item.AdapterItem
import kale.adapter.item.Section

/**
 * @author [陈治谋](mailto:zenochan@qq.com)
 * @since 2018/6/4
 */
class SectionHeaderItem : AdapterItem<Section<Any>> {

  override val layoutResId: Int
    get() = android.R.layout.simple_list_item_1

  private lateinit var textView: TextView

  override fun bindViews(root: View) {
    textView = root as TextView
  }

  override fun setViews() {
  }

  override fun handleData(t: Section<Any>, position: Int) {
    textView.text = "Section Header $position"
  }
}