package kale.adapter;

import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;


/**
 * Create Date: 16/6/28
 *
 * @author 陈治谋 (513500085@qq.com)
 */
@SuppressWarnings("WeakerAccess")
public interface RecyclerLoadMoreAdapter
{
  int STATE_NONE         = 0;//啥也没有
  int STATE_LOADING      = 1;//第一次加载
  int STATE_EMPTY        = 2;//空
  int STATE_PULL_TO_LOAD = 3;//等待下拉加载
  int STATE_LOADING_MORE = 4;//正在加载
  int STATE_LOAD_ALL     = 5;//已加载全部
  int STATE_LOAD_FAILED  = 6;//加载失败

  @IntDef({STATE_LOADING, STATE_EMPTY, STATE_PULL_TO_LOAD, STATE_LOADING_MORE, STATE_LOAD_ALL, STATE_NONE, STATE_LOAD_FAILED})
  @interface State {}

  /** 刷新时调用 */
  void setOnRefresh();

  /** 无状态 */
  void none();

  /** 正在加载中 */
  void loading();

  /** 切换为空数据状态 */
  void empty();

  /** 正在加载中 */
  void loadingMore();

  /** 下拉加载更多 */
  void pullToLoad();

  /** 切换为没有更多数据状态 */
  void all();

  /** 切换为加载失败 */
  void loadFailed();

  void onLoadMore(Action0 onLoadMore);

  void setupWithRcv(RecyclerView recyclerView);

  int getHeaderCount();

  int getFooterCount();
}
