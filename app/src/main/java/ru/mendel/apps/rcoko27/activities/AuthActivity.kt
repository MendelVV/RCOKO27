package ru.mendel.apps.rcoko27.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ru.mendel.apps.rcoko27.QueryPreference
import ru.mendel.apps.rcoko27.R
import ru.mendel.apps.rcoko27.fragments.auth.AutoLoginFragment
import ru.mendel.apps.rcoko27.fragments.auth.RegFragment

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)

        val login = QueryPreference.getLogin(this@AuthActivity)
        val password = QueryPreference.getPassword(this@AuthActivity)
        //проверка наличия данных авторизации
        if (login!=null && password!=null){
            //переходим к фрагметну который пытается войти в приложение
            val fragment = AutoLoginFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit()
        }else{
            //переходим к фрагменту регистрации
            val fragment = RegFragment()
            supportFragmentManager.beginTransaction().replace(R.id.fragment_layout, fragment).commit()
        }



/*        val intent = Intent(this@AuthActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)*/

    }
}
