package kale.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import kale.adapter.item.AdapterItem;
import kale.adapter.item.DefalutEmptyItem;
import kale.adapter.item.ItemCreator;
import lombok.Getter;
import lombok.Setter;

/**
 * 使用{@link Builder}创建
 *
 * @author 陈治谋 (513500085@qq.com)
 * @since 16/6/28
 */
@SuppressWarnings("WeakerAccess")
public class LoadAdapterWrapper extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerLoadMoreAdapter
{
  //<editor-fold desc="view的基本类型，这里只有头/底部/普通，在子类中可以扩展">
  public static final int TYPE_HEADER = 0xfff0;

  public static final int TYPE_FOOTER_NONE         = 0xfff1;
  public static final int TYPE_FOOTER_LOADING      = 0xfff2;
  public static final int TYPE_FOOTER_EMPTY        = 0xfff3;
  public static final int TYPE_FOOTER_PULL_TO_LOAD = 0xfff4;
  public static final int TYPE_FOOTER_LOAD_MORE    = 0xfff5;
  public static final int TYPE_FOOTER_LOAD_ALL     = 0xfff6;
  public static final int TYPE_FOOTER_LOAD_FAILED  = 0xfff7;
  //</editor-fold>

  @State
  private int     state         = STATE_LOADING;
  private boolean onGettingData = false;
  private Action0 onLoadMore;

  @Getter
  private RecyclerView.LayoutManager layoutManager;
  private RecyclerView.Adapter       wrappedAdapter;

  private int currCount;


  @Getter @Setter
  private int pageSize = 20;

  @Setter
  private ItemCreator<Integer> itemCreator;

  @Getter
  private View headerView;

  private LoadAdapterWrapper(@NonNull RecyclerView.Adapter wrappedAdapter, @NonNull RecyclerView.LayoutManager layoutManager)
  {
    this.layoutManager = layoutManager;
    this.wrappedAdapter = wrappedAdapter;
    if (this.layoutManager instanceof GridLayoutManager) {
      setSpanSizeLookup(this, (GridLayoutManager) this.layoutManager); // 设置头部和尾部都是跨列的
    }
    observeData();
  }

  public void setHeaderView(@NonNull View headerView)
  {
    this.headerView = headerView;
    setFullSpan(headerView, layoutManager);
  }

  @Override public int getItemCount()
  {
    int offset = 0;
    offset += getHeaderCount();
    offset += getFooterCount();
    return wrappedAdapter.getItemCount() + offset;
  }

  @Override public int getItemViewType(int position)
  {
    position = position > 0 ? position : 0;

    int type;
    if (getHeaderCount() == 1 && position == 0) {
      type = TYPE_HEADER;
    } else if (getFooterCount() == 1 && position == getItemCount() - 1) {
      type = TYPE_FOOTER_NONE + state;
    } else {
      type = wrappedAdapter.getItemViewType(position - getHeaderCount());
    }
    return type;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
  {
    switch (viewType) {
      case TYPE_HEADER:
        return new RecyclerView.ViewHolder(headerView) {};
      case TYPE_FOOTER_LOADING:
      case TYPE_FOOTER_EMPTY:
      case TYPE_FOOTER_PULL_TO_LOAD:
      case TYPE_FOOTER_LOAD_MORE:
      case TYPE_FOOTER_LOAD_ALL:
        return new CommonRcvAdapter.RcvAdapterItem(parent.getContext(), parent, createItem(viewType));
      default:
        return wrappedAdapter.onCreateViewHolder(parent, viewType);
    }
  }

  public AdapterItem createItem(int type)
  {
    AdapterItem item = null;
    if (itemCreator != null) {
      item = itemCreator.create(type);
    }
    if (item == null) {
      item = new AdapterItem<Integer>()
      {
        @Override public void bindViews(View root) { }

        @Override public int getLayoutResId()
        {
          int resId = R.layout.view_load_empty;
          switch (type) {
            case TYPE_FOOTER_LOADING:
              resId = R.layout.view_load_loading;
              break;
            case TYPE_FOOTER_EMPTY:
              resId = R.layout.view_load_empty;
              break;
            case TYPE_FOOTER_PULL_TO_LOAD:
              resId = R.layout.view_load_pull_to_load;
              break;
            case TYPE_FOOTER_LOAD_MORE:
              resId = R.layout.view_load_load_more;
              break;
            case TYPE_FOOTER_LOAD_ALL:
              resId = R.layout.view_load_load_all;
              break;
          }
          return resId;
        }

        @Override public void setViews() { }

        @Override public void handleData(Integer integer, int position) { }
      };
    }

    return item;
  }

  @SuppressWarnings("unchecked")
  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position)
  {
    final int type = getItemViewType(position);
    switch (type) {
      case TYPE_HEADER:
      case TYPE_FOOTER_LOADING:
      case TYPE_FOOTER_EMPTY:
      case TYPE_FOOTER_PULL_TO_LOAD:
      case TYPE_FOOTER_LOAD_MORE:
      case TYPE_FOOTER_LOAD_ALL:
        break;
      default:
        wrappedAdapter.onBindViewHolder(holder, position - getHeaderCount());
    }
  }

  public void reset()
  {
    onGettingData = false;
  }

  @Override public void setOnRefresh()
  {
    none();
    currCount = 0;
  }

  @Override public void none()
  {
    setState(STATE_NONE);
  }

  @Override public void empty()
  {
    setState(STATE_EMPTY);
  }

  @Override public void loading()
  {
    setState(STATE_LOADING);
  }

  @Override public void pullToLoad()
  {
    setState(STATE_PULL_TO_LOAD);
  }

  @Override public void loadingMore()
  {
    if (state != STATE_LOADING_MORE) {
      onGettingData = false;
      setState(STATE_LOADING_MORE);
    }
  }

  @Override public void all()
  {
    setState(STATE_LOAD_ALL);
  }

  @Override public void loadFailed()
  {
    setState(STATE_LOAD_FAILED);
  }

  @Override public void onLoadMore(Action0 onLoadMore)
  {
    this.onLoadMore = onLoadMore;
  }

  @Override public void setupWithRcv(RecyclerView recyclerView)
  {
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
    {
      @Override public void onScrollStateChanged(RecyclerView recyclerView, int newState)
      {
        //The RecyclerView is not currently scrolling.
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
          RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
          if (layoutManager instanceof LinearLayoutManager) {
            int lastVisiblePosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            if (lastVisiblePosition >= recyclerView.getAdapter().getItemCount() - 1) {
              if (state == STATE_PULL_TO_LOAD) {
                loadingMore();
              }
              onLoadMore();
            }
          } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int                        last[]                     = new int[staggeredGridLayoutManager.getSpanCount()];
            staggeredGridLayoutManager.findLastVisibleItemPositions(last);

            for (int aLast : last) {
              if (aLast >= recyclerView.getAdapter().getItemCount() - 1) {
                if (state == STATE_PULL_TO_LOAD) {
                  loadingMore();
                }
                if (state == STATE_LOADING_MORE)
                  onLoadMore();
              }
            }
          }
        }
      }
    });

    recyclerView.setLayoutManager(layoutManager);
    recyclerView.setAdapter(this);
  }

  public int getHeaderCount()
  {
    return headerView != null ? 1 : 0;
  }

  public int getFooterCount()
  {
    return state == STATE_NONE ? 0 : 1;
  }

  private void onLoadMore()
  {
    if (!onGettingData && isLoadMoreEnable()) {
      onGettingData = true;
      if (onLoadMore != null) {
        onLoadMore.call();
      }
    }
  }

  private void onDataChange()
  {
    int newCount = wrappedAdapter.getItemCount();
    if (newCount == 0) {
      empty();
    } else if (newCount < pageSize || newCount - currCount < pageSize) {
      all();
    } else {
      pullToLoad();
    }
    if (currCount == 0) {
      //如果刷新数据前是空的,回到顶部
      layoutManager.scrollToPosition(0);
    }
    currCount = newCount;
  }

  private void setState(@State int newState)
  {
    if (state != newState) {
      int oldState = state;
      state = newState;
      if (newState == STATE_NONE) {
        notifyItemRemoved(getItemCount());
      } else if (oldState == STATE_NONE) {
        notifyItemInserted(getItemCount() - 1);
      } else {
        notifyItemChanged(getItemCount() - 1);
      }
    }
  }

  private boolean isLoadMoreEnable()
  {
    return state == STATE_LOADING_MORE;
  }

  private void observeData()
  {
    this.wrappedAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver()
    {
      @Override public void onChanged()
      {
        notifyDataSetChanged();
        reset();
        onDataChange();
      }

      @Override public void onItemRangeChanged(int positionStart, int itemCount)
      {
        notifyItemChanged(positionStart + getHeaderCount(), itemCount);
        notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
        reset();
        onDataChange();
      }

      @Override public void onItemRangeInserted(int positionStart, int itemCount)
      {
        notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
        reset();
        onDataChange();
      }

      @Override public void onItemRangeRemoved(int positionStart, int itemCount)
      {
        notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
        if ((getFooterCount() != 0) && (positionStart + getFooterCount() + 1 == getItemCount())) { // last one
          notifyDataSetChanged();
        }
        reset();
        onDataChange();
      }

      @Override public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
      {
        reset();
        onDataChange();
      }
    });
  }

  private void setFullSpan(View view, RecyclerView.LayoutManager layoutManager)
  {
    final int itemHeight = view.getLayoutParams() != null
        ? view.getLayoutParams().height
        : ViewGroup.LayoutParams.WRAP_CONTENT;
    if (layoutManager instanceof StaggeredGridLayoutManager) {
      StaggeredGridLayoutManager.LayoutParams layoutParams = new StaggeredGridLayoutManager.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
      layoutParams.setFullSpan(true);
      view.setLayoutParams(layoutParams);
    } else if (layoutManager instanceof GridLayoutManager) {
      view.setLayoutParams(new ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT, itemHeight));

    }
    notifyDataSetChanged();
  }

  /**
   * 设置头和底部的跨列
   */
  private static void setSpanSizeLookup(final RecyclerView.Adapter adapter, final GridLayoutManager layoutManager)
  {
    GridLayoutManager.SpanSizeLookup originLookUp = layoutManager.getSpanSizeLookup();
    layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
    {
      private GridLayoutManager.SpanSizeLookup wrapperedLookup = originLookUp;

      @Override
      public int getSpanSize(int position)
      {
        final int type = adapter.getItemViewType(position);
        if (type >= TYPE_HEADER && type <= TYPE_FOOTER_LOAD_ALL) {
          // 如果是头部和底部，那么就横跨
          return layoutManager.getSpanCount();
        } else {
          // 如果是普通的，那么就保持原样
          int offset = 0;
          if (adapter instanceof LoadAdapterWrapper) {
            offset = ((LoadAdapterWrapper) adapter).getHeaderCount();
          }
          //return 1;
          return wrapperedLookup == null ? 1 : wrapperedLookup.getSpanSize(position - offset);
        }
      }
    });
  }


  @Override public void onViewAttachedToWindow(RecyclerView.ViewHolder holder)
  {
    super.onViewAttachedToWindow(holder);
    if (wrappedAdapter != null) {
      wrappedAdapter.onViewAttachedToWindow(holder);
    }
  }

  @Override public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder)
  {
    super.onViewDetachedFromWindow(holder);
    if (wrappedAdapter != null) {
      wrappedAdapter.onViewDetachedFromWindow(holder);
    }
  }

  @SuppressWarnings("unused")
  public static class Builder
  {
    Context context;

    RecyclerView               recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter       adapter;

    Action0 onLoadMoreListener;

    AdapterItem loadingItem;
    AdapterItem emptyItem = new DefalutEmptyItem();
    AdapterItem pullToLoadItem;
    AdapterItem loadMoreItem;
    AdapterItem loadAllItem;
    AdapterItem loadFailedItem;

    public Builder(Context context)
    {
      this.context = context;
    }

    public Builder recycler(RecyclerView recyclerView)
    {
      this.recyclerView = recyclerView;
      return this;
    }

    public Builder adapter(RecyclerView.Adapter adapter)
    {
      this.adapter = adapter;
      return this;
    }

    public Builder layoutManager(RecyclerView.LayoutManager layoutManager)
    {
      this.layoutManager = layoutManager;
      return this;
    }

    public Builder onLoadMore(Action0 onLoadMoreListener)
    {
      this.onLoadMoreListener = onLoadMoreListener;
      return this;
    }

    public Builder loadingItem(AdapterItem item)
    {
      loadingItem = item;
      return this;
    }

    public Builder emptyItem(AdapterItem item)
    {
      emptyItem = item;
      return this;
    }

    public Builder pullToLoadItem(AdapterItem item)
    {
      pullToLoadItem = item;
      return this;
    }

    public Builder loadMoreItem(AdapterItem item)
    {
      loadMoreItem = item;
      return this;
    }

    public Builder loadAllItem(AdapterItem item)
    {
      loadAllItem = item;
      return this;
    }

    public Builder loadFailedItem(AdapterItem item)
    {
      loadFailedItem = item;
      return this;
    }

    public LoadAdapterWrapper build()
    {
      LoadAdapterWrapper adapterWrapper = new LoadAdapterWrapper(adapter, layoutManager);
      adapterWrapper.setupWithRcv(recyclerView);
      adapterWrapper.onLoadMore(onLoadMoreListener);


      adapterWrapper.setItemCreator(type -> {
        AdapterItem item = null;
        switch (type) {
          case TYPE_FOOTER_LOADING:
            item = loadingItem;
            break;
          case TYPE_FOOTER_EMPTY:
            item = emptyItem;
            break;
          case TYPE_FOOTER_PULL_TO_LOAD:
            item = pullToLoadItem;
            break;
          case TYPE_FOOTER_LOAD_MORE:
            item = loadMoreItem;
            break;
          case TYPE_FOOTER_LOAD_ALL:
            item = loadAllItem;
            break;
          case TYPE_FOOTER_LOAD_FAILED:
            item = loadFailedItem;
            break;
        }
        return item;
      });

      return adapterWrapper;
    }
  }
}
