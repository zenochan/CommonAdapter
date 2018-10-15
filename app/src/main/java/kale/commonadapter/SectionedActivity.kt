package kale.commonadapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import kale.adapter.SectionedCardDecoration
import kale.adapter.SectionedRcvAdapter
import kale.adapter.SectionedSpanSizeLookup
import kale.commonadapter.item.SpecificHeader
import kale.adapter.item.AdapterItem
import kale.adapter.item.Section
import kale.commonadapter.item.FullTextItem
import kale.commonadapter.item.SectionHeaderItem
import kale.commonadapter.item.SimpleTextItem
import kale.commonadapter.model.FullStringSection
import kale.commonadapter.model.HeaderSection
import kale.commonadapter.model.StringSection
import org.jetbrains.anko.dip
import org.jetbrains.anko.padding

/**
 * @author [陈治谋](mailto : zenochan @ qq.com)
 * @since 2018/6/4
 */
class SectionedActivity : AppCompatActivity() {
  lateinit var recycler: RecyclerView
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    recycler = RecyclerView(this)
    recycler.clipToPadding = false
    recycler.layoutParams = ViewGroup.LayoutParams(-1, -1)
    recycler.padding = dip(8)
    setContentView(recycler)

    val data = listOf(
        HeaderSection(),
        StringSection(),
        FullStringSection(),
        StringSection(),
        FullStringSection(),
        StringSection(),
        FullStringSection(),
        StringSection()
    )

    val lm = GridLayoutManager(this, 3)
    val adapter = object : SectionedRcvAdapter<Section<Any>>(data) {
      override fun createSectionItem(sectionType: Int, itemType: Int): AdapterItem<Any> = when (itemType) {
        SectionedRcvAdapter.TYPE_SECTION_HEADER -> when (sectionType) {
          3 -> SpecificHeader()
          else -> SectionHeaderItem()
        }
        else -> when (sectionType) {
          2 -> FullTextItem()
          else -> SimpleTextItem()
        }
      } as AdapterItem<Any>
    }

    lm.spanSizeLookup = SectionedSpanSizeLookup(adapter, lm)
    recycler.layoutManager = lm
    recycler.adapter = adapter
    recycler.addItemDecoration(SectionedCardDecoration(adapter,startPosition = 1))
  }
}
