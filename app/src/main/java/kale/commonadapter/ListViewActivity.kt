package kale.commonadapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import android.widget.Toast

import kale.adapter.CommonAdapter
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
 *
 * 这里展示adapter和item类型相同和不同时的各种处理方案
 */
class ListViewActivity : AppCompatActivity() {

  private var mListView: ListView? = null

  private var mData: List<DemoModel> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mListView = ListView(this)
    LayoutUtil.setContentView(this, mListView!!)

    mData = DataManager.loadData(baseContext)
    mListView!!.adapter = multiType(mData)
  }

  /**
   * 多种类型的type
   */
  private fun multiType(data: List<DemoModel>): CommonAdapter<DemoModel> {
    return object : CommonAdapter<DemoModel>(data, 3) {
      override fun getItemType(t: DemoModel): Any {
        return t.type
      }

      override fun createItem(type: Any): AdapterItem<DemoModel> {
        when (type as String) {
          "text" -> return TextItem()
          "button" -> return ButtonItem()
          "image" -> return ImageItem()
          else -> return ImageItem()
        }
      }
    }
  }

  /**
   * 一种类型的type
   */
  private fun singleType(data: List<DemoModel>): CommonAdapter<DemoModel> {
    return object : CommonAdapter<DemoModel>(data, 1) {

      override fun createItem(type: Any): AdapterItem<DemoModel> {
        // 如果就一种，那么直接return一种类型的item即可。
        return TextItem()
      }
    }
  }

  /**
   * 这里的adapter的类型是[DemoModel]，但item的类型是Integer.
   * 所以需要调用[IAdapter.getConvertedData]方法，来进行数据的转换
   *
   * 多种类型的type
   */
  private fun convertedData(data: List<DemoModel>): CommonAdapter<DemoModel> {
    return object : CommonAdapter<DemoModel>(data, 3) {

      override fun getItemType(t: DemoModel): Any {
        return t.type
      }

      override fun createItem(type: Any): AdapterItem<DemoModel> {
        when (type as String) {
          "image", "text" -> return TextItem()
          "button" -> return ButtonItem()
          else -> return ImageItem(object : ImageItem.ImageItemCallback() {
            override fun onImageClick(view: View) {
              Toast.makeText(this@ListViewActivity, "click", Toast.LENGTH_SHORT).show()
            }
          })
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menu.add(0, 0, 0, "1").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    menu.add(0, 1, 0, "2").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    menu.add(0, 2, 0, "3").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      0 -> {
        mListView!!.adapter = multiType(mData)
        return true
      }
      1 -> {
        mListView!!.adapter = singleType(mData)
        return true
      }
      2 -> {
        mListView!!.adapter = convertedData(mData)
        return true
      }
      else -> return super.onOptionsItemSelected(item)
    }
  }
}
