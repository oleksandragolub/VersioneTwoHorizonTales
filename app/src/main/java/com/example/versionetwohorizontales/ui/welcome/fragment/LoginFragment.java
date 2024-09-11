package com.example.versionetwohorizontales.ui.welcome.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.versionetwohorizontales.data.repository.user.IUserRepository;
import com.example.versionetwohorizontales.data.repository.user.UserRepository;
import com.example.versionetwohorizontales.data.source.user.UserRemoteDataSource;
import com.example.versionetwohorizontales.model.Result;
import com.example.versionetwohorizontales.R;
import com.example.versionetwohorizontales.databinding.FragmentLoginBinding;
import com.example.versionetwohorizontales.ui.main.fragment.MainActivity;
import com.example.versionetwohorizontales.ui.welcome.viewmodel.GoogleSignInViewModel;
import com.example.versionetwohorizontales.ui.welcome.viewmodelfactory.GoogleSignInViewModelFactory;
import com.example.versionetwohorizontales.ui.welcome.viewmodel.LoginViewModel;
import com.example.versionetwohorizontales.ui.welcome.viewmodelfactory.LoginViewModelFactory;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private GoogleSignInViewModel googleSignInViewModel;
    private GoogleSignInViewModelFactory googleFactory;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        // Crea UserRemoteDataSource con il context e passalo a UserRepository
        UserRemoteDataSource userRemoteDataSource = new UserRemoteDataSource(requireContext());
        IUserRepository userRepository = new UserRepository(userRemoteDataSource);

        // Usa il ViewModelFactory per creare il LoginViewModel
        LoginViewModelFactory factory = new LoginViewModelFactory(userRepository);
        loginViewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        // Use GoogleSignInViewModelFactory to pass context to GoogleSignInViewModel
        googleFactory = new GoogleSignInViewModelFactory(requireContext());
        googleSignInViewModel = new ViewModelProvider(this, googleFactory).get(GoogleSignInViewModel.class);

        // Aggiungi TextWatcher per monitorare il cambiamento dei campi email e password
        binding.email.addTextChangedListener(textWatcher);
        binding.password.addTextChangedListener(textWatcher);

        // Disabilita il pulsante login all'inizio
        binding.btnLogin.setEnabled(false);

        // Setup del bottone login
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            if (validateInput(email, password)) {
                loginViewModel.loginUser(email, password);
            }
        });

        // Bottone per il login tramite Google
        binding.btnLoginGoogle.setOnClickListener(v -> {
            Log.d("GoogleSignIn", "Bottone Google Login cliccato");
            Intent signInIntent = getGoogleSignInClient().getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        setupGoogleSignInLauncher();

        // Osserva i dati dall'oggetto LiveData del ViewModel
        observeLoginResult();
        observeGoogleSignInResult();

        // Gestione click per la registrazione
        binding.registerNow.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registrationFragment);
        });

        // Gestione click per il cambio della password
        binding.textForgotPw.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_forgotPasswordFragment);
        });

        return binding.getRoot();
    }



    // Funzione per validare l'input
    private boolean validateInput(String email, String password) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("Email non valida");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.password.setError("Password non valida");
            return false;
        }
        return true;
    }

    // Funzione per osservare il risultato del login
    private void observeLoginResult() {
        loginViewModel.getUserLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Toast.makeText(getActivity(), "Login riuscito", Toast.LENGTH_SHORT).show();

                // Lancia MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Pulisce la back stack
                startActivity(intent);

            } else if (result instanceof Result.Error) {
                String errorMessage = ((Result.Error<?>) result).getError().getMessage();
                if (errorMessage.contains("Email non verificata")) {
                    Snackbar.make(requireView(), "Per favore verifica la tua email.", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(requireView(), "Errore di login: " + errorMessage, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    // Osserva il risultato del login via Google
    private void observeGoogleSignInResult() {
        googleSignInViewModel.getGoogleUserLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Toast.makeText(requireContext(), "Login Google riuscito", Toast.LENGTH_SHORT).show();

                // Lancia MainActivity
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Pulisce la back stack
                startActivity(intent);

            } else if (result instanceof Result.Error) {
                Exception error = ((Result.Error<?>) result).getError();
                String errorMessage = "Errore Google Sign-In: " + error.getMessage();

                if (error instanceof ApiException) {
                    ApiException apiException = (ApiException) error;
                    errorMessage += " [Codice errore: " + apiException.getStatusCode() + "]";
                    Log.e("GoogleSignIn", "Errore ApiException: " + apiException.getMessage());
                } else {
                    Log.e("GoogleSignIn", "Errore generico: " + error.getMessage());
                }

                Snackbar.make(requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Configurazione del GoogleSignInClient
    private GoogleSignInClient getGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(requireContext(), gso);
    }

    // Setup del launcher per Google Sign-In
    private void setupGoogleSignInLauncher() {
        googleSignInLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Log.d("GoogleSignIn", "Google Sign-In RESULT_OK ricevuto");
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                handleGoogleSignInTask(task);
            } else {
                Log.e("GoogleSignIn", "Google Sign-In non riuscito, codice risultato: " + result.getResultCode());
                Toast.makeText(requireContext(), "Google Sign-In annullato o fallito.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Gestione del risultato del task Google Sign-In
    private void handleGoogleSignInTask(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Log.d("GoogleSignIn", "Account Google ricevuto: " + account.getEmail());
            Log.d("GoogleSignIn", "ID Token: " + account.getIdToken());
            googleSignInViewModel.signInWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            int statusCode = e.getStatusCode();
            Log.e("GoogleSignIn", "Errore di Google Sign-In: " + statusCode + " - " + GoogleSignInStatusCodes.getStatusCodeString(statusCode));
            Toast.makeText(requireContext(), "Google Sign-In fallito. Codice errore: " + statusCode, Toast.LENGTH_LONG).show();

            if (statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                Log.e("GoogleSignIn", "Login annullato dall'utente.");
            } else {
                Log.e("GoogleSignIn", "Codice errore: " + statusCode);
            }
        }
    }


    // TextWatcher per abilitare/disabilitare il pulsante login
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            binding.btnLogin.setEnabled(fieldsAreValid());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    // Funzione per validare i campi email e password
    private boolean fieldsAreValid() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(password) && password.length() >= 6;
    }
}
