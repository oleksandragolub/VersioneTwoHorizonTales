package com.example.versionetwohorizontales.ui.welcome.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.versionetwohorizontales.R;
import com.example.versionetwohorizontales.adapter.TextWatcherAdapter;
import com.example.versionetwohorizontales.ui.welcome.viewmodel.ForgotPasswordViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import com.google.android.material.textfield.TextInputEditText;


public class ForgotPasswordFragment extends Fragment {

    private TextInputEditText editEmail;
    private ForgotPasswordViewModel forgotPasswordViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        MaterialToolbar topAppBar = view.findViewById(R.id.top_appbar);
        editEmail = view.findViewById(R.id.email);
        view.findViewById(R.id.btn_ripristina_password).setEnabled(false);  // Disabilita il pulsante inizialmente

        forgotPasswordViewModel = new ViewModelProvider(this).get(ForgotPasswordViewModel.class);

        // Abilita il bottone solo quando l'email è valida
        editEmail.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                view.findViewById(R.id.btn_ripristina_password).setEnabled(
                        !TextUtils.isEmpty(editEmail.getText()) &&
                                Patterns.EMAIL_ADDRESS.matcher(editEmail.getText()).matches()
                );
            }
        });

        view.findViewById(R.id.btn_ripristina_password).setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            forgotPasswordViewModel.resetPassword(email);
        });

        // Osserva il risultato della richiesta di reset password
        forgotPasswordViewModel.getPasswordResetSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Controlla la tua email per il reset della password.", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_forgotPasswordFragment_to_loginFragment);
            }
        });

        forgotPasswordViewModel.getPasswordResetError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                editEmail.setError(errorMessage);
                editEmail.requestFocus();
            }
        });

        // Navigazione verso la schermata di login
        topAppBar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });

        return view;
    }
}