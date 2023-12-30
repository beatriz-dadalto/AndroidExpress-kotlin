package co.tiagoaguiar.course.instagram.home.view

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import co.tiagoaguiar.course.instagram.R
import co.tiagoaguiar.course.instagram.common.base.BaseFragment
import co.tiagoaguiar.course.instagram.common.base.DependencyInjector
import co.tiagoaguiar.course.instagram.common.model.Post
import co.tiagoaguiar.course.instagram.databinding.FragmentHomeBinding
import co.tiagoaguiar.course.instagram.home.Home
import co.tiagoaguiar.course.instagram.home.presenter.HomePresenter
import co.tiagoaguiar.course.instagram.main.LogoutListener

class HomeFragment : BaseFragment<FragmentHomeBinding, Home.Presenter>(
  R.layout.fragment_home,
  FragmentHomeBinding::bind
), Home.View {

  override lateinit var presenter: Home.Presenter

  private val adapter = FeedAdapter()

  private var logoutListener: LogoutListener? = null

  override fun onAttach(context: Context) {
    super.onAttach(context)
    if (context is LogoutListener) {
      logoutListener = context
    }
  }

  override fun setupViews() {
    binding?.homeRv?.layoutManager = LinearLayoutManager(requireContext())
    binding?.homeRv?.adapter = adapter

    presenter.fetchFeed()
  }

  override fun setupPresenter() {
    presenter = HomePresenter(this, DependencyInjector.homeRepository())
  }

  override fun getMenu() = R.menu.menu_profile

  override fun showProgress(enabled: Boolean) {
    binding?.homeProgress?.visibility = if (enabled) View.VISIBLE else View.GONE
  }

  override fun displayRequestFailure(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
  }

  override fun displayEmptyPosts() {
    binding?.homeTxtEmpty?.visibility = View.VISIBLE
    binding?.homeRv?.visibility = View.GONE
  }

  override fun displayFullPosts(posts: List<Post>) {
    binding?.homeTxtEmpty?.visibility = View.GONE
    binding?.homeRv?.visibility = View.VISIBLE
    adapter.items = posts
    adapter.notifyDataSetChanged()
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when(item.itemId) {
      R.id.menu_logout -> {
        logoutListener?.logout()
        return true
      }
    }
    return super.onOptionsItemSelected(item)
  }

}