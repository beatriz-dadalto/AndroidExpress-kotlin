package co.tiagoaguiar.course.instagram.profile.presenter

import co.tiagoaguiar.course.instagram.common.model.Post
import co.tiagoaguiar.course.instagram.common.model.UserAuth
import co.tiagoaguiar.course.instagram.profile.Profile

class ProfileState(private val posts: List<Post>?, private val userAuth: UserAuth?) :
    Profile.State {

    override fun fetchUserProfile(): UserAuth? {
        return userAuth
    }

    override fun fetchUserPosts(): List<Post>? {
        return posts
    }
}