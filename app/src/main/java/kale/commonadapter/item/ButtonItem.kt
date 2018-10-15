package kale.commonadapter.item

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

import kale.adapter.item.AdapterItem
import kale.commonadapter.R
import kale.commonadapter.model.DemoModel

/**
 * @tips 优化小技巧：这个就等于一个viewHolder，用于复用，所以不会重复建立对象
 */
class ButtonItem : AdapterItem<DemoModel> {

  private var mPosition: Int = 0

  private var btn: Button? = null

  override val layoutResId: Int
    get() = R.layout.demo_item_button

  override fun bindViews(root: View) {
    btn = root as Button
  }

  /**
   * @tips: 优化小技巧：在这里直接设置按钮的监听器。
   * 因为这个方法仅仅在item建立时才调用，所以不会重复建立监听器。
   */
  override fun setViews() {
    // 这个方法仅仅在item构建时才会触发，所以在这里也仅仅建立一次监听器，不会重复建立
    btn!!.setOnClickListener { Toast.makeText(btn!!.context, "pos = $mPosition", Toast.LENGTH_SHORT).show() }
  }

  override fun handleData(model: DemoModel, position: Int) {
    Log.d(TAG, "handleData: " + model.content!!)
    // 在每次适配器getView的时候就会触发，这里避免做耗时的操作
    mPosition = position

    btn!!.text = model.content
  }

  companion object {

    private val TAG = "ButtonItem"
  }

}
