package edu.byu.cs.tweeter.client.presenter;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {

    public interface View {
        void displayMessage(String message);
        void clearMessage();
        void displayErrorView(String message);
        void clearErrorView();

        void navigateToUser(User registeredUser);
    }

    private View view;

    public RegisterPresenter(View view) {
        this.view = view;
    }

    // Register and move to MainActivity.
    public void initiateRegister(EditText firstName, EditText lastName, EditText alias, EditText password, ImageView imageToUpload) {
        try {
            validateRegistration(firstName, lastName, alias, password, imageToUpload);
            view.clearErrorView();
            view.displayMessage("Registering...");

            // Convert image to byte array.
            Bitmap image = ((BitmapDrawable) imageToUpload.getDrawable()).getBitmap();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

            new UserService().registerUser(firstName.getText().toString(), lastName.getText().toString(), alias.getText().toString(),
                    password.getText().toString(), imageBytesBase64, new ResgisterObserver());

        } catch (Exception e) {
            view.displayErrorView(e.getMessage());
        }
    }

    public void validateRegistration(EditText firstName, EditText lastName, EditText alias, EditText password, ImageView imageToUpload) {
        if (firstName.getText().length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.getText().length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.getText().length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.getText().charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.getText().length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.getText().length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

    private class ResgisterObserver implements UserService.RegisterObserver {

        @Override
        public void registerSucceeded(User registeredUser, String name) {
            view.clearMessage();
            try {
                view.navigateToUser(registeredUser);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            view.displayMessage("Hello " + name);
        }

        @Override
        public void registerFailed(String error) {
            view.displayMessage(error);
        }
    }


}
