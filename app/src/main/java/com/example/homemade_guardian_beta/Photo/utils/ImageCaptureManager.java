package com.example.homemade_guardian_beta.Photo.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

//  PhotoPickerFragment에 나열되어 있는 이미지, 카메라 버튼 중에 카메라 버튼을 누르면 실행되는 이벤트이다.
//    (PhotoPickerFragment) -> ImageCaptureManager

public class ImageCaptureManager {

  private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
  public static final int REQUEST_TAKE_PHOTO = 1;
  private String MCurrentPhotoPath;
  private Context MContext;
  private boolean IsNativeCamera = false;
  public ImageCaptureManager(Context MContext) {
    this.MContext = MContext;
  }

  //찍은 이미지의 이름과 형식을 설정
  private File createImageFile() throws IOException {
    // Create an Image file name
    String TimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String ImageFileName = "JPEG_" + TimeStamp + "_";
    File StorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    if (!StorageDir.exists()) {
      if (!StorageDir.mkdir()) {
        throw new IOException();
      }
    }
    File Image = File.createTempFile(
        ImageFileName,  /* prefix */
        ".jpg",         /* suffix */
        StorageDir      /* directory */
    );
    // Save a file: path for use with ACTION_VIEW intents
    MCurrentPhotoPath = Image.getAbsolutePath();
    return Image;
  }

  public Intent dispatchTakePictureIntent() throws IOException {
    Intent TakePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

    if (TakePictureIntent.resolveActivity(MContext.getPackageManager()) != null) {
      ResolveInfo MInfo = MContext.getPackageManager().resolveActivity(TakePictureIntent, 0);
      TakePictureIntent.setComponent(new ComponentName(MInfo.activityInfo.packageName, MInfo.activityInfo.name));
      if(IsNativeCamera){
          TakePictureIntent.setAction(Intent.ACTION_MAIN);
          TakePictureIntent.addCategory(Intent.CATEGORY_LAUNCHER);
      }
      File PhotoFile = createImageFile();
      if (PhotoFile != null) {
        Uri URI = FileProvider.getUriForFile(MContext, "com.example.homemade_guardian_beta.provider", PhotoFile);
        TakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, URI);
      }
    }
    return TakePictureIntent;
  }

  //찍은 이미지를 디렉토리에 저장
  public void galleryAddPic() {
    String DcimPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    String TimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String ResultImageDri = DcimPath + "/"+TimeStamp+".jpg";
    copyFile(MCurrentPhotoPath, ResultImageDri );
    File TempImage = new File(MCurrentPhotoPath);
    File f = new File(ResultImageDri);
    Intent MediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
    MediaIntent.setData(Uri.fromFile(f));
    MContext.sendBroadcast(MediaIntent);
    if(TempImage.delete()){Log.d("Y-Photo-Picker","#### DELETE TEMP IMAGE");}
  }

  /**
   * 파일 복사
   * @param strSrc
   * @param save_file
   * @return
   */
  private boolean copyFile(String strSrc , String save_file){
    File CopyFile = new File(strSrc);
    boolean Result;
    if(CopyFile!=null&&CopyFile.exists()){
      try {
        FileInputStream Fis = new FileInputStream(CopyFile);
        FileOutputStream Newfos = new FileOutputStream(save_file);
        int Readcount=0;
        byte[] Buffer = new byte[1024];
        while((Readcount = Fis.read(Buffer,0,1024))!= -1){
          Newfos.write(Buffer,0,Readcount);
        }
        Newfos.close();
        Fis.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      Result = true;
    }else{
      Result = false;
    }
    return Result;
  }

  public String getCurrentPhotoPath() {
    return MCurrentPhotoPath;
  }

  public void onSaveInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null && MCurrentPhotoPath != null) {
      savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, MCurrentPhotoPath);
    }
  }

  public void onRestoreInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null && savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
      MCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
    }
  }
}
