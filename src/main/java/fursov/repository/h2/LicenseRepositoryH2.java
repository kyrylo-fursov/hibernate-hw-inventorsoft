package fursov.repository.h2;

import fursov.model.License;
import fursov.model.User;

import java.util.List;
import static fursov.util.JpaUtil.close;
import static fursov.util.JpaUtil.init;
import static fursov.util.JpaUtil.performReturningWithinPersistenceContext;
import static fursov.util.JpaUtil.performWithinPersistenceContext;

public class LicenseRepositoryH2 {
    public static void main(String[] args) {
        init("BasicEntitiesH2");

        License license = new License();
        license.setLicenseNumber("LIC12345");
        User user = new User();
        user.setName("Licence User Name");
        user.setEmail("licence@user.com");
        UserRepositoryH2.saveUser(user);
        license.setUser(user);

        System.out.printf("License before save: %s%n", license);

        saveLicense(license);
        System.out.printf("Stored license: %s%n", license);

        printAllLicenses();

        License foundLicense = findLicenseByNumber(license.getLicenseNumber());
        System.out.printf("License found by number (%s): %s%n", license.getLicenseNumber(), foundLicense);

        license.setLicenseNumber("LIC67890");
        license = updateLicense(license);
        System.out.printf("Updated license: %s%n", license);

        printAllLicenses();

        removeLicense(license);
        printAllLicenses();

        close();
    }

    public static void saveLicense(License license) {
        performWithinPersistenceContext(em -> em.persist(license));
    }

    public static void printAllLicenses() {
        System.out.printf("All licenses: %s%n", findAllLicenses());
    }

    public static List<License> findAllLicenses() {
        return performReturningWithinPersistenceContext(em ->
                em.createQuery("SELECT l FROM License l", License.class).getResultList()
        );
    }

    public static License findLicenseByNumber(String licenseNumber) {
        return performReturningWithinPersistenceContext(em ->
                em.createQuery("SELECT l FROM License l WHERE l.licenseNumber = :licenseNumber", License.class)
                        .setParameter("licenseNumber", licenseNumber)
                        .getSingleResult()
        );
    }

    public static License updateLicense(License license) {
        return performReturningWithinPersistenceContext(em -> em.merge(license));
    }

    public static void removeLicense(License license) {
        performWithinPersistenceContext(em -> {
            License managedLicense = em.merge(license);
            em.remove(managedLicense);
        });
    }
}
