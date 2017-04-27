package kale.adapter.item;

/**
 * Create Date: 16/7/4
 *
 * @author 陈治谋 (513500085@qq.com)
 */
public interface ItemCreator<T>
{
  AdapterItem<T> create(T t);
}
