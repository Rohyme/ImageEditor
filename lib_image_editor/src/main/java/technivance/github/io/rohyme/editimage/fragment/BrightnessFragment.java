package technivance.github.io.rohyme.editimage.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import technivance.github.io.rohyme.R;
import technivance.github.io.rohyme.editimage.EditImageActivity;
import technivance.github.io.rohyme.editimage.ModuleConfig;
import technivance.github.io.rohyme.editimage.utils.Utils;
import technivance.github.io.rohyme.editimage.view.BrightnessView;
import technivance.github.io.rohyme.editimage.view.imagezoom.ImageViewTouchBase;


public class BrightnessFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_BRIGHTNESS;
    public static final String TAG = BrightnessFragment.class.getName();

    private static final int INITIAL_BRIGHTNESS = 0;

    private BrightnessView mBrightnessView;
    private SeekBar mSeekBar;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBrightnessView =null;
        mSeekBar = null;
    }

    public static BrightnessFragment newInstance() {
        return new BrightnessFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_brightness, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSeekBar = view.findViewById(R.id.seekBar);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View mBackToMenu = getView().findViewById(R.id.back_to_main);

        this.mBrightnessView = ensureEditActivity().brightnessView;
        mBackToMenu.setOnClickListener(new BrightnessFragment.BackToMenuClick());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                ensureEditActivity().brightnessView.setBright(value / 10f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initView();
    }

    @Override
    public void onShow() {
        ensureEditActivity().mode = EditImageActivity.MODE_BRIGHTNESS;
        ensureEditActivity().mainImage.setImageBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        ensureEditActivity().mainImage.setVisibility(View.GONE);

        ensureEditActivity().brightnessView.setImageBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().brightnessView.setVisibility(View.VISIBLE);
        initView();
        ensureEditActivity().bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        ensureEditActivity().mode = EditImageActivity.MODE_NONE;
        ensureEditActivity().bottomGallery.setCurrentItem(0);
        ensureEditActivity().mainImage.setVisibility(View.VISIBLE);
        ensureEditActivity().brightnessView.setVisibility(View.GONE);
        ensureEditActivity().bannerFlipper.showPrevious();
        ensureEditActivity().brightnessView.setBright(INITIAL_BRIGHTNESS);
    }

    public void applyBrightness() {
        if (mSeekBar.getProgress() == mSeekBar.getMax() / 2) {
            backToMain();
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) mBrightnessView.getDrawable()).getBitmap();
        ensureEditActivity().changeMainBitmap(Utils.brightBitmap(bitmap, mBrightnessView.getBright()), true);
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax() / 2);
    }


    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
