package kale.adapter

import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Author:    valuesfeng
 * Version    V1.0
 * Date:      16/11/30
 * Description:
 * Modification  History:
 * Date         	Author        		Version        	Description
 * -----------------------------------------------------------------------------------
 * 16/11/30          valuesfeng              1.0                    1.0
 */
class RecycledViewPool : RecyclerView.RecycledViewPool() {
  val typePool: HashMap<Any, Int> = HashMap()
}