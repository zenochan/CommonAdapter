package kale.commonadapter

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast

import kale.adapter.CommonRcvAdapter
import kale.adapter.RcvAdapterWrapper
import kale.adapter.item.AdapterItem
import kale.commonadapter.item.ButtonItem
import kale.commonadapter.item.ImageItem
import kale.commonadapter.item.TextItem
import kale.commonadapter.model.DemoModel
import kale.commonadapter.util.DataManager
import kale.commonadapter.util.LayoutUtil
import kale.commonadapter.util.ObservableArrayList
import kale.commonadapter.util.OnItemClickListener

/**
 * @author Kale
 * @date 2016/3/16
 */
class HeaderFooterActivity : AppCompatActivity() {

  private val data = ObservableArrayList<DemoModel>()

  private var wrapper: RcvAdapterWrapper? = null

  private var recyclerView: RecyclerView? = null

  private var layoutManager: LinearLayoutManager? = null

  private var layoutManager1: GridLayoutManager? = null

  private var layoutManager2: StaggeredGridLayoutManager? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    recyclerView = RecyclerView(this)
    LayoutUtil.setContentView(this, recyclerView!!)

    layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    layoutManager1 = GridLayoutManager(this, 2)
    layoutManager2 = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
    recyclerView!!.layoutManager = layoutManager

    data.addAll(DataManager.loadData(baseContext))

    val adapter = initAdapter()

    wrapper = RcvAdapterWrapper(adapter, recyclerView!!.layoutManager)

    val header = Button(this)
    header.text = "Header\n\n (click to add)"
    header.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 300)

    val footer = Button(this)
    footer.text = "footer"

    wrapper!!.headerView = header
    wrapper!!.footerView = null

    val empty = Button(this)
    empty.setBackgroundColor(Color.RED)
    empty.text = "empty text"
    wrapper!!.setEmptyView(empty, recyclerView)

    recyclerView!!.adapter = wrapper

    handItemClick()

    recyclerView!!.postDelayed({
      data.reset(DataManager.loadData(baseContext, 10))
      wrapper!!.footerView = footer
    }, 1000)
  }

  private fun handItemClick() {
    // 建议把点击事件写入item里面，在外面写会有各种各样的不可控的问题，需要谨慎。
    // 这里给出的监听器不会影响item自身的点击事件，可以保证外面和内部的监听事件同时被响应
    recyclerView!!.addOnItemTouchListener(OnItemClickListener(this,
        AdapterView.OnItemClickListener { parent, view, position, id ->
          var position = position
          position = position - wrapper!!.headerCount
          if (position >= 0 && position < data.size) {
            Toast.makeText(this@HeaderFooterActivity, "pos = $position", Toast.LENGTH_SHORT).show()
            data.removeAt(position)
          }

          if (position == -1) {
            // click header
            val model = DemoModel()
            model.type = "text"
            model.content = "kale"
            data.add(0, model)
          }
        }))
  }

  private fun initAdapter(): CommonRcvAdapter<DemoModel> {
    return object : CommonRcvAdapter<DemoModel>(data) {
      override fun getItemType(demoModel: DemoModel): Any {
        return demoModel.type
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

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val linear = menu.add(0, 0, 0, "L")
    linear.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

    val grid = menu.add(1, 1, 0, "G")
    grid.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

    val staggered = menu.add(2, 2, 0, "S")
    staggered.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == 0) {
      recyclerView!!.layoutManager = layoutManager
      wrapper!!.layoutManager = layoutManager
      return true
    } else if (item.itemId == 1) {
      recyclerView!!.layoutManager = layoutManager1
      wrapper!!.layoutManager = layoutManager1
      return true
    } else if (item.itemId == 2) {
      recyclerView!!.layoutManager = layoutManager2
      wrapper!!.layoutManager = layoutManager2
      return true
    } else {
      return super.onOptionsItemSelected(item)
    }
  }
}
