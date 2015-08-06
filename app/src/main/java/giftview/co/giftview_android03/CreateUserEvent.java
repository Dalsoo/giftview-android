package giftview.co.giftview_android03;

public final class CreateUserEvent {
    public String email;
    public String password;

    public CreateUserEvent(String Email, String Password) {
        email = Email;
        password = Password;
    }
}
