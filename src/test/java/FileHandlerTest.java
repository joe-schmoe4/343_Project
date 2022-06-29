import data.Tenant;
import handlers.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

public class FileHandlerTest {

    @Test
    void TenantJSONTest() {

        System.out.println("|-----\n| RUNNING TEST\n| Tenant: Save and Load\n|-----");

        // Create Testing Variables
        Tenant.addTenant("Jared", 1);
        Tenant.addTenant("Garret", 2);
        Tenant.addTenant("Jocelyn", 3);
        Tenant.addTenant("Darian", 4);
        List<Tenant> dataToSave = List.copyOf(Tenant.getTenants());
        System.out.println("Data Saved: " + dataToSave);

        // Assert that saving is successful...
        assert FileHandler.getInstance().saveData();

        // Add additional Tenants (these should be overridden once loading Tenant is complete)
        System.out.println("Size Before Adding Throwaway Element: " + Tenant.getTenants().size());
        Tenant.addTenant("SHOULD BE GONE", 5);
        System.out.println("Size After Adding Throwaway Element: " + Tenant.getTenants().size());

        // Assert that loading data is successful...
        assert FileHandler.getInstance().loadData();

        // Assert that the loading was successful...
        System.out.println("Data Saved:  " + dataToSave);
        System.out.println("Data Loaded: " + Tenant.getTenants());
        System.out.println("First Tenant ID (Save):   " + dataToSave.get(0).getId());
        System.out.println("First Tenant ID (Load):   " + Tenant.getTenants().get(0).getId());
        assert Tenant.getTenants().equals(dataToSave);

        System.out.println("|-----\n| TEST PASSED\n| Tenant: Save and Load\n|-----");
    }

}
