package kale.adapter;

/**
 * Create Date: 16/6/20
 *
 * @author 陈治谋 (513500085@qq.com)
 */
public interface ISectionedRcvAdapter
{
  int getSectionCount();

  int getItemCount();

  int getItemSpan(int position);

  int getItemCountForSection(int section);

  boolean hasFooterInSection(int section);

  int getItemViewType(int position);

  int getSectionItemViewType(int section, int position);
}
