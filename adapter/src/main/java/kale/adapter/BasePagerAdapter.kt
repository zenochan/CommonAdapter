@file:Suppress("UNCHECKED_CAST")

package kale.adapter

import android.support.v4.util.ArrayMap
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup

import java.util.LinkedList
import java.util.Queue


/**
 * @author Jack Tony
 * @date 2015/11/21
 *
 *
 * 如果调用[.notifyDataSetChanged]来更新，
 * 它会自动调用[.instantiateItem]重新new出需要的item，算是完全初始化一次。
 */
abstract class BasePagerAdapter<T : Any> : PagerAdapter() {

  var currentItem: T? = null
    protected set

  /**
   * 这的cache的最大大小是：type * pageSize
   */
  protected val cache = PagerCache<T>()

  /**
   * 注意：这里必须是view和view的比较
   */
  override fun isViewFromObject(view: View, obj: Any): Boolean {
    return view === getViewFromItem(obj as T, 0)
  }

  override fun instantiateItem(container: ViewGroup, position: Int): T {
    val type = getItemType(position)
    // get item from type or create item
    val item = cache.getItem(type) ?: createItem(container, position)
    // 通过item得到将要被add到viewpager中的view
    val view = getViewFromItem(item, position)
    view.setTag(R.id.item_type, type)

    (view.parent as? ViewGroup)?.removeView(view)
    container.addView(view)
    return item
  }

  override fun setPrimaryItem(container: ViewGroup, position: Int, any: Any) {
    super.setPrimaryItem(container, position, any)
    if (any !== currentItem) {
      // 可能是currentItem不等于null，可能是二者不同
      currentItem = any as T
    }
  }

  override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
    val item = `object` as T
    // 现在通过item拿到其中的view，然后remove掉
    container.removeView(getViewFromItem(item, position))
    val type = getViewFromItem(item, position).getTag(R.id.item_type)
    cache.putItem(type, item)
  }

  override fun getItemPosition(`object`: Any): Int {
    return PagerAdapter.POSITION_NONE
  }

  protected open fun getItemType(position: Int): Any {
    return -1 // default
  }

  /**
   * 这里要实现一个从item拿到view的规则
   *
   * @param item     包含view的item对象
   * @param position item所处的位置
   * @return item中的view对象
   */
  protected abstract fun getViewFromItem(item: T, position: Int): View

  /**
   * 当缓存中无法得到所需item时才会调用
   *
   * @return 需要放入容器的view
   */
  protected abstract fun createItem(viewPager: ViewGroup, position: Int): T

  ///////////////////////////////////////////////////////////////////////////
  // 缓存类
  ///////////////////////////////////////////////////////////////////////////

  protected class PagerCache<T> {

    private val mCacheMap: MutableMap<Any, Queue<T>>

    init {
      mCacheMap = ArrayMap()
    }

    /**
     * @param type item type
     * @return cache中的item，如果拿不到就返回null
     */
    fun getItem(type: Any): T? {
      val queue = mCacheMap[type]
      return queue?.poll()
    }

    /**
     * @param type item's type
     */
    fun putItem(type: Any, item: T) {
      val queue = mCacheMap[type] ?: LinkedList()
      queue.offer(item)

      if (mCacheMap[type] == null) {
        mCacheMap[type] = queue
      }
    }
  }

}
