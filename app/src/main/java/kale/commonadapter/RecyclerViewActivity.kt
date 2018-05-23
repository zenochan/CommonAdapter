package kale.commonadapter

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast

import kale.adapter.CommonRcvAdapter
import kale.adapter.item.AdapterItem
import kale.adapter.util.IAdapter
import kale.commonadapter.item.ButtonItem
import kale.commonadapter.item.ImageItem
import kale.commonadapter.item.TextItem
import kale.commonadapter.model.DemoModel
import kale.commonadapter.util.DataManager
import kale.commonadapter.util.LayoutUtil

/**
 * @author Kale
 * @date 2016/1/28
 */
class RecyclerViewActivity : AppCompatActivity() {

  private var mRecyclerView: RecyclerView? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mRecyclerView = RecyclerView(this)
    LayoutUtil.setContentView(this, mRecyclerView!!)

    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    layoutManager.recycleChildrenOnDetach = true
    mRecyclerView!!.layoutManager = layoutManager

    // 放一个默认空数据
    mRecyclerView!!.adapter = getAdapter()

    // 现在得到数据
    val data = DataManager.loadData(baseContext)
    (mRecyclerView!!.adapter as IAdapter<DemoModel>).data = data // 设置新的数据
    mRecyclerView!!.adapter.notifyDataSetChanged() // 通知数据刷新

    loadNewData(data)
  }

  private fun loadNewData(data: MutableList<DemoModel>) {
    mRecyclerView!!.postDelayed({
      data.clear()
      data.addAll(DataManager.loadData(baseContext)) // 对data进行操作

      mRecyclerView!!.adapter.notifyDataSetChanged() // 通知数据刷新

      Toast.makeText(this@RecyclerViewActivity, "refresh completed", Toast.LENGTH_SHORT).show()
    }, 1000)
  }

  /**
   * CommonAdapter的类型和item的类型是一致的
   * 这里的都是[DemoModel]
   *
   * 多种类型的type
   */
  private fun getAdapter(data: List<DemoModel> = ArrayList()): CommonRcvAdapter<DemoModel> {
    return object : CommonRcvAdapter<DemoModel>(data) {

      override fun getItemType(demoModel: DemoModel): Any {
        return demoModel.type
      }

      override fun createItem(type: Any): AdapterItem<*> {
        Log.d(TAG, "createItem $type view")
        when (type as String) {
          "text" -> return TextItem()
          "button" -> return ButtonItem()
          "image" -> return ImageItem()
          else -> throw IllegalArgumentException("不合法的type")
        }
      }
    }
  }

  companion object {

    private val TAG = "RecyclerViewActivity"
  }

}
