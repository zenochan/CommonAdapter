package kale.commonadapter

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    val adapter = object : DiffRcvAdapter<DemoModel>(DataManager.loadData(this, 3)) {
      override fun createItem(type: Any): AdapterItem<Any> {
        return TextItem() as AdapterItem<Any>
      }

      override fun isContentSame(oldItemData: DemoModel, newItemData: DemoModel): Boolean {
        return oldItemData.content == newItemData.content
      }
    }

    recyclerView.adapter = adapter

    recyclerView.postDelayed({
      adapter.data = DataManager.loadData(this@DiffRcvActivity, 3)
    }, 1000)
  }

  abstract class DiffRcvAdapter<T : Any> internal constructor(data: List<T> = ArrayList()) : CommonRcvAdapter<T>(data) {


    override var data
      get() = super.data
      set(value) {
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
            val result = isContentSame(data[oldItemPosition], data[newItemPosition])
            Log.d(TAG, "areContentsTheSame: $result")
            return result
          }
        }).dispatchUpdatesTo(this)
        super.data = value
      }

    protected abstract fun isContentSame(oldItemData: T, newItemData: T): Boolean
  }

  companion object {

    private val TAG = "DiffRcvActivity"
  }

}
