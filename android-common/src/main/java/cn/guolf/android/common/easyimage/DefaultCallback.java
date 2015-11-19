package cn.guolf.android.common.easyimage;

import java.io.File;

public class DefaultCallback implements EasyImage.Callbacks {

    @Override
    public void onImagePickerError(Exception e, EasyImage.ImageSource source) {
    }

    @Override
    public void onImagePicked(File imageFile, EasyImage.ImageSource source) {
    }

    @Override
    public void onCanceled(EasyImage.ImageSource source) {
    }
}
