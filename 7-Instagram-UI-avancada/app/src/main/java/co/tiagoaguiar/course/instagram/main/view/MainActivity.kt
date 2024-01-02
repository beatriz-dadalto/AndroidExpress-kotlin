package co.tiagoaguiar.course.instagram.main.view

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.WindowInsetsController
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import co.tiagoaguiar.course.instagram.R
import co.tiagoaguiar.course.instagram.post.view.AddFragment
import co.tiagoaguiar.course.instagram.common.extension.replaceFragment
import co.tiagoaguiar.course.instagram.databinding.ActivityMainBinding
import co.tiagoaguiar.course.instagram.home.view.HomeFragment
import co.tiagoaguiar.course.instagram.main.LogoutListener
import co.tiagoaguiar.course.instagram.profile.view.ProfileFragment
import co.tiagoaguiar.course.instagram.search.view.SearchFragment
import co.tiagoaguiar.course.instagram.splash.view.SplashActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener,
    AddFragment.AddListener, SearchFragment.SearchListener, LogoutListener, ProfileFragment.FollowListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: Fragment
    private lateinit var addFragment: Fragment
    private lateinit var profileFragment: ProfileFragment
    private var currentFragment: Fragment? = null

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    // dark mode
                    window.statusBarColor = ContextCompat.getColor(this, R.color.black)
                    binding.mainImgLogo.imageTintList = ColorStateList.valueOf(Color.WHITE)
                    binding.mainBottomNav.backgroundTintList = ColorStateList.valueOf(Color.BLACK)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    // light mode
                    window.insetsController?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                    window.statusBarColor = ContextCompat.getColor(this, R.color.gray)
                }
            }
        }

        // toolbar responsavel por escutar os eventos de acao em cima tela
        setSupportActionBar(binding.mainToolbar)

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_insta_camera)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // garantir que deixa os icones do lado esquerdo

        homeFragment = HomeFragment()
        searchFragment = SearchFragment()
        addFragment = AddFragment()
        profileFragment = ProfileFragment()

        binding.mainBottomNav.setOnNavigationItemSelectedListener(this)
        binding.mainBottomNav.selectedItemId = R.id.menu_bottom_home
    }

    private fun setScrollToolbarEnabled(enabled: Boolean) {
        // pq as AppBarLayout? pq mainToolbar eh filho de AppBarLayout
        val params = binding.mainToolbar.layoutParams as AppBarLayout.LayoutParams
        // pq as CoordinatorLayout? pq mainAppbar eh filho de CoordinatorLayout
        val coordinatParams = binding.mainAppbar.layoutParams as CoordinatorLayout.LayoutParams

        if (enabled) {
            params.scrollFlags =
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
            coordinatParams.behavior = AppBarLayout.Behavior()
        } else {
            params.scrollFlags = 0
            coordinatParams.behavior = null
        }
        binding.mainAppbar.layoutParams = coordinatParams
    }

    override fun goToProfile(uuid: String) {
        val fragment = ProfileFragment().apply {
            arguments = Bundle().apply {
                putString(ProfileFragment.KEY_USER_ID, uuid)
            }
        }
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_fragment, fragment, fragment.javaClass.simpleName + "detail")
            addToBackStack(null)
            commit()
        }
    }

    override fun followUpdated() {
        homeFragment.presenter.clearCache()
        if (supportFragmentManager.findFragmentByTag(profileFragment.javaClass.simpleName) != null) {
            profileFragment.presenter.clearCache()
        }
    }

    override fun logout() {
        if (supportFragmentManager.findFragmentByTag(profileFragment.javaClass.simpleName) != null) {
            profileFragment.presenter.clearCache()
        }

        homeFragment.presenter.clearCache()
        homeFragment.presenter.logout()

        val intent = Intent(baseContext, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var scrollToolbarEnabled = false

        when (item.itemId) {
            R.id.menu_bottom_home -> {
                // if para nao empilhar se clicar varias vezes no icone
                if (currentFragment == homeFragment) return false
                currentFragment = homeFragment
            }

            R.id.menu_bottom_search -> {
                if (currentFragment == searchFragment) return false
                currentFragment = searchFragment
                scrollToolbarEnabled = false
            }

            R.id.menu_bottom_add -> {
                if (currentFragment == addFragment) return false
                currentFragment = addFragment
                scrollToolbarEnabled = false
            }

            R.id.menu_bottom_profile -> {
                if (currentFragment == profileFragment) return false
                currentFragment = profileFragment
                scrollToolbarEnabled = true
            }
        }

        setScrollToolbarEnabled(scrollToolbarEnabled)

        // se nao for nulo
        currentFragment?.let { currentFragment ->
            replaceFragment(R.id.main_fragment, currentFragment)
        }
        return true
    }

    override fun onPostCreated() {
        homeFragment.presenter.clearCache()
        if (supportFragmentManager.findFragmentByTag(profileFragment.javaClass.simpleName) != null) {
            profileFragment.presenter.clearCache()
        }
        binding.mainBottomNav.selectedItemId = R.id.menu_bottom_home
    }
}