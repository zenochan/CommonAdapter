package kale.commonadapter.util


import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * @author Jack Tony
 * recyle view 滚动监听器
 * @since 2015/4/6
 */
class OnRcvScrollListener
/**
 * @param offset 设置：倒数几个才判定为到底，默认是0
 */
(offset: Int) : RecyclerView.OnScrollListener() {

  /**
   * 最后一个的位置
   */
  private var mLastPositions: IntArray? = null

  /**
   * 最后一个可见的item的位置
   */
  private var mLastVisibleItemPosition: Int = 0

  /**
   * 滑动的距离
   */
  private var mDistance = 0

  /**
   * 是否需要监听控制
   */
  private var mIsScrollDown = true

  /**
   * Y轴移动的实际距离（最顶部为0）
   */
  private var mScrolledYDistance = 0

  /**
   * X轴移动的实际距离（最左侧为0）
   */
  private var mScrolledXDistance = 0

  private var mOffset = 0

  init {
    mOffset = offset
  }

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)
    var firstVisibleItemPosition = 0
    val layoutManager = recyclerView.layoutManager ?: return

    // 判断layout manager的类型
    val type = judgeLayoutManager(layoutManager)
    // 根据类型来计算出第一个可见的item的位置，由此判断是否触发到底部的监听器
    firstVisibleItemPosition = calculateFirstVisibleItemPos(type, layoutManager, firstVisibleItemPosition)
    // 计算并判断当前是向上滑动还是向下滑动
    calculateScrollUpOrDown(firstVisibleItemPosition, dy)
    // 移动距离超过一定的范围，我们监听就没有啥实际的意义了
    mScrolledXDistance += dx
    mScrolledYDistance += dy
    mScrolledXDistance = if (mScrolledXDistance < 0) 0 else mScrolledXDistance
    mScrolledYDistance = if (mScrolledYDistance < 0) 0 else mScrolledYDistance
    onScrolled(mScrolledXDistance, mScrolledYDistance)
  }


  /**
   * 判断layoutManager的类型
   */
  private fun judgeLayoutManager(layoutManager: RecyclerView.LayoutManager): Int {
    return if (layoutManager is GridLayoutManager) {
      TYPE_GRID
    } else if (layoutManager is LinearLayoutManager) {
      TYPE_LINEAR
    } else if (layoutManager is StaggeredGridLayoutManager) {
      TYPE_STAGGERED_GRID
    } else {
      throw RuntimeException("Unsupported LayoutManager used. Valid ones are " + "LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager")
    }
  }

  /**
   * 计算第一个元素的位置
   */
  private fun calculateFirstVisibleItemPos(type: Int, layoutManager: RecyclerView.LayoutManager, firstVisibleItemPosition: Int): Int {
    var firstVisibleItemPosition = firstVisibleItemPosition
    when (type) {
      TYPE_LINEAR -> {
        mLastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
      }
      TYPE_GRID -> {
        mLastVisibleItemPosition = (layoutManager as GridLayoutManager).findLastVisibleItemPosition()
        firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
      }
      TYPE_STAGGERED_GRID -> {
        val staggeredGridLayoutManager = layoutManager as StaggeredGridLayoutManager
        if (mLastPositions == null) {
          mLastPositions = IntArray(staggeredGridLayoutManager.spanCount)
        }
        mLastPositions = staggeredGridLayoutManager.findLastVisibleItemPositions(mLastPositions)
        mLastVisibleItemPosition = findMax(mLastPositions!!)
        staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(mLastPositions)
        firstVisibleItemPosition = findMax(mLastPositions!!)
      }
    }
    return firstVisibleItemPosition
  }

  /**
   * 计算当前是向上滑动还是向下滑动
   */
  private fun calculateScrollUpOrDown(firstVisibleItemPosition: Int, dy: Int) {
    if (firstVisibleItemPosition == 0) {
      if (!mIsScrollDown) {
        onScrollDown()
        mIsScrollDown = true
      }
    } else {
      if (mDistance > HIDE_THRESHOLD && mIsScrollDown) {
        onScrollUp()
        mIsScrollDown = false
        mDistance = 0
      } else if (mDistance < -HIDE_THRESHOLD && !mIsScrollDown) {
        onScrollDown()
        mIsScrollDown = true
        mDistance = 0
      }
    }
    if (mIsScrollDown && dy > 0 || !mIsScrollDown && dy < 0) {
      mDistance += dy
    }
  }

  override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
    super.onScrollStateChanged(recyclerView, newState)
    val layoutManager = recyclerView.layoutManager ?: return

    val visibleItemCount = layoutManager.getChildCount()
    val totalItemCount = layoutManager.getItemCount()

    var bottomCount = totalItemCount - 1 - mOffset
    if (bottomCount < 0) {
      bottomCount = totalItemCount - 1
    }

    if (visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE
        && mLastVisibleItemPosition >= bottomCount && !mIsScrollDown) {
      onBottom()
    }
  }

  protected fun onScrollUp() {

  }

  protected fun onScrollDown() {

  }

  protected fun onBottom() {}

  protected fun onScrolled(distanceX: Int, distanceY: Int) {}

  private fun findMax(lastPositions: IntArray): Int {
    var max = lastPositions[0]
    for (value in lastPositions) {
      max = Math.max(max, value)
    }
    return max
  }

  companion object {

    private val TYPE_LINEAR = 0

    private val TYPE_GRID = 1

    private val TYPE_STAGGERED_GRID = 2

    /**
     * 触发在上下滑动监听器的容差距离
     */
    private val HIDE_THRESHOLD = 20
  }
}
