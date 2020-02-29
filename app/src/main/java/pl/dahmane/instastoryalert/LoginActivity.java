package pl.dahmane.instastoryalert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Magic magic;
    EditText usernameEditText;
    EditText passwordEditText;
    Button loginButton;
    ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryLogin();
            }
        });

        magic = new Magic(this);
    }

    private void tryLogin(){
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if(username.length() < 2 || password.length() < 2){
            showMessage("Login", "Please enter a valid username & password.", null);
            return;
        }
        setUIState(true);
        login(username, password);
    }

    private void login(String username, String password){
        new LoginTask().execute(username, password);
    }

    private void loginResult(boolean isSuccess){
        if(isSuccess){
            showMessage("Login succeeded", "You have successfully logged in!",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
        }else{
            setUIState(false);
            showMessage("Login failed", "We couldn't login to your instagram account, Make sure you enter valid username and password.", null);
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... credentials) {
            String username = credentials[0];
            String password = credentials[1];
            return magic.logic(username, password);
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            loginResult(aBoolean);
        }
    }


    private void setUIState(boolean working){
        usernameEditText.setEnabled(!working);
        passwordEditText.setEnabled(!working);
        loginButton.setEnabled(!working);
        loadingProgressBar.setVisibility(working ? View.VISIBLE : View.INVISIBLE);
    }

    private void showMessage(String title, String text, @Nullable DialogInterface.OnClickListener clickListener){
        DialogInterface.OnClickListener _clickListener = clickListener;
        if(_clickListener == null){
            _clickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            };
        }
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage(text);
        dlgAlert.setTitle(title);
        dlgAlert.setPositiveButton("OK", _clickListener);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

}
