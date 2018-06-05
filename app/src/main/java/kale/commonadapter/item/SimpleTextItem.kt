package kale.commonadapter.item


import android.graphics.Color
import android.view.View
import android.widget.TextView

import kale.adapter.item.AdapterItem
import org.jetbrains.anko.backgroundColor

/**
 * @author Jack Tony
 * @date 2015/5/15
 */
class SimpleTextItem : AdapterItem<String> {

  override val layoutResId: Int
    get() = android.R.layout.simple_list_item_1

  private lateinit var textView: TextView

  override fun bindViews(root: View) {
    textView = root as TextView
  }

  override fun setViews() {
  }

  override fun handleData(t: String, position: Int) {
    textView.text = "$t pos=$position"
  }

}

