package com.ayusma.pharamacymanagementsystem;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class AlertDialogHelper {
    private static AlertDialog alertDialog;


    public static void createAlertDialog(Context context, String text) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Processing");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(R.layout.loading_dialoag);
        View dialog = View.inflate(context, R.layout.loading_dialoag, null);
        TextView textView = dialog.findViewById(R.id.text_view_dialog);
        textView.setText(text);
        alertDialog = alertDialogBuilder.create();

    }

    public static void showDialog() {
        alertDialog.show();
    }

    public static void hideDialog() {
        alertDialog.hide();
    }

}
