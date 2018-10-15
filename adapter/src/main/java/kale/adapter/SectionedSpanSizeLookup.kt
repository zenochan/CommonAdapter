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
package kale.adapter

import androidx.recyclerview.widget.GridLayoutManager

/**
 * A SpanSizeLookup to draw section headers or footer spanning the whole width of the RecyclerView
 * when using a GridLayoutManager
 */
class SectionedSpanSizeLookup(
    protected var adapter: SectionedRcvAdapter<*>,
    protected var layoutManager: GridLayoutManager
) : GridLayoutManager.SpanSizeLookup() {

  override fun getSpanSize(position: Int): Int {
    return if (adapter.isSectionHeaderPosition(position) || adapter.isSectionFooterPosition(position)) {
      layoutManager.spanCount
    } else {
      var span = adapter.getItemSpan(position)
      if (span == -1) span = layoutManager.spanCount
      span
    }
  }
}
