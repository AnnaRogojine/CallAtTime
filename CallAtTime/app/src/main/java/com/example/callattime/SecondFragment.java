package com.example.callattime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import static com.example.callattime.utilities.CompareContent;
import static com.example.callattime.utilities.getTimeCallInMilli;
import static com.example.callattime.utilities.isNull;
import static com.example.callattime.utilities.isNullOrBlank;
import static com.example.callattime.utilities.isTimeInRange;

public class SecondFragment extends Fragment {
    private FragmentActivity _activity;
    private Context _context;
    final String SecondFragmentTag = "Add call";
    private static final int REQUEST_CALL = 1;
    private static final long FIVE_MINUTES = 300000;
    private String nameInput;
    private String callAtString;

    private Calendar calendarCallAt;

    private Gson gson = new Gson();
    private TextView callAtInput;
    private TimePickerDialog callAtDialog;

    private ContactsAvailability submittedContact;
    private List<ContactsAvailability> contactsAvailability = new ArrayList<ContactsAvailability>();
    private List<String> availableNames = new ArrayList<String>();
    private Handler handler = new Handler();

    ListView availableNamesDisplayView;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        _activity = getActivity();
        _context = getContext();

        // Extract the shared Contacts Availability
        assert _activity != null;
        SharedPreferences sharedPreferences = _activity.getSharedPreferences("ContactsInfo", Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("ContactsAvailability", "");

        // Save to local contactsAvailability the list from fist fragment
        Type collectionType = new TypeToken<Collection<ContactsAvailability>>(){}.getType();
        contactsAvailability = gson.fromJson(json, collectionType);

        setAutoCompleteTextView(view);

        availableNamesDisplayView = (ListView) view.findViewById(R.id.displayList);
        updateDisplayList();

        // Set the time picker
        callAtInput = view.findViewById(R.id.callAt);
        callAtInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAtDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendarCallAt = Calendar.getInstance();
                                calendarCallAt.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendarCallAt.set(Calendar.MINUTE, minute);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                                callAtString = format.format(calendarCallAt.getTime());
                                callAtInput.setText("From: " + callAtString);
                            }
                        }, 12, 00, true);
                callAtDialog.show();
            }
        });

        // Set Submit button
        Button submitButton = (Button) view.findViewById(R.id.addCallSubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Validate the inputs
                if(formValidation())
                {
                    // Person was chosen
                    // Extract the contact object
                    for (int i = 0; i < contactsAvailability.size(); i++)
                    {
                        if(CompareContent(contactsAvailability.get(i).getName(), nameInput))
                            submittedContact = contactsAvailability.get(i);
                    }

                    // Check if the time is valid
                    if(!isTimeInRange(submittedContact, calendarCallAt))
                        toastMessage("The selected time is not available.","red");
                    else
                    {
                        submittedContact.setCallAt(calendarCallAt);
                        updateDisplayList();
                        callAtTimeHandler();
                    }
                }
            }
        });
        // Inflate the layout for this fragment
        return view;
    }

    private void setAutoCompleteTextView(View view) {
        // Extract all the name on the contacts availability list
        for (int i = 0; i < contactsAvailability.size(); i++)
        {
            availableNames.add(contactsAvailability.get(i).getName());
        }

        // Set the names on the AutoCompleteTextView
        final AutoCompleteTextView addCallAutoComplete = (AutoCompleteTextView) view.findViewById(R.id.addCallName);
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(_context, android.R.layout.simple_list_item_1, availableNames);
        addCallAutoComplete.setAdapter(adapter);
        addCallAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos, long id) {
                nameInput = addCallAutoComplete.getText().toString();
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    private boolean formValidation() {
        List<String> listValidationMessages = new ArrayList<String>();
        if (isNullOrBlank(nameInput)){
            listValidationMessages.add("The name is invalid");
        }
        if (isNull(calendarCallAt)){
            listValidationMessages.add("The call at field was not set");
        }
        for (int i = 0; i < listValidationMessages.size(); i++)
            toastMessage(listValidationMessages.get(i), "red");
        return listValidationMessages.size() == 0;
    }

    private void toastMessage(String messageToToast, String color) {
        String colorToToast = CompareContent(color, "red") ? "#F6AE2D" : "#2df6ae";
        if(isNullOrBlank(colorToToast)) colorToToast="green"; // if the color was not selected - green
        Toast toast = Toast.makeText(getActivity(), messageToToast, Toast.LENGTH_LONG);
        toast.getView().setBackgroundColor(Color.parseColor(colorToToast));
        toast.show();
    }

    private void setPhoneCall() {
        String number = String.valueOf(submittedContact.getNumber());
        if (ContextCompat.checkSelfPermission(_activity,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_activity,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            String dial = "tel:" + number;
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setPhoneCall();
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void callAtTimeHandler(){
        ContactsAvailability contact = this.submittedContact;
        long  delayTillCall = getTimeCallInMilli(contact);
        if(delayTillCall<0)
        {
            toastMessage("Requested time already passed", "red");
        }
        else
        {
            reminderHandler(contact, delayTillCall);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setPhoneCall();
                }
            }, delayTillCall); //the time you want to delay in milliseconds
        }
    }

    @SuppressLint("DefaultLocale")
    public void reminderHandler(final ContactsAvailability contact, final long delayTime){
        if((delayTime / 1000)  / 60 < 5) { // if there is less then 5 min toast
            String message = "You will call to "+ contact.getName() +" in "+ (delayTime / 1000)  / 60 +" mins";
            toastMessage(message, "green");
        }
        else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String message = "You will call to "+ contact.getName() +" in 5 mins";
                    toastMessage(message, "green");
                }
            }, delayTime - FIVE_MINUTES); //the time you want to delay in milliseconds
        }
    }

    public void updateDisplayList() {

        List<String> listToDisplay = new ArrayList<String>();
        for (ContactsAvailability ca : contactsAvailability) {
            listToDisplay.add(ca.getDisplayString());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(_activity, android.R.layout.simple_list_item_1, listToDisplay);
        availableNamesDisplayView.setAdapter(adapter);
    }
}
