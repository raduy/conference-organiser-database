package pl.dbgen.entities;

import pl.dbgen.partialgenerators.PrimitiveDataGenerator;
import pl.dbgen.namesandpathes.SQLProcedureName;

/**
 * @author Lukasz Raduj
 */
public class Client {
    private final int clientID;
    private static int id = 1;
    private final String name;
    private final boolean isCompany;
    private final String phone;
    private final String country;
    private final String street;
    private final String city;


    public Client(String name, boolean company, String phone, String country, String street, String city) {
        this.clientID = id++;
        this.name = name;
        isCompany = company;
        this.phone = phone;
        this.country = country;
        this.street = street;
        this.city = city;
    }

    public String buildClientExecString() {
        StringBuilder builder = new StringBuilder();
        builder.append(SQLProcedureName.ADD_CLIENT);

        builder.append("'").append(name).append("', ")
                .append(isCompany ? 1 : 0).append(", '")
                .append(phone).append("', '")
                .append(country).append("', '")
                .append(street).append("', '")
                .append(city).append("';\n");

        return builder.toString();
    }

    public int getClientID() {
        return clientID;
    }

    public static int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isCompany() {
        return isCompany;
    }

    public String getPhone() {
        return phone;
    }

    public String getCountry() {
        return country;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }
}
