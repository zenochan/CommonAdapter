package kale.adapter.item

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Create Date: 16/7/4
 *
 * @author 陈治谋 (513500085@qq.com)
 */
class RcvAdapterItem(
    context: Context,
    parent: ViewGroup,
    val item: AdapterItem<*>
) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(item.layoutResId, parent, false)) {

  init {
    this.item.bindViews(itemView)
    this.item.setViews()
  }
}

