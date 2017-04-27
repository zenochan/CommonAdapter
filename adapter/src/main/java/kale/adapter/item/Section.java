package kale.adapter.item;

import android.support.annotation.IntRange;

/**
 * Create Date: 16/6/20
 *
 * @author 陈治谋 (513500085@qq.com)
 */
public interface Section<T>
{
  @IntRange(from = 0) int getItemCount();

  T getItem(int position);

  int getSectionType();

  boolean hasFooter();
}
