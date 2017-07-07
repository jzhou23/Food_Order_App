package com.example.jhzhou.foodordercustomer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.jhzhou.foodordercustomer.adapter.ViewPagerAdapter;
import com.example.jhzhou.foodordercustomer.entity.Bag;
import com.example.jhzhou.foodordercustomer.entity.Customer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jhzhou on 6/8/17.
 */

public class CustomerActivity extends AppCompatActivity {

    @BindView(R.id.header_portrait_profile) CircleImageView circleImageView;
    @BindView(R.id.toolbar_profile) Toolbar toolbar;
    @BindView(R.id.view_pager_profile) ViewPager viewPager;
    @BindView(R.id.sliding_tabs) TabLayout tabLayout;
    @BindView(R.id.scroll_view) NestedScrollView scrollView;
    @BindView(R.id.collapsing_profile) CollapsingToolbarLayout collapsingProfile;

    public static final String LOG_TAG = CustomerActivity.class.getSimpleName();
    public static final int RC_PHOTO_PICKER = 1;
    public static final String CUSTOMER = "customer";

    private Customer mCustomer;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPortraitStorageReference;
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingProfile.setTitleEnabled(false);

        Intent intent = getIntent();
        if (intent != null) {
            mCustomer = intent.getParcelableExtra(CUSTOMER);

            String image = mCustomer.getImage();
            if (image != null && !image.equals("")) {
                Glide.with(this).load(Uri.parse(image)).into(circleImageView);
            } else {
                circleImageView.setImageResource(R.drawable.ic_person_black_48dp);
            }
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mCustomer);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        scrollView.setFillViewport(true);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        mFirebaseStorage = FirebaseStorage.getInstance();
        mPortraitStorageReference = mFirebaseStorage.getReference().child("users_photo");

        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_PHOTO_PICKER) {
                Uri selectedImageUri = data.getData();

                // Get a reference to store file at chat_photos/<FILENAME>
                StorageReference photoRef = mPortraitStorageReference.child(selectedImageUri.getLastPathSegment());

                // Upload file to Firebase Storage
                photoRef.putFile(selectedImageUri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // When the image has successfully uploaded, we get its download URL
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                String imageString = downloadUrl.toString();
                                mCustomer.setImage(imageString);

                                Glide.with(CustomerActivity.this).load(downloadUrl).into(circleImageView);

                                uploadImage(imageString);
                            }
                        });
            }
        }
    }

    public void uploadImage(final String imageString) {
        DatabaseReference reference = mFirebaseDatabase.getReference().child("users");
        reference.orderByChild("uuid").equalTo(mCustomer.getUUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot shot : dataSnapshot.getChildren()) {
                    shot.child("image").getRef().setValue(imageString);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
