package giftview.co.giftview_android03;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;


public class LoginActivity extends AppCompatActivity {

    FirebaseWrap mFirebase;

    private Button Find_Pwd, NewUser, Loginbutton;
    private EditText Mail, Pwd;

    private ProgressDialog progressDialog;
    private Handler mHandler;

    public void onEvent(LoginResultEvent e) {
        switch (e.result) {
            case 0:
                MainActivity.MailPanel.setText(Mail.getText().toString());
                MainActivity.Login_Button.setText(getString(R.string.Logout));
                LoginActivity.this.finish();
                break;
            case FirebaseError.USER_DOES_NOT_EXIST:
                Toast.makeText(LoginActivity.this, R.string.notemailString, Toast.LENGTH_LONG).show();
                break;
            case FirebaseError.INVALID_PASSWORD:
                Toast.makeText(LoginActivity.this, R.string.falsepwdString, Toast.LENGTH_LONG).show();
                break;
            case FirebaseError.INVALID_EMAIL:
                Toast.makeText(LoginActivity.this, R.string.falseemailString, Toast.LENGTH_LONG).show();
                break;
            case FirebaseError.NETWORK_ERROR:
                Toast.makeText(LoginActivity.this, R.string.falsenetworkString, Toast.LENGTH_LONG).show();
                break;
            case FirebaseError.UNKNOWN_ERROR:
                Toast.makeText(LoginActivity.this, R.string.falseunknownString, Toast.LENGTH_LONG).show();
                break;
            default:
                // handle other errors
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_login);
        mFirebase = new FirebaseWrap();

        Find_Pwd = (Button)findViewById(R.id.Find_Pwd);
        NewUser = (Button)findViewById(R.id.NewUser);
        Loginbutton = (Button)findViewById(R.id.Loginbutton);
        Mail = (EditText)findViewById(R.id.mail);
        Pwd = (EditText)findViewById(R.id.pwd);

        Mail.setFilters(new InputFilter[]{filterAlphaNum});
        Pwd.setFilters(new InputFilter[]{filterAlphaNum});

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.Login));
        setSupportActionBar(toolbar);

        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.setEnabled(false);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Find_Pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FindPwdActivity.class));
            }
        });

        NewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewUserActivity.class));
            }
        });

        Loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = Mail.getText().toString();
                String pwd = Pwd.getText().toString();

                if(mail.equals("") || pwd.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.notidpwdString, Toast.LENGTH_LONG).show();
                }else {
                    StringTokenizer token = new StringTokenizer(mail, "@");
                    if (token.countTokens() == 2) {
                        //Toast toast = Toast.makeText(getApplicationContext(), R.string.emailcheckString, Toast.LENGTH_SHORT);
                        //toast.setGravity(Gravity.CENTER, 0, 100);
                        //toast.show();

                        //패스워드 길이 체크.
                        if(pwd.length() > 20 || pwd.length() < 4) {
                            Toast.makeText(getApplicationContext(), R.string.pwdlongString, Toast.LENGTH_LONG).show();
                            Pwd.setText("");
                            Pwd.setFocusable(true);
                        }else {
                            mHandler = new Handler();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog = ProgressDialog.show(LoginActivity.this, "", getString(R.string.loadingString), true);
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if(progressDialog != null && progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }
                                            }catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, 2000);
                                }
                            });
                            EventBus.getDefault().post(new LoginEvent(
                                    ((EditText) findViewById(R.id.mail)).getText().toString(),
                                    ((EditText) findViewById(R.id.pwd)).getText().toString()
                            ));
                        }
                    } else {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.notemailString, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
            }
        });
    }

    // 영문만 허용 (숫자 포함)
    protected InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            Pattern ps = Pattern.compile("^[a-zA-Z0-9@._]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home :
                LoginActivity.this.finish();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
