package co.tiagoaguiar.course.instagram.search.data

import co.tiagoaguiar.course.instagram.common.base.RequestCallback
import co.tiagoaguiar.course.instagram.common.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseSearchDataSource: SearchDataSource {
    override fun fetchUsers(name: String, callback: RequestCallback<List<User>>) {
        FirebaseFirestore.getInstance()
            .collection("/users")
            .whereGreaterThanOrEqualTo("name", name)
            .whereLessThanOrEqualTo("name", name + "\uf8ff")
            .get()
            .addOnSuccessListener { response ->
                val documents = response.documents
                val users = mutableListOf<User>()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    if (user != null && user.uuid != FirebaseAuth.getInstance().uid) {
                        users.add(user)
                    }
                }
                callback.onSuccess(users)
            }
            .addOnFailureListener { exception ->
                callback.onFailure(exception.message ?: "Falha ao buscar o usuário!")
            }
            .addOnCompleteListener {
                callback.onComplete()
            }
    }
}