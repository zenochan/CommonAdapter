package kale.commonadapter.item

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kale.adapter.item.AdapterItem

/**
 * @author [陈治谋](mailto:zenochan@qq.com)
 * @since 2018/6/4
 */
class SectionHeaderItem : AdapterItem<Any> {

  override fun layout(context: Context, parent: ViewGroup): View? {
    val view = TextView(context)
    val lp = RecyclerView.LayoutParams(-1, 0)
    lp.bottomMargin = 48
    view.layoutParams = lp
    return view
  }

  private lateinit var textView: TextView

  override fun bindViews(root: View) {
    textView = root as TextView

  }

  override fun setViews() {
  }


  override fun handleData(t: Any, position: Int) {
    textView.text = "section $position"
  }
}