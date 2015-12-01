package com.mediatek.mcstutorial;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mediatek.mcs.domain.McsResponse;
import com.mediatek.mcs.domain.McsSession;
import com.mediatek.mcs.pref.McsUser;
import org.json.JSONObject;

public class SessionActivity extends AppCompatActivity {

  EditText et_email;
  EditText et_pwd;
  TextView tv_info;
  Button btn_sign_in;
  Button btn_sign_out;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_session);

    this.et_email = (EditText) findViewById(R.id.et_email);
    this.et_pwd = (EditText) findViewById(R.id.et_pwd);
    this.tv_info = (TextView) findViewById(R.id.tv_info);
    this.btn_sign_in = (Button) findViewById(R.id.btn_sign_in);
    this.btn_sign_out = (Button) findViewById(R.id.btn_sign_out);

    btn_sign_in.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        requestSignIn();
      }
    });

    btn_sign_out.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        requestSignOut();
      }
    });
  }

  private void requestSignIn() {
    String email = et_email.getText().toString();
    String pwd = et_pwd.getText().toString();

    McsSession.getInstance().requestSignIn(email, pwd,
        new McsResponse.SuccessListener<JSONObject>() {
          @Override public void onSuccess(JSONObject response) {
            tv_info.setText("User sign in successfully: \n"
                + "\nEmail:" + McsUser.getInstance().getEmail() + "\n"
                + "\nAccess Token:" + McsUser.getInstance().getAccessToken());
          }
        },
        /**
         * Optional.
         * Default error message would be shown in logcat.
         */
        new McsResponse.ErrorListener() {
          @Override public void onError(Exception e) {
            tv_info.setText(e.toString());
          }
        });
  }

  private void requestSignOut() {
    McsSession.getInstance().requestSignOut(
        new McsResponse.SuccessListener<JSONObject>() {
          @Override public void onSuccess(JSONObject response) {
            tv_info.setText("User sign out successfully: "
                + McsUser.getInstance().getEmail().isEmpty() + ", "
                + McsUser.getInstance().getPassword().isEmpty()
            );
          }
        }
    );
  }
}
