package kale.commonadapter.item

import android.view.View
import kale.adapter.R
import kale.adapter.item.Section
import kale.adapter.item.SectionItem

/**
 * @author [陈治谋](mailto:zenochan@qq.com)
 * @since 2018/6/5
 */
class SpecificHeader : SectionItem<Any> {


  override val layoutResId: Int = R.layout.header_section

  override fun bindViews(root: View) {
  }

  override fun setViews() {
  }

  override fun handleData(t: Any, position: Int) {

  }

  override fun handleSection(section: Section<Any>, sectionPosition: Int) {
  }
}