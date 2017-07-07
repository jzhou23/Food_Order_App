package com.example.jhzhou.foodordersaler.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jhzhou.foodordersaler.R;
import com.example.jhzhou.foodordersaler.adapter.HistoryAdapter;
import com.example.jhzhou.foodordersaler.entity.Staff;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by jhzhou on 6/5/17.
 */

public class HomeFragment extends Fragment {

    @BindView(R.id.staff_image_home) CircleImageView staffImage;
    @BindView(R.id.staff_name) TextView staffName;
    @BindView(R.id.staff_email) TextView staffEmail;
    @BindView(R.id.total_profit) TextView totalProfit;
    @BindView(R.id.log_out) TextView logOut;

    public static final int RC_PHOTO_PICKER = 1;

    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mPortraitStorageReference;

    private Staff mStaff;
    private OnClickListener mOnClickListener;


    public static HomeFragment getInstance(Staff staff) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable("staff", staff);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mOnClickListener = (OnClickListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStaff = getArguments().getParcelable("staff");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnClickListener.onClick();
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mPortraitStorageReference = mFirebaseStorage.getReference().child("staffs_photo");

        staffImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        staffName.setText(mStaff.getUserName());
        staffEmail.setText(mStaff.getEmail());
        totalProfit.setText(String.valueOf(mStaff.getTotalProfit()));

        String image = mStaff.getImage();
        if (image != null && !image.equals(image)) {
            Glide.with(getActivity()).load(Uri.parse(image)).into(staffImage);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_PHOTO_PICKER) {
                Uri selectedImageUri = data.getData();

                // Get a reference to store file at chat_photos/<FILENAME>
                StorageReference photoRef = mPortraitStorageReference.child(selectedImageUri.getLastPathSegment());

                photoRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        String imageString = downloadUrl.toString();
                        mStaff.setImage(imageString);

                        Glide.with(getActivity()).load(downloadUrl).into(staffImage);

                        uploadImage(imageString);
                    }
                });
            }
        }
    }

    public void uploadImage(final String imageString) {
        DatabaseReference reference = mFirebaseDatabase.getReference().child("users");
        reference.orderByChild("uuid").equalTo(mStaff.getUUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public interface OnClickListener {
        void onClick();
    }

}
