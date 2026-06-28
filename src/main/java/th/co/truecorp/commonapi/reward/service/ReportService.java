package th.co.truecorp.commonapi.reward.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import th.co.truecorp.commonapi.reward.cms.jpa.RedeemReportProjection;
import th.co.truecorp.commonapi.reward.cms.jpa.RedeemReportSummaryProjection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ReportService {

    private static Logger log = LoggerFactory.getLogger(ReportService.class);

    public byte[] generateSummaryRedeemExcelFile(List<RedeemReportSummaryProjection> reports) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Summary Redeem Report");

            // สร้าง Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Brand Code", "Redeem Status", "Campaign Type", "Period Date", "Total Transaction"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            // ใส่ข้อมูลลงในแถวของ Excel
            int rowNum = 1;
            for (RedeemReportSummaryProjection report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getBrand_code());
                row.createCell(1).setCellValue(report.getRedeem_status());
                row.createCell(2).setCellValue(report.getCampaign_type());
                row.createCell(3).setCellValue(report.getPeriod_date());
                row.createCell(4).setCellValue(report.getTotal_transaction());
            }

            // ปรับขนาดคอลัมน์อัตโนมัติ
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // แปลง Workbook เป็น byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    public byte[] generateRedeemExcelFile(List<RedeemReportProjection> reports) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Redeem Report");

            // สร้าง Header Row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Action Date", "Brand Code", "Digital Id", "Campaign Id", "Campaign Code", "Description", "Redeem Message", "Redeem Status"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(getHeaderCellStyle(workbook));
            }

            // ใส่ข้อมูลลงในแถวของ Excel
            int rowNum = 1;
            for (RedeemReportProjection report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getAction_date());
                row.createCell(1).setCellValue(report.getBrand_code());
                row.createCell(2).setCellValue(report.getDigital_id());
                row.createCell(3).setCellValue(report.getCampaign_id());
                row.createCell(4).setCellValue(report.getCampaign_code());
                row.createCell(5).setCellValue(report.getDescription());
                row.createCell(6).setCellValue(report.getRedeem_message());
                row.createCell(7).setCellValue(report.getRedeem_status());
            }

            // ปรับขนาดคอลัมน์อัตโนมัติ
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // แปลง Workbook เป็น byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray();
            }
        }
    }

    private CellStyle getHeaderCellStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }
}
