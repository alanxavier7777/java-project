package controller;

import model.DatabaseManager;
import model.User;
import view.LoginFrame;
import view.RegisterFrame;

public class AuthController {
    private DatabaseManager dbManager;
    
    public AuthController() {
        this.dbManager = new DatabaseManager();
    }
    
    public User authenticateUser(String username, String password) {
        return dbManager.loginUser(username, password);
    }
    
    public boolean registerUser(User user) {
        return dbManager.registerUser(user);
    }
    
    public boolean userExists(String username, String email) {
        return dbManager.userExists(username, email);
    }
    
    public void showLoginView() {
        new LoginFrame(this).setVisible(true);
    }
    
    public void showRegisterView() {
        new RegisterFrame(this).setVisible(true);
    }
    
    public DatabaseManager getDatabaseManager() {
        return dbManager;
    }
}
