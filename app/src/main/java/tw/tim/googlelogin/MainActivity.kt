package tw.tim.googlelogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private val RC_SIGN_IN = 9001
    private val TAG = "Tim"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        googleLogin()
        initButtons()

    }

    private fun googleLogin() {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun googleSignIn(){
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // GoogleSignInAccount對象包含有關登錄用戶的信息，例如用戶名。
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        if (account != null) {
//            account.displayName //暱稱  account. google帳號所有的東西都在這邊 可以選
//            account.getEmail() //信箱
//            account.getGivenName() //Firstname
//            account.getFamilyName() //Last name

            tv_name.text = account.displayName

            Glide.with(this)
                    .load(account.photoUrl)
                    .error(R.color.black)
                    .into(img_avatar)

            Log.e("account.photoUrl",account.photoUrl.toString())
            // 有接收到 但照片沒反應忘了Manifest + INTERNET

            //-------改變圖像--------------
//            val User_IMAGE = account.photoUrl ?: return
//            img = findViewById<View>(R.id.google_icon) as CircleImgView
//            object : AsyncTask<String?, Void?, Bitmap?>() {
//                protected fun doInBackground(vararg params: String): Bitmap? {
//                    val url = params[0]
//                    return getBitmapFromURL(url)
//                }
//
//                override fun onPostExecute(result: Bitmap?) {
//                    img.setImageBitmap(result) //setImageBitmap
//                    super.onPostExecute(result)
//                }
//
//                override fun doInBackground(vararg params: String?): Bitmap? {
//                    TODO("Not yet implemented")
//                }
//            }.execute(User_IMAGE.toString().trim())
//            //            String g_id=account.getId();
//            findViewById<View>(R.id.sign_in_button).visibility = View.GONE
//            findViewById<View>(R.id.sign_out_and_disconnect).visibility = View.VISIBLE

        } else {
            tv_name.text = "No"
        }
    }

    private fun googleSignOut() {
        mGoogleSignInClient!!.signOut() //登出
                .addOnCompleteListener(this) {
                    //--START_EXCLUDE--
                    updateUI(null) //登出動作寫在這
                    // [END_EXCLUDE]
                    Glide.with(this)
                            .load(R.color.teal_700)
                            .error(R.color.black)
                            .into(img_avatar)
//                img.setImageResource(R.drawable.googleg_color) //還原圖示  換頭像
                }
    }

    // 要能直接抓元件 要再Gradle plugins + id 'kotlin-android-extensions'
    private fun initButtons() {
        btn_login.setOnClickListener {
            googleSignIn()
        }
        btn_logout.setOnClickListener {
            googleSignOut()
        }
    }

    // 用戶GoogleSignInAccount後，您可以在活動的onActivityResult方法中為用戶獲取GoogleSignInAccount對象。
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    // 在您的 Activity 的onStart方法中，檢查用戶是否已通過 Google 登錄您的應用。
    override fun onStart() {
        super.onStart()
        // --START on_start_sign_in--
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        // 取得上次登入的狀態
        val account = GoogleSignIn.getLastSignedInAccount(this)
        updateUI(account)
        //--END on_start_sign_in--
    }

}