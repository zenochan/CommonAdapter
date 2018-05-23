package kale.adapter.util

import java.util.HashMap

import android.support.annotation.VisibleForTesting

/**
 * @author Jack Tony
 * @date 2015/8/29
 */
@VisibleForTesting
/*package*/ class ItemTypeUtil {

  private var typePool: HashMap<Any, Int>? = null

  fun setTypePool(typePool: HashMap<Any, Int>) {
    this.typePool = typePool
  }

  /**
   * @param type item的类型
   * @return 通过object类型的type来得到int类型的type
   */
  fun getIntType(type: Any): Int {
    val pool: HashMap<Any, Int> = typePool ?: {
      val map = HashMap<Any, Int>()
      typePool = map
      map
    }()

    return pool[type] ?: {
      val newType = pool.size
      pool[type] = newType
      newType
    }()
  }
}
