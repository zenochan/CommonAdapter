package kale.adapter.item;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Create Date: 16/7/4
 *
 * @author 陈治谋 (513500085@qq.com)
 */
public class RcvAdapterItem extends RecyclerView.ViewHolder
{

  public final AdapterItem item;

  public RcvAdapterItem(Context context, ViewGroup parent, AdapterItem item)
  {
    super(LayoutInflater.from(context).inflate(item.getLayoutResId(), parent, false));
    this.item = item;
    this.item.bindViews(itemView);
    this.item.setViews();
  }
}

