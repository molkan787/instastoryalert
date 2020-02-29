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

public class AddUserActivity extends AppCompatActivity {

    Magic magic;
    EditText usernameEditText;
    Button addButton;
    ProgressBar loadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        magic = new Magic(this);

        usernameEditText = findViewById(R.id.username);
        addButton = findViewById(R.id.add);
        loadingProgressBar = findViewById(R.id.loading);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryAddUser();
            }
        });
    }

    private void tryAddUser(){
        String username = usernameEditText.getText().toString();
        if(username.length() < 2){
            showMessage("Form", "Please enter a valid username.", null);
            return;
        }
        setUIState(true);
        addUser(username);
    }

    private void addUser(String username){
        new AddUserTask().execute(username);
    }

    private void addUserResult(Magic.AddUserResult result){
        if(result == Magic.AddUserResult.Success){
            showMessage("Success", "You have successfully added user.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(AddUserActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
        }else if(result == Magic.AddUserResult.AlreadyAdded){
            showMessage("Already added", "This user is already added to the alert list.", null);
            setUIState(false);
        } else{
            showMessage("Failed", "We couldn't add the user, Make sure you've entered the correct username.", null);
            setUIState(false);
        }
    }

    private class AddUserTask extends AsyncTask<String, Void, Magic.AddUserResult>{

        @Override
        protected Magic.AddUserResult doInBackground(String... strings) {
            magic.getReady();
            return magic.addUser(strings[0]);
        }

        @Override
        protected void onPostExecute(Magic.AddUserResult result) {
            addUserResult(result);
        }
    }

    private void setUIState(boolean working){
        usernameEditText.setEnabled(!working);
        addButton.setEnabled(!working);
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
