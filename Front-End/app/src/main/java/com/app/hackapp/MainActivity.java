package com.app.hackapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {

    private MeowBottomNavigation oBtmNav;
    private Fragment home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        home = new Home();

        // Mostrar como primer fragment el inicio y seteamos el bottom navigation view
        replace(home);
        oBtmNav = findViewById(R.id.bottomNavigation);
        setoBtmNav();
    }

    private void replace (Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, fragment);
        transaction.commit();
    }

    private void setoBtmNav () {
        oBtmNav.add(new MeowBottomNavigation.Model(1, R.drawable.home));
        oBtmNav.add(new MeowBottomNavigation.Model(2, R.drawable.search));
        oBtmNav.add(new MeowBottomNavigation.Model(3, R.drawable.add_circle));
        oBtmNav.add(new MeowBottomNavigation.Model(4, R.drawable.newspaper));
        oBtmNav.add(new MeowBottomNavigation.Model(5, R.drawable.bm_person));

        oBtmNav.show(1, true);
        oBtmNav.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                        replace(home);
                        break;
                    case 2:
                        replace(home);
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        replace(new User());
                        break;
                }
                return null;
            }
        });
    }

    private void setHideFragments () {

    }
}