package kale.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author Jack Tony
 * @date 2015/6/2
 */
class RcvAdapterWrapper(
    val wrappedAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    private var layoutManager: RecyclerView.LayoutManager?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  private var hasShownEmptyView = false

  private var emptyViewParent: RecyclerView? = null

  private var headerView: View? = null

  private var footerView: View? = null

  var emptyView: View? = null
    private set

  val headerCount: Int
    get() = if (headerView != null) 1 else 0

  val footerCount: Int
    get() = if (footerView != null) 1 else 0


  init {
    wrappedAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        notifyDataSetChanged()
      }

      override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        notifyItemRangeChanged(positionStart + headerCount, itemCount)
      }

      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        notifyItemRangeInserted(positionStart + headerCount, itemCount)
        if (hasShownEmptyView && getItemCount() != 0) {
          notifyItemRemoved(headerCount)
          hasShownEmptyView = false
        }
      }

      override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        notifyItemRangeRemoved(positionStart + headerCount, itemCount)
        /*if (getFooterCount() != 0) {
                    if (positionStart + getFooterCount() + 1 == getItemCount()) { // last one
                        notifyDataSetChanged();
                    }
                }*/
      }

      override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        // FIXME: 2015/11/23 还没支持"多个"item的转移的操作
      }
    })

    if (this.layoutManager is GridLayoutManager) {
      setSpanSizeLookup(this, (this.layoutManager as GridLayoutManager?)!!) // 设置头部和尾部都是跨列的
    }
  }

  fun getLayoutManager(): RecyclerView.LayoutManager? {
    return layoutManager
  }

  fun getHeaderView(): View? {
    return headerView
  }

  fun getFooterView(): View? {
    return footerView
  }

  /**
   * @return The total number of items in this adapter.
   */
  override fun getItemCount(): Int {
    val count = wrappedAdapter.itemCount

    var offset = 0
    if (headerView != null) {
      offset++
    }
    if (footerView != null) {
      offset++
    }
    if (emptyView != null) {
      if (count == 0) {
        offset++
        hasShownEmptyView = true
      } else {
        hasShownEmptyView = false
      }
    }
    return offset + count
  }

  override fun getItemViewType(position: Int): Int {
    return if (headerView != null && position == 0) {
      TYPE_HEADER
    } else if (footerView != null && position == itemCount - 1) {
      TYPE_FOOTER
    } else if (emptyView != null && wrappedAdapter.itemCount == 0 && position == headerCount) {
      TYPE_EMPTY
    } else {
      wrappedAdapter.getItemViewType(position - headerCount)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    when (viewType) {
      TYPE_HEADER -> return SimpleViewHolder(headerView ?: throw IllegalStateException("WTF"))
      TYPE_FOOTER -> return SimpleViewHolder(footerView ?: throw IllegalStateException("WTF"))
      TYPE_EMPTY -> return SimpleViewHolder(emptyView ?: throw IllegalStateException("WTF"))
      else -> return wrappedAdapter.onCreateViewHolder(parent, viewType)
    }
  }

  /**
   * 载入ViewHolder，这里仅仅处理header和footer视图的逻辑
   */
  override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
    val type = getItemViewType(position)
    if (type != TYPE_HEADER && type != TYPE_FOOTER && type != TYPE_EMPTY) {
      wrappedAdapter.onBindViewHolder(viewHolder, position - headerCount)
    }

    if (type == TYPE_EMPTY && emptyViewParent != null) {
      val params = viewHolder.itemView.layoutParams as RecyclerView.LayoutParams
      val headerHeight = if (headerView != null) headerView!!.height else 0
      params.height = emptyViewParent!!.height - headerHeight
    }
  }

  fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
    if (this.layoutManager === layoutManager) {
      return
    }

    this.layoutManager = layoutManager
    if (this.layoutManager is GridLayoutManager) {
      setSpanSizeLookup(this, (this.layoutManager as GridLayoutManager?)!!) // 设置头部和尾部都是跨列的
    }
    setFullSpan(headerView, layoutManager)
    setFullSpan(footerView, layoutManager)
    setFullSpan(emptyView, layoutManager)
  }

  ///////////////////////////////////////////////////////////////////////////
  // 添加/移除头部、尾部的操作
  ///////////////////////////////////////////////////////////////////////////

  fun setHeaderView(headerView: View?) {
    if (this.headerView === headerView) {
      return
    }
    this.headerView = headerView
    setFullSpan(headerView, layoutManager)
  }

  fun setFooterView(footerView: View?) {
    if (this.footerView === footerView) {
      return
    }
    this.footerView = footerView
    setFullSpan(footerView, layoutManager)
  }

  /**
   * 设置空状态的view
   *
   * @param emptyViewParent 如果需要EmptyView的高度占满RecyclerView，则此参数必填；
   * 传null，则保持EmptyView的自有高度
   */
  fun setEmptyView(emptyView: View?, emptyViewParent: RecyclerView?) {
    if (this.emptyView === emptyView) {
      return
    }
    this.emptyView = emptyView
    this.emptyViewParent = emptyViewParent
    setFullSpan(emptyView, layoutManager)
  }

  /**
   * notifyItemRemoved(0);如果这里需要做头部的删除动画，
   */
  fun removeHeaderView() {
    headerView = null
    notifyDataSetChanged()
  }

  /**
   * 这里因为删除尾部不会影响到前面的pos的改变，所以不用刷新
   */
  fun removeFooterView() {
    footerView = null
    val footerPos = itemCount
    notifyItemRemoved(footerPos)
  }

  private fun setFullSpan(view: View?, layoutManager: RecyclerView.LayoutManager?) {
    if (view != null) {
      var itemWidth = if (view.layoutParams != null)
        view.layoutParams.height
      else
        RecyclerView.LayoutParams.WRAP_CONTENT
      var itemHeight = if (view.layoutParams != null)
        view.layoutParams.height
      else
        RecyclerView.LayoutParams.WRAP_CONTENT

      if (layoutManager is StaggeredGridLayoutManager) {
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
          itemWidth = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
          itemHeight = ViewGroup.LayoutParams.MATCH_PARENT
        }
        val layoutParams = StaggeredGridLayoutManager.LayoutParams(itemWidth, itemHeight)
        layoutParams.isFullSpan = true
        view.layoutParams = layoutParams
      } else if (layoutManager is LinearLayoutManager) {
        if (layoutManager.orientation == LinearLayoutManager.VERTICAL) {
          itemWidth = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
          itemHeight = ViewGroup.LayoutParams.MATCH_PARENT
        }
        view.layoutParams = RecyclerView.LayoutParams(itemWidth, itemHeight)
      }
      notifyDataSetChanged()
    }
  }

  /**
   * 设置头和底部的跨列
   */
  private fun setSpanSizeLookup(adapter: RecyclerView.Adapter<*>, layoutManager: GridLayoutManager) {
    layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
      override fun getSpanSize(position: Int): Int {
        val type = adapter.getItemViewType(position)
        return if (type == TYPE_HEADER || type == TYPE_FOOTER || type == TYPE_EMPTY) {
          layoutManager.spanCount
        } else {
          // 如果是普通的，那么就保持原样
          //return layoutManager.getSpanSizeLookup().getSpanSize(position - adapter.getHeaderCount());
          1
        }
      }
    }
  }

  override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
    super.onViewAttachedToWindow(holder)
    if (holder !is SimpleViewHolder) {
      wrappedAdapter.onViewAttachedToWindow(holder)
    }
  }

  override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    if (holder !is SimpleViewHolder) {
      wrappedAdapter.onViewDetachedFromWindow(holder)
    }
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    wrappedAdapter.onAttachedToRecyclerView(recyclerView)
  }

  override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
    super.onDetachedFromRecyclerView(recyclerView)
    wrappedAdapter.onDetachedFromRecyclerView(recyclerView)
  }

  override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
    super.onViewRecycled(holder)
    if (holder !is SimpleViewHolder) {
      wrappedAdapter.onViewRecycled(holder)
    }
  }

  /**
   * Keep it simple!
   */
  private class SimpleViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

  companion object {
    /**
     * view的基本类型，这里只有头/底部/普通，在子类中可以扩展
     */
    const val TYPE_HEADER = 99930
    const val TYPE_FOOTER = 99931
    const val TYPE_EMPTY = 99932
  }

}
