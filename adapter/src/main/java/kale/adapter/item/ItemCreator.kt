package kale.adapter.item

/**
 * Create Date: 16/7/4
 *
 * @author 陈治谋 (513500085@qq.com)
 */
interface ItemCreator<in T : Any> {
  fun create(t: T): AdapterItem<T>?
}
