package com.sample;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.common.net.MediaType;
import com.sample.model.Contact;


@WebServlet(
        name = "selectliquorservlet",
        urlPatterns = "/SelectLiquor"
)
public class SelectLiquorServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    	try {
    	
        String liquorType = req.getParameter("Type");
        String accessToken = req.getParameter("code");;
        LiquorService liquorService = new LiquorService();
        

       JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//        LiquorType l = LiquorType.valueOf(liquorType);

//        List liquorBrands = liquorService.getAvailableBrands(l);

//        req.setAttribute("brands", liquorBrands);
        
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential =
                new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
        credential.refreshToken();
        credential.setExpirationTimeMilliseconds(30000l);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(SelectLiquorServlet.class.getResourceAsStream("../../client_secrets.json")));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JacksonFactory.getDefaultInstance(), clientSecrets,
                Collections.singleton(DriveScopes.DRIVE_FILE))
                .build();
            // authorize
        
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setCallbackPath("/LiquorStoreApp/SelectLiquor").build();
         Credential credential2 = new AuthorizationCodeInstalledApp(flow, receiver).authorize("");
        Drive drive = new Drive.Builder(
        		httpTransport, JacksonFactory.getDefaultInstance(), credential2)
        		.setApplicationName("FCATest")
          .build();
			/*
			 * ByteArrayContent mediaContent = new ByteArrayContent(DOCX_MIME,
			 * getDocxAsByteArray("wordMLPackage"));
			 */
        File body = new File();
        body.setName("PraksahFile.csv");
        body.setMimeType("application/vnd.google-apps.spreadsheet");
        body.set("Test", "TestValue");
        
        //create a file
		  FileWriter myWriter = new FileWriter("PraksahFile.csv");
		  myWriter.write("Welcome to the google sheet, This is Prakash, I hope this would work for you.");
		  myWriter.close();
			 
        createXlsSheet();
        
        // uploading newly created xlsx file
//        InputStream inputStream = new FileInputStream("contacts.xlsx");
        
        InputStream inputStream = new FileInputStream("PraksahFile.csv");

        InputStreamContent inputStreamContent = new InputStreamContent("*/*", inputStream);
        File file = drive.files().create(body, inputStreamContent).execute();
        req.setAttribute("FID", file.getId());
        
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        RequestDispatcher view = req.getRequestDispatcher("result.jsp");
        view.forward(req, resp);

    }
    
    
    private void createXlsSheet () {
    	try {
    		List<Contact> contacts = new ArrayList<Contact>();
    		String[] columns = { "First Name", "Last Name", "Email",
    	    "Date Of Birth" };
    	    contacts.add(new Contact("Sylvain", "Saurel",
    	    	      "sylvain.saurel@gmail.com", "17/01/1980"));
    	    	    contacts.add(new Contact("Albert", "Dupond",
    	    	      "sylvain.saurel@gmail.com", "17/08/1989"));
    	    	    contacts.add(new Contact("Pierre", "Dupont",
    	    	      "sylvain.saurel@gmail.com", "17/07/1956"));
    	    	    contacts.add(new Contact("Mariano", "Diaz", "sylvain.saurel@gmail.com",
    	    	      "17/05/1988"));

    	    	    Workbook workbook = new XSSFWorkbook();
    	    	    Sheet sheet = workbook.createSheet("Contacts");

    	    	    Font headerFont = workbook.createFont();
    	    	    headerFont.setBold(true);
    	    	    headerFont.setFontHeightInPoints((short) 14);
    	    	    headerFont.setColor(IndexedColors.RED.getIndex());

    	    	    CellStyle headerCellStyle = workbook.createCellStyle();
    	    	    headerCellStyle.setFont(headerFont);

    	    	    // Create a Row
    	    	    Row headerRow = sheet.createRow(0);

    	    	    for (int i = 0; i < columns.length; i++) {
    	    	      Cell cell = headerRow.createCell(i);
    	    	      cell.setCellValue(columns[i]);
    	    	      cell.setCellStyle(headerCellStyle);
    	    	    }

    	    	    // Create Other rows and cells with contacts data
    	    	    int rowNum = 1;

    	    	    for (Contact contact : contacts) {
    	    	      Row row = sheet.createRow(rowNum++);
    	    	      row.createCell(0).setCellValue(contact.firstName);
    	    	      row.createCell(1).setCellValue(contact.lastName);
    	    	      row.createCell(2).setCellValue(contact.email);
    	    	      row.createCell(3).setCellValue(contact.dateOfBirth);
    	    	    }

    	    	    // Resize all columns to fit the content size
    	    	    for (int i = 0; i < columns.length; i++) {
    	    	      sheet.autoSizeColumn(i);
    	    	    }

    	    	    // Write the output to a file
    	    	    FileOutputStream fileOut = new FileOutputStream("contacts.xlsx");
    	    	    workbook.write(fileOut);
    	    	    fileOut.close();
    	} catch ( Exception e) {
    		e.printStackTrace();
    	}
    }
}
