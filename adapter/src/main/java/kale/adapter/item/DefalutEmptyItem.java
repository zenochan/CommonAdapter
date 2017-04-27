package kale.adapter.item;

import android.view.View;

import kale.adapter.R;


/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2016/12/21.
 */
public class DefalutEmptyItem implements AdapterItem
{
  @Override public int getLayoutResId()
  {
    return R.layout.item_empty_default;
  }

  @Override public void bindViews(View root) { }

  @Override public void setViews() { }

  @Override public void handleData(Object o, int position) { }
}
