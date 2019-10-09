package technivance.github.io.rohyme.editimage.fragment;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import technivance.github.io.rohyme.BaseActivity;
import technivance.github.io.rohyme.R;
import technivance.github.io.rohyme.editimage.EditImageActivity;
import technivance.github.io.rohyme.editimage.ModuleConfig;
import technivance.github.io.rohyme.editimage.adapter.FilterAdapter;
import technivance.github.io.rohyme.editimage.fliter.PhotoProcessing;
import technivance.github.io.rohyme.editimage.view.imagezoom.ImageViewTouchBase;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FilterListFragment extends BaseEditFragment {
    public static final int INDEX = ModuleConfig.INDEX_FILTER;
    public static final int NULL_FILTER_INDEX = 0;
    public static final String TAG = FilterListFragment.class.getName();

    private Bitmap filterBitmap;
    private Bitmap currentBitmap;
    private Dialog loadingDialog;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static FilterListFragment newInstance() {
        return new FilterListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         return inflater.inflate(R.layout.fragment_edit_image_fliter, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingDialog = BaseActivity.getLoadingDialog(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_loading,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RecyclerView filterRecyclerView = getView().findViewById(R.id.filter_recycler);
        FilterAdapter filterAdapter = new FilterAdapter(this, getContext());
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        filterRecyclerView.setLayoutManager(layoutManager);
        filterRecyclerView.setAdapter(filterAdapter);

        View backBtn = getView().findViewById(R.id.back_to_main);
        backBtn.setOnClickListener(v -> backToMain());
    }

    @Override
    public void onShow() {
        ensureEditActivity().mode = EditImageActivity.MODE_FILTER;
        ensureEditActivity().filterListFragment.setCurrentBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().mainImage.setImageBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().mainImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        ensureEditActivity().mainImage.setScaleEnabled(false);
        ensureEditActivity().bannerFlipper.showNext();
    }

    @Override
    public void backToMain() {
        currentBitmap = ensureEditActivity().getMainBit();
        filterBitmap = null;
        ensureEditActivity().mainImage.setImageBitmap(ensureEditActivity().getMainBit());
        ensureEditActivity().mode = EditImageActivity.MODE_NONE;
        ensureEditActivity().bottomGallery.setCurrentItem(0);
        ensureEditActivity().mainImage.setScaleEnabled(true);
        ensureEditActivity().bannerFlipper.showPrevious();
    }

    public void applyFilterImage() {
        if (currentBitmap == ensureEditActivity().getMainBit()) {
            backToMain();
        } else {
            ensureEditActivity().changeMainBitmap(filterBitmap, true);
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
        tryRecycleFilterBitmap();
        compositeDisposable.dispose();
        loadingDialog =null;
        super.onDestroy();
    }

    private void tryRecycleFilterBitmap() {
        if (filterBitmap != null && (!filterBitmap.isRecycled())) {
            filterBitmap.recycle();
        }
    }

    public void enableFilter(int filterIndex) {
        if (filterIndex == NULL_FILTER_INDEX) {
            ensureEditActivity().mainImage.setImageBitmap(ensureEditActivity().getMainBit());
            currentBitmap = ensureEditActivity().getMainBit();
            return;
        }

        compositeDisposable.clear();

        Disposable applyFilterDisposable = applyFilter(filterIndex)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(subscriber -> loadingDialog.show())
                .doFinally(() -> loadingDialog.dismiss())
                .subscribe(
                        this::updatePreviewWithFilter,
                        e -> showSaveErrorToast()
                );

        compositeDisposable.add(applyFilterDisposable);
    }

    private void updatePreviewWithFilter(Bitmap bitmapWithFilter) {
        if (bitmapWithFilter == null) return;

        if (filterBitmap != null && (!filterBitmap.isRecycled())) {
            filterBitmap.recycle();
        }

        filterBitmap = bitmapWithFilter;
        ensureEditActivity().mainImage.setImageBitmap(filterBitmap);
        currentBitmap = filterBitmap;
    }

    private void showSaveErrorToast() {
        Toast.makeText(getActivity(), R.string.iamutkarshtiwari_github_io_ananas_save_error, Toast.LENGTH_SHORT).show();
    }

    private Single<Bitmap> applyFilter(int filterIndex) {
        return Single.fromCallable(() -> {

            Bitmap srcBitmap = Bitmap.createBitmap(ensureEditActivity().getMainBit().copy(
                    Bitmap.Config.RGB_565, true));
            return PhotoProcessing.filterPhoto(srcBitmap, filterIndex);
        });
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }
}
