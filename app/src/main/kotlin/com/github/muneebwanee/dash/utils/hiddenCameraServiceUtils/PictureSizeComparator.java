package com.github.muneebwanee.dash.utils.hiddenCameraServiceUtils;

import android.hardware.Camera;
import java.util.Comparator;

/**
 * Created by muneebwanee on 15/12/20.
 */
class PictureSizeComparator implements Comparator<Camera.Size> {
    // Used for sorting in ascending order of
    // roll name
    public int compare(Camera.Size a, Camera.Size b) {
        return (b.height * b.width) - (a.height * a.width);
    }
}
