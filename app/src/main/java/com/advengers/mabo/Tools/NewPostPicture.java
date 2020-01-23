package com.advengers.mabo.Tools;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by priyadarshniau on 10/5/2017.
 */
public class NewPostPicture {

    public boolean showDeletete, isThumbnail;
    public Bitmap bitmap;
    public View.OnClickListener onClickDelete;


    public NewPostPicture(Bitmap b, View.OnClickListener onDelete, boolean showDeletete) {
        bitmap = b;
        onClickDelete = onDelete;
        this.showDeletete = showDeletete;
    }

    public NewPostPicture(Bitmap b, View.OnClickListener onDelete, boolean showDeletete, boolean isThumbnail) {
        bitmap = b;
        onClickDelete = onDelete;
        this.showDeletete = showDeletete;
        this.isThumbnail = isThumbnail;

    }
}
