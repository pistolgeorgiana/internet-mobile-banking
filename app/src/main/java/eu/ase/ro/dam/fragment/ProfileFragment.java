package eu.ase.ro.dam.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import eu.ase.ro.dam.BanksActivity;
import eu.ase.ro.dam.BuildConfig;
import eu.ase.ro.dam.MapsActivity;
import eu.ase.ro.dam.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private static final int GALERY_REQ_CODE = 300;
    private static final int CAMERA_REQ_CODE = 100;
    private String imagePath;
    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    ImageView profilePhoto;
    Button btnLocations;
    Button btnBanks;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        expandableListView = view.findViewById(R.id.profile_elv_vaults);
        expandableListAdapter = new eu.ase.ro.dam.util.ExpandableListAdapter(getContext());
        expandableListView.setAdapter(expandableListAdapter);

        btnBanks = view.findViewById(R.id.profile_btn_banks);
        btnBanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), BanksActivity.class);
                startActivity(intent);
            }
        });

        btnLocations = view.findViewById(R.id.profile_btn_locations);
        btnLocations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        profilePhoto = view.findViewById(R.id.profile_iv_avatar);
        profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items = getResources().getStringArray(R.array.banks_dialog_picker);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                alertDialog.setTitle(getString(R.string.profile_dialog_source))
                        .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                galleryIntent();
                                break;
                            case 1:
                                cameraIntent();
                                break;
                        }
                    }
                }).create().show();
            }
        });
    }

    private void galleryIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK).setType("image/*");
        String[] extensions = {"image/png", "image/bmp", "image/jpeg"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, extensions);
        startActivityForResult(intent, GALERY_REQ_CODE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getContext()
                .getPackageManager()) != null) {
            try {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(
                                getContext(),
                                "eu.ase.ro.dam.provider",
                                create()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            startActivityForResult(intent, CAMERA_REQ_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALERY_REQ_CODE
                && resultCode == RESULT_OK && data != null) {
            Uri photoUri = data.getData();
            Bitmap image = getImageForImageView(photoUri);

            profilePhoto.setImageBitmap(image);
        } else if (requestCode == CAMERA_REQ_CODE) {
            profilePhoto.setImageURI(Uri.parse(imagePath));
        }
    }

    private Bitmap getImageForImageView(Uri photoUri) {
        String[] path = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver()
                .query(photoUri, path, null, null, null);
        cursor.moveToFirst();
        int index = cursor.getColumnIndex(path[0]);
        String decodedImage = cursor.getString(index);
        cursor.close();

        Bitmap image = BitmapFactory.decodeFile(decodedImage);
        image = fitToImageView(image, 700);

        return image;
    }

    private File create() throws IOException{
        String imageName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                .format(new Date());
        File directory = getContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File imgResult = File.createTempFile(
                imageName,
                ".jpg",
                directory);
        imagePath = imgResult.getAbsolutePath();
        return imgResult;
    }

    private Bitmap fitToImageView(Bitmap image, int maxSize) {
        float height = image.getHeight();
        float width = image.getWidth();
        float ratio = width / height;

        if(ratio > 0) {
            height = (int) (width / ratio);
            width = maxSize;
        } else {
            width = (int) (height * ratio);
            height = maxSize;
        }
        return Bitmap.createScaledBitmap(image, (int) width,
                (int) height, true);
    }
}
