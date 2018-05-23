package kale.commonadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log

import kale.adapter.CommonRcvAdapter
import kale.adapter.item.AdapterItem
import kale.commonadapter.item.TextItem
import kale.commonadapter.model.DemoModel
import kale.commonadapter.util.DataManager
import kale.commonadapter.util.LayoutUtil

/**
 * @author Kale
 * @date 2017/2/8
 */
class DiffRcvActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val recyclerView = RecyclerView(this)
    LayoutUtil.setContentView(this, recyclerView)
    recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    val adapter = object : DiffRcvAdapter<DemoModel>(DataManager.loadData(this, 3)) {
      override fun createItem(type: Any): AdapterItem<*> {
        return TextItem()
      }

      override fun isContentSame(oldItemData: DemoModel, newItemData: DemoModel): Boolean {
        return oldItemData.content == newItemData.content
      }
    }

    recyclerView.adapter = adapter

    recyclerView.postDelayed({ adapter.setData(DataManager.loadData(this@DiffRcvActivity, 3)) }, 1000)
  }

  abstract class DiffRcvAdapter<T> internal constructor(data: List<T> = ArrayList()) : CommonRcvAdapter<T>(data) {

    override fun setData(data: List<T>) {
      DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
          return itemCount
        }

        override fun getNewListSize(): Int {
          return data.size
        }

        /**
         * 检测是否是相同的item，这里暂时通过位置判断
         */
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          val result = oldItemPosition == newItemPosition
          Log.d(TAG, "areItemsTheSame: $result")
          return result
        }

        /**
         * 检测是否是相同的数据
         * 这个方法仅仅在areItemsTheSame()返回true时，才调用。
         */
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          val result = isContentSame(getData()!![oldItemPosition], data[newItemPosition])
          Log.d(TAG, "areContentsTheSame: $result")
          return result
        }
      }).dispatchUpdatesTo(this)
      super.setData(data)

    }

    protected abstract fun isContentSame(oldItemData: T, newItemData: T): Boolean
  }

  companion object {

    private val TAG = "DiffRcvActivity"
  }

}
