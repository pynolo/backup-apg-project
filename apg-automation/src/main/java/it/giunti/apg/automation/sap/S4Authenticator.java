package it.giunti.apg.automation.sap;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class S4Authenticator extends Authenticator {
    private String user;
    private String password;

    public S4Authenticator(String user,String password) {
      this.user = user;
      this.password = password;
    }
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        PasswordAuthentication auth = new PasswordAuthentication(user,password.toCharArray());
        return auth;
    }
}
