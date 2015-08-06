package giftview.co.giftview_android03;

/**
 * Created by deneb on 6/10/15.
 */
public final class LoginEvent {
    public String email;
    public String password;

    public LoginEvent (String email, String password) {
        this.email = email;
        this.password = password;
    }
}
