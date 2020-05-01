package com.example.patryk.work_time_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.patryk.work_time_app.adapters.HistoryAdapter;
import com.example.patryk.work_time_app.fragments.FilterDialogFragment;
import com.example.patryk.work_time_app.fragments.HistoryFragment;
import com.example.patryk.work_time_app.fragments.SettingsFragment;
import com.example.patryk.work_time_app.fragments.TimerFragment;

import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogFragmentListener {

    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        TimerFragment timerFragment = new TimerFragment();

        fragmentTransaction.add(R.id.fragmentContainer, timerFragment);
        fragmentTransaction.commit();

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        if (preferences.getLong(getString(R.string.init_date), -1) == -1) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putLong(getString(R.string.init_date), new GregorianCalendar().getTimeInMillis());
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        FragmentTransaction fragmentTransaction;

        switch (item.getItemId()) {
            case R.id.action_timer:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                TimerFragment timerFragment = new TimerFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, timerFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;

            case R.id.action_history:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                historyFragment = new HistoryFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, historyFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;

            case R.id.action_settings:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentTransaction.replace(R.id.fragmentContainer, settingsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onApplyButtonClick(DialogFragment dialogFragment, long date1, long date2) {
        Bundle bundle = new Bundle();
        bundle.putLong("from", date1);
        bundle.putLong("to", date2);
        historyFragment.setArguments(bundle);
        historyFragment.update(bundle);
//        dialogFragment.dismiss();
    }


}


































