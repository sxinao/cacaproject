package userpage;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caca.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SelfFragment extends Fragment {

    public SelfFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View self_view = inflater.inflate(R.layout.fragment_self, container, false);



        return self_view;
    }


}
