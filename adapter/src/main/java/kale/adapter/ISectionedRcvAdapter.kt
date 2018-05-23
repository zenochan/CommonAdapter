package kale.adapter

/**
 * Create Date: 16/6/20
 *
 * @author 陈治谋 (513500085@qq.com)
 */
interface ISectionedRcvAdapter {
  fun getSectionCount(): Int

  fun getItemCount(): Int

  fun getItemSpan(position: Int): Int

  fun getItemCountForSection(section: Int): Int

  fun hasFooterInSection(section: Int): Boolean

  fun getItemViewType(position: Int): Int

  fun getSectionItemViewType(section: Int, position: Int): Int
}
