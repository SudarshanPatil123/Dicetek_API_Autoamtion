package TestCases;

import BaseClass.BaseClass;
import POJO.BookingDates;
import POJO.BookingRequest;
import POJO.BookingResponse;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
@Listeners(BaseClass.class)
public class BookingTests extends BaseClass {
    private int bookingId;

    @Test(priority = 1)
    public void addBookingTest() {
        // Create POJO for booking request
        BookingDates bookingDates = new BookingDates("2022-01-01", "2024-01-01");
        BookingRequest bookingRequest = new BookingRequest("testFirstName", "lastName", 10.11, true, bookingDates, "testAdd");

        // Send POST request with requestSpec and POJO
        Response response = given()
                .spec(bookingRequestSpec)
                .body(bookingRequest)
                .when()
                .post("/booking")
                .then()
                .spec(bookingResponsespec)
                .extract().response();
        System.out.println(response.asPrettyString());

        // Deserialize response to BookingResponse POJO
        BookingResponse bookingResponse = response.as(BookingResponse.class);

        // Extract booking ID
        bookingId = bookingResponse.getBookingid();
        Assert.assertNotEquals(bookingId, 0, "Booking ID should not be zero.");

        // Validate booking details from response
        Assert.assertEquals(bookingResponse.getBooking().getFirstname(), "testFirstName");
        Assert.assertEquals(bookingResponse.getBooking().getLastname(), "lastName");
    }

    @Test(priority = 2, dependsOnMethods = "addBookingTest")
    public void validateBookingTest() {
        // Send GET request with requestSpec and validate response
        given()
                .spec(bookingRequestSpec)
                .when()
                .get("/booking/" + bookingId)
                .then()
                .spec(bookingResponsespec)
                .body("firstname", equalTo("testFirstName"))
                .body("lastname", equalTo("lastName"))
                .body("totalprice", equalTo(10))
                .body("depositpaid", equalTo(true))
                .body("bookingdates.checkin", equalTo("2022-01-01"))
                .body("bookingdates.checkout", equalTo("2024-01-01"))
                .body("additionalneeds", equalTo("testAdd"));
    }

    @Test(priority = 3)
    public void negativeTest_InvalidBookingId() {
        // Send GET request with invalid booking ID
        given()
                .spec(bookingRequestSpec)
                .when()
                .get("/booking/99999999")
                .then()
                .statusCode(404);
    }

    @Test
    public void negativeTest_InvalidHttpMethod() {
        //Sending request with invalid HTTP method.
        given()
                .spec(bookingRequestSpec)
                .when()
                .put("/booking/1")  // or .delete("/booking/1")
                .then()
                .statusCode(403);
    }
}
