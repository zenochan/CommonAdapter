package kale.adapter

import androidx.databinding.ObservableList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kale.adapter.item.AdapterItem
import kale.adapter.util.DataBindingJudgement
import kale.adapter.util.IAdapter
import kale.adapter.util.ItemTypeUtil

/**
 * @author Jack Tony
 * @since 2015/5/15
 */
abstract class CommonAdapter<T : Any>(data: List<T> = ArrayList(), viewTypeCount: Int) : BaseAdapter(), IAdapter<T> {

  private var mDataList: List<T>

  private var mViewTypeCount = 1

  private lateinit var mType: Any

  private var mInflater: LayoutInflater? = null

  private val util: ItemTypeUtil

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
    mViewTypeCount = viewTypeCount
    util = ItemTypeUtil()
  }

  override fun getCount(): Int {
    return mDataList.size
  }

  override var data: List<T> = ArrayList()

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  /**
   * 通过数据得到obj的类型的type
   * 然后，通过[ItemTypeUtil]来转换位int类型的type
   *
   * instead by[.getItemType]
   */
  @Deprecated("")
  override fun getItemViewType(position: Int): Int {
    currentPos = position
    mType = getItemType(mDataList[position])
    // 如果不写这个方法，会让listView更换dataList后无法刷新数据
    return util.getIntType(mType)
  }

  override fun getItemType(t: T): Any {
    return -1 // default
  }

  override fun getViewTypeCount(): Int {
    return mViewTypeCount
  }

  override fun getView(position: Int, _convertView: View?, parent: ViewGroup): View {
    var convertView = _convertView
    if (mInflater == null) {
      mInflater = LayoutInflater.from(parent.context)
    }

    val item: AdapterItem<Any>
    if (convertView == null) {
      item = createItem(mType) as AdapterItem<Any>
      convertView = mInflater!!.inflate(item.layoutResId, parent, false)
      convertView!!.setTag(R.id.tag_item, item) // get item

      item.bindViews(convertView)
      item.setViews()
    } else {
      item = convertView.getTag(R.id.tag_item) as AdapterItem<Any> // save item
    }
    item.handleData(getConvertedData(mDataList[position], mType), position)
    return convertView
  }


  override fun getItem(position: Int): T {
    return mDataList[position]
  }

  override val currentPosition: Int
    get() = currentPos

}
