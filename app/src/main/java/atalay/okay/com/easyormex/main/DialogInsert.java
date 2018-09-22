package atalay.okay.com.easyormex.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Random;

import atalay.okay.com.easyormex.Constant;
import atalay.okay.com.easyormex.DatabaseManager;
import atalay.okay.com.easyormex.R;
import atalay.okay.com.easyormex.dbObject.Information;
import atalay.okay.com.easyormex.dbObject.User;
import okay.atalay.com.easyorm.src.exception.FieldNotFoundException;
import okay.atalay.com.easyorm.src.exception.QueryExecutionException;
import okay.atalay.com.easyorm.src.exception.QueryNotFoundException;

/**
 * Created by 1 on 22.09.2018.
 */

public class DialogInsert extends android.app.Dialog {
    public DialogInsert(@NonNull Context context) {
        super(context);
    }

    private EditText editTextName, editTextLastName, editTextNumber, editTextPhone, editTextAddress;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setContentView(R.layout.dialog_insert);
        editTextName = findViewById(R.id.editTextName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextNumber = findViewById(R.id.editTextNumber);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                if ("".equals(name)) {
                    return;
                }
                String lastName = editTextLastName.getText().toString().trim();
                String number = editTextNumber.getText().toString().trim();
                String phone = editTextPhone.getText().toString().trim();
                String address = editTextAddress.getText().toString().trim();
                Information information = new Information();
                information.setAddress(address);
                information.setPhone(phone);
                information.setID(new Random().nextInt());
                User user = new User();
                user.setName(name);
                user.setLastName(lastName);
                user.setNumber(Integer.valueOf(number));
                user.setInfoID(information.getID());
                try {
                    DatabaseManager.easyExecute.insert(Constant.ADD_INFORMATIONS, information);
                    DatabaseManager.easyExecute.insert(Constant.ADDUSER, user);
                } catch (QueryNotFoundException e) {
                    e.printStackTrace();
                } catch (FieldNotFoundException e) {
                    e.printStackTrace();
                } catch (QueryExecutionException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
    }
}
