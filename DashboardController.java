package controller;

import model.DatabaseManager;
import model.User;
import view.DashboardFrame;

public class DashboardController {
    private DatabaseManager dbManager;
    private User user;
    
    public DashboardController(User user, DatabaseManager dbManager) {
        this.user = user;
        this.dbManager = dbManager;
    }
    
    public User getUser() {
        return user;
    }
    
    public boolean updateUserDetails(double height, double weight, double bmi) {
        user.setHeight(height);
        user.setWeight(weight);
        user.setBmi(bmi);
        return dbManager.updateUserDetails(user.getId(), height, weight, bmi);
    }
    
    public void showDashboardView() {
        new DashboardFrame(this).setVisible(true);
    }
}
