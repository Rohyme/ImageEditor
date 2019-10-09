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
import technivance.github.io.rohyme.editimage.view.SaturationView;
import technivance.github.io.rohyme.editimage.view.imagezoom.ImageViewTouchBase;

public class SaturationFragment extends BaseEditFragment {

    public static final int INDEX = ModuleConfig.INDEX_CONTRAST;
    private static final int INITIAL_SATURATION = 100;
    public static final String TAG = SaturationFragment.class.getName();
    private SaturationView mSaturationView;
    private SeekBar mSeekBar;

    public static SaturationFragment newInstance() {
        return new SaturationFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mSaturationView =null;
        mSeekBar =null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_image_saturation, null);

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

        this.mSaturationView = ensureEditActivity().saturationView;
        mBackToMenu.setOnClickListener(new SaturationFragment.BackToMenuClick());
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float value = progress - (seekBar.getMax() / 2);
                ensureEditActivity().saturationView.setSaturation(value / 10f);
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
        ensureEditActivity().mode = EditImageActivity.MODE_SATURATION;
        ensureEditActivity().mainImage.setImageBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        ensureEditActivity().mainImage.setVisibility(View.GONE);

        ensureEditActivity().saturationView.setImageBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().saturationView.setVisibility(View.VISIBLE);
        initView();
        ensureEditActivity().bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        ensureEditActivity().mode = EditImageActivity.MODE_NONE;
        ensureEditActivity().bottomGallery.setCurrentItem(0);
        ensureEditActivity().mainImage.setVisibility(View.VISIBLE);
        ensureEditActivity().saturationView.setVisibility(View.GONE);
        ensureEditActivity().bannerFlipper.showPrevious();
        ensureEditActivity().saturationView.setSaturation(INITIAL_SATURATION);
    }

    public void applySaturation() {
        if (mSeekBar.getProgress() == mSeekBar.getMax()) {
            backToMain();
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) mSaturationView.getDrawable()).getBitmap();
        ensureEditActivity().changeMainBitmap(Utils.saturationBitmap(bitmap, mSaturationView.getSaturation()), true);
        backToMain();
    }

    private void initView() {
        mSeekBar.setProgress(mSeekBar.getMax());
    }

    private final class BackToMenuClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }
}
