package BaseClass;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class BaseClass implements ITestListener {
    public RequestSpecification bookingRequestSpec;
    public RequestSpecification addDeviceRequestSpec;
    public ResponseSpecification bookingResponsespec;

    public ResponseSpecification addDeviceResponsespec;
    public static ExtentReports extent;
    public ExtentTest extentTest;

    @BeforeTest
    public static void disableSSLVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            RestAssured.useRelaxedHTTPSValidation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeTest
    public void setupSpecs() {

        RestAssured.baseURI = "https://api.restful-api.dev";
        addDeviceRequestSpec = RestAssured
                .given()
                .baseUri("https://api.restful-api.dev")
                .header("Content-Type", "application/json")
                .basePath("/objects");
        addDeviceResponsespec = RestAssured
                .expect()
                .statusCode(200)
                .contentType("application/json")
                .body("id", notNullValue())
                .body("createdAt", notNullValue());

        bookingRequestSpec = given()
                .baseUri("https://restful-booker.herokuapp.com")
                .header("Content-Type", "application/json");

        // Initialize reusable response specification for
        bookingResponsespec = RestAssured.expect()
                .statusCode(200)
                .contentType("application/json");
    }



    @BeforeSuite
    public void setupExtentReport() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("extentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setSystemInfo("Environment", "Test Automation");
        extent.setSystemInfo("Tester", "John Doe");
    }

    @AfterSuite
    public void tearDownExtentReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Start the ExtentTest for this test case
        extentTest = extent.createTest(result.getMethod().getMethodName());
        extentTest.info("Test Started: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // Log success message
        extentTest.pass("Test Passed: " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // Log failure message and capture exception
        extentTest.fail("Test Failed: " + result.getMethod().getMethodName());
        extentTest.fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // Log skip message
        extentTest.skip("Test Skipped: " + result.getMethod().getMethodName());
    }

    @Override
    public void onFinish(ITestContext context) {
        // Finalize Extent Report
        if (extent != null) {
            extent.flush();
        }
    }
}
