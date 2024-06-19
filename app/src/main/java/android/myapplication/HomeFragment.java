package android.myapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.viewpager2.widget.ViewPager2;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private ImageView previousButton, nextButton;
    private int[] adImages = {
            R.drawable.adver1, // Replace with your actual drawable resource IDs
            R.drawable.adver2,
            R.drawable.adver3,
            R.drawable.adver4,
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        viewPager2 = view.findViewById(R.id.ad_background);
        AdImageAdapter adapter = new AdImageAdapter(getContext(), adImages);
        viewPager2.setAdapter(adapter);

        previousButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);

        // Handle previous button click
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager2.getCurrentItem();
                if (currentItem > 0) {
                    viewPager2.setCurrentItem(currentItem - 1, true);
                }
            }
        });

        // Handle next button click
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager2.getCurrentItem();
                if (currentItem < adImages.length - 1) {
                    viewPager2.setCurrentItem(currentItem + 1, true);
                }
            }
        });

        // Auto-scroll functionality
        autoScrollImages();

        return view;
    }

    private void autoScrollImages() {
        final int scrollInterval = 3000; // 3 seconds
        viewPager2.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % adImages.length;
                viewPager2.setCurrentItem(nextItem, true);
                viewPager2.postDelayed(this, scrollInterval);
            }
        }, scrollInterval);
    }
}
