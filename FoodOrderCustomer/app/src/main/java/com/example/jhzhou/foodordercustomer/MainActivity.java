package com.example.jhzhou.foodordercustomer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.jhzhou.foodordercustomer.adapter.FoodAdapter;
import com.example.jhzhou.foodordercustomer.entity.Bag;
import com.example.jhzhou.foodordercustomer.entity.Customer;
import com.example.jhzhou.foodordercustomer.entity.Food;
import com.example.jhzhou.foodordercustomer.entity.Order;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements FoodAdapter.ClickListener {

    @BindView(R.id.toolbar_main)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.recycler_view_main)
    RecyclerView recyclerView;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String ANONYMOUS = "Customer";

    public static final int CATEGORY_APPETIZER = 1;
    public static final int CATEGORY_MEAL = 2;
    public static final int CATEGORY_DRINKS = 3;

    public static final int REQUEST_DETAILS = 1;
    public static final int REQUEST_BAG = 2;
    public static final int RC_SIGN_IN = 3;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private Bag mBag;
    private List<Food> mFoodList;
    private FoodAdapter mFoodAdapter;
    private Customer mCustomer;

    private TextView mLogTextView;
    private TextView mNameTextView;
    private CircleImageView mProfileCircleImageView;
    private TextView mEmailTextView;

    private boolean signUpFromBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mCustomer = new Customer(ANONYMOUS);
        // initialize UI part in headlayout
        View head = navigationView.getHeaderView(0);
        mLogTextView = (TextView) head.findViewById(R.id.log);
        mNameTextView = (TextView) head.findViewById(R.id.customer_name);
        mProfileCircleImageView = (CircleImageView) head.findViewById(R.id.header_portrait);
        mEmailTextView = (TextView) head.findViewById(R.id.customer_email);

        mLogTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = mLogTextView.getText().toString();
                if (status.equals("Log In")) {
                    logIn();
                } else {
                    Toast.makeText(MainActivity.this, getString(R.string.log_out), Toast.LENGTH_SHORT).show();
                    mFirebaseAuth.signOut();
                }
            }
        });

        mProfileCircleImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user == null) {
                    logIn();
                } else {
                    intent.putExtra(CustomerActivity.CUSTOMER, mCustomer);
                    startActivity(intent);
                }
            }
        });

        // initialize the tool bar
        setSupportActionBar(toolbar);

        // hamburget icon
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBag = new Bag();
        mFoodList = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(this, mFoodList, this);
        recyclerView.setAdapter(mFoodAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        // Initialize Firebase Navigation
        navigationView.setCheckedItem(R.id.nav_all);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_all:
                        mFoodAdapter.setAdapter(mFoodList);
                        break;
                    case R.id.nav_appetizer:
                        mFoodAdapter.setAdapter(getFoodList(CATEGORY_APPETIZER));
                        break;
                    case R.id.nav_meal:
                        mFoodAdapter.setAdapter(getFoodList(CATEGORY_MEAL));
                        break;
                    case R.id.nav_drinks:
                        mFoodAdapter.setAdapter(getFoodList(CATEGORY_DRINKS));
                        break;
                    case R.id.nav_daily_special:
                        mFoodAdapter.setAdapter(getDailySpecialFoodList());
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        // Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        DatabaseReference mDatabaseReference = mFirebaseDatabase.getReference().child("foods");

        ChildEventListener mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                mFoodList.add(food);
                mFoodAdapter.notifyDataSetChanged();
                if (food.getIsSpecial()) {
                    Log.v(LOG_TAG, "is a special");
                } else {
                    Log.v(LOG_TAG, "is not a special");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };

        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        if (mAuthStateListener == null) {
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        initialUser(user);
                    } else {
                        clearUserUI();
                    }
                }
            };
        }
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "onPause");
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.action_bag:
                Intent intent = new Intent(MainActivity.this, BagActivity.class);
                intent.putExtra(BagActivity.BAG, mBag);
                startActivityForResult(intent, REQUEST_BAG);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_toolbar, menu);
        return true;
    }

    public List<Food> getFoodList(int category) {
        List<Food> foodList = new ArrayList<>();
        for (Food food : mFoodList) {
            if (food.getCategory() == category) {
                foodList.add(food);
            }
        }
        return foodList;
    }

    public List<Food> getDailySpecialFoodList() {
        List<Food> foodList = new ArrayList<>();
        for (Food food : mFoodList) {
            if (food.getIsSpecial()) {
                foodList.add(food);
            }
        }
        Log.v(LOG_TAG, "foodList.size() " + foodList.size());
        return foodList;
    }

    @Override
    public void onClick(Food food) {
        Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
        intent.putExtra(DetailsActivity.FOOD, food);
        startActivityForResult(intent, REQUEST_DETAILS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_DETAILS) {

                Order order = data.getParcelableExtra(DetailsActivity.ORDER);
                Log.d(LOG_TAG, "nums of food " + order.getNums());
                mBag.addOrder(order);
                mBag.addOrderPrice(order);
                Log.d(LOG_TAG, "size of bag " + mBag.getOrderList().size());

            } else if (requestCode == REQUEST_BAG) {

                Bag bag = data.getParcelableExtra(BagActivity.BAG);
                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                if (user != null) {
                    // add the bag info to order list
                    bag.setCustomerUUid(user.getUid());
                    bag.setCurrentTime();
                    final DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("orders");
                    databaseReference.push().setValue(bag);

                    // upload baglist to firebase database
                    addOrderToHistory(bag);
                    mBag = new Bag();
                } else {
                    signUpFromBag = true;
//                    drawerLayout.openDrawer(GravityCompat.START);
                    CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout_main);
                    Snackbar.make(layout, getString(R.string.log_in_reminder), Snackbar.LENGTH_LONG).setAction(R.string.action_log_in, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    logIn();
                                }
                            }).show();
                }
            } else if (requestCode == RC_SIGN_IN && signUpFromBag) {
                signUpFromBag = false;
                Intent intent = new Intent(MainActivity.this, BagActivity.class);
                intent.putExtra(BagActivity.BAG, mBag);
                startActivityForResult(intent, REQUEST_BAG);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void initialUser(final FirebaseUser user) {
        String uuid = user.getUid();
        Log.v(LOG_TAG, "uuid " + uuid);
        checkAccount(user, uuid);
    }

    public void checkAccount(final FirebaseUser user, final String uuid) {
        final DatabaseReference reference = mFirebaseDatabase.getReference().child("users");
        reference.orderByChild("uuid").equalTo(uuid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChildren()) {
                    Log.v(LOG_TAG, "this is the first time");

                    mCustomer = new Customer(uuid, user.getDisplayName(), user.getEmail());
                    reference.push().setValue(mCustomer);
                } else {
                    for (DataSnapshot shot : dataSnapshot.getChildren()) {
                        mCustomer = shot.getValue(Customer.class);
                        List<Bag> bagList = mCustomer.getHistory();

                        if (bagList == null) {
                            mCustomer.setHistory(new ArrayList<Bag>());
                        }
                    }
                }
                updateUserUI();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateUserUI() {
        // user name
        mNameTextView.setText(mCustomer.getUserName());

        // email
        String email = mCustomer.getEmail();
        if (email != null && !email.equals("")) {
            mEmailTextView.setVisibility(View.VISIBLE);
            mEmailTextView.setText(email);
        }

        // image
        String imageUri = mCustomer.getImage();
        if (imageUri != null && !imageUri.equals("")) {
            Glide.with(this).load(Uri.parse(imageUri)).into(mProfileCircleImageView);
        }

        // Log out
        mLogTextView.setText(R.string.log_out);
    }

    public void clearUserUI() {
        // user name
        mNameTextView.setText(ANONYMOUS);

        // email
        mEmailTextView.setText("");
        mEmailTextView.setVisibility(View.INVISIBLE);

        // image
        mProfileCircleImageView.setImageResource(R.drawable.ic_person_black_48dp);

        // Log in
        mLogTextView.setText(R.string.log_in);
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

    public void addOrderToHistory(final Bag bag) {
        DatabaseReference reference = mFirebaseDatabase.getReference().child("users");
        reference.orderByChild("uuid").equalTo(mCustomer.getUUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<List<Bag>> t = new GenericTypeIndicator<List<Bag>>() {};
                    List<Bag> orderList = shot.child("history").getValue(t);

                    if (orderList == null || orderList.size() == 0) {
                        orderList = new ArrayList<Bag>();
                    }

                    orderList.add(bag);
                    shot.child("history").getRef().setValue(orderList);

                    // update order list of customer
                    mCustomer.setHistory(orderList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
