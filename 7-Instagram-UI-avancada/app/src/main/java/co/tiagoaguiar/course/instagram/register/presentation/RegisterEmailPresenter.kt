package co.tiagoaguiar.course.instagram.register.presentation

import android.util.Patterns
import co.tiagoaguiar.course.instagram.R
import co.tiagoaguiar.course.instagram.common.model.UserAuth
import co.tiagoaguiar.course.instagram.login.Login
import co.tiagoaguiar.course.instagram.login.data.LoginCallback
import co.tiagoaguiar.course.instagram.login.data.LoginRepository
import co.tiagoaguiar.course.instagram.register.RegisterEmail
import co.tiagoaguiar.course.instagram.register.data.RegisterEmailCallback
import co.tiagoaguiar.course.instagram.register.data.RegisterEmailRepository

class RegisterEmailPresenter(
    private var view: RegisterEmail.View?,
    private val repository: RegisterEmailRepository
) : RegisterEmail.Presenter {

    override fun create(email: String) {
        val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

        if (!isEmailValid) {
            view?.displayEmailFailure(R.string.invalid_email)
        } else {
            view?.displayEmailFailure(null)
        }

        // aqui o formulario ja esta validado, então chamamos o MODEL
        if (isEmailValid) {
            view?.showProgress(true)

            repository.create(email, object : RegisterEmailCallback {
                override fun onSuccess() {
                    view?.goToNameAndPasswordScreen(email)
                }

                override fun onFailure(message: String) {
                    view?.onEmailFailure(message)
                }

                override fun onComplete() {
                    view?.showProgress(false)
                }
            })
        }
    }

    // retirar a referência
    override fun onDestroy() {
        view = null
    }

}