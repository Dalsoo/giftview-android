package giftview.co.giftview_android03;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import java.util.StringTokenizer;


public class FindPwdActivity extends AppCompatActivity {

    private Button PwdTrans;
    private EditText Mail;

    private ProgressDialog progressDialog;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);

        PwdTrans = (Button)findViewById(R.id.PwdTrans);
        Mail = (EditText)findViewById(R.id.mail);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.PasswordFind));
        setSupportActionBar(toolbar);

        ScrollView scrollView = (ScrollView)findViewById(R.id.scrollView);
        scrollView.setEnabled(false);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        PwdTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = Mail.getText().toString();

                StringTokenizer token = new StringTokenizer(mail, "@");
                if (token.countTokens() == 2) {
                    mHandler = new Handler();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog = ProgressDialog.show(FindPwdActivity.this, "", getString(R.string.Forwarding), true);
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
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_find_pwd, menu);
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
                FindPwdActivity.this.finish();
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
