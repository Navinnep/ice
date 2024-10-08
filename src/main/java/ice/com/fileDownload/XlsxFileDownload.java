package ice.com.fileDownload;

import com.exports.XlsxFile;
import org.moqui.context.ExecutionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XlsxFileDownload {

    public Map<String, Object> printLogXLSX(ExecutionContext ec) throws Exception {
        Map<String, Object> results = new HashMap<>();
        HttpServletResponse response = ec.getWeb().getResponse();

        String pageNumber = ec.getContext().get("pageNumber") != null ? (String) ec.getContext().get("pageNumber") : null;
        String pageSize = ec.getContext().get("pageSize") != null ? (String) ec.getContext().get("pageSize") : null;
        // Extract filter parameters from execution context
        String usernameFilter = ec.getContext().get("usernameFilter") != null ? (String) ec.getContext().get("usernameFilter") : null;
        String username = ec.getContext().get("username") != null ? (String) ec.getContext().get("username") : null;
        String documentNameFilter = ec.getContext().get("documentNameFilter") != null ? (String) ec.getContext().get("documentNameFilter") : null;
        String documentNumber = ec.getContext().get("documentNumberFilter") != null ? (String) ec.getContext().get("documentNumberFilter") : null;
        String executionReferenceFilter = ec.getContext().get("executionReferenceFilter") != null ? (String) ec.getContext().get("executionReferenceFilter") : null;
        String printReasonFilter = ec.getContext().get("printReasonFilter") != null ? (String) ec.getContext().get("printReasonFilter") : null;
        String startDateStr = ec.getContext().get("startDateStr") != null ? (String) ec.getContext().get("startDateStr") : "";
        String endDateStr = ec.getContext().get("endDateStr") != null ? (String) ec.getContext().get("endDateStr") : "";
        String durationFilter = ec.getContext().get("durationFilter") != null ? (String) ec.getContext().get("durationFilter") : "";
        String printEventId = ec.getContext().get("printEventId") != null ? (String) ec.getContext().get("printEventId") : null;
        String documentFilter = ec.getContext().get("documentFilter") != null ? (String) ec.getContext().get("documentFilter") : null;
        String printRecoverStatusFilter = ec.getContext().get("printRecoverStatusFilter") != null ? (String) ec.getContext().get("printRecoverStatusFilter") : null;

        // Fetch report data
        Map<String, Object> repLog = ec.getService().sync().name("ice.JackrabbitServices.get#ExportPrintReportDocument")
                .parameters(Collections.singletonMap("pageNumber", pageNumber))
                .parameters(Collections.singletonMap("pageSize", pageSize))
                .parameters(Collections.singletonMap("usernameFilter", usernameFilter))
                .parameters(Collections.singletonMap("username", username))
                .parameters(Collections.singletonMap("documentNameFilter", documentNameFilter))
                .parameters(Collections.singletonMap("printReasonFilter", printReasonFilter))
                .parameters(Collections.singletonMap("executionReferenceFilter", executionReferenceFilter))
                .parameters(Collections.singletonMap("documentNumberFilter", documentNumber))
                .parameters(Collections.singletonMap("startDateStr", startDateStr))
                .parameters(Collections.singletonMap("endDateStr", endDateStr))
                .parameters(Collections.singletonMap("durationFilter", durationFilter))
                .parameters(Collections.singletonMap("printEventId", printEventId))
                .parameters(Collections.singletonMap("documentFilter", documentFilter))
                .parameters(Collections.singletonMap("printRecoverStatusFilter", printRecoverStatusFilter))
                .call();

        List<Map<String, String>> reportLogs = (List<Map<String, String>>) repLog.get("reportLogs");

        // Set response headers
        String filename = "PrintLog_" + ec.getUser().getUserId() + "_" + System.currentTimeMillis() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Access-Control-Expose-Headers", "filename");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("filename", filename);

        try (OutputStream out = response.getOutputStream()) {
            XlsxFile xlsl = new XlsxFile();
            xlsl.createXLSX(reportLogs,out);
            results.put("status", "success");
            results.put("message", "generated successfully");
        } catch (IOException e) {
            ec.getMessage().addError("Error writing XLSX file to output stream: " + e.getMessage());
            results.put("status", "error");
            results.put("message", "Failed to generate report: " + e.getMessage());
        }

        return results;
    }


    public Map<String, Object> activityLogXLSX(ExecutionContext ec) throws Exception {
        Map<String, Object> results = new HashMap<>();
        HttpServletResponse response = ec.getWeb().getResponse();

        String pageNumber = ec.getContext().get("pageNumber") != null ? (String) ec.getContext().get("pageNumber") : null;
        String pageSize = ec.getContext().get("pageSize") != null ? (String) ec.getContext().get("pageSize") : null;
        String usernameFilter = ec.getContext().get("usernameFilter") != null ? (String) ec.getContext().get("usernameFilter") : null;
        String username = ec.getContext().get("username") != null ? (String) ec.getContext().get("username") : null;
        String actionFilter = ec.getContext().get("actionFilter") != null ? (String) ec.getContext().get("actionFilter") : null;
        String documentStatusFilter = ec.getContext().get("documentStatusFilter") != null ? (String) ec.getContext().get("documentStatusFilter") : null;
        String documentNameFilter = ec.getContext().get("documentNameFilter") != null ? (String) ec.getContext().get("documentNameFilter") : null;
        String fileTypeFilter = ec.getContext().get("fileTypeFilter") != null ? (String) ec.getContext().get("fileTypeFilter") : null;
        String fileFilter = ec.getContext().get("fileFilter") != null ? (String) ec.getContext().get("fileFilter") : null;
        String documentNumberFilter = ec.getContext().get("documentNumberFilter") != null ? (String) ec.getContext().get("documentNumberFilter") : null;
        String startDateStr = ec.getContext().get("startDateStr") != null ? (String) ec.getContext().get("startDateStr") : "";
        String endDateStr = ec.getContext().get("endDateStr") != null ? (String) ec.getContext().get("endDateStr") : "";
        String durationFilter = ec.getContext().get("durationFilter") != null ? (String) ec.getContext().get("durationFilter") : "";
        String printEventId = ec.getContext().get("printEventId") != null ? (String) ec.getContext().get("printEventId") : null;
        String documentFilter = ec.getContext().get("documentFilter") != null ? (String) ec.getContext().get("documentFilter") : null;
        boolean includeyFolders = Boolean.parseBoolean(ec.getContext().get("includeyFolders").toString());
        // Fetch report data
        Map<String, Object> repLog = ec.getService().sync().name("ice.JackrabbitServices.get#ExportActivityReportDocument")
                .parameters(Collections.singletonMap("pageNumber", pageNumber))
                .parameters(Collections.singletonMap("pageSize", pageSize))
                .parameters(Collections.singletonMap("usernameFilter", usernameFilter))
                .parameters(Collections.singletonMap("username", username))
                .parameters(Collections.singletonMap("actionFilter", actionFilter))
                .parameters(Collections.singletonMap("documentStatusFilter", documentStatusFilter))
                .parameters(Collections.singletonMap("documentNameFilter", documentNameFilter))
                .parameters(Collections.singletonMap("fileTypeFilter", fileTypeFilter))
                .parameters(Collections.singletonMap("fileFilter", fileFilter))
                .parameters(Collections.singletonMap("documentNumberFilter", documentNumberFilter))
                .parameters(Collections.singletonMap("startDateStr", startDateStr))
                .parameters(Collections.singletonMap("endDateStr", endDateStr))
                .parameters(Collections.singletonMap("durationFilter", durationFilter))
                .parameters(Collections.singletonMap("printEventId", printEventId))
                .parameters(Collections.singletonMap("documentFilter", documentFilter))
                .parameters(Collections.singletonMap("includeyFolders", includeyFolders))
                .call();

        List<Map<String, String>> reportLog = (List<Map<String, String>>) repLog.get("reportLogs");

        // Set response headers
        String filename = "ActivityLog_" + ec.getUser().getUserId() + "_" + System.currentTimeMillis() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Access-Control-Expose-Headers", "filename");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setHeader("filename", filename);

        try (OutputStream out = response.getOutputStream()) {
            XlsxFile xlsl = new XlsxFile();
            xlsl.createXLSX(reportLog,out);
            results.put("status", "success");
            results.put("message", "generated successfully");
        } catch (IOException e) {
            ec.getMessage().addError("Error writing XLSX file to output stream: " + e.getMessage());
            results.put("status", "error");
            results.put("message", "Failed to generate report: " + e.getMessage());
        }

        return results;
    }





}