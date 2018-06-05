package kale.adapter

import android.graphics.*
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import kale.adapter.item.Section

/**
 * @author [陈治谋](mailto:zenochan@qq.com)
 * @since 2018/6/4
 */
class SectionedCardDecoration<T : Any>
(
    val sectionedAdapter: SectionedRcvAdapter<Section<T>>,
    var bgColor: Int = Color.WHITE,
    var radius: Float = 16F,
    var startPosition: Int = 0
) : RecyclerView.ItemDecoration() {

  val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  val rectF = RectF()


  override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
    super.getItemOffsets(outRect, view, parent, state)
    if ((parent.layoutManager as? GridLayoutManager)?.spanSizeLookup != null) {
      val lm: GridLayoutManager = parent.layoutManager as GridLayoutManager
      val position = parent.getChildAdapterPosition(view)
      val span = lm.spanSizeLookup.getSpanSize(position)
      if (span == lm.spanCount && position < startPosition) {
        outRect.top = -parent.paddingTop
        outRect.left = -parent.paddingLeft
        outRect.right = -parent.paddingRight
      }
    }
  }

  override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    super.onDraw(c, parent, state)
    val childCount = parent.childCount
    val left = parent.paddingLeft.toFloat()
    val right = parent.width - parent.paddingRight.toFloat()
    val lm = parent.layoutManager as? GridLayoutManager ?: return
    val sizeLookup = lm.spanSizeLookup ?: return


    var spanGroupIndex = -1
    for (i in 0 until childCount) {
      val view = parent.getChildAt(i)
      val position = parent.getChildAdapterPosition(view)

      if (position < startPosition) continue

      val isHeader = sectionedAdapter.isSectionHeaderPosition(position)

      val currentSpanIndex = sizeLookup.getSpanGroupIndex(position, lm.spanCount)
      if (currentSpanIndex != spanGroupIndex) {
        spanGroupIndex = currentSpanIndex
        Log.e("", "position -> $position")

        val top = view.top.toFloat()
        val bottom = view.bottom.toFloat()
        paint.color = bgColor
        rectF.set(left, top - (if (isHeader) 0F else radius * 2), right, bottom)
        c.drawRoundRect(rectF, radius, radius, paint)
      }
    }
  }
}