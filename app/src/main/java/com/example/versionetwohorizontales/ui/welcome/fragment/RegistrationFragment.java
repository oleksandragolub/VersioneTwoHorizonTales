package com.example.versionetwohorizontales.ui.welcome.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
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
import com.example.versionetwohorizontales.databinding.FragmentRegistrationBinding;
import com.example.versionetwohorizontales.ui.welcome.viewmodel.RegistrationViewModel;
import com.example.versionetwohorizontales.ui.welcome.viewmodelfactory.RegistrationViewModelFactory;
import com.google.android.material.snackbar.Snackbar;

import java.util.Calendar;

public class RegistrationFragment extends Fragment {
    private FragmentRegistrationBinding binding;
    private IUserRepository userRepository;
    private RegistrationViewModel registrationViewModel;
    private RegistrationViewModelFactory factory;
    private DatePickerDialog datePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false);

        // Crea UserRepository con UserRemoteDataSource e passalo a RegistrationViewModelFactory
        userRepository = new UserRepository(new UserRemoteDataSource(requireContext()));
        factory = new RegistrationViewModelFactory(userRepository);
        registrationViewModel = new ViewModelProvider(this, factory).get(RegistrationViewModel.class);

        binding.username.addTextChangedListener(textWatcher);
        binding.email.addTextChangedListener(textWatcher);
        binding.DoB.addTextChangedListener(textWatcher);
        binding.password.addTextChangedListener(textWatcher);
        binding.confermaPassword.addTextChangedListener(textWatcher);

        // Resta il bottone disabilitato all'inizio
        binding.btnRegister.setEnabled(fieldsAreValid());

        binding.radioGroupRegisterGender.setOnCheckedChangeListener((group, checkedId) -> {
            binding.btnRegister.setEnabled(fieldsAreValid());
        });

        // Setup del bottone di registrazione
        binding.btnRegister.setOnClickListener(v -> {
            String name = binding.username.getText().toString().trim();
            String email = binding.email.getText().toString().trim();
            String dob = binding.DoB.getText().toString().trim();
            String gender = binding.radioGroupRegisterGender.getCheckedRadioButtonId() == R.id.gender_male ? "Male" : "Female";
            String password = binding.password.getText().toString().trim();
            String confirmPassword = binding.confermaPassword.getText().toString().trim();

            if (validateInput(name, email, dob, password, confirmPassword)) {
                Log.d("RegistrationFragment", "Calling registerUser with data: " + name + ", " + email);
                registrationViewModel.registerUser(name, email, dob, gender, password);
            }
        });

        // Impostare il selettore della data di nascita
        setupDatePicker();

        // Osserva i dati dall'oggetto LiveData del ViewModel
        observeRegistrationResult();

        // Impostazione dell'icona di navigazione per tornare indietro
        binding.topAppbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(binding.getRoot()).navigateUp();
        });

        return binding.getRoot();
    }

    // Funzione per configurare il DatePicker
    private void setupDatePicker() {
        binding.DoB.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            datePicker = new DatePickerDialog(getContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
                String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                binding.DoB.setText(selectedDate);
            }, year, month, day);

            datePicker.show();
        });
    }

    // Funzione per validare l'input
    private boolean validateInput(String name, String email, String dob, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name)) {
            binding.username.setError("Nome richiesto");
            return false;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.email.setError("Email non valida");
            return false;
        }
        if (TextUtils.isEmpty(dob)) {
            binding.DoB.setError("Data di nascita richiesta");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            binding.password.setError("Password troppo corta");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            binding.confermaPassword.setError("Le password non corrispondono");
            return false;
        }
        return true;
    }

    // Funzione per osservare il risultato della registrazione
    private void observeRegistrationResult() {
        registrationViewModel.getUserLiveData().observe(getViewLifecycleOwner(), result -> {
            if (result instanceof Result.Success) {
                Toast.makeText(getActivity(), "Registrazione riuscita", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_registrationFragment_to_loginFragment);
            } else if (result instanceof Result.Error) {
                Snackbar.make(requireView(), ((Result.Error) result).getError().getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            binding.btnRegister.setEnabled(fieldsAreValid());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    };

    private boolean fieldsAreValid() {
        String name = binding.username.getText().toString().trim();
        String email = binding.email.getText().toString().trim();
        String dob = binding.DoB.getText().toString().trim();
        String password = binding.password.getText().toString().trim();
        String confirmPassword = binding.confermaPassword.getText().toString().trim();
        int selectedGenderId = binding.radioGroupRegisterGender.getCheckedRadioButtonId();

        return !TextUtils.isEmpty(name) &&
                !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                !TextUtils.isEmpty(dob) &&
                !TextUtils.isEmpty(password) && password.length() >= 6 &&
                password.equals(confirmPassword) &&
                selectedGenderId != -1;
    }
}
