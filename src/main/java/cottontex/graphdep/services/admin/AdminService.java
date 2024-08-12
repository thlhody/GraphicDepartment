package cottontex.graphdep.services.admin;

import cottontex.graphdep.database.interfaces.admin.IAdminHandler;

public class AdminService {
    private final IAdminHandler adminHandler;

    public AdminService(IAdminHandler adminHandler) {
        this.adminHandler = adminHandler;
    }

    // Add methods for admin-specific operations here
}