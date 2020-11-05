package com.example.javamail;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    EditText To,Subject,Message;
    Button btSend;
    String sEmail,sPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        To = findViewById(R.id._to);
        Subject= findViewById(R.id._subject);
        Message=findViewById(R.id._message);
        btSend=findViewById(R.id.bt_send);

        sEmail="Enter sender email here"; //Enter sender email
        sPassword="Enter sender email password here";  //Enter password

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Properties properties = new Properties();
                properties.put("mail.smtp.auth","true");
                properties.put("mail.smtp.starttls.enable","true");
                properties.put("mail.smtp.host","smtp.gmail.com");
                properties.put("mail.smtp.port","587");

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sEmail,sPassword);
                    }
                });


                try {
                    //Initialise Email content
                    javax.mail.Message message = new MimeMessage(session);
                    //Sender email
                    message.setFrom(new InternetAddress(sEmail));
                    //Recipient email
                    message.setRecipients(javax.mail.Message.RecipientType.TO,InternetAddress.parse(To.getText().toString().trim()));
                    //Email subject
                    message.setSubject(Subject.getText().toString().trim());
                    //Email message
                    message.setText(Message.getText().toString().trim());

                    //Send email

                    new SendMail().execute(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private class SendMail extends AsyncTask<Message,String,String> {
        //Initialise progress dialog
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Create and show progress dialog
            progressDialog = ProgressDialog.show(MainActivity.this,"Please Wait","Sending Mail...",true,false);
        }

        @Override
        protected String doInBackground(javax.mail.Message... messages) {
            try {
                //When success
                Transport.send(messages[0]);
                return "Success";
            } catch (MessagingException e) {
                //When error
                e.printStackTrace();
                return "Error";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //dismiss progress dialog
            progressDialog.dismiss();
            if(s.equals("Success")){
                //initialise alert dialog
                AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color = '#509324'> Success </font>"));
                builder.setMessage("Mail sent successfully!");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //clear all EditText
                        To.setText("");
                        Subject.setText("");
                        Message.setText("");
                    }
                });
                //show alert dialog
                builder.show();

            }else {
                Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT).show();

            }
        }
    }
}