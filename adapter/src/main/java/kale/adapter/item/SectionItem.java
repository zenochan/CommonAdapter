package kale.adapter.item;

/**
 * Create Date: 16/7/20
 *
 * @author 陈治谋 (513500085@qq.com)
 */
public interface SectionItem<S, T> extends AdapterItem<T>
{
  void handleSection(S section, int position);
}
