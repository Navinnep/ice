package ice.com.fileDownload;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.moqui.context.ExecutionContext;
import org.moqui.entity.EntityFacade;
import org.moqui.entity.EntityValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PrintServer2 {

    public Map<String, Object> printDocument(ExecutionContext ec){
        Map<String, Object> result = new HashMap<>();
             String printerId = ec.getContext().get("printerId").toString();
             String userId = ec.getContext().get("userId").toString();
             String userFullName = ec.getContext().get("userFullName").toString();
             String printEventId = ec.getContext().get("printEventId").toString();
             String expiryTime = ec.getContext().get("expiryTime").toString();
             String printCopyLimt = ec.getContext().get("printCopyLimt").toString();
             String encodedString = ec.getContext().get("encodedString").toString();
             String PrintDocumentFileName = ec.getContext().get("PrintDocumentFileName").toString();
             String selectedPageSize = ec.getContext().get("selectedPageSize").toString();
             String inputFilePath = ec.getContext().get("inputFilePath") != null ? ec.getContext().get("inputFilePath").toString() : null;
             String PrintReason = ec.getContext().get("PrintReason") != null ? ec.getContext().get("PrintReason").toString() : null;
             String PrintExecutionReference = ec.getContext().get("PrintExecutionReference") != null ? ec.getContext().get("PrintExecutionReference").toString() : null;
             String printRemark = ec.getContext().get("printRemark") != null ? ec.getContext().get("printRemark").toString() : null;

        try {
            EntityFacade ef = ec.getEntity();
            // get the initiator
            EntityValue printServer = ef.find("moqui.basic.Enumeration")
                    .condition("enumId", "Documents$PRINT_URL")
                    .one();
            String printServerUrl=printServer.getString("description");
            StringBuilder sb1 = new StringBuilder();

            sb1.append("{\n    \"printerId\": \"").append(printerId).append("\",\n");
            sb1.append("    \"userId\": \"").append(userId).append("\",\n");
            sb1.append("    \"userFullName\": \"").append(userFullName).append("\",\n");
            sb1.append("    \"printEventId\": \"").append(printEventId).append("\",\n");
            sb1.append("    \"selectedPageSize\": \"").append(selectedPageSize).append("\",\n");
            sb1.append("    \"expiryTime\": \"").append(expiryTime).append("\",\n");
            sb1.append("    \"encodedString\": \"").append(encodedString).append("\",\n");
            sb1.append("    \"printCopyLimt\": \"").append(printCopyLimt).append("\",\n");
            sb1.append("    \"PrintDocumentFileName\": \"").append(PrintDocumentFileName).append("\"\n}");
            String value1 = sb1.toString();
            // OkHttp request
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, value1);

            Request request = new Request.Builder()
                    .url(printServerUrl+"/rest/s1/cups/Print/PrintDocument")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Basic am9obi5kb2U6bW9xdWk=")
                    .build();

            // Execute the request and close the response
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();

//


                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(responseBody);
                String PrintEventId = jsonResponse.getString("PrintLogEventId");
                String expiryDate = jsonResponse.getString("expiryDate");

                // Extract the printCopiesInfo array
                JSONArray printCopiesArray = jsonResponse.getJSONObject("printCopiesInfo").getJSONArray("printCopiesInfo");

                // Iterate over the printCopiesInfo array and get each encoded string

                for (int i = 0; i < printCopiesArray.length(); i++) {
                    JSONObject copyInfo = printCopiesArray.getJSONObject(i);

                    // Extract the fields
                    String PrintencodedString = copyInfo.getString("PrintencodedString");  // Base64 encoded PDF
                    String outputFormattedTime = copyInfo.getString("outputFormattedTime"); // Formatted output time
                    String printDocumentName = copyInfo.getString("printDocumentName");  // Document name

                  /*  // Print the extracted values (for debugging)
                    System.out.println("PrintencodedString:--------------- " + PrintencodedString);
                    System.out.println("outputFormattedTime:--------------- " + outputFormattedTime);
                    System.out.println("printDocumentName:------------------- " + printDocumentName);*/

                    // Store the printed PDF in ICE server
                    ec.getService().sync().name("ice.JackrabbitServices.store#PrintPdfDocument")
                            .parameters(Collections.singletonMap("PrintencodedString", PrintencodedString))
                            .parameters(Collections.singletonMap("printDocumentName", printDocumentName))
                            .parameters(Collections.singletonMap("outputFormattedTime", outputFormattedTime))
                            .call();
                }

                // Create the activity log for managing the print logs
                ec.getService().sync().name("ice.JackrabbitServices.create#ActivityLogReport")
                        .parameters(Collections.singletonMap("uploadPath", inputFilePath))
                        .parameters(Collections.singletonMap("action", "Print"))
                        .parameters(Collections.singletonMap("actionDetails", "Document Printed by"))
                        .parameters(Collections.singletonMap("PrintReason", PrintReason))
                        .parameters(Collections.singletonMap("PrintExecutionReference", PrintExecutionReference))
                        .parameters(Collections.singletonMap("uniqueId", PrintEventId))
                        .parameters(Collections.singletonMap("PrintExpiryDate", expiryDate))
                        .parameters(Collections.singletonMap("fileType", "pdf"))
                        .parameters(Collections.singletonMap("type", "file"))
                        .parameters(Collections.singletonMap("printRemark", printRemark))
                        .parameters(Collections.singletonMap("printCopes", printCopyLimt))
                        .call();

                response.close();
            } else {
                System.out.println("Request failed with code: " + response.code());

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;

    }
    public Map<String, Object> connectedPrinter(ExecutionContext ec){


        Map<String, Object> result = new HashMap<>();
        try {
            EntityFacade ef = ec.getEntity();

            // get the initiator
            EntityValue printServer = ef.find("moqui.basic.Enumeration")
                    .condition("enumId", "Documents$PRINT_URL")
                    .one();
            System.out.println("printServerUrl-----------------------------"+printServer.getString("description"));
            String printServerUrl=printServer.getString("description");

            // Retrieve the connected printers
            OkHttpClient client = new OkHttpClient().newBuilder().build();

            //GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
            //delegator.removeByAnd("NetworkPrinter", UtilMisc.toMap());

            Request request = new Request.Builder()
//                    .url("http://192.168.0.158:8080/rest/s1/cups/Print/ConnectedPrinters")
                    //.url("http://192.168.0.177:8082/rest/s1/cups/Print/ConnectedPrinters") keerthana ip
                    .url(printServerUrl+"/rest/s1/cups/Print/ConnectedPrinters")
                    .get()
                    //.addHeader("Authorization", "Basic am9obi5kb2U6bW9xdWk=")
                    .addHeader("Authorization", "Basic bW9xdWkucHJpbnQ6bW9xdWk=")
                    .addHeader("Cookie", "JSESSIONID=node017rzf0nl28xv2pd4zl497a9ct25.node0")
                    .build();
            Response response = client.newCall(request).execute();


            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("responseBody-----"+responseBody);

                // Parse the JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode networkPrinterIdList = root.path("networkPrinterList");

                result.put("networkPrinterIdList", networkPrinterIdList);
                response.close();
            } else {
                System.out.println("Request failed with code: " + response.code());

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    return result;

    }

    public Map<String, Object> setupPrinter(ExecutionContext ec){
      String serverHost = ec.getContext().get("serverHost").toString();
        Map<String, Object> result = new HashMap<>();
        try {
            EntityFacade ef = ec.getEntity();
            // get the initiator
            EntityValue printServer = ef.find("moqui.basic.Enumeration")
                    .condition("enumId", "Documents$PRINT_URL")
                    .one();
            String printServerUrl=printServer.getString("description");
            // Retrieve the connected printers
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(printServerUrl+"/rest/s1/cups/Print/SetupPrinters?serverHost=" + serverHost)
                    .get()
                    .addHeader("Authorization", "Basic am9obi5kb2U6bW9xdWk=")
                    .addHeader("Cookie", "JSESSIONID=node017rzf0nl28xv2pd4zl497a9ct25.node0")
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                System.out.println("responseBody-----"+responseBody);
                // Parse the JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode networkPrinterIdList = root.path("networkPrinterIdList");
                JsonNode networkPrinterList = root.path("networkPrinterList");
                result.put("networkPrinterIdList", networkPrinterIdList);
                result.put("networkPrinterList", networkPrinterList);
                response.close();
            } else {
                System.out.println("Request failed with code: " + response.code());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return result;

    }

}
