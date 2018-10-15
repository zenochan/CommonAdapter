package kale.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kale.adapter.LoadAdapterWrapper.Builder
import kale.adapter.RecyclerLoadMoreAdapter.State
import kale.adapter.item.AdapterItem
import kale.adapter.item.DefaultEmptyItem
import kale.adapter.item.ItemCreator

/**
 * 使用[Builder]创建
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 16/6/28
 */
class LoadAdapterWrapper private constructor(
    private val wrappedAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    val layoutManager: RecyclerView.LayoutManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), RecyclerLoadMoreAdapter {

  @State
  private var state = RecyclerLoadMoreAdapter.STATE_LOADING
  private var onGettingData = false
  var onLoadMore: (() -> Unit)? = null

  private var currCount: Int = 0


  var pageSize = 20

  var itemCreator: ItemCreator<Int>? = null

  var headerView: View? = null
    set(headerView) {
      field = headerView
      if (headerView != null) {
        setFullSpan(headerView, layoutManager)
      }
    }

  override val headerCount: Int
    get() = if (this.headerView != null) 1 else 0

  override val footerCount: Int
    get() = if (state == RecyclerLoadMoreAdapter.STATE_NONE) 0 else 1

  private val isLoadMoreEnable: Boolean
    get() = state == RecyclerLoadMoreAdapter.STATE_LOADING_MORE

  init {
    if (this.layoutManager is GridLayoutManager) {
      setSpanSizeLookup(this, this.layoutManager) // 设置头部和尾部都是跨列的
    }
    observeData()
  }

  override fun getItemCount(): Int {
    var offset = 0
    offset += headerCount
    offset += footerCount
    return wrappedAdapter.itemCount + offset
  }

  override fun getItemViewType(_position: Int): Int {
    var position = _position
    position = if (position > 0) position else 0

    val type: Int
    if (headerCount == 1 && position == 0) {
      type = TYPE_HEADER
    } else if (footerCount == 1 && position == itemCount - 1) {
      type = TYPE_FOOTER_NONE + state
    } else {
      type = wrappedAdapter.getItemViewType(position - headerCount)
    }
    return type
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    when (viewType) {
      TYPE_HEADER -> return object : RecyclerView.ViewHolder(this.headerView!!) {

      }
      TYPE_FOOTER_LOADING,
      TYPE_FOOTER_EMPTY,
      TYPE_FOOTER_PULL_TO_LOAD,
      TYPE_FOOTER_LOAD_MORE,
      TYPE_FOOTER_LOAD_ALL -> {
        val item = createItem(viewType) as AdapterItem<Any>
        val holder = CommonRcvAdapter.RcvAdapterItem(parent.context, parent, item)
        val view = holder.itemView
        val itemHeight = if (view.layoutParams != null)
          view.layoutParams.height
        else
          ViewGroup.LayoutParams.WRAP_CONTENT
        if (layoutManager is StaggeredGridLayoutManager) {
          val layoutParams = StaggeredGridLayoutManager.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, itemHeight)
          layoutParams.isFullSpan = true
          view.layoutParams = layoutParams
        } else if (layoutManager is GridLayoutManager) {
          view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight)
        }
        return holder
      }
      else -> return wrappedAdapter.onCreateViewHolder(parent, viewType)
    }
  }

  fun createItem(type: Int): AdapterItem<Int> {
    return itemCreator?.create(type) ?: object : AdapterItem<Int> {

      override val layoutResId: Int
        get() {
          var resId = R.layout.view_load_empty
          when (type) {
            TYPE_FOOTER_LOADING -> resId = R.layout.view_load_loading
            TYPE_FOOTER_EMPTY -> resId = R.layout.view_load_empty
            TYPE_FOOTER_PULL_TO_LOAD -> resId = R.layout.view_load_pull_to_load
            TYPE_FOOTER_LOAD_MORE -> resId = R.layout.view_load_load_more
            TYPE_FOOTER_LOAD_ALL -> resId = R.layout.view_load_load_all
          }
          return resId
        }

      override fun bindViews(root: View) {}

      override fun setViews() {}

      override fun handleData(t: Int, position: Int) {}
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    val type = getItemViewType(position)
    when (type) {
      TYPE_HEADER, TYPE_FOOTER_LOADING, TYPE_FOOTER_EMPTY, TYPE_FOOTER_PULL_TO_LOAD, TYPE_FOOTER_LOAD_MORE, TYPE_FOOTER_LOAD_ALL -> {
      }
      else -> wrappedAdapter.onBindViewHolder(holder, position - headerCount)
    }
  }

  fun reset() {
    onGettingData = false
  }

  override fun setOnRefresh() {
    none()
    currCount = 0
  }

  override fun none() {
    setState(RecyclerLoadMoreAdapter.STATE_NONE)
  }

  override fun empty() {
    setState(RecyclerLoadMoreAdapter.STATE_EMPTY)
  }

  override fun loading() {
    setState(RecyclerLoadMoreAdapter.STATE_LOADING)
  }

  override fun pullToLoad() {
    setState(RecyclerLoadMoreAdapter.STATE_PULL_TO_LOAD)
  }

  override fun loadingMore() {
    if (state != RecyclerLoadMoreAdapter.STATE_LOADING_MORE) {
      onGettingData = false
      setState(RecyclerLoadMoreAdapter.STATE_LOADING_MORE)
    }
  }

  override fun all() {
    setState(RecyclerLoadMoreAdapter.STATE_LOAD_ALL)
  }

  override fun loadFailed() {
    setState(RecyclerLoadMoreAdapter.STATE_LOAD_FAILED)
  }


  override fun setupWithRcv(recyclerView: RecyclerView) {
    recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        //The RecyclerView is not currently scrolling.
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          val layoutManager = recyclerView.layoutManager
          if (layoutManager is LinearLayoutManager) {
            val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
            if (lastVisiblePosition >= recyclerView.adapter!!.itemCount - 1) {
              if (state == RecyclerLoadMoreAdapter.STATE_PULL_TO_LOAD) {
                loadingMore()
              }
              onLoadMore()
            }
          } else if (layoutManager is StaggeredGridLayoutManager) {
            val staggeredGridLayoutManager = layoutManager
            val last = IntArray(staggeredGridLayoutManager.spanCount)
            staggeredGridLayoutManager.findLastVisibleItemPositions(last)

            for (aLast in last) {
              if (aLast >= recyclerView.adapter!!.itemCount  - 1) {
                if (state == RecyclerLoadMoreAdapter.STATE_PULL_TO_LOAD) {
                  loadingMore()
                }
                if (state == RecyclerLoadMoreAdapter.STATE_LOADING_MORE)
                  onLoadMore()
              }
            }
          }
        }
      }
    })

    recyclerView.layoutManager = layoutManager
    recyclerView.adapter = this
  }

  private fun onLoadMore() {
    if (!onGettingData && isLoadMoreEnable) {
      onGettingData = true
      onLoadMore?.invoke()
    }
  }

  private fun onDataChange() {
    val newCount = wrappedAdapter.itemCount
    if (newCount == 0) {
      empty()
    } else if (newCount < pageSize || newCount - currCount < pageSize) {
      all()
    } else {
      pullToLoad()
    }
    if (currCount == 0) {
      //如果刷新数据前是空的,回到顶部
      layoutManager.scrollToPosition(0)
    }
    currCount = newCount
  }

  private fun setState(@State newState: Int) {
    if (state != newState) {
      val oldState = state
      state = newState
      if (newState == RecyclerLoadMoreAdapter.STATE_NONE) {
        notifyItemRemoved(itemCount)
      } else if (oldState == RecyclerLoadMoreAdapter.STATE_NONE) {
        notifyItemInserted(itemCount - 1)
      } else {
        notifyItemChanged(itemCount - 1)
      }
    }
  }

  private fun observeData() {
    this.wrappedAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        notifyDataSetChanged()
        reset()
        onDataChange()
      }

      override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        notifyItemChanged(positionStart + headerCount, itemCount)
        notifyItemRangeChanged(positionStart + headerCount, itemCount)
        reset()
        onDataChange()
      }

      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        notifyItemRangeInserted(positionStart + headerCount, itemCount)
        reset()
        onDataChange()
      }

      override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        notifyItemRangeRemoved(positionStart + headerCount, itemCount)
        if (footerCount != 0 && positionStart + footerCount + 1 == getItemCount()) { // last one
          notifyDataSetChanged()
        }
        reset()
        onDataChange()
      }

      override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        reset()
        onDataChange()
      }
    })
  }

  private fun setFullSpan(view: View, layoutManager: RecyclerView.LayoutManager) {
    val itemHeight = if (view.layoutParams != null)
      view.layoutParams.height
    else
      ViewGroup.LayoutParams.WRAP_CONTENT
    if (layoutManager is StaggeredGridLayoutManager) {
      val layoutParams = StaggeredGridLayoutManager.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, itemHeight)
      layoutParams.isFullSpan = true
      view.layoutParams = layoutParams
    } else if (layoutManager is GridLayoutManager) {
      view.layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, itemHeight)

    }
    notifyDataSetChanged()
  }


  override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
    super.onViewAttachedToWindow(holder)
    wrappedAdapter.onViewAttachedToWindow(holder)
  }

  override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
    super.onViewDetachedFromWindow(holder)
    wrappedAdapter.onViewDetachedFromWindow(holder)
  }

  override fun onLoadMore(onLoadMore: (() -> Unit)?) {
    this.onLoadMore = onLoadMore
  }


  class Builder(internal var context: Context) {

    lateinit internal var recyclerView: RecyclerView
    lateinit internal var layoutManager: RecyclerView.LayoutManager
    lateinit internal var adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>

    lateinit internal var onLoadMoreListener: () -> Unit

    internal var loadingItem: AdapterItem<Int>? = null
    internal var emptyItem: AdapterItem<Int> = DefaultEmptyItem()
    internal var pullToLoadItem: AdapterItem<Int>? = null
    internal var loadMoreItem: AdapterItem<Int>? = null
    internal var loadAllItem: AdapterItem<Int>? = null
    internal var loadFailedItem: AdapterItem<Int>? = null

    fun recycler(recyclerView: RecyclerView): Builder {
      this.recyclerView = recyclerView
      return this
    }

    fun <T : RecyclerView.ViewHolder> adapter(adapter: RecyclerView.Adapter<T>): Builder {
      this.adapter = adapter as RecyclerView.Adapter<RecyclerView.ViewHolder>
      return this
    }

    fun layoutManager(layoutManager: RecyclerView.LayoutManager): Builder {
      this.layoutManager = layoutManager
      return this
    }

    fun onLoadMore(onLoadMoreListener: () -> Unit): Builder {
      this.onLoadMoreListener = onLoadMoreListener
      return this
    }

    fun loadingItem(item: AdapterItem<Int>): Builder {
      loadingItem = item
      return this
    }

    fun emptyItem(item: AdapterItem<Int>): Builder {
      emptyItem = item
      return this
    }

    fun pullToLoadItem(item: AdapterItem<Int>): Builder {
      pullToLoadItem = item
      return this
    }

    fun loadMoreItem(item: AdapterItem<Int>): Builder {
      loadMoreItem = item
      return this
    }

    fun loadAllItem(item: AdapterItem<Int>): Builder {
      loadAllItem = item
      return this
    }

    fun loadFailedItem(item: AdapterItem<Int>): Builder {
      loadFailedItem = item
      return this
    }

    fun build(): LoadAdapterWrapper {
      val adapterWrapper = LoadAdapterWrapper(adapter, layoutManager)
      adapterWrapper.setupWithRcv(recyclerView)
      adapterWrapper.onLoadMore(onLoadMoreListener)


      adapterWrapper.itemCreator = object : ItemCreator<Int> {
        override fun create(t: Int): AdapterItem<Int>? {
          var item: AdapterItem<Int>? = null
          when (t) {
            TYPE_FOOTER_LOADING -> item = loadingItem
            TYPE_FOOTER_EMPTY -> item = emptyItem
            TYPE_FOOTER_PULL_TO_LOAD -> item = pullToLoadItem
            TYPE_FOOTER_LOAD_MORE -> item = loadMoreItem
            TYPE_FOOTER_LOAD_ALL -> item = loadAllItem
            TYPE_FOOTER_LOAD_FAILED -> item = loadFailedItem
          }
          return item
        }
      }

      return adapterWrapper
    }
  }

  companion object {
    //<editor-fold desc="view的基本类型，这里只有头/底部/普通，在子类中可以扩展">
    val TYPE_HEADER = 0xfff0

    val TYPE_FOOTER_NONE = 0xfff1
    val TYPE_FOOTER_LOADING = 0xfff2
    val TYPE_FOOTER_EMPTY = 0xfff3
    val TYPE_FOOTER_PULL_TO_LOAD = 0xfff4
    val TYPE_FOOTER_LOAD_MORE = 0xfff5
    val TYPE_FOOTER_LOAD_ALL = 0xfff6
    val TYPE_FOOTER_LOAD_FAILED = 0xfff7

    /**
     * 设置头和底部的跨列
     */
    private fun setSpanSizeLookup(adapter: RecyclerView.Adapter<*>, layoutManager: GridLayoutManager) {
      val originLookUp = layoutManager.spanSizeLookup
      layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {

        override fun getSpanSize(position: Int): Int {
          val type = adapter.getItemViewType(position)
          if (type >= TYPE_HEADER && type <= TYPE_FOOTER_LOAD_ALL) {
            // 如果是头部和底部，那么就横跨
            return layoutManager.spanCount
          } else {
            // 如果是普通的，那么就保持原样
            var offset = 0
            if (adapter is LoadAdapterWrapper) {
              offset = adapter.headerCount
            }
            //return 1;
            return originLookUp?.getSpanSize(position - offset) ?: 1
          }
        }
      }
    }
  }

}
