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
package kale.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import kale.adapter.item.AdapterItem;
import kale.adapter.item.Section;
import kale.adapter.item.SectionItem;

/**
 * @param <S> Section
 * @param <I> Item
 * @author 陈治谋 (wechat: puppet2436)
 */
public abstract class SectionedRcvAdapter<S extends Section<I>, I>
    extends CommonRcvAdapter<S> implements ISectionedRcvAdapter
{

  public static final int TYPE_SECTION_HEADER = 0xf100;
  public static final int TYPE_SECTION_FOOTER = 0xf200;
  public static final int TYPE_ITEM           = 0xf300;

  //{{sectionForPosition,positionWithinSection,type},{},...}
  private int[][] itemsInfo;
  private int SECTION_POSITION  = 0;
  private int ITEM_POSITION     = 1;
  private int TYPE_FOR_POSITION = 2;

  private int count = 0;

  @SuppressWarnings("unused")
  public SectionedRcvAdapter()
  {
    this(null);
  }

  public SectionedRcvAdapter(List<S> data)
  {
    super(data);
    registerAdapterDataObserver(new SectionDataObserver());
  }

  @Override public void setData(List<S> data)
  {
    super.setData(data);
    setupIndices();
  }

  @Override
  public void onAttachedToRecyclerView(RecyclerView recyclerView)
  {
    super.onAttachedToRecyclerView(recyclerView);
    setupIndices();
  }

  /**
   * Returns the sum of number of items for each section plus headers and footers if they
   * are provided.
   */
  @Override
  public int getItemCount()
  {
    return count;
  }

  @Override public int getItemSpan(int position)
  {
    if (isSectionHeaderPosition(position) || isSectionFooterPosition(position)) {
      return 1;
    }

    if (itemsInfo.length > position) {
      int[] itemInfo = itemsInfo[position];
      S     section  = getSection(itemInfo[SECTION_POSITION]);
      I     item     = section == null ? null : section.getItem(itemInfo[ITEM_POSITION]);
      if (item != null && item instanceof ISpan) {
        return ((ISpan) item).getSpan();
      }
    }

    return 1;
  }

  @Override
  public RcvAdapterItem onCreateViewHolder(ViewGroup parent, int viewType)
  {
    return new RcvAdapterItem(parent.getContext(), parent, createItem(viewType));
  }

  @SuppressWarnings("unchecked")
  @Override public void onBindViewHolder(RcvAdapterItem holder, int position)
  {
    int[] itemInfo = itemsInfo[position];

    AdapterItem item = ((RcvAdapterItem) holder).item;
    if (isSectionFooterPosition(position) || isSectionHeaderPosition(position)) {
      S section = getSection(itemInfo[SECTION_POSITION]);
      item.handleData(section, position);
    } else {
      I i = getSection(itemInfo[SECTION_POSITION]).getItem(itemInfo[ITEM_POSITION]);
      item.handleData(i, position);
      if (item instanceof SectionItem) {
        ((SectionItem) item).handleSection(getSection(itemInfo[SECTION_POSITION]), itemInfo[SECTION_POSITION]);
      }
    }
  }

  @NonNull @Override public AdapterItem createItem(Object type)
  {
    int sectionType = ((int) type & 0xff);
    return createSectionItem(sectionType, ((int) type) & 0xff00);
  }

  public abstract AdapterItem createSectionItem(int sectionType, int type);


  @SuppressWarnings("deprecation") @Override
  public int getItemViewType(int position)
  {
    int type;

    if (itemsInfo == null) {
      setupIndices();
    }


    if (isSectionHeaderPosition(position)) {
      type = TYPE_SECTION_HEADER;
    } else if (isSectionFooterPosition(position)) {
      type = TYPE_SECTION_FOOTER;
    } else {
      type = TYPE_ITEM;
    }

    int[] info = itemsInfo[position];
    type |= getSection(info[SECTION_POSITION]).getSectionType();
    return type;
  }

//  abstract protected

  /**
   * Returns true if the argument position corresponds to a header
   */
  public boolean isSectionHeaderPosition(int position)
  {
    if (itemsInfo == null) {
      setupIndices();
    }

    return position >= 0 && itemsInfo.length > position && itemsInfo[position][TYPE_FOR_POSITION] == TYPE_SECTION_HEADER;

  }

  /**
   * Returns true if the argument position corresponds to a footer
   */
  public boolean isSectionFooterPosition(int position)
  {
    if (itemsInfo == null) {
      setupIndices();
    }
    return position >= 0 && itemsInfo.length > position && itemsInfo[position][TYPE_FOR_POSITION] == TYPE_SECTION_FOOTER;
  }

  @SuppressWarnings("unused")
  protected boolean isSectionHeaderViewType(int viewType)
  {
    return viewType == TYPE_SECTION_HEADER;
  }

  @SuppressWarnings("unused")
  protected boolean isSectionFooterViewType(int viewType)
  {
    return viewType == TYPE_SECTION_FOOTER;
  }

  public int getSectionItemViewType(int section, int position)
  {
    return TYPE_ITEM;
  }

  /**
   * Returns the number of sections in the RecyclerView
   */
  public int getSectionCount()
  {
    return super.getItemCount();
  }


  /**
   * Returns the number of items for a given section
   */
  public int getItemCountForSection(int section)
  {
    return getSection(section).getItemCount();
  }

  /**
   * Returns true if a given section should have a footer
   */
  public boolean hasFooterInSection(int section)
  {
    return getSection(section).hasFooter();
  }

  //设置索引
  private void setupIndices()
  {
    count = countItems();
    allocateAuxiliaryArrays(count);
    preComputeIndices();
  }

  private int countItems()
  {
    int count    = 0;
    int sections = getSectionCount();

    for (int i = 0; i < sections; i++) {
      count += 1 + getItemCountForSection(i) + (hasFooterInSection(i) ? 1 : 0);
    }
    return count;
  }

  private void preComputeIndices()
  {
    int sections = getSectionCount();
    int index    = 0;

    for (int i = 0; i < sections; i++) {
      setPrecomputedItem(index, i, 0, TYPE_SECTION_HEADER);
      index++;

      for (int j = 0; j < getItemCountForSection(i); j++) {
        setPrecomputedItem(index, i, j, TYPE_ITEM);
        index++;
      }

      if (hasFooterInSection(i)) {
        setPrecomputedItem(index, i, 0, TYPE_SECTION_FOOTER);
        index++;
      }
    }
  }

  private void allocateAuxiliaryArrays(int count)
  {
    itemsInfo = new int[count][3];
  }

  private void setPrecomputedItem(int position, int section, int itemPosition, int type)
  {
    int[] itemInfo = itemsInfo[position];
    itemInfo[SECTION_POSITION] = section;
    itemInfo[ITEM_POSITION] = itemPosition;
    itemInfo[TYPE_FOR_POSITION] = type;
  }


  class SectionDataObserver extends RecyclerView.AdapterDataObserver
  {
    @Override
    public void onChanged()
    {
      setupIndices();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount)
    {
      setupIndices();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount)
    {
      setupIndices();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount)
    {
      setupIndices();
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
    {
      setupIndices();
    }
  }

  private S getSection(int position)
  {
    if (getSectionCount() > position) {
      return getData().get(position);
    } else {
      return null;
    }
  }

}
