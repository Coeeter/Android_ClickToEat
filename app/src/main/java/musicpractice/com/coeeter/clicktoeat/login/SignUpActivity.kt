package musicpractice.com.coeeter.clicktoeat.login

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Pair
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import musicpractice.com.coeeter.clicktoeat.R
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {
    private lateinit var userCreationLink: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        supportActionBar?.hide()

        userCreationLink = "${getString(R.string.base_url)}/users?d=mobile"

        val error = findViewById<TextView>(R.id.error)
        val submitBtn = findViewById<Button>(R.id.submitBtn)

        val nameInput = findViewById<EditText>(R.id.name)
        val usernameInput = findViewById<EditText>(R.id.username)
        val passwordInput = findViewById<EditText>(R.id.password)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirmPassword)
        val emailInput = findViewById<EditText>(R.id.email)
        val phoneNumInput = findViewById<EditText>(R.id.phoneNum)
        val addressInput = findViewById<EditText>(R.id.address)
        val genderInput = findViewById<RadioGroup>(R.id.gender)
        val parent = findViewById<ScrollView>(R.id.parent)

        findViewById<RadioButton>(R.id.male).isChecked = true

        submitBtn.setOnClickListener {
            LoginActivity.hideKeyboard(this)

            val name = nameInput.text.toString()
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val confirmPassword = confirmPasswordInput.text.toString()
            val email = emailInput.text.toString()
            val phoneNum = phoneNumInput.text.toString()
            val address = addressInput.text.toString()
            var gender = ""
            when (genderInput.checkedRadioButtonId) {
                R.id.male -> gender = "M"
                R.id.female -> gender = "F"
            }

            for (item in arrayOf(
                name,
                username,
                password,
                confirmPassword,
                email,
                phoneNum,
                address
            )) {
                if (item.isEmpty()) {
                    LoginActivity.animateErrorView(
                        this,
                        error,
                        R.anim.slide_down,
                        View.VISIBLE,
                        "Empty Field.\nPlease fill up " +
                                "the fields below to register",
                        parent
                    )
                    return@setOnClickListener
                }
            }

            val nameArray = name.split(" ")

            val firstName = nameArray[0]
            val lastName =
                if (nameArray.size >= 2)
                    nameArray
                        .slice(1 until nameArray.size)
                        .joinToString(" ")
                else ""

            if (password != confirmPassword) {
                LoginActivity.animateErrorView(
                    this,
                    error,
                    R.anim.slide_down,
                    View.VISIBLE,
                    "Wrong confirm password " +
                            "entered.\nPlease input correct password",
                    parent
                )
                return@setOnClickListener
            }

            val queue = Volley.newRequestQueue(this.applicationContext)

            val payload = JSONObject()
            payload.put("username", username)
            payload.put("password", password)
            payload.put("email", email)
            payload.put("phoneNum", phoneNum)
            payload.put("firstName", firstName)
            payload.put("lastName", lastName)
            payload.put("gender", gender)
            payload.put("address", address)

            val request = JsonObjectRequest(
                Request.Method.POST,
                userCreationLink,
                payload,
                {
                    request: JSONObject ->
                    run {
                        if (request.has("result")) {
                            LoginActivity.animateErrorView(
                                this@SignUpActivity,
                                error,
                                R.anim.slide_down,
                                View.VISIBLE,
                                request.getString("result"),
                                parent
                            )
                            return@JsonObjectRequest
                        }
                        if (request.has("affectedRows") && request.getInt("affectedRows") == 1) {
                            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                            intent.putExtra("username", username)
                            val options = ActivityOptions
                                .makeSceneTransitionAnimation(
                                    this@SignUpActivity,
                                    Pair.create(findViewById(R.id.brand), "brand"),
                                    Pair.create(findViewById(R.id.form), "field"),
                                    Pair.create(submitBtn, "button")
                                )
                            startActivity(intent, options.toBundle())
                            finish()
                            return@JsonObjectRequest
                        }
                    }
                },
                {
                    error: VolleyError -> Log.d("error", error.toString())
                }
            )

            queue.add(request)
        }
    }

}