package com.example.collegeeventpass;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private ListView listViewEvents;
    private HashMap<String, List<String>> eventsMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerCategory = findViewById(R.id.spinnerCategory);
        listViewEvents = findViewById(R.id.listViewEvents);

        // Setup Data
        eventsMap = new HashMap<>();
        
        List<String> techEvents = new ArrayList<>();
        techEvents.add("Hackathon");
        techEvents.add("Robo Wars");
        techEvents.add("Code Debugging");
        eventsMap.put("Technical", techEvents);

        List<String> culturalEvents = new ArrayList<>();
        culturalEvents.add("Dance Battle");
        culturalEvents.add("Singing Idol");
        culturalEvents.add("Fashion Show");
        eventsMap.put("Cultural", culturalEvents);

        List<String> sportsEvents = new ArrayList<>();
        sportsEvents.add("Cricket Tournament");
        sportsEvents.add("Football Relay");
        sportsEvents.add("Chess Championship");
        eventsMap.put("Sports", sportsEvents);

        String[] categories = {"Technical", "Cultural", "Sports"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        spinnerCategory.setAdapter(spinnerAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = categories[position];
                List<String> events = eventsMap.get(selectedCategory);
                ArrayAdapter<String> listAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, events);
                listViewEvents.setAdapter(listAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        listViewEvents.setOnItemClickListener((parent, view, position, id) -> {
            String selectedEvent = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            intent.putExtra("EVENT_NAME", selectedEvent);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "About");
        menu.add(0, 2, 0, "Help");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Toast.makeText(this, "College Event Pass App v1.0", Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == 2) {
            Toast.makeText(this, "Select a category and click on an event to register.", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
