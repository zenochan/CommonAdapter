package kale.adapter

import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView


/**
 * Create Date: 16/6/28
 *
 * @author 陈治谋 (513500085@qq.com)
 */
interface RecyclerLoadMoreAdapter {

  val headerCount: Int

  val footerCount: Int

  @IntDef(STATE_LOADING, STATE_EMPTY, STATE_PULL_TO_LOAD, STATE_LOADING_MORE, STATE_LOAD_ALL, STATE_NONE, STATE_LOAD_FAILED)
  annotation class State

  /** 刷新时调用  */
  fun setOnRefresh()

  /** 无状态  */
  fun none()

  /** 正在加载中  */
  fun loading()

  /** 切换为空数据状态  */
  fun empty()

  /** 正在加载中  */
  fun loadingMore()

  /** 下拉加载更多  */
  fun pullToLoad()

  /** 切换为没有更多数据状态  */
  fun all()

  /** 切换为加载失败  */
  fun loadFailed()

  fun onLoadMore(onLoadMore: Action0)

  fun setupWithRcv(recyclerView: RecyclerView)

  companion object {
    const val STATE_NONE = 0                //啥也没有
    const val STATE_LOADING = 1             //第一次加载
    const val STATE_EMPTY = 2               //空
    const val STATE_PULL_TO_LOAD = 3        //等待下拉加载
    const val STATE_LOADING_MORE = 4        //正在加载
    const val STATE_LOAD_ALL = 5            //已加载全部
    const val STATE_LOAD_FAILED = 6         //加载失败
  }
}
