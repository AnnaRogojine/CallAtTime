
/*
    Assignment: 1
    Campus: Ashdod
    Author:George Djabarov ID: 321335531
    Author2: Anna Rogojine, ID: 323686477
    */

package com.example.callattime;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.callattime.utilities.isNullOrBlank;

public class FirstFragment extends Fragment {
    final String FirstFragmentTag = "Input Form State";

    private String name, phone, fromTime, toTime;
    private boolean isValidForm;

    private EditText nameInput;
    private EditText phoneInput;

    private TextView fromTimeInput;
    private TextView toTimeInput;

    Calendar calendarFrom, calendarTo;


    private TimePickerDialog fromTimeDialog;
    private TimePickerDialog toTimeDialog;

    private Button submitButton;

    private SharedPreferences sharedPreferences;
    private Gson gson = new Gson();

    private List<ContactsAvailability> contactsAvailability = new ArrayList<ContactsAvailability>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_first, container, false);

        nameInput =(EditText) view.findViewById(R.id.nameInput);
        phoneInput = (EditText) view.findViewById(R.id.phoneInput);

        fromTimeInput = view.findViewById(R.id.fromTime);
        fromTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromTimeDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendarFrom = Calendar.getInstance();
                                calendarFrom.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendarFrom.set(Calendar.MINUTE, minute);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                                fromTime = format.format(calendarFrom.getTime());
                                fromTimeInput.setText("From: " + fromTime);

                            }
                        }, 12, 00, true);
                fromTimeDialog.show();
            }
        });

        toTimeInput = view.findViewById(R.id.toTime);
        toTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toTimeDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressLint("SetTextI18n")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                calendarTo = Calendar.getInstance();
                                calendarTo.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendarTo.set(Calendar.MINUTE, minute);
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                                toTime = format.format(calendarTo.getTime());
                                toTimeInput.setText("To: " + toTime);
                            }
                        }, 12, 00, true);
                toTimeDialog.show();
            }
        });


        submitButton = (Button) view.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameInput.getText().toString();
                phone = phoneInput.getText().toString();

                if(formValidation())
                {
                    // Create object of ContactsAvailability
                    ContactsAvailability newContactsAvailability = new ContactsAvailability(name, Integer.parseInt(phone), calendarFrom, calendarTo);

                    // Add it to local list
                    contactsAvailability.add(newContactsAvailability);


                    // Create SharedPreferences("ContactsInfo") and save all the ContactsAvailability under ContactsAvailability
                    sharedPreferences = getActivity().getSharedPreferences("ContactsInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String jsonContactsAvailability = gson.toJson(contactsAvailability);
                    editor.putString("ContactsAvailability", jsonContactsAvailability);
                    editor.apply();
                    nameInput.setText("");
                    phoneInput.setText("");

                    //

                }



            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private boolean formValidation() {
        List<String> listValidationMessages = new ArrayList<String>();
        if (isNullOrBlank(name)){
            // TODO check duplication
            listValidationMessages.add("The name is empty");
        }
        if (isNullOrBlank(phone)){
            listValidationMessages.add("The phone number is empty");
        }
        else
        {
            if(!phone.matches("\\d+(?:\\.\\d+)?"))
            {
                listValidationMessages.add("The phone number is not numeric");
            }
            if(phone.length()<10)
            {
                listValidationMessages.add("The phone number is too short");
            }
            if(phone.length()>10)
            {
                listValidationMessages.add("The phone number is too long");
            }
        }
        if (isNullOrBlank(fromTime)){
            listValidationMessages.add("The fromTime is empty");
        }
        if (isNullOrBlank(toTime)){
            listValidationMessages.add("The toTime is empty");
        }

        for (int i = 0; i < listValidationMessages.size(); i++)
        {
            Toast toast = Toast.makeText(getActivity(), listValidationMessages.get(i), Toast.LENGTH_LONG);
            toast.getView().setBackgroundColor(Color.parseColor("#F6AE2D"));
            toast.show();
        }

        return listValidationMessages.size() == 0;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.button_first).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }
}