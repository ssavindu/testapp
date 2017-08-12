package com.janithwannidev.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , GoogleApiClient.OnConnectionFailedListener{
    private RecyclerView recyclerV;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutman;

    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    public TextView acnt_name_txt;
    public Button sign_out_btn;
    public Button disconnect_btn;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "FUCKING LOGGING NOW!";
    public SharedPreferences sharedPreferences;

    private String[] Dataset = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.".split(" ");

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerV = (RecyclerView) findViewById((R.id.recycler_view));
        recyclerV.setHasFixedSize(true); //performance tip
        layoutman = new LinearLayoutManager(this);
        recyclerV.setLayoutManager(layoutman);
        adapter = new rec_adapter(Dataset);
        recyclerV.setAdapter(adapter);

        sign_out_btn = (Button) findViewById(R.id.sign_out_button);
        disconnect_btn = (Button) findViewById(R.id.disconnect_button);
        acnt_name_txt = (TextView) findViewById(R.id.acnt_name);
        sign_out_btn.setOnClickListener(this);
        disconnect_btn.setOnClickListener(this);

        updateUI(null);
        Log.i(TAG,"Signout and disconnect buttons hidden");

        findViewById(R.id.login_btn).setOnClickListener(this);
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,options).build();
        Log.i(TAG,"googelApiClient Instantiated");
        firebaseAuth = FirebaseAuth.getInstance();
        Log.i(TAG,"firebaseAUth Instantiated");
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        Log.i(TAG,"firebase user recieved from on start");
        updateUI(currentUser);
    }

    public void item_click(View v){
        Intent intent = new Intent(this,DisplayMessage.class);
        int elem = 0;
        TextView targettxt = (TextView)v.findViewById(R.id.info_text);
        String target = targettxt.getText().toString();
        int pos = 0;
        for(String s:Dataset){
            if(s.equals(target)){
                pos = elem;
            }
            elem++;
        }
        intent.putExtra("com.janithwannidev.noteapp.TITLE",pos);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                Log.i(TAG,"Sign in clicked");
                signIn();
                break;
            case R.id.sign_out_button:
                Log.i(TAG,"Sign out clicked");
                signOut();
                break;
            case R.id.disconnect_button:
                Log.i(TAG,"Sign disconnect clicked");
                revokeAccess();
                break;
        }
    }

    private void signIn(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        Log.i(TAG,"Starting Activity");
        startActivityForResult(signInIntent,RC_SIGN_IN);
        Log.i(TAG,"Started Activity");
    }

    public void signOut(){
        firebaseAuth.signOut();
        Log.i(TAG,"Signing out user");
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                }
        );
    }

    public void revokeAccess(){
        firebaseAuth.signOut();
        Log.i(TAG,"Signing out user");
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        updateUI(null);
                    }
                }
        );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"in activity result");
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode==RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result){
        Log.i(TAG,result.toString()+" "+result.isSuccess());
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            Log.i(TAG,account.getDisplayName());
            acnt_name_txt.setText(account.getDisplayName().toString());
            firebaseAuthwithGoogle(account);
        }else{
            updateUI(null);
        }
    }

    public void firebaseAuthwithGoogle(GoogleSignInAccount account){
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getIdToken());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "signInWithCredential:success");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                }else{
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            sign_out_btn.setVisibility(View.VISIBLE);
            disconnect_btn.setVisibility(View.VISIBLE);
            acnt_name_txt.setText(user.getDisplayName()+"\n"+user.getEmail());
            findViewById(R.id.login_btn).setVisibility(View.GONE);
        }else{
            sign_out_btn.setVisibility(View.GONE);
            disconnect_btn.setVisibility(View.GONE);
            acnt_name_txt.setText(null);
            findViewById(R.id.login_btn).setVisibility(View.VISIBLE);
        }
    }

}
