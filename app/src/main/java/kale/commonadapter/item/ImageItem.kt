package kale.commonadapter.item

import android.view.View
import android.widget.ImageView
import android.widget.Toast

import kale.adapter.item.AdapterItem
import kale.commonadapter.R
import kale.commonadapter.model.DemoModel

/**
 * @param callback 一般的点击事件可以在内部处理，如果需要通知到外部的事件。可以通过构造函数传入监听器, 或者是通过public方法set监听器。
 * @author Jack Tony
 * @date 2015/5/15
 */
class ImageItem(
    val callback: ImageItemCallback? = null

) : AdapterItem<DemoModel> {

  private var mOldImageUrl = 0

  private var mIv: ImageView? = null

  private var mCallback: ImageItemCallback? = null

  override val layoutResId: Int = R.layout.demo_item_image

  override fun bindViews(root: View) {
    mIv = root as ImageView
  }

  override fun setViews() {
    mIv!!.setOnClickListener { view ->
      Toast.makeText(view.context, "is clicked", Toast.LENGTH_SHORT).show()
      mCallback?.onImageClick(view)
    }
  }

  override fun handleData(model: DemoModel, position: Int) {
    val drawableId = Integer.valueOf(model.content)
    // 我们先判断要加载的图片是不是之前的图片，如果是就不重复加载了
    if (mOldImageUrl == 0 && mOldImageUrl != drawableId) {
      mIv!!.setImageResource(drawableId) // load image
      mOldImageUrl = drawableId
    }
  }

  /**
   * 作为item的回调可以放一个内部类在这里，然后从外部传入callback的实现。
   *
   * 为什么做空实现，而不是一个接口呢？如果item被多个页面复用了，而且多个页面接收到的回调是不同的，
   * 那么别的页面可以针对性的实现某方法，如onImageClick()，不用全部实现。
   */
  open class ImageItemCallback {

    /**
     * 这个名字一定要取得和item中具体的事务有关，不要叫的太抽象。比如onClick就不好了。
     */
    open fun onImageClick(view: View) {
      // do nothing
    }

    fun onViewChanged() {
      // do nothing
    }

    fun otherListener() {
      // do noting
    }
  }

}
