package atalay.okay.com.easyormex.main;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class DialogUpdate extends android.app.Dialog {

    private User user;
    private Information information;
    private EditText editTextName, editTextLastName, editTextNumber, editTextPhone, editTextAddress;
    private Button buttonUpdate;

    public DialogUpdate(@NonNull Context context, User user) {
        super(context);
        this.user = user;
        try {
            information = DatabaseManager.easyExecute.select(Constant.GET_INFORMATION, new Object[]{user.getInfoID()}, Information.class).get(0);
        } catch (QueryNotFoundException e) {
            e.printStackTrace();
        } catch (QueryExecutionException e) {
            e.printStackTrace();
        }
    }

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
        buttonUpdate = findViewById(R.id.buttonSave);
        fillFields();
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
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
                information.setAddress(address);
                information.setPhone(phone);
                user.setName(name);
                user.setLastName(lastName);
                user.setNumber(Integer.valueOf(number));
                try {
                    DatabaseManager.easyExecute.updateObject(Constant.UPDUTE_INFORMATION_BYID, information);
                    DatabaseManager.easyExecute.updateObject(Constant.UPDUTE_USERBYID, user);
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

    private void fillFields() {
        editTextName.setText(user.getName());
        editTextLastName.setText(user.getLastName());
        editTextNumber.setText("" + user.getNumber());
        editTextPhone.setText(information.getPhone());
        editTextAddress.setText(information.getAddress());
    }
}
