package it.polito.group2.restaurantowner.owner;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.Utils.PermissionUtil;
import it.polito.group2.restaurantowner.firebasedata.Meal;

/**
 * Created by Alessio on 16/04/2016.
 */
public class FragmentMainInfo extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    String selectedCategory1 = null;
    String selectedCategory2 = null;
    String selectedCategory3 = null;
    onMainInfoPass dataPasser;
    private EditText meal_name;
    private EditText meal_price;
    private String photouri = "";
    private Spinner type1;
    private Spinner type2;
    private Spinner category;
    private CheckBox take_away;
    private boolean is_take_away;
    public int PICK_IMAGE = 0;
    public int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_WRITE_STORAGE = 112;
    public View rootView = null;
    public static FragmentMainInfo fragment;
    private boolean photoUploaded;

    public FragmentMainInfo() {
    }

    public static FragmentMainInfo newInstance(Meal m) {
        fragment = new FragmentMainInfo();
        Bundle args = new Bundle();
        if(m.getMeal_name()!=null) {
            args.putString("meal_name", m.getMeal_name());
            args.putDouble("meal_price", m.getMeal_price());
            //TODO upload both thumbnail and full size pictures
            args.putString("meal_photo", m.getMeal_photo_firebase_URL());
            args.putBoolean("is_vegan", m.getMealVegan());
            args.putBoolean("is_vegetarian", m.getMealVegetarian());
            args.putBoolean("is_celiac", m.getMealGlutenFree());
            args.putString("category", m.getMeal_category());
            args.putBoolean("take_away", m.getMealTakeAway());
        }
        fragment.setArguments(args);
        Log.d("aaa", "passed in fragment main info1");
        return fragment;
    }

    /*
    public void onSaveInstanceState(Bundle outState){
        getFragmentManager().putFragment(outState,"myfragment",fragment);
    }
    public void onRestoreInstanceState(Bundle inState){
        fragment = (FragmentMainInfo) getFragmentManager().getFragment(inState,"myfragment");
    }
    */

    public void passData() {
        Log.d("aaa", "passed in fragment main info3");
        if(dataPasser==null)
            Log.d("aaa", "datapasser is null3");
        if(meal_price.getText().toString().equals(""))
            meal_price.setText("0.0");
        Log.d("ddd", String.valueOf(category.getSelectedItem()));
        boolean is_vegan=false, is_vegetarian=false, is_celiac = false;
        if(type1.getSelectedItem().equals("Vegan") || type1.getSelectedItem().equals("Vegano") ||
                type2.getSelectedItem().equals("Vegan") || type2.getSelectedItem().equals("Vegano"))
            is_vegan = true;
        if(type1.getSelectedItem().equals("Vegetarian") || type1.getSelectedItem().equals("Vegetariano") ||
                type2.getSelectedItem().equals("Vegetarian") || type2.getSelectedItem().equals("Vegetariano"))
            is_vegetarian = true;
        if(type1.getSelectedItem().equals("Celiac") || type1.getSelectedItem().equals("Celiaco") ||
                type2.getSelectedItem().equals("Celiac") || type2.getSelectedItem().equals("Celiaco"))
            is_celiac = true;
                dataPasser.
                onMainInfoPass(
                        meal_name.getText().toString(),
                        Double.parseDouble(meal_price.getText().toString()),
                        photouri,
                        is_vegan,
                        is_vegetarian,
                        is_celiac,
                        String.valueOf(category.getSelectedItem()),
                        is_take_away,
                        photoUploaded);
    }

    public interface onMainInfoPass {
        public void onMainInfoPass(String meal_name, double meal_price, String  photouri,  boolean is_vegan, boolean is_vegetarian, boolean is_celiac, String category, boolean take_away, boolean photoUploaded);
    }

    @Override
    public void onAttach(Activity a) {
        super.onAttach(a);
        // This makes sure that the container activity has implemented the callback interface. If not, it throws an exception
        try {
            Log.d("aaa", "passed in fragment main info2");
            dataPasser = (onMainInfoPass) a;
            if(dataPasser==null)
                Log.d("aaa", "datapasser is null2");
        } catch (ClassCastException e) {
            throw new ClassCastException(a.toString()
                    + " must implement OnMainInfoPass");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main_info_meal, container, false);

        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        //feed already present data and set behaviour
        ImageView image = (ImageView) rootView.findViewById(R.id.meal_photo);
        if(!getArguments().isEmpty()) {
            if (getArguments().getString("meal_photo") != null) {
                progressBar.setVisibility(View.VISIBLE);
                Glide.with(getContext())
                        .load(getArguments().getString("meal_photo")) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .error(R.drawable.no_image)
                        .into(image);
                photouri = getArguments().getString("meal_photo");
            }
        }
        //feed spinner3
        type1 = (Spinner) rootView.findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.meal_types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectedCategory1 = String.valueOf(type1.getSelectedItem());
        type1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory1 = String.valueOf(parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        type1.setAdapter(adapter);
        //feed spinner4
        type2 = (Spinner) rootView.findViewById(R.id.spinner4);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getActivity(),
                R.array.meal_types_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        type2.setAdapter(adapter2);
        selectedCategory2 = String.valueOf(type2.getSelectedItem());
        type2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory2 = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(!getArguments().isEmpty()) {
            if(getArguments().getBoolean("is_celiac")==true && getArguments().getBoolean("is_vegan")==true) {
                type1.setSelection(1);
                type2.setSelection(2);
            }
            else if(getArguments().getBoolean("is_celiac")==true && getArguments().getBoolean("is_vegetarian")==true) {
                type1.setSelection(1);
                type2.setSelection(3);
            }
            else if(getArguments().getBoolean("is_vegan")==true && getArguments().getBoolean("is_vegetarian")==true) {
                type1.setSelection(2);
                type2.setSelection(3);
            }
            else if(getArguments().getBoolean("is_celiac")==true && getArguments().getBoolean("is_vegan")==false && getArguments().getBoolean("is_vegetarian")==false) {
                type1.setSelection(1);
                type2.setSelection(0);
            }
            else if(getArguments().getBoolean("is_celiac")==false && getArguments().getBoolean("is_vegan")==true && getArguments().getBoolean("is_vegetarian")==false) {
                type1.setSelection(2);
                type2.setSelection(0);
            }
            else if(getArguments().getBoolean("is_celiac")==false && getArguments().getBoolean("is_vegan")==false && getArguments().getBoolean("is_vegetarian")==true) {
                type1.setSelection(3);
                type2.setSelection(0);
            }
            else {
                type1.setSelection(0);
                type2.setSelection(0);
            }
        }

        //feed spinner5
        category = (Spinner) rootView.findViewById(R.id.spinner5);
        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getActivity(),
                R.array.meal_categories_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        category.setAdapter(adapter3);
        selectedCategory3 = String.valueOf(category.getSelectedItem());
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory3 = String.valueOf(parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if(!getArguments().isEmpty()) {
            if(getArguments().getString("category")!=null){
                if(getArguments().getString("category").equals("Nessuno") || getArguments().getString("category").equals("None"))
                    category.setSelection(0);
                else if(getArguments().getString("category").equals("Antipasti") || getArguments().getString("category").equals("Starter"))
                    category.setSelection(1);
                else if(getArguments().getString("category").equals("Primo piatto") || getArguments().getString("category").equals("Primary dish"))
                    category.setSelection(2);
                else if(getArguments().getString("category").equals("Secondo piatto") || getArguments().getString("category").equals("Secondary dish"))
                    category.setSelection(3);
                else if(getArguments().getString("category").equals("Contorno") || getArguments().getString("category").equals("Contour"))
                    category.setSelection(4);
                else if(getArguments().getString("category").equals("Dolce") || getArguments().getString("category").equals("Sweet"))
                    category.setSelection(5);
                else if(getArguments().getString("category").equals("Frutta") || getArguments().getString("category").equals("Fruit"))
                    category.setSelection(6);
            }
        }

        //catch other changes and save them
        meal_name = (EditText) rootView.findViewById(R.id.edit_meal_name);
        meal_price = (EditText) rootView.findViewById(R.id.edit_meal_price);
        take_away = (CheckBox) rootView.findViewById(R.id.check_take_away);
        take_away.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is_take_away = take_away.isChecked();
            }
        });
        if(!getArguments().isEmpty()) {
            if(getArguments().getString("meal_name")!=null) {
                meal_name.setText(getArguments().getString("meal_name"));
            }
            if(getArguments().getDouble("meal_price")!=0) {
                meal_price.setText(String.valueOf(getArguments().getDouble("meal_price")));
            }
            if(getArguments().getBoolean("take_away")==false) {
                take_away.setChecked(false);
                is_take_away = false;
            } else {
                take_away.setChecked(true);
                is_take_away = true;
            }

        }

        Button button2 = (Button) rootView.findViewById(R.id.button_choose_photo2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //choose a photo
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        Button button1 = (Button) rootView.findViewById(R.id.button_take_photo2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //take the photo
                PermissionUtil.checkWritePermission(getActivity(), REQUEST_WRITE_STORAGE);
                dispatchTakePictureIntent();
            }
        });

        return rootView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(getActivity(), "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //take a photo result
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == -1) {
            //view photo
            Log.d("aaa", "BREAK2");
            ImageView image = (ImageView) rootView.findViewById(R.id.meal_photo);
            setPic();
            //add photo to gallery
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            if(photouri!=null) {
                File f = new File(photouri); //here is passed the mCurrentPhotoPath
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);
                //photouri = contentUri.toString();
                Log.d("aaa", "BREAK3"+contentUri.toString());
                Log.d("aaa", "BREAK4" + Uri.parse(photouri));
                Glide.with(getContext())
                        .load(photouri) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.blank_profile)
                        .into(image);
                photoUploaded = true;
                //photouri = (contentUri.toString()); // ***MAYBE***
            }
        }
        //choose a photo result
        if (requestCode == PICK_IMAGE && resultCode == -1) {
            Uri imageUri = data.getData();
            //photouri = imageUri.toString();
            InputStream imageStream = null;
            try {
                imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                ImageView meal_photo = (ImageView) rootView.findViewById(R.id.meal_photo);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                Glide.with(getContext())
                        .load(getImageUri(getContext(), bitmap)) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(meal_photo);
                photouri = saveToInternalStorage(bitmap);
                photoUploaded = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (imageStream != null) {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        // Ignore the exception
                    }
                }
            }
            photouri = (photouri);
            /*
            SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
            SharedPreferences.Editor edit = userDetails.edit();
            edit.putString(restaurant_id, photouri);
            //I can not save the photo, but i could save its URI
            edit.commit();
            */
        }
        /*
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString(restaurant_id, photouri);
        //I can not save the photo, but i could save its URI
        edit.commit();
        */
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private String saveToInternalStorage(Bitmap bitmapImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath= new File(directory, "profile.png");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
        return myPath.getAbsolutePath();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("aaa", "BREAK1");
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPic() {
        ImageView mImageView = (ImageView) rootView.findViewById(R.id.meal_photo);
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photouri, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        // Determine how much to scale down the image
        int scaleFactor = 1;
        if(targetW!=0 && targetH!=0) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }
        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(photouri, bmOptions);
        if(bitmap!=null)
            Glide.with(getContext())
                    .load(getImageUri(getContext(), bitmap)) //"http://nuuneoi.com/uploads/source/playstore/cover.jpg"
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(mImageView);
        /*
        SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
        SharedPreferences.Editor edit = userDetails.edit();
        edit.putString("photouri", photouri);
        //I can not save the photo, but i could save its URI
        edit.commit();
        */
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //import util or sql?
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        photouri = image.getAbsolutePath();
        return image;
    }
}


