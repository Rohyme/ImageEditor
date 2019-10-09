package technivance.github.io.rohyme.editimage.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import technivance.github.io.rohyme.BaseActivity;
import technivance.github.io.rohyme.R;
import technivance.github.io.rohyme.editimage.EditImageActivity;
import technivance.github.io.rohyme.editimage.ModuleConfig;
import technivance.github.io.rohyme.editimage.view.RotateImageView;
import technivance.github.io.rohyme.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class RotateFragment extends BaseEditFragment implements OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_ROTATE;
    public static final String TAG = RotateFragment.class.getName();

    private static final int RIGHT_ANGLE = 90;

    private Dialog loadingDialog;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static RotateFragment newInstance() {
        return new RotateFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       return inflater.inflate(R.layout.fragment_edit_image_rotate, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = BaseActivity.getLoadingDialog(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_loading,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setClickListeners();
    }

    private void setClickListeners() {
        View backToMenu = getView().findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        ImageView rotateLeft = getView().findViewById(R.id.rotate_left);
        ImageView rotateRight = getView().findViewById(R.id.rotate_right);
        rotateLeft.setOnClickListener(this);
        rotateRight.setOnClickListener(this);
    }

    @Override
    public void onShow() {
        ensureEditActivity().mode = EditImageActivity.MODE_ROTATE;
        ensureEditActivity().mainImage.setImageBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        ensureEditActivity().mainImage.setVisibility(View.GONE);

        ensureEditActivity().rotatePanel.addBit(ensureEditActivity().getMainBit(),
                ensureEditActivity().mainImage.getBitmapRect());

        ensureEditActivity().rotatePanel.reset();
        ensureEditActivity().rotatePanel.setVisibility(View.VISIBLE);
        ensureEditActivity().bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        ensureEditActivity().mode = EditImageActivity.MODE_NONE;
        ensureEditActivity().bottomGallery.setCurrentItem(0);
        ensureEditActivity().mainImage.setVisibility(View.VISIBLE);
        ensureEditActivity().rotatePanel.setVisibility(View.GONE);
        ensureEditActivity().bannerFlipper.showPrevious();
    }

    @Override
    public void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        loadingDialog =null ;
        super.onDestroy();
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rotate_left) {
            int updatedAngle = ensureEditActivity().rotatePanel.getRotateAngle() - RIGHT_ANGLE;
            ensureEditActivity().rotatePanel.rotateImage(updatedAngle);
        } else if (id == R.id.rotate_right) {
            int updatedAngle = ensureEditActivity().rotatePanel.getRotateAngle() + RIGHT_ANGLE;
            ensureEditActivity().rotatePanel.rotateImage(updatedAngle);
        }
    }

    public void applyRotateImage() {
        if (ensureEditActivity().rotatePanel.getRotateAngle() == 0 || (ensureEditActivity().rotatePanel.getRotateAngle() % 360) == 0) {
            backToMain();
        } else {
            compositeDisposable.clear();
            Disposable applyRotationDisposable = applyRotation(ensureEditActivity().getMainBit())
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(subscriber -> loadingDialog.show())
                    .doFinally(() -> loadingDialog.dismiss())
                    .subscribe(processedBitmap -> {
                        if (processedBitmap == null)
                            return;

                        applyAndExit(processedBitmap);
                    }, e -> {
                        // Do nothing on error
                    });

            compositeDisposable.add(applyRotationDisposable);
        }
    }

    private Single<Bitmap> applyRotation(Bitmap sourceBitmap) {
        return Single.fromCallable(() -> {
            RectF imageRect = ensureEditActivity().rotatePanel.getImageNewRect();
            Bitmap resultBitmap = Bitmap.createBitmap((int) imageRect.width(),
                    (int) imageRect.height(), Bitmap.Config.ARGB_4444);

            Canvas canvas = new Canvas(resultBitmap);
            int w = sourceBitmap.getWidth() >> 1;
            int h = sourceBitmap.getHeight() >> 1;

            float centerX = imageRect.width() / 2;
            float centerY = imageRect.height() / 2;

            float left = centerX - w;
            float top = centerY - h;

            RectF destinationRect = new RectF(left, top, left + sourceBitmap.getWidth(), top
                    + sourceBitmap.getHeight());
            canvas.save();
            canvas.rotate(
                    ensureEditActivity().rotatePanel.getRotateAngle(),
                    imageRect.width() / 2,
                    imageRect.height() / 2
            );

            canvas.drawBitmap(
                    sourceBitmap,
                    new Rect(
                            0,
                            0,
                            sourceBitmap.getWidth(),
                            sourceBitmap.getHeight()),
                    destinationRect,
                    null);
            canvas.restore();
            return resultBitmap;
        });
    }

    private void applyAndExit(Bitmap resultBitmap) {
        ensureEditActivity().changeMainBitmap(resultBitmap, true);
        backToMain();
    }
}
