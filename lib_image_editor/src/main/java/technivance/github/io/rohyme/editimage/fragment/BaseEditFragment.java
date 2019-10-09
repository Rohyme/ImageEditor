package technivance.github.io.rohyme.editimage.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import technivance.github.io.rohyme.editimage.EditImageActivity;
import technivance.github.io.rohyme.editimage.view.CustomPaintView;

public abstract class BaseEditFragment extends Fragment {
    protected EditImageActivity activity;

    protected EditImageActivity ensureEditActivity(){
        if(activity == null){
            activity = (EditImageActivity) getActivity();
        }
        return activity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ensureEditActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        ensureEditActivity();
    }

    public abstract void onShow();

    public abstract void backToMain();

    public CustomPaintView getCustomPaintView(){
        return ensureEditActivity().getCustomPaintView();
    }
}
