package kale.commonadapter.item


import android.view.View
import android.widget.TextView

import kale.adapter.item.AdapterItem
import kale.commonadapter.R
import kale.commonadapter.model.DemoModel

/**
 * @author Jack Tony
 * @date 2015/5/15
 */
class TextItem : AdapterItem<DemoModel> {

  override val layoutResId: Int
    get() = R.layout.demo_item_text

  internal lateinit var textView: TextView

  override fun bindViews(root: View) {
    textView = root.findViewById<View>(R.id.textView) as TextView
  }

  override fun setViews() {
    //Log.d(TextItem.class.getSimpleName(), "setViews--------->");
  }

  override fun handleData(model: DemoModel, position: Int) {
    textView.text = model.content + " pos=" + position
  }

}

