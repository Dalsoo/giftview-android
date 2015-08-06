package giftview.co.giftview_android03;

/**
 * Created by deneb on 6/10/15.
 */

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;

import de.greenrobot.event.EventBus;

public class FirebaseWrap {
    private static final String FIREBASE_URL = "https://resplendent-heat-6040.firebaseio.com/";
    private Firebase mFirebaseRef;

    public void onEvent(CreateUserEvent e) {
        CreateUser(e.email, e.password);
    }

    public void onEvent(LoginEvent e) {
        Login(e.email, e.password);
    }

    public void onEvent(LogoutEvent e) {
        Logout();
    }

    public FirebaseWrap() {
        mFirebaseRef = new Firebase(FIREBASE_URL);
        EventBus.getDefault().register(this);
    }

    private void DetectConnect() {
        mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    System.out.println("connected");
                } else {
                    System.out.println("not connected");
                }
            }

            @Override
            public void onCancelled(FirebaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    private void CreateUser(String email, String password) {
        mFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                System.out.println("Successfully created user account with uid: " + result.get("uid"));
                EventBus.getDefault().post(new CreateUserResultEvent(0));
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // there was an error
                EventBus.getDefault().post(new CreateUserResultEvent(firebaseError.getCode()));
            }
        });
    }

    private void Login(String email, String password) {
        mFirebaseRef.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                System.out.println("User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                EventBus.getDefault().post(new LoginResultEvent(0));
            }

            @Override
            public void onAuthenticationError(FirebaseError error) {
                // Something went wrong :(
                EventBus.getDefault().post(new LoginResultEvent(error.getCode()));
            }
        });
    }

    private void Logout() {
        mFirebaseRef.unauth();
    }

    private void ChangeEmail(String oldEmail, String newEmail, String password) {
        mFirebaseRef.changeEmail(oldEmail, password, newEmail, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // email changed
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // error encountered
            }
        });
    }

    private void ChangePassword(String newPassword, String email, String oldPassword) {
        mFirebaseRef.changePassword(email, oldPassword, newPassword, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // email changed
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // error encountered
            }
        });
    }

    private void ResetPassword(String email) {
        mFirebaseRef.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // email changed
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // error encountered
            }
        });
    }

    private void RemoveUser(String email, String password) {
        mFirebaseRef.removeUser(email, password, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // user removed
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                // error encountered
            }
        });
    }

    private void GetUserData() {

    }

    private void SetUserData() {

    }
}
