package kale.commonadapter

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity

import kale.adapter.CommonPagerAdapter
import kale.adapter.item.AdapterItem
import kale.commonadapter.item.ButtonItem
import kale.commonadapter.item.ImageItem
import kale.commonadapter.item.TextItem
import kale.commonadapter.model.DemoModel
import kale.commonadapter.util.DataManager
import kale.commonadapter.util.LayoutUtil

/**
 * @author Kale
 * @date 2016/1/26
 *
 * 这里演示的是viewpager的适配器.
 * 有懒加载和正常加载两种的情况.
 *
 * 正常加载：是在viewpager初始化item的时候就进行数据的更新操作；
 * 懒加载：指当一页真正出现在用户眼前时才做数据的更新操作
 */
class ViewPagerActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val viewPager = ViewPager(this)
    LayoutUtil.setContentView(this, viewPager)

    val data = DataManager.loadData(baseContext)
    viewPager.adapter = test01(data)
  }

  /**
   * 正常加载
   */
  private fun test01(data: List<DemoModel>): CommonPagerAdapter<DemoModel> {
    return object : CommonPagerAdapter<DemoModel>(data) {

      override fun getItemType(t: DemoModel): Any {
        return t.type
      }

      override fun createItem(type: Any): AdapterItem<*> {
        when (type as String) {
          "text" -> return TextItem()
          "button" -> return ButtonItem()
          "image" -> return ImageItem()
          else -> throw IllegalArgumentException("不合法的type")
        }
      }
    }
  }

  /**
   * 懒加载
   */
  private fun test02(data: List<DemoModel>): CommonPagerAdapter<DemoModel> {
    return object : CommonPagerAdapter<DemoModel>(data, true) {

      override fun getItemType(t: DemoModel): Any {
        return t.type
      }

      override fun createItem(type: Any): AdapterItem<*> {
        when (type as String) {
          "text" -> return TextItem()
          "button" -> return ButtonItem()
          "image" -> return ImageItem()
          else -> throw IllegalArgumentException("不合法的type")
        }
      }
    }
  }

}
