package com.example.homemade_guardian_beta.Photo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.homemade_guardian_beta.R;
import com.example.homemade_guardian_beta.Photo.entity.Photo;
import com.example.homemade_guardian_beta.Photo.event.OnItemCheckListener;
import com.example.homemade_guardian_beta.Photo.fragment.ImagePagerFragment;
import com.example.homemade_guardian_beta.Photo.fragment.PhotoPickerFragment;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_LONG;

public class PhotoPickerActivity extends AppCompatActivity {

  private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 7;
  private final int MY_PERMISSIONS_REQUEST_CAMERA = 8;

  private PhotoPickerFragment pickerFragment;
  private ImagePagerFragment imagePagerFragment;

  public final static String EXTRA_MAX_COUNT     = "MAX_COUNT";
  public final static String EXTRA_SHOW_CAMERA   = "SHOW_CAMERA";
  public final static String EXTRA_SHOW_GIF      = "SHOW_GIF";
  public final static String EXTRA_CHECK_BOX_ONLY       = "CHECK_BOX_ONLY";
  public final static String KEY_SELECTED_PHOTOS        = "SELECTED_PHOTOS";
  public final static String EXTRA_MAX_GRIDE_ITEM_COUNT = "MAX_GRIDE_IMAGE_COUNT";
  private boolean showCamera = true;


  private MenuItem menuDoneItem;

  public final static int DEFAULT_MAX_COUNT = 9;
  public final static int DEFAULT_MAX_GRIDE_ITEM_COUNT = 3;

  private int maxCount = DEFAULT_MAX_COUNT;
  public int maxGrideItemCount = DEFAULT_MAX_GRIDE_ITEM_COUNT;
  public boolean isCheckBoxOnly = false;

  /** to prevent multiple calls to inflate menu */
  private boolean menuIsInflated = false;
  private boolean showGif = false;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.util_activity_photo_picker);
    checkExternalStoragePermission();
  }

  @Override
  protected void onResume() {
    super.onResume();

  }

  private void checkExternalStoragePermission(){
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
      }
    }else {
      checkCameraPermission();
    }
  }

  private void checkCameraPermission(){
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
      }
    }else {
      init();
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    switch (requestCode) {
      case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          checkCameraPermission();
        } else {
          Toast.makeText(PhotoPickerActivity.this , "You do not have read permissions." , Toast.LENGTH_LONG ).show();
          finish();
        }
        return;
      }

      case MY_PERMISSIONS_REQUEST_CAMERA: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          init();
        } else {
          Toast.makeText(PhotoPickerActivity.this , "There is no camera permissions." , Toast.LENGTH_LONG ).show();
        }
        return;
      }
    }
  }

  private void init(){
    showCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, true);
    showGif    = getIntent().getBooleanExtra(EXTRA_SHOW_GIF, false);
    isCheckBoxOnly = getIntent().getBooleanExtra(EXTRA_CHECK_BOX_ONLY, false);
    maxGrideItemCount = getIntent().getIntExtra(EXTRA_MAX_GRIDE_ITEM_COUNT , DEFAULT_MAX_GRIDE_ITEM_COUNT);

    setShowGif(showGif);
    Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
    mToolbar.setTitle(R.string.y_photopicker_image_select_title);
    setSupportActionBar(mToolbar);

    ActionBar actionBar = getSupportActionBar();

    assert actionBar != null;
    actionBar.setDisplayHomeAsUpEnabled(true);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      actionBar.setElevation(25);
    }
    maxCount = getIntent().getIntExtra(EXTRA_MAX_COUNT, DEFAULT_MAX_COUNT);
    setPickerFragment();
  }

  private void setPickerFragment(){

    if(pickerFragment == null){
      pickerFragment = (PhotoPickerFragment) getSupportFragmentManager().findFragmentById(R.id.photoPickerFragment);

      pickerFragment.getPhotoGridAdapter().setShowCamera(showCamera);
      pickerFragment.getPhotoGridAdapter().setOnItemCheckListener(new OnItemCheckListener() {
        @Override public boolean OnItemCheck(int position, Photo photo, final boolean isCheck, int selectedItemCount) {

          int total = selectedItemCount + (isCheck ? -1 : 1);

          menuDoneItem.setEnabled(total > 0);

          if (maxCount <= 1) {
            List<Photo> photos = pickerFragment.getPhotoGridAdapter().getSelectedPhotos();
            if (!photos.contains(photo)) {
              photos.clear();
              pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
            }
            return true;
          }

          if (total > maxCount) {
            Toast.makeText(getActivity(), getString(R.string.y_photopicker_over_max_count_tips, maxCount),
                    LENGTH_LONG).show();
            return false;
          }
          menuDoneItem.setTitle(getString(R.string.y_photopicker_done_with_count, total, maxCount));
          return true;
        }
      });
    }else{
      pickerFragment.getPhotoGridAdapter().notifyDataSetChanged();
    }
  }

  /**
   * Overriding this method allows us to run our exit animation first, then exiting
   * the activity when it complete.
   */
  @Override public void onBackPressed() {
    if (imagePagerFragment != null && imagePagerFragment.isVisible()) {
      imagePagerFragment.runExitAnimation(new Runnable() {
        public void run() {
          if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
          }
        }
      });
    } else {
      super.onBackPressed();
    }
  }

   // 앨범 화면에서 사진을 클릭하여 크게 볼때
  public void addImagePagerFragment(ImagePagerFragment imagePagerFragment) {
    this.imagePagerFragment = imagePagerFragment;
    getSupportFragmentManager()
        .beginTransaction()
        .replace(R.id.container, this.imagePagerFragment)
        .addToBackStack(null)
        .commit();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    if (!menuIsInflated) {
      getMenuInflater().inflate(R.menu.menu_picker, menu);
      menuDoneItem = menu.findItem(R.id.done);
      menuDoneItem.setEnabled(false);
      menuIsInflated = true;
      return true;
    }
    return false;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      super.onBackPressed();
      return true;
    }
    if (item.getItemId() == R.id.done) {
      Intent intent = new Intent();
      ArrayList<String> selectedPhotos = pickerFragment.getPhotoGridAdapter().getSelectedPhotoPaths();
      intent.putStringArrayListExtra(KEY_SELECTED_PHOTOS, selectedPhotos);
      setResult(RESULT_OK, intent);
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public PhotoPickerActivity getActivity() {
    return this;
  }

  public boolean isShowGif() {
    return showGif;
  }

  public void setShowGif(boolean showGif) {
    this.showGif = showGif;
  }

}
