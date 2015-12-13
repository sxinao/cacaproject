package fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caca.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostingFragment extends Fragment {


    public PostingFragment() {
        // Required empty public constructor
    }

    PostInfoFragment postInfo = new PostInfoFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View postingView = inflater.inflate(R.layout.fragment_posting, container, false);

        postingView.findViewById(R.id.postingBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_container, postInfo);
                fragmentTransaction.commit();
            }
        });

        return postingView;
    }


}
