package com.example.versionetwohorizontales.ui.welcome.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.versionetwohorizontales.R;

import java.util.Random;

public class SplashFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_splash, container, false);

        // Randomize bookmark image in the splash fragment
        ImageView bookmarkImage = view.findViewById(R.id.imageView_bookmark);  // Qui utilizza view.findViewById()

        int[] images = {R.drawable.bookmark_batman, R.drawable.bookmark_ironman,
                R.drawable.bookmark_joker, R.drawable.bookmark_spiderman, R.drawable.bookmark_superman};

        Random rand = new Random();
        bookmarkImage.setBackgroundResource(images[rand.nextInt(images.length)]);

        // Esegui la navigazione al LoginFragment dopo un ritardo
        new Handler().postDelayed(() -> {
            Navigation.findNavController(view).navigate(R.id.action_splashFragment_to_loginFragment);
        }, 2000);  // Imposta 2 secondi di ritardo

        return view;
    }
}
