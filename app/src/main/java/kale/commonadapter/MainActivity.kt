package kale.commonadapter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import kotlin.reflect.KClass


class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main_activity)
    list_view_btn.onClick { nav(ListViewActivity::class) }
    rcv_btn.onClick { nav(RecyclerViewActivity::class) }
    rcv_btn2.onClick { nav(HeaderFooterActivity::class) }
    viewpager_btn.onClick { nav(ViewPagerActivity::class) }
    diff_btn.onClick { nav(DiffRcvActivity::class) }
    sectioned_btn.onClick { nav(SectionedActivity::class) }

  }

  fun <T : Activity> nav(clazz: KClass<T>) {
    startActivity(Intent(this, clazz.java))
  }
}
