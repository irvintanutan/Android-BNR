package com.novigosolutions.certiscisco_pcsbr.activites;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco_pcsbr.R;
import com.novigosolutions.certiscisco_pcsbr.service.UserLogService;
import com.novigosolutions.certiscisco_pcsbr.utils.Preferences;
import com.novigosolutions.certiscisco_pcsbr.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco_pcsbr.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco_pcsbr.webservices.UnsafeOkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.novigosolutions.certiscisco_pcsbr.constant.UserLog.CHANGE_PASSWORD;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MAX_PASSWORD_AGE;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MAX_PASSWORD_LENGTH;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MIN_PASSWORD_ALPHABET;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MIN_PASSWORD_LENGTH;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MIN_PASSWORD_LOWERCASE;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MIN_PASSWORD_NUMERIC;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MIN_PASSWORD_SPECIAL_CHARACTER;
import static com.novigosolutions.certiscisco_pcsbr.utils.Constants.MIN_PASSWORD_UPPERCASE;

public class ChangePasswordActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    EditText oldPassword, newPassword, confirmPassword;
    Button next;
    TextView errorText;

    int numericCount = 0, alphaCount = 0, specialCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPassword = findViewById(R.id.old_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirmPassword);
        errorText = findViewById(R.id.errorMessage);
        next = findViewById(R.id.next);

        List<Config> configs = new ArrayList<>();
        configs.add(new Config(MAX_PASSWORD_LENGTH, Integer.parseInt(Preferences.getString(MAX_PASSWORD_LENGTH, this))));
        configs.add(new Config(MIN_PASSWORD_LENGTH, Integer.parseInt(Preferences.getString(MIN_PASSWORD_LENGTH, this))));
        configs.add(new Config(MIN_PASSWORD_UPPERCASE, Integer.parseInt(Preferences.getString(MIN_PASSWORD_UPPERCASE, this))));
        configs.add(new Config(MIN_PASSWORD_LOWERCASE, Integer.parseInt(Preferences.getString(MIN_PASSWORD_LOWERCASE, this))));
        configs.add(new Config(MIN_PASSWORD_NUMERIC, Integer.parseInt(Preferences.getString(MIN_PASSWORD_NUMERIC, this))));
        configs.add(new Config(MIN_PASSWORD_SPECIAL_CHARACTER, Integer.parseInt(Preferences.getString(MIN_PASSWORD_SPECIAL_CHARACTER, this))));
        configs.add(new Config(MIN_PASSWORD_ALPHABET, Integer.parseInt(Preferences.getString(MIN_PASSWORD_ALPHABET, this))));
        configs.add(new Config(MAX_PASSWORD_AGE , Integer.parseInt(Preferences.getString(MAX_PASSWORD_AGE, this))));

        String originalPassword = Preferences.getString("Password", this);
        int userId = Preferences.getInt("UserId", this);
        String userName = Preferences.getString("UserName", this);
        UserLogService.save(CHANGE_PASSWORD.toString(), "USER_ID: " + userId, "CHANGE PASSWORD ATTEMPT", getApplicationContext());


        next.setOnClickListener(view -> {
            List<String> errors = validate(configs, originalPassword);
            if (errors.isEmpty()) {
                showProgressDialog("Changing password is in progress");
                Call<ResponseBody> call = getService().CustomerChangePassword(userId, oldPassword.getText().toString(), newPassword.getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (response.code() == 200) {
                                hideProgressDialog();
                                Toast.makeText(getApplicationContext(), "Password change successful", Toast.LENGTH_LONG).show();
                                Preferences.saveString("Password", newPassword.getText().toString(), getApplicationContext());
                                UserLogService.save(CHANGE_PASSWORD.toString(), "USER_ID: " + userId, "CHANGE PASSWORD SUCCESS", getApplicationContext());
                                startActivity(new Intent(ChangePasswordActivity.this, HomeActivity.class));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("Failed", "Change Password " + call.toString());
                        UserLogService.save(CHANGE_PASSWORD.toString(), "USER_ID: " + userName, "CHANGE PASSWORD FAILED", getApplicationContext());
                    }
                });
            } else {
                String errorMessage = String.join(", ", errors);
                errorText.setText(errorMessage);

            }
        });
    }

    private List<String> validate(List<Config> configs, String originalPassword) {
        List<String> errors = new ArrayList<>();

        String newPasswordString = newPassword.getText().toString();
        String oldPasswordString = oldPassword.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();

        countNumericAlphaSpecial(newPasswordString);

        if (!originalPassword.equals(oldPasswordString)) {
            errors.add("Invalid Current Password");
        }

        if (!newPasswordString.equals(confirmPasswordString)) {
            errors.add("Password did not match");
        }

        for (Config config : configs) {
            switch (config.getConfig()) {
//                case MAX_PASSWORD_LENGTH:
//                    if (newPasswordString.length() < config.getValue()) {
//                        errors.add("The password must be maximum of " + config.getValue());
//                    }
//                    break;
                case MAX_PASSWORD_LENGTH:
                    if (newPasswordString.length() < config.getValue()) {
                        errors.add("The password must be minimum of " + config.getValue());
                    }
                    break;
                case MIN_PASSWORD_UPPERCASE:
                    if (upperLowerCaseCount(newPasswordString, true) < config.getValue()) {
                        errors.add("The password must have at least " + config.getValue() + " Upper Case character");
                    }
                    break;
                case MIN_PASSWORD_LOWERCASE:
                    if (upperLowerCaseCount(newPasswordString, false) < config.getValue()) {
                        errors.add("The password must have at least " + config.getValue() + " Lower Case character");
                    }
                    break;
                case MIN_PASSWORD_NUMERIC:
                    if (numericCount < config.getValue()) {
                        errors.add("The password must have at least " + config.getValue() + " Numeric character");
                    }
                    break;
                case MIN_PASSWORD_SPECIAL_CHARACTER:
                    if (specialCount < config.getValue()) {
                        errors.add("The password must have at least " + config.getValue() + " Special character");
                    }
                    break;
                case MIN_PASSWORD_ALPHABET:
                    if (alphaCount < config.getValue()) {
                        errors.add("The password must have at least " + config.getValue() + " Alphabet character");
                    }
                    break;
            }
        }

        return errors;
    }

    private int upperLowerCaseCount(String str, boolean isUpperCase) {
        int count = 0;

        for (int a = 0; a < str.length(); a++) {
            if (isUpperCase && Character.isUpperCase(str.charAt(a))) {
                count++;
            } else if (!isUpperCase && Character.isLowerCase(str.charAt(a))) {
                count++;
            }
        }
        return count;
    }

    private int countNumericAlphaSpecial(String str) {
        int count = 0;
        numericCount = alphaCount = specialCount = 0;
        char ch;
        for (int a = 0; a < str.length(); a++) {
            ch = str.charAt(a);
            if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
                alphaCount++;
            } else if (ch >= '0' && ch <= '9') {
                numericCount++;
            } else {
                specialCount++;
            }
        }
        return count;
    }


    private class Config {
        private String config;

        public String getConfig() {
            return config;
        }

        public void setConfig(String config) {
            this.config = config;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        private int value;

        Config(String config, int value) {
            this.config = config;
            this.value = value;
        }

    }

    private CertisCISCOServices getService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(200, TimeUnit.SECONDS);
        httpClient.readTimeout(200, TimeUnit.SECONDS);
        httpClient.writeTimeout(200, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                //.client(client)
                .baseUrl(CertisCISCOServer.getPATH())
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        return retrofit.create(CertisCISCOServices.class);
    }

    public void showProgressDialog(String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(message);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
