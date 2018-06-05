package kale.adapter.item

/**
 * Create Date: 16/7/20
 *
 * @author 陈治谋 (513500085@qq.com)
 */
interface SectionItem<T : Any> : AdapterItem<T> {
  fun handleSection(section: Section<T>, sectionPosition: Int)
}
