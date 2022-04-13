package ru.kremlev.ncsrecognitonmanager.auth.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth

import ru.kremlev.ncsrecognitonmanager.R
import ru.kremlev.ncsrecognitonmanager.databinding.ActivityManagerAuthBinding
import ru.kremlev.ncsrecognitonmanager.databinding.DialogResetPasswordBinding
import ru.kremlev.ncsrecognitonmanager.manager.acitivity.ManagerActivity
import ru.kremlev.ncsrecognitonmanager.utils.LogManager


class ManagerAuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManagerAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LogManager.d("")

        buttonClicks()
    }

    private fun buttonClicks() {
        binding.apply {
            btnReg.setOnClickListener {
                LogManager.d("btn reg pressed")
                singUpUser()
            }
            btnAuth.setOnClickListener {
                LogManager.d("btn auth pressed")
                loginUser()
            }
            btnForget.setOnClickListener {
                LogManager.d("btn forget pressed")
                resetPass()
            }
        }
    }

    private fun resetPass() {
        var dialogBinding: DialogResetPasswordBinding? = DialogResetPasswordBinding.inflate(layoutInflater)

        val builder = AlertDialog
            .Builder(this)
            .setView(dialogBinding?.root)
        val mAlertDialog = builder.show()

        mAlertDialog.setOnDismissListener {
            dialogBinding = null
        }

        dialogBinding?.apply {
            dialogResetPasswordBtnReset.setOnClickListener {
                LogManager.d("Reset button was clicked")
                val mail = dialogResetPasswordEt.text.toString()

                if (mail.isNotEmpty()) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnCompleteListener { taskReset ->
                        if (taskReset.isSuccessful) {
                            Toast.makeText(
                                this@ManagerAuthActivity,
                                "Mail sent to $mail",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            LogManager.e("", taskReset.exception)
                            Toast.makeText(
                                this@ManagerAuthActivity,
                                taskReset.exception?.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    mAlertDialog.dismiss()
                } else {
                    Toast.makeText(
                        this@ManagerAuthActivity,
                        getString(R.string.enter_email),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            //btn -
            dialogResetPasswordBtnCancel.setOnClickListener {
                LogManager.d("Cancel button was clicked")
                mAlertDialog.dismiss()
            }
        }
    }

    private fun singUpUser() {
        val email: String = binding.etLogin.text.toString()
        val password: String = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { taskSingUP ->
                    if (taskSingUP.isSuccessful) {
                        val firebaseUser = taskSingUP.result!!.user!!

                        Toast.makeText(
                            this@ManagerAuthActivity,
                            getString(R.string.sing_up_successful),
                            Toast.LENGTH_SHORT
                        ).show()

                        val safeIntent = Intent(this@ManagerAuthActivity, ManagerActivity::class.java)
                        safeIntent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        safeIntent.putExtra("user_id", firebaseUser.uid)
                        startActivity(safeIntent)
                        finish()

                    } else {
                        LogManager.e("", taskSingUP.exception)
                        Toast.makeText(
                            this@ManagerAuthActivity,
                            taskSingUP.exception!!.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    LogManager.e("", it)
                }
        } else {
            Toast.makeText(
                this@ManagerAuthActivity,
                getString(R.string.enter_login_and_password),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun loginUser() {
        val email: String = binding.etLogin.text.toString()
        val password: String = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { taskLogin ->
                    if (taskLogin.isSuccessful) {
                        Toast.makeText(
                            this@ManagerAuthActivity,
                            getString(R.string.login_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                        val safeIntent =
                            Intent(this@ManagerAuthActivity, ManagerActivity::class.java)
                        safeIntent.flags =
                            (Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        safeIntent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
                        startActivity(safeIntent)
                        finish()
                    } else {
                        LogManager.e("", taskLogin.exception)
                        Toast.makeText(
                            this@ManagerAuthActivity,
                            taskLogin.exception!!.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                .addOnFailureListener {
                    LogManager.e("", it)
                }
        } else {
            Toast.makeText(
                this@ManagerAuthActivity,
                getString(R.string.enter_login_and_password),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onStart() {
        super.onStart()
        ifUserIsLoggedIn()
    }

    private fun ifUserIsLoggedIn() {
        FirebaseAuth.getInstance().currentUser?.let {
            val safeIntent = Intent(this@ManagerAuthActivity, ManagerActivity::class.java)
            startActivity(safeIntent)
            finish()
        }
    }
}
