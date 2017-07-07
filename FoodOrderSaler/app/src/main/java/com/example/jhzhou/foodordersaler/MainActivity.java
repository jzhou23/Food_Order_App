package com.example.jhzhou.foodordersaler;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.jhzhou.foodordersaler.entity.Staff;
import com.example.jhzhou.foodordersaler.fragment.HistoryFragment;
import com.example.jhzhou.foodordersaler.fragment.HomeFragment;
import com.example.jhzhou.foodordersaler.fragment.TodoFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnClickListener{

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    private static final int RC_SIGN_IN = 1;

    private HomeFragment mFragmentHome;
    private TodoFragment mFragmentTodo;
    private HistoryFragment mFragmentHistory;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;

    private Staff mStaff;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.action_home:
                    selectedFragment = mFragmentHome;
                    break;
                case R.id.action_todo:
                    selectedFragment = mFragmentTodo;
                    break;
                case R.id.action_history:
                    selectedFragment = mFragmentHistory;
                    break;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.framelayout, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();

//        if (savedInstanceState == null) {
//            mFragmentHome = HomeFragment.getInstance();
//            mFragmentTodo = TodoFragment.getInstance();
//            mFragmentHistory = HistoryFragment.getInstance();
//
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.framelayout, mFragmentHome);
//            transaction.commit();
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAuthStateListener == null) {
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser staff = firebaseAuth.getCurrentUser();
                    if (staff != null) {
                        initialUser(staff);
                    } else {
                        logIn();
                    }
                }
            };
        }
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void initialUser(final FirebaseUser staff) {
        String uuid = staff.getUid();
        Log.v(LOG_TAG, "uuid " + uuid);
        checkAccount(staff, uuid);
    }

    public void checkAccount(final FirebaseUser user, final String uuid) {
        final DatabaseReference reference = mFirebaseDatabase.getReference().child("staffs");
        reference.orderByChild("uuid").equalTo(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Log.v(LOG_TAG, "this is the first time");

                    Staff staff = new Staff();
                    staff.setUserName(user.getDisplayName());
                    staff.setEmail(user.getEmail());
                    staff.setUUid(uuid);
                    reference.push().setValue(staff);
                } else {

                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        mStaff = shot.getValue(Staff.class);
                    }

                    String uuid = mStaff.getUUid();
                    mFragmentHome = HomeFragment.getInstance(mStaff);
                    mFragmentTodo = TodoFragment.getInstance(uuid);
                    mFragmentHistory = HistoryFragment.getInstance(uuid);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.framelayout, mFragmentHome);
                    transaction.commit();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void logIn() {
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(false)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onClick() {
        mFirebaseAuth.signOut();
    }
}
