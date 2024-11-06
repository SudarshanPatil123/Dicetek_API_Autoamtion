package TestCases;

import BaseClass.BaseClass;
import POJO.DeviceData;
import POJO.DeviceRequest;
import POJO.DeviceResponse;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
@Listeners(BaseClass.class)
public class AddNewDeivceTests extends BaseClass {
    @Test
    public void testAddNewDeviceWithPOJOAndSpecs()
    {
        // Create request payload using POJO
        DeviceData deviceData = new DeviceData();
        deviceData.setYear(2023);
        deviceData.setPrice(7999.99);
        deviceData.setCPUModel("Apple ARM A7");
        deviceData.setHardDiskSize("1 TB");

        DeviceRequest deviceRequest = new DeviceRequest();
        deviceRequest.setName("Apple Max Pro 1TB");
        deviceRequest.setData(deviceData);

        // Send POST request using the RequestSpecification
        Response response = given()
                .spec(addDeviceRequestSpec)
                .body(deviceRequest)
                .post();

        // Validate response using ResponseSpecification
        response.then().spec(addDeviceResponsespec);
        System.out.println(response.asPrettyString());

        // Deserialize response to DeviceResponse POJO
        DeviceResponse deviceResponse = response.as(DeviceResponse.class);

        // Additional field-specific validations using assertions
        Assert.assertEquals(deviceResponse.getName(), "Apple Max Pro 1TB", "Device name is correct");
        Assert.assertEquals(deviceResponse.getData().getYear(), 2023, "Year is correct");
        Assert.assertEquals(deviceResponse.getData().getPrice(), 7999.99, "Price is correct");
    }
}
