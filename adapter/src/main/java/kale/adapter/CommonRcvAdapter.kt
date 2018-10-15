package kale.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import kale.adapter.item.AdapterItem
import kale.adapter.util.DataBindingJudgement
import kale.adapter.util.IAdapter
import kale.adapter.util.ItemTypeUtil
import java.util.*

/**
 * @author Jack Tony
 * @date 2015/5/17
 */
abstract class CommonRcvAdapter<T : Any>(data: List<T> = ArrayList()) : RecyclerView.Adapter<CommonRcvAdapter.RcvAdapterItem>(), IAdapter<T> {
  private var mDataList: List<T>
  private lateinit var mType: Any
  private val mUtil: ItemTypeUtil
  private var currentPos: Int = 0

  init {
    if (DataBindingJudgement.SUPPORT_DATABINDING && data is ObservableList<*>) {
      (data as ObservableList<T>).addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(sender: ObservableList<T>) {
          notifyDataSetChanged()
        }

        override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
          notifyItemRangeChanged(positionStart, itemCount)
        }

        override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
          notifyItemRangeInserted(positionStart, itemCount)
          notifyChange(sender, positionStart)
        }

        override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
          notifyItemRangeRemoved(positionStart, itemCount)
          notifyChange(sender, positionStart)
        }

        override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
          notifyChange(sender, Math.min(fromPosition, toPosition))
        }

        private fun notifyChange(sender: ObservableList<T>, start: Int) {
          onItemRangeChanged(sender, start, itemCount - start)
        }

      })
    }
    mDataList = data
    mUtil = ItemTypeUtil()
  }

  /**
   * 配合RecyclerView的pool来设置TypePool
   */
  fun setTypePool(typePool: HashMap<Any, Int>) {
    mUtil.setTypePool(typePool)
  }

  override fun getItemCount(): Int = mDataList.size

  override var data: List<T>
    get() = mDataList
    set(value) {
      mDataList = value
    }

  override fun getItemId(position: Int): Long {
    return position.toLong()
  }

  /**
   * instead by[.getItemType]
   *
   *
   * 通过数据得到obj的类型的type
   * 然后，通过[ItemTypeUtil]来转换位int类型的type
   */
  @Deprecated("")
  override fun getItemViewType(position: Int): Int {
    this.currentPos = position
    mType = getItemType(mDataList[position])
    return mUtil.getIntType(mType)
  }

  override fun getItemType(t: T): Any {
    return -1 // default
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RcvAdapterItem {
    return RcvAdapterItem(parent.context, parent, createItem(mType) as AdapterItem<Any>)
  }

  override fun onBindViewHolder(holder: RcvAdapterItem, position: Int) {
    debug(holder)
    holder.item.handleData(getConvertedData(mDataList[position], mType), position)
  }


  override val currentPosition: Int = currentPos

  ///////////////////////////////////////////////////////////////////////////
  // 内部用到的viewHold
  ///////////////////////////////////////////////////////////////////////////
  class RcvAdapterItem internal constructor(
      context: Context,
      parent: ViewGroup,
      var item: AdapterItem<Any>
  ) : RecyclerView.ViewHolder(
      item.layout(context, parent)
          ?: LayoutInflater.from(context).inflate(item.layoutResId, parent, false)
  ) {

    internal var isNew = true // debug中才用到

    init {
      this.item.bindViews(itemView)
      this.item.setViews()
    }

  }

  ///////////////////////////////////////////////////////////////////////////
  // For debug
  ///////////////////////////////////////////////////////////////////////////

  private fun debug(holder: RcvAdapterItem) {
    val debug = false
    if (debug) {
      holder.itemView.setBackgroundColor(if (holder.isNew) -0x10000 else -0xff0100)
      holder.isNew = false
    }
  }

}
