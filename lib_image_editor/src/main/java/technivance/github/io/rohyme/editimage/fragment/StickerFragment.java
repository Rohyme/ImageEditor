package technivance.github.io.rohyme.editimage.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import technivance.github.io.rohyme.BaseActivity;
import technivance.github.io.rohyme.R;
import technivance.github.io.rohyme.editimage.EditImageActivity;
import technivance.github.io.rohyme.editimage.ModuleConfig;
import technivance.github.io.rohyme.editimage.adapter.StickerAdapter;
import technivance.github.io.rohyme.editimage.adapter.StickerTypeAdapter;
import technivance.github.io.rohyme.editimage.utils.Matrix3;
import technivance.github.io.rohyme.editimage.view.StickerItem;
import technivance.github.io.rohyme.editimage.view.StickerView;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StickerFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_STICKER;
    public static final String TAG = StickerFragment.class.getName();

    private ViewFlipper flipper;
    private StickerView stickerView;
    private StickerAdapter stickerAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Dialog loadingDialog;

    public static StickerFragment newInstance() {
        return new StickerFragment();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        flipper = null;
        stickerView =null;
        stickerAdapter = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_edit_image_sticker_type,
                null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = BaseActivity.getLoadingDialog(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_saving_image,
                false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.stickerView = ensureEditActivity().stickerView;
        flipper = getView().findViewById(R.id.flipper);
        flipper.setInAnimation(ensureEditActivity(), R.anim.in_bottom_to_top);
        flipper.setOutAnimation(ensureEditActivity(), R.anim.out_bottom_to_top);

        RecyclerView typeList = getView()
                .findViewById(R.id.stickers_type_list);
        typeList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ensureEditActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        typeList.setLayoutManager(mLayoutManager);
        typeList.setAdapter(new StickerTypeAdapter(this));

        RecyclerView stickerList = getView().findViewById(R.id.stickers_list);
        stickerList.setHasFixedSize(true);
        LinearLayoutManager stickerListLayoutManager = new LinearLayoutManager(
                ensureEditActivity());
        stickerListLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        stickerList.setLayoutManager(stickerListLayoutManager);
        stickerAdapter = new StickerAdapter(this);
        stickerList.setAdapter(stickerAdapter);

        View backToMenu = getView().findViewById(R.id.back_to_main);
        backToMenu.setOnClickListener(new BackToMenuClick());

        View backToType = getView().findViewById(R.id.back_to_type);
        backToType.setOnClickListener(v -> flipper.showPrevious());
    }

    @Override
    public void onShow() {
        ensureEditActivity().mode = EditImageActivity.MODE_STICKERS;
//        ensureEditActivity().stickerFragment.getStickerView().setVisibility(
//                View.VISIBLE);
        ensureEditActivity().bannerFlipper.showNext();
    }

    public void swipToStickerDetails(String path, int stickerCount) {
        stickerAdapter.addStickerImages(path, stickerCount);
        flipper.showNext();
    }

    public void selectedStickerItem(String path) {
        int imageKey = getResources().getIdentifier(path, "drawable", getContext().getPackageName());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageKey);
        stickerView.addBitImage(bitmap);
    }

    private StickerView getStickerView() {
        return stickerView;
    }

    private final class BackToMenuClick implements OnClickListener {
        @Override
        public void onClick(View v) {
            backToMain();
        }
    }

    @Override
    public void onPause() {
        compositeDisposable.clear();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        loadingDialog = null;
        super.onDestroy();
    }

    @Override
    public void backToMain() {
        ensureEditActivity().mode = EditImageActivity.MODE_NONE;
        ensureEditActivity().bottomGallery.setCurrentItem(0);
        stickerView.clear();
        stickerView.setVisibility(View.GONE);
        ensureEditActivity().bannerFlipper.showPrevious();
    }

    public void applyStickers() {
        compositeDisposable.clear();

        Disposable saveStickerDisposable = applyStickerToImage(ensureEditActivity().getMainBit())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> loadingDialog.show())
                .doFinally(() -> loadingDialog.dismiss())
                .subscribe(bitmap -> {
                    if (bitmap == null) {
                        return;
                    }

                    stickerView.clear();
                    ensureEditActivity().changeMainBitmap(bitmap, true);
                    backToMain();
                }, e -> {
                    Toast.makeText(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_save_error, Toast.LENGTH_SHORT).show();
                });

        compositeDisposable.add(saveStickerDisposable);
    }

    private Single<Bitmap> applyStickerToImage(Bitmap mainBitmap) {
        return Single.fromCallable(() -> {
            EditImageActivity context = (EditImageActivity) requireActivity();
            Matrix touchMatrix = context.mainImage.getImageViewMatrix();

            Bitmap resultBitmap = Bitmap.createBitmap(mainBitmap).copy(
                    Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(resultBitmap);

            float[] data = new float[9];
            touchMatrix.getValues(data);
            Matrix3 cal = new Matrix3(data);
            Matrix3 inverseMatrix = cal.inverseMatrix();
            Matrix m = new Matrix();
            m.setValues(inverseMatrix.getValues());
            handleImage(canvas, m);
            return resultBitmap;
        });
    }

    private void handleImage(Canvas canvas, Matrix m) {
        LinkedHashMap<Integer, StickerItem> addItems = stickerView.getBank();
        for (Integer id : addItems.keySet()) {
            StickerItem item = addItems.get(id);
            item.matrix.postConcat(m);
            canvas.drawBitmap(item.bitmap, item.matrix, null);
        }
    }
}
