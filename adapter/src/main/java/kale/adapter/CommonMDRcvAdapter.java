package kale.adapter;

import android.support.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDAdapter;

import java.util.List;

/**
 * @author 陈治谋 (513500085@qq.com)
 * @since 2017/2/27.
 */
public abstract class CommonMDRcvAdapter<T> extends CommonRcvAdapter<T> implements MDAdapter
{
  private MaterialDialog dialog;

  protected CommonMDRcvAdapter(@Nullable List<T> data)
  {
    super(data);
  }

  @Override public void setDialog(MaterialDialog dialog)
  {
    this.dialog = dialog;
  }

  protected void dismiss()
  {
    if (dialog != null) {
      dialog.dismiss();
    }
  }


}