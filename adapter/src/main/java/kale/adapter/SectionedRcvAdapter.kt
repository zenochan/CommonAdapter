/*
 * Copyright (C) 2015 Tomás Ruiz-López.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package kale.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

import kale.adapter.item.AdapterItem
import kale.adapter.item.Section
import kale.adapter.item.SectionItem

/**
 * @param <S> Section
 * @param <I> Item
 * @author 陈治谋 (wechat: puppet2436)
</I></S> */
abstract class SectionedRcvAdapter<S : Section<out Any>>
@JvmOverloads constructor(data: List<Section<*>> = ArrayList())
  : CommonRcvAdapter<Section<*>>(data), ISectionedRcvAdapter {

  //{{sectionForPosition,positionWithinSection,type},{},...}
  private var itemsInfo: Array<IntArray>? = null
  private val SECTION_POSITION = 0
  private val ITEM_POSITION = 1
  private val TYPE_FOR_POSITION = 2

  private var count = 0

  init {
    registerAdapterDataObserver(SectionDataObserver())
  }

  override fun setData(data: List<Section<*>>) {
    super.setData(data)
    setupIndices()
  }

  override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
    super.onAttachedToRecyclerView(recyclerView)
    setupIndices()
  }

  /**
   * Returns the sum of number of items for each section plus headers and footers if they
   * are provided.
   */
  override fun getItemCount(): Int {
    return count
  }

  override fun getItemSpan(position: Int): Int {
    if (isSectionHeaderPosition(position) || isSectionFooterPosition(position)) {
      return 1
    }

    if (itemsInfo!!.size > position) {
      val itemInfo = itemsInfo!![position]
      val section = getSection(itemInfo[SECTION_POSITION])
      val item = section?.getItem(itemInfo[ITEM_POSITION])
      if (item != null && item is ISpan) {
        return item.span
      }
    }

    return 1
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRcvAdapter.RcvAdapterItem {
    return CommonRcvAdapter.RcvAdapterItem(parent.context, parent, createItem(viewType))
  }

  override fun onBindViewHolder(holder: CommonRcvAdapter.RcvAdapterItem, position: Int) {
    val itemInfo = itemsInfo!![position]

    val item = holder.item
    if (isSectionFooterPosition(position) || isSectionHeaderPosition(position)) {
      val section = getSection(itemInfo[SECTION_POSITION])
      item.handleData(section as Any, position)
    } else {
      val i = getSection(itemInfo[SECTION_POSITION])!!.getItem(itemInfo[ITEM_POSITION])
      item.handleData(i, position)
      (item as? SectionItem<S, Int>)?.handleSection(getSection(itemInfo[SECTION_POSITION])!!, itemInfo[SECTION_POSITION])
    }
  }

  override fun createItem(type: Any): AdapterItem<Any> {
    val sectionType = type as Int and 0xff
    return createSectionItem(sectionType, type and 0xff00)
  }

  /**
   * 需要结合两个类型来返回 Item
   *
   * @param sectionType [Section.sectionType] scetion 的类型
   * @param itemType item 类型
   * - [SectionedRcvAdapter.TYPE_ITEM]  section 的 item
   * - [SectionedRcvAdapter.TYPE_SECTION_HEADER] section 的 header
   * - [SectionedRcvAdapter.TYPE_SECTION_HEADER] section 的 footer
   */
  abstract fun createSectionItem(sectionType: Int, itemType: Int): AdapterItem<Any>


  override fun getItemViewType(position: Int): Int {
    var type: Int

    if (itemsInfo == null) {
      setupIndices()
    }


    if (isSectionHeaderPosition(position)) {
      type = TYPE_SECTION_HEADER
    } else if (isSectionFooterPosition(position)) {
      type = TYPE_SECTION_FOOTER
    } else {
      type = TYPE_ITEM
    }

    val info = itemsInfo!![position]
    type = type or getSection(info[SECTION_POSITION])!!.sectionType
    return type
  }

  //  abstract protected

  /**
   * Returns true if the argument position corresponds to a header
   */
  fun isSectionHeaderPosition(position: Int): Boolean {
    if (itemsInfo == null) {
      setupIndices()
    }

    return position >= 0 && itemsInfo!!.size > position && itemsInfo!![position][TYPE_FOR_POSITION] == TYPE_SECTION_HEADER

  }

  /**
   * Returns true if the argument position corresponds to a footer
   */
  fun isSectionFooterPosition(position: Int): Boolean {
    if (itemsInfo == null) {
      setupIndices()
    }
    return position >= 0 && itemsInfo!!.size > position && itemsInfo!![position][TYPE_FOR_POSITION] == TYPE_SECTION_FOOTER
  }

  protected fun isSectionHeaderViewType(viewType: Int): Boolean {
    return viewType == TYPE_SECTION_HEADER
  }

  protected fun isSectionFooterViewType(viewType: Int): Boolean {
    return viewType == TYPE_SECTION_FOOTER
  }

  override fun getSectionItemViewType(section: Int, position: Int): Int {
    return TYPE_ITEM
  }

  /**
   * Returns the number of sections in the RecyclerView
   */
  override fun getSectionCount(): Int {
    return super.getItemCount()
  }


  /**
   * Returns the number of items for a given section
   */
  override fun getItemCountForSection(section: Int): Int {
    return getSection(section)!!.itemCount
  }

  /**
   * Returns true if a given section should have a footer
   */
  override fun hasFooterInSection(section: Int): Boolean {
    return getSection(section)!!.hasFooter()
  }

  //设置索引
  private fun setupIndices() {
    count = countItems()
    allocateAuxiliaryArrays(count)
    preComputeIndices()
  }

  private fun countItems(): Int {
    var count = 0
    val sections = getSectionCount()

    for (i in 0 until sections) {
      count += 1 + getItemCountForSection(i) + if (hasFooterInSection(i)) 1 else 0
    }
    return count
  }

  private fun preComputeIndices() {
    val sections = getSectionCount()
    var index = 0

    for (i in 0 until sections) {
      setPrecomputedItem(index, i, 0, TYPE_SECTION_HEADER)
      index++

      for (j in 0 until getItemCountForSection(i)) {
        setPrecomputedItem(index, i, j, TYPE_ITEM)
        index++
      }

      if (hasFooterInSection(i)) {
        setPrecomputedItem(index, i, 0, TYPE_SECTION_FOOTER)
        index++
      }
    }
  }

  private fun allocateAuxiliaryArrays(count: Int) {
    itemsInfo = Array(count) { IntArray(3) }
  }

  private fun setPrecomputedItem(position: Int, section: Int, itemPosition: Int, type: Int) {
    val itemInfo = itemsInfo!![position]
    itemInfo[SECTION_POSITION] = section
    itemInfo[ITEM_POSITION] = itemPosition
    itemInfo[TYPE_FOR_POSITION] = type
  }


  internal inner class SectionDataObserver : RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
      setupIndices()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
      setupIndices()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
      setupIndices()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
      setupIndices()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
      setupIndices()
    }
  }

  private fun getSection(position: Int): S? {
    return data?.getOrNull(position) as S
  }

  companion object {

    val TYPE_SECTION_HEADER = 0xf100
    val TYPE_SECTION_FOOTER = 0xf200
    val TYPE_ITEM = 0xf300
  }

}
