package ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.zakir.classy.R;
import com.zakir.classy.ShowRoutineActivity;

public class RoutineFragment extends Fragment implements View.OnClickListener {
    private CardView mon, tue, wed, thu,fri, sat;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_routine, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mon = view.findViewById(R.id.MondayCard_routine);
        tue = view.findViewById(R.id.TuesdayCard_routine);
        wed = view.findViewById(R.id.WednesdayCard_routine);
        thu = view.findViewById(R.id.ThursdayCard_routine);
        fri = view.findViewById(R.id.FridayCard_routine);
        sat = view.findViewById(R.id.SaturdayCard_routine);
        mon.setOnClickListener(this);
        tue.setOnClickListener(this);
        wed.setOnClickListener(this);
        thu.setOnClickListener(this);
        fri.setOnClickListener(this);
        sat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), ShowRoutineActivity.class);
        switch (v.getId()){
            case R.id.MondayCard_routine:
                intent.putExtra("Day","Monday");
                startActivity(intent);
                break;
            case R.id.TuesdayCard_routine :
                intent.putExtra("Day","Tuesday");
                startActivity(intent);
                // finish();
                break;
            case R.id.WednesdayCard_routine :
                intent.putExtra("Day","Wednesday");
                startActivity(intent);
                //   finish();
                break;
            case R.id.ThursdayCard_routine :
                intent.putExtra("Day","Thursday");
                startActivity(intent);
                //  finish();
                break;
            case R.id.FridayCard_routine :
                intent.putExtra("Day","Friday");
                startActivity(intent);
                //  finish();
                break;
            case R.id.SaturdayCard_routine :
                intent.putExtra("Day","Saturday");
                startActivity(intent);
                //  finish();
                break;
        }

    }
}