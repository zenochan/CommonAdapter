package kale.commonadapter.item


import android.view.View
import android.widget.TextView

import kale.adapter.item.Section
import kale.adapter.item.SectionItem
import kale.commonadapter.model.FullString

/**
 * @author Jack Tony
 * @date 2015/5/15
 */
class FullTextItem : SectionItem<FullString> {

  override val layoutResId: Int
    get() = android.R.layout.simple_list_item_1

  private lateinit var textView: TextView

  override fun bindViews(root: View) {
    textView = root as TextView
  }

  override fun setViews() {
  }

  override fun handleData(t: FullString, position: Int) {
    textView.text = "$position"
  }

  override fun handleSection(section: Section<FullString>, sectionPosition: Int) {
    textView.text = "$sectionPosition : ${textView.text}"
  }
}

