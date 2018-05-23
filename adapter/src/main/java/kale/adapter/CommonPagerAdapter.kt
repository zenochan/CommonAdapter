package kale.adapter

import android.databinding.ObservableList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kale.adapter.item.AdapterItem
import kale.adapter.util.DataBindingJudgement
import kale.adapter.util.IAdapter

abstract class CommonPagerAdapter<T> @JvmOverloads constructor(data: List<T> = ArrayList(), isLazy: Boolean = false) : BasePagerAdapter<View>(), IAdapter<T> {

  private var mDataList: List<T>? = null

  private var mInflater: LayoutInflater? = null

  private var mIsLazy = false

  private var currentPos: Int = 0

  init {
    if (DataBindingJudgement.SUPPORT_DATABINDING && data is ObservableList<*>) {
      (data as ObservableList<T>).addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(sender: ObservableList<T>) {
          notifyDataSetChanged()
        }

        override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
          notifyDataSetChanged()
        }

        override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
          notifyDataSetChanged()
        }

        override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
          notifyDataSetChanged()
        }

        override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
          notifyDataSetChanged()
        }
      })
    }
    mDataList = data
    mIsLazy = isLazy
  }

  override fun getCount(): Int {
    return if (mDataList == null) 0 else mDataList!!.size
  }

  override fun getViewFromItem(item: View, position: Int): View {
    return item
  }

  override fun instantiateItem(container: ViewGroup, position: Int): View {
    val view = super.instantiateItem(container, position)
    if (!mIsLazy) {
      initItem(position, view)
    }
    return view
  }

  override fun setPrimaryItem(container: ViewGroup, position: Int, any: Any) {
    if (mIsLazy && any !== currentItem) {
      initItem(position, any as View)
    }
    super.setPrimaryItem(container, position, any)
  }

  private fun initItem(position: Int, view: View) {
    val item = view.getTag(R.id.tag_item) as AdapterItem<Any>
    item.handleData(getConvertedData(mDataList!![position], getItemType(position)), position)
  }

  override fun createItem(viewPager: ViewGroup, position: Int): View {
    if (mInflater == null) {
      mInflater = LayoutInflater.from(viewPager.context)
    }
    val item = createItem(getItemType(position))
    val view = mInflater!!.inflate(item.layoutResId, null)
    view.setTag(R.id.tag_item, item)
    item.bindViews(view)
    item.setViews()
    return view
  }

  fun setIsLazy(isLazy: Boolean) {
    mIsLazy = isLazy
  }

  override fun getConvertedData(data: T, type: Any): Any {
    return data as Any
  }

  /**
   * instead by [.getItemType]
   */
  @Deprecated("")
  override fun getItemType(position: Int): Any {
    currentPos = position
    return if (position < mDataList!!.size) {
      getItemType(mDataList!![position])
    } else {
      super.getItemType(position)
    }
  }

  /**
   * 强烈建议返回string,int,bool类似的基础对象做type
   */
  override fun getItemType(t: T): Any {
    return -1 // default
  }

  override fun setData(data: List<T>) {
    mDataList = data
  }

  override fun getData(): List<T>? {
    return mDataList
  }

  override fun getCurrentPosition(): Int {
    return currentPos
  }
}
