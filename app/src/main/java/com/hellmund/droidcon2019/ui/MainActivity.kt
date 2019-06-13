package com.hellmund.droidcon2019.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.transaction
import com.hellmund.droidcon2019.R
import com.hellmund.droidcon2019.data.model.Session
import com.hellmund.droidcon2019.ui.schedule.ScheduleFragment
import com.hellmund.droidcon2019.ui.shared.BackPressable
import com.hellmund.droidcon2019.ui.shared.Reselectable
import com.hellmund.droidcon2019.ui.speakers.SpeakersFragment
import com.hellmund.droidcon2019.util.toggleLightDarkSystemWindows
import kotlinx.android.synthetic.main.activity_main.bottomNavigation

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toggleLightDarkSystemWindows()

        bottomNavigation.setOnNavigationItemSelectedListener {
            val fragment = when (it.itemId) {
                R.id.schedule -> ScheduleFragment.newInstance()
                R.id.speakers -> SpeakersFragment.newInstance()
                else -> throw IllegalStateException()
            }

            supportFragmentManager.transaction {
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                replace(R.id.contentFrame, fragment)
            }
            true
        }

        bottomNavigation.selectedItemId = R.id.schedule

        bottomNavigation.setOnNavigationItemReselectedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
            val reselectable = fragment as? Reselectable

            if (reselectable != null) {
                reselectable.onReselected()
            } else {
                val childFragmentManager = checkNotNull(fragment?.childFragmentManager)
                while (childFragmentManager.backStackEntryCount > 0) {
                    childFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.contentFrame) ?: return
        if (fragment is BackPressable && fragment.onBackPressed()) {
            return
        }

        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {

        private const val KEY_EVENT = "KEY_EVENT"

        fun newNotificationIntent(
            context: Context,
            event: Session
        ): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtras(bundleOf(KEY_EVENT to event))
            }
        }

    }

}
