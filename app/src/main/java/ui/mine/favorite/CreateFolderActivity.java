package ui.mine.favorite;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.naojianghh.bilibili3.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import Data.FavoriteDatabaseHelper;
import base.BaseActivity;
import ui.detailed_video.FavoriteDialogFragment;

public class CreateFolderActivity extends BaseActivity {
    private Button buttonReturn;
    private Button buttonFinish;
    private EditText editText;
    private FavoriteDatabaseHelper databaseHelper;

    private static final int REQUEST_CAMERA = 100;
    private static final int REQUEST_GALLERY = 101;
    private static final int REQUEST_PERMISSIONS = 102;

    private ImageView ivSelectedImage;
    private String currentPhotoPath;
    private String selectedImageIdentifier;
    private Button btnSelectImage;
    // 使用BottomSheetDialog替代PopupWindow
    private BottomSheetDialog bottomSheetDialog;

    @Override
    protected void initViews() {
        databaseHelper = new FavoriteDatabaseHelper(this);
        buttonReturn = findViewById(R.id.create_folder_return);
        buttonReturn.setOnClickListener(v -> finish());

        editText = findViewById(R.id.create_folder_new_folder_name);
        buttonFinish = findViewById(R.id.create_folder_finish);
        buttonFinish.setOnClickListener(v -> {
            String newFolderName = editText.getText().toString().trim();
            if(!newFolderName.isEmpty()){
                databaseHelper.addFolder(newFolderName);
                if (selectedImageIdentifier != null) {
                    databaseHelper.setFolderCover(newFolderName, selectedImageIdentifier);
                }
                Intent intent = new Intent("MY_BROADCAST_ACTION");
                intent.putExtra("folderName",newFolderName);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        btnSelectImage = findViewById(R.id.setPicture);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);

        btnSelectImage.setOnClickListener(v -> showImagePickerBottomSheet());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_createfolder;
    }

    // 显示从底部弹出的BottomSheetDialog
    private void showImagePickerBottomSheet() {
        // 初始化BottomSheetDialog
        bottomSheetDialog = new BottomSheetDialog(this);
        // 加载布局
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_picture, null);
        bottomSheetDialog.setContentView(view);

        // 设置点击事件
        view.findViewById(R.id.btn_camera).setOnClickListener(v -> {
            checkPermissionsAndOpenCamera();
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btn_gallery).setOnClickListener(v -> {
            openGallery();
            bottomSheetDialog.dismiss();
        });

        // 显示对话框
        bottomSheetDialog.show();
    }

    private void checkPermissionsAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.naojianghh.bilibili3",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            ivSelectedImage.setImageBitmap(bitmap);
            selectedImageIdentifier = currentPhotoPath;
        }
        else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                ivSelectedImage.setImageURI(selectedImageUri);

                try {
                    int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
                    selectedImageIdentifier = selectedImageUri.toString() + "|with_permission";
                } catch (SecurityException e) {
                    Log.e("PermissionError", "无法获取持久化权限: " + e.getMessage());
                    selectedImageIdentifier = selectedImageUri.toString();
                    Toast.makeText(this, "无法永久访问该图片，仅本次有效", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "需要相机和存储权限才能使用该功能", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
